package com.bros.spinner.asuper.superspinnerbros;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import utils.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set maximum size
        Point max = new Point();
        getWindowManager().getDefaultDisplay().getSize(max);
        Utility.setMaxPoint(max);

        // add buttons functionality
        Button gravityBTN = findViewById(R.id.gravity_btn);
        gravityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                setDefaultValuesForBundle(b);
                fillWithFormValues(b);
                b.putInt("sensor", Sensor.TYPE_GRAVITY);
                goToNextActivity(b);
            }
        });

        Button gyroscopeBTN = findViewById(R.id.gyroscope_btn);
        gyroscopeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                setDefaultValuesForBundle(b);
                fillWithFormValues(b);
                b.putInt("sensor", Sensor.TYPE_GYROSCOPE);
                goToNextActivity(b);
            }
        });
    }

    private void goToNextActivity(Bundle b) {
        Intent intent = new Intent(MainActivity.this, BattleFieldActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void fillWithFormValues(Bundle b) {
        try {
            EditText x1 = (EditText) findViewById(R.id.x1);
            EditText y1 = (EditText) findViewById(R.id.y1);
            EditText vx1 = (EditText) findViewById(R.id.vx1);
            EditText vy1 = (EditText) findViewById(R.id.vy1);

            EditText x2 = (EditText) findViewById(R.id.x2);
            EditText y2 = (EditText) findViewById(R.id.y2);
            EditText vx2 = (EditText) findViewById(R.id.vx2);
            EditText vy2 = (EditText) findViewById(R.id.vy2);

            int x1num = Integer.parseInt(x1.getText().toString());
            int y1num = Integer.parseInt(y1.getText().toString());
            int vx1num = Integer.parseInt(vx1.getText().toString());
            int vy1num = Integer.parseInt(vy1.getText().toString());
            int x2num = Integer.parseInt(x2.getText().toString());
            int y2num = Integer.parseInt(y2.getText().toString());
            int vx2num = Integer.parseInt(vx2.getText().toString());
            int vy2num = Integer.parseInt(vy2.getText().toString());

            b.putInt("x1", x1num);
            b.putInt("y1", y1num);
            b.putInt("vx1", vx1num);
            b.putInt("vy1", vy1num);
            b.putInt("x2", x2num);
            b.putInt("y2", y2num);
            b.putInt("vx2", vx2num);
            b.putInt("vy2", vy2num);
        } catch (Exception e) {
            Log.e("Error", "invalid input. using a default set");
        }
    }

    private void setDefaultValuesForBundle(Bundle b) {
        Random rand = new Random();
        int n = rand.nextInt();
        int x1, x2, y1, y2, vx1, vx2, vy1, vy2;

        // some default, starting, illustrative values, so that we don't need to fill the form every time!
        if (n % 3 == 0) {
            x1 = 0;
            y1 = 0;
            vx1 = 2;
            vy1 = 5;

            x2 = 400;
            y2 = 500;
            vx2 = 0;
            vy2 = 0;
        } else if (n % 3 == 1) {
            x1 = 0;
            y1 = 0;
            vx1 = 3;
            vy1 = 4;

            x2 = 3 * Utility.X_MAX / 4;
            y2 = 3 * Utility.Y_MAX / 4;
            vx2 = -2;
            vy2 = -5;
        } else {
            x1 = Utility.X_MAX / 2;
            y1 = Utility.Y_MAX / 2;
            vx1 = -2;
            vy1 = -5;

            x2 = 0;
            y2 = 0;
            vx2 = 2;
            vy2 = 5;
        }

        b.putInt("x1", x1);
        b.putInt("y1", y1);
        b.putInt("vx1", vx1);
        b.putInt("vy1", vy1);
        b.putInt("x2", x2);
        b.putInt("y2", y2);
        b.putInt("vx2", vx2);
        b.putInt("vy2", vy2);
    }
}
