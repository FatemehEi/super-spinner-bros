package utils;

import android.hardware.Sensor;
import android.util.Log;
import android.widget.ImageView;

/**
 * The entity for a ball in the field
 */
public class Ball {
    /*
    Just a name for ball, which is used for debugging
     */
    private String name;

    /*
    The current position of ball
     */
    private PointFF p;

    /*
    The current velocity of ball
     */
    private PointFF v;

    /*
     * The angle (in radian) from normal surface. Can be positive or negative.
     * It's used for gyroscope sensor.
     */
    private PointFF theta;

    /*
     * These are the base values that when we enter the battle field, the balls are given.
     * These are used when we reset the battle by touching the screen
     */
    private PointFF basePos;
    private PointFF baseVel;

    private ImageView view;

    /*
    The radius of ball, in pixel. The larger the ball, the bigger its radius is.
     */
    private int radius;

    /*
    The mass of ball
     */
    private int mass;

    /*
     * Determines if the balls collided in the last iteration.
     * It's like a spare clock to give balls time to get aside from each other
     */
    private boolean lastCollide;

    /*
     * This is a reference to the ball that this ball might collide with
     * It's a bad design to put it here but it was the easiest way! :/
     */
    private Ball adj;

    public Ball(String name, float x, float y, float vX, float vY, int radius, int mass, ImageView view) {
        this.name = name;
        this.radius = radius;
        this.mass = mass;
        this.view = view;

        this.p = new PointFF(this.centralizeX(x), this.centralizeY(y));
        this.v = new PointFF(vX, vY);

        this.theta = new PointFF();

        this.basePos = new PointFF(this.p);
        this.baseVel = new PointFF(this.v);


        this.adj = null;

        this.lastCollide = false;

        Log.d(this.name + "_init", String.format("[pos: %s, vel: %s, rad: %d]", this.p.toString(), this.v.toString(), this.radius));
    }

    public void setAdjBall(Ball b) {
        this.adj = b;
    }

    public void updateView() {
        this.updateXView();
        this.updateYView();
        Log.v("view_x", String.format("(%f, %f)", this.p.x, this.p.y));
    }

    public void resetValues() {
        this.p = new PointFF(this.basePos);
        this.v = new PointFF(this.baseVel);
        this.theta = new PointFF();
    }

    /**
     * Gets sensor value, applies "a", "v", collision, and "x"
     *
     * @param deltaThetaOrA if gyroscope, it's deltaTheta. if gravity, it's acceleration
     */
    public void applySensorChange(PointFF deltaThetaOrA, float deltaTime) {
        float aX = 0, aY = 0;

        if (Utility.sensorType == Sensor.TYPE_GYROSCOPE) {
            deltaThetaOrA.multiply(deltaTime);
            this.theta.offset(deltaThetaOrA);
            this.debugLog("theta", "", "theta becomes", this.theta.toString());
            Log.w(this.name + "_sin-cos_x", String.format("sin-cos of theta: %f: (%f, %f)", this.theta.x, Math.sin(this.theta.x), Math.cos(this.theta.x)));
            Log.w(this.name + "_sin-cos_y", String.format("sin-cos of theta: %f: (%f, %f)", this.theta.y, Math.sin(this.theta.y), Math.cos(this.theta.y)));
        } else if (Utility.sensorType == Sensor.TYPE_GRAVITY) {
            aX = deltaThetaOrA.x;
            aY = deltaThetaOrA.y;
        }

        try {
            if (this.adj != null) {
                float currentDist = this.getDistanceFrom(this.adj);
                float collisionThres = this.getCollisionDistanceFrom(this.adj) + Utility.COLLISION_OFFSET;
                boolean doesCollide = (currentDist <= collisionThres);
                if (doesCollide && !this.lastCollide) {
                    this.debugLog("dist", "_", Float.toString(currentDist), Float.toString(collisionThres));
                    this.applyCollisionAndSetNewV();
                    this.lastCollide = this.adj.lastCollide = true;
                }
            }

            if (Utility.sensorType == Sensor.TYPE_GYROSCOPE) {
                aX = this.applyForceInDimensionAndGetA("x");
                aY = this.applyForceInDimensionAndGetA("y");
            }
            this.debugLog("acceleration", "x", "applied acceleration", aX);
            this.debugLog("acceleration", "y", "applied acceleration", aY);

            this.applyAAndSetNewV("x", aX, deltaTime);
            this.applyAAndSetNewV("y", aY, deltaTime);
        } catch (NoMovementException e) {
            this.warnLog("movement", "_", e.getMessage());
        }

        this.applyVAndSetNewP("x", aX, deltaTime);
        this.applyVAndSetNewP("y", aY, deltaTime);
        this.normalize();

        if (this.lastCollide)
            this.lastCollide = false;
    }


    /**
     * calculates "a" when using gyroscope
     */
    private float applyForceInDimensionAndGetA(String f) throws NoMovementException {
        // If both of these two conditions meet, we don't have movement:
        // 1- velocity ~= 0
        // 2- tan(theta) <= uS
        boolean cond1 = Math.abs(this.v.getField(f)) <= Utility.EPSILON;
        boolean cond2 = Math.abs(Math.tan(this.theta.getField(f))) <= Utility.uS;
        if (cond1 && cond2)
            throw new NoMovementException("conditions not met");
        this.warnLog("movement", f, "applied due to conditions", String.format("%s - %s", cond1, cond2));

        // Now that we reach here, it means that we have movement! We must calculate "a" and use "uK" now.
        float a = Utility.calculateAccelerationFromTheta(this.epsilonize(this.theta.getField(f)));
        a = Utility.calculateAccelerationNormalizer(a, this.v.getField(f), this.epsilonize(this.theta.getField(f)));
        return this.epsilonize(a);
    }

    /**
     * calculates new "v" after balls' collision -> the 7 step formula
     */
    private void applyCollisionAndSetNewV() {
        // Use given formula to calculate new v for balls
        PointFF c1 = this.p.add(this.radius);
        PointFF c2 = this.adj.p.add(this.adj.radius);
        PointFF nVector = new PointFF(c2.x - c1.x, c2.y - c1.y);
        PointFF un = nVector.normal();
        PointFF ut = un.perpendicular();

        float V1n = un.dot(this.v);
        float V1t = ut.dot(this.v);
        float V2n = un.dot(this.adj.v);
        float V2t = ut.dot(this.adj.v);
        this.debugLog("collision", "_", "old Vs", String.format("(V1n: %f, V1t: %f, V2n: %f, V2t: %f", V1n, V1t, V2n, V2t));

        float newV1t = V1t;
        float newV2t = V2t;
        float newV1n = Utility.calculateNewVInOneDimension(V1n, V2n, this.mass, this.adj.mass);
        float newV2n = Utility.calculateNewVInOneDimension(V2n, V1n, this.adj.mass, this.mass);
        this.debugLog("collision", "_", "new Vs", String.format("(V1n: %f, V1t: %f, V2n: %f, V2t: %f", newV1n, newV1t, newV2n, newV2t));

        PointFF newV1nVector = un.mult(newV1n);
        PointFF newV1tVector = ut.mult(newV1t);
        PointFF newV2nVector = un.mult(newV2n);
        PointFF newV2tVector = ut.mult(newV2t);

        PointFF newV1 = newV1nVector.add(newV1tVector);
        PointFF newV2 = newV2nVector.add(newV2tVector);

        this.v.setFrom(newV1);
        this.adj.v.setFrom(newV2);
        this.debugLog("collision", "_", "new v1", this.v.toString());
        this.debugLog("collision", "_", "new v2", this.adj.v.toString());
    }

    /**
     * calculates new "v" after sensor change
     */
    private void applyAAndSetNewV(String f, float a, float deltaTime) {
        // Now calculate new velocity
        this.debugLog("velocity", f, "changed velocity from", this.v.getField(f));
        this.v.setField(f, Utility.calculateNewVelocity(a, deltaTime, this.v.getField(f)));
        this.v.setField(f, this.epsilonize(this.v.getField(f)));
        this.debugLog("velocity", f, "to", this.v.getField(f));
    }

    /**
     * calculates new "x" after finding the final applicable "v"
     */
    private void applyVAndSetNewP(String f, float a, float deltaTime) {
        // Now apply movement
        this.debugLog("movement", f, "changed position from", this.p.getField(f));
        this.p.setField(f, Utility.calculateNewPosition(a, deltaTime, this.v.getField(f), this.p.getField(f)));
        this.debugLog("movement", f, "to", this.p.getField(f));
    }

    /**
     * "getDistanceFrom" calculates the distance between balls' centers
     * "getCollisionDistanceFrom" calculates the distance that balls would collide at (literally, the sum of their radius)
     */
    private float getDistanceFrom(Ball b) {
        return PointFF.distance(this.p.add(this.radius), b.p.add(b.radius));
    }

    private float getCollisionDistanceFrom(Ball b) {
        return this.radius + b.radius;
    }


    /**
     * Updates X and Y position of balls in the view
     */
    private void updateXView() {
        this.view.setX(this.p.x);
    }

    private void updateYView() {
        this.view.setY(this.p.y);
    }

    /**
     * These normalizer are used to adjust entered positions considering:
     * - center of object is its pivot                  -> centralize functions
     * - follow the rule of 10-margin for each side     -> normalize functions
     * <p>
     * "normalize" normals position if out of screen, and applies wall-bouncy to v
     */
    private float centralizeX(float x) {
        return normalizeX(x - this.radius);
    }

    private float centralizeY(float y) {
        return normalizeY(y - this.radius);
    }

    private float normalizeX(float x) {
        float min = Utility.X_MIN;
        if (x < min)
            return min;

        float max = Utility.X_MAX - 2 * this.radius;
        if (x > max)
            return max;

        return x;
    }

    private float normalizeY(float y) {
        float min = Utility.Y_MIN;
        if (y < min)
            return min;

        float max = Utility.Y_MAX - 2 * this.radius;
        if (y > max)
            return max;

        return y;
    }

    private void normalize() {
        float x = this.normalizeX(this.p.x);
        float y = this.normalizeY(this.p.y);

        /*
        This sets a coefficient that when a ball collide with wall, they lose a fraction of their velocity.
        But when balls collide with each other and also with wall, these losing fraction can cause
        a bit disordering. So in this case, we let velocity to be as is, only in the negative side.
         */
        float coeff = this.lastCollide ? 1 : Utility.WALL_EFFECT_COEFF;
        if (this.p.x != x) { // i.e. collision with wall happened in x dimension
            this.v.x *= -coeff;
        }
        if (this.p.y != y) {
            this.v.y *= -coeff;
        }

        this.p.x = x;
        this.p.y = y;
    }

    /**
     * Returns 0 if the value is less than defined EPSILON, o.w. its original value
     */
    private float epsilonize(float f) {
        if (Math.abs(f) <= Utility.EPSILON)
            return 0;
        return f;
    }


    // log helpers -> for debugging
    private void debugLog(String tag, String f, String msg, float value) {
        Log.d(String.format("%s_%s_%s", this.name, tag, f), String.format("%s: %f", msg, value));
    }

    private void debugLog(String tag, String f, String msg, String value) {
        Log.d(String.format("%s_%s_%s", this.name, tag, f), String.format("%s: %s", msg, value));
    }

    private void warnLog(String tag, String f, String msg) {
        Log.w(String.format("%s_%s_%s", this.name, tag, f), msg);
    }

    private void warnLog(String tag, String f, String msg, String value) {
        Log.w(String.format("%s_%s_%s", this.name, tag, f), String.format("%s: %s", msg, value));
    }
}
