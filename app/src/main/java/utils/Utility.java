package utils;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Contains helper functions and constant values for application usage
 */
public class Utility {
    /**
     * All Objects (i.e. balls) must be in (X_MIN, X_MAX) and (Y_MIN, Y_MAX) range
     */
    static final int X_MIN = 10;
    static final int Y_MIN = 10;

    public static int X_MAX;
    public static int Y_MAX;

    static int WIDTH;
    static int HEIGHT;

    /*
    Minimum value that is considered physically 0
     */
    static final float EPSILON = 0.05f;

    public static final float TIMESCALE_COEFF = 1.0E-9f;    // ns -> s
    static final float GRAVITY_MOVEMENT_COEFF = 50;         // approximation for gravity sensor, converting meter to pixel
    static final float GYROSCOPE_MOVEMENT_COEFF = 100;      // approximation for meter -> pixel
    static final float WALL_EFFECT_COEFF = 0.3f;            // for wall collision
    static final float COLLISION_OFFSET = 2;                // tolerance for distance between balls

    static final float g = SensorManager.STANDARD_GRAVITY;
    static final float uS = 0.15f;
    static final float uK = 0.1f;

    /*
    The type of sensor that we send from main menu to battle field.
    Bad practice to put it here like a global variable, but easiest!
     */
    public static int sensorType;

    /**
     * use this in the beginning of code start up to calibrate mobile size and dimensions.
     *
     * @param p is a point received from "getWindowManager().getDefaultDisplay().getSize(p)"
     */
    public static void setMaxPoint(Point p) {
        X_MAX = p.x - 10;
        Y_MAX = p.y - 10;

        WIDTH = p.x;
        HEIGHT = p.y;
    }

    /**
     * After simplifying Sum(F) = ma, we can infer this:
     * a = g * ( sin(theta) - uK * cos(theta) )
     */
    static float calculateAccelerationFromTheta(float theta) {
        return (float) (g * (Math.sin(theta) - uK * Math.cos(theta)));
    }

    /**
     * when going from a slide to normal surface, the sign of "a" can be inferred wrongly from formula above. this function fixes it
     */
    static float calculateAccelerationNormalizer(float a, float v, float theta) {
        if (theta == 0 && v < 0) {
            a *= -1;
        }
        return a;
    }

    /**
     * applies "v = at + v0" formula
     */
    static float calculateNewVelocity(float a, float deltaTime, float v0) {
        return v0 + (a * deltaTime);
    }

    /**
     * applies "x = 1/2 at^2 + vt + x0", considering the position scale from sensors
     */
    static float calculateNewPosition(float a, float deltaTime, float v, float x0) {
        return x0
                + (a * deltaTime * deltaTime / 2 + v * deltaTime)
                * (Utility.sensorType == Sensor.TYPE_GYROSCOPE ? GYROSCOPE_MOVEMENT_COEFF : GRAVITY_MOVEMENT_COEFF);
    }

    /**
     * applies "v1' = ((v1(m2 - m1) + 2 m2 v2) / (m1 + m2))" formula, to calculate new "v" after collision
     */
    static float calculateNewVInOneDimension(float v, float v2, float m, float m2) {
        return ((v * (m - m2) + 2 * m2 * v2) / (m + m2));
    }
}
