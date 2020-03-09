package com.bros.spinner.asuper.superspinnerbros;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import utils.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: call this when clicked on a button
        this.goToGyroscopeActivity();
    }

    private void goToGyroscopeActivity() {
        Intent intent = new Intent(MainActivity.this, Gyroscope.class);

        Point max = new Point();
        getWindowManager().getDefaultDisplay().getSize(max);
        Utility.setMaxPoint(max);

        Bundle b = new Bundle();
        b.putInt("x1", max.x / 2);
        b.putInt("y1", max.y / 2);
        b.putInt("vx1", -2);
        b.putInt("vy1", -5);
        b.putInt("x2", 0);
        b.putInt("y2", 0);
        b.putInt("vx2", 2);
        b.putInt("vy2", 5);
        Log.d("view_max", String.format("(%d, %d)", max.x, max.y));

        // TODO: get all fields from layout and put them here (x1, y1, v1, x2, y2, v2)
        intent.putExtras(b);
        startActivity(intent);
    }
}
