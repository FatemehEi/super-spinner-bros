package utils;

import android.graphics.PointF;

import java.util.Objects;

/**
 * An extension of PointF, with more handy features
 */
public class PointFF extends PointF {
    public PointFF() {
        super();
    }

    public PointFF(float x, float y) {
        super(x, y);
    }

    public PointFF(PointFF p) {
        super(p.x, p.y);
    }

    public void offset(PointFF p) {
        this.offset(p.x, p.y);
    }

    public void multiply(float m) {
        this.x *= m;
        this.y *= m;
    }

    public PointFF mult(float m) {
        return new PointFF(this.x * m, this.y * m);
    }

    /**
     * Use these two functions to get fields dynamically.
     * Adds reusability, but reduces code readability!
     */
    public float getField(String f) {
        if (Objects.equals(f, "x"))
            return this.x;
        if (Objects.equals(f, "y"))
            return this.y;
        return 0;
    }

    public void setField(String f, float val) {
        if (Objects.equals(f, "x"))
            this.x = val;
        if (Objects.equals(f, "y"))
            this.y = val;
    }

    public static float distance(PointFF p1, PointFF p2) {
        float deltaX = p1.x - p2.x;
        float deltaY = p1.y - p2.y;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public PointFF add(float delta) {
        return new PointFF(this.x + delta, this.y + delta);
    }

    public PointFF add(PointFF p) {
        return new PointFF(this.x + p.x, this.y + p.y);
    }

    public PointFF normal() {
        return new PointFF(this.x / this.length(), this.y / this.length());
    }

    public PointFF perpendicular() {
        return new PointFF(-this.y, this.x);
    }

    public float dot(PointFF p) {
        return this.x * p.x + this.y * p.y;
    }

    public void setFrom(PointFF p) {
        this.x = p.x;
        this.y = p.y;
    }

}
