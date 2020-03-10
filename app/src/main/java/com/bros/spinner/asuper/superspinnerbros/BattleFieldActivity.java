package com.bros.spinner.asuper.superspinnerbros;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Objects;

import utils.Ball;
import utils.PointFF;
import utils.Utility;

public class BattleFieldActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sm;
    private Sensor sensor;
    private Ball b1;
    private Ball b2;
    private long timestamp = 0;

    @Override
    protected void onStart() {
        super.onStart();
        if (sensor != null) {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.w("sensor", "doesn't exist");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getIntent().getExtras();
        if (b == null) {
            Log.e("bundle", "bundle is empty. reverting to default values");
            return;
        }

        utilizeView();
        setUpBalls(b);
        setUpSensors(b);
    }

    private void utilizeView() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_battlefield);
    }

    private void setUpBalls(Bundle b) {
        b1 = new Ball("b1", b.getInt("x1"), b.getInt("y1"), b.getInt("vx1"), b.getInt("vy1"), 60, 10, (ImageView) this.findViewById(R.id.ball1id));
        b2 = new Ball("b2", b.getInt("x2"), b.getInt("y2"), b.getInt("vx2"), b.getInt("vy2"), 150, 50, (ImageView) this.findViewById(R.id.ball2id));
        /* If we want to equalize radius, note that "40dp" is "60" and "100dp" is "150"
        So if we change radius in here, we should change its height/width in its activity, too
        */

        b1.setAdjBall(b2);
        b2.setAdjBall(b1);

        b1.updateView();
        b2.updateView();
    }

    private void setUpSensors(Bundle b) {
        try {
            this.sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            Utility.sensorType = b.getInt("sensor");
            this.sensor = this.sm.getDefaultSensor(Utility.sensorType);
            if (this.sensor == null) {
                Log.w("sensor", "sensor not set up");
            }
        } catch (NullPointerException e) {
            Log.e("sensor-exc", e.toString());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy < SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
            Log.w("sensor", "not enough accuracy");
            return;
        }
        if (event.sensor.getType() != sensor.getType()) {
            Log.v("sensor", "not intended sensor event");
            return;
        }
        if (this.timestamp == 0) {
            Log.d("timestamp", String.format("init with %d", event.timestamp));
            this.timestamp = event.timestamp;
            return;
        }

        // on-device-debug-mode ~= 100 FPS | expected ~= 50 FPS | enough to be counted as "real-time"
        PointFF p;
        if (Utility.sensorType == Sensor.TYPE_GYROSCOPE)
            p = new PointFF(event.values[1], event.values[0]);
        else
            p = new PointFF(-event.values[0], event.values[1]);

        Log.v("sensor values", p.toString());
        float deltaTime = (event.timestamp - this.timestamp) * Utility.TIMESCALE_COEFF;
        this.timestamp = event.timestamp;

        this.updateBalls(p, deltaTime);
    }

    private void updateBalls(PointFF p, float deltaTime) {
        this.b1.applySensorChange(new PointFF(p), deltaTime);
        this.b2.applySensorChange(new PointFF(p), deltaTime);
        this.b1.updateView();
        this.b2.updateView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.b1.resetValues();
        this.b2.resetValues();
        return super.onTouchEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }
}
