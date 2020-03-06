package com.bros.spinner.asuper.superspinnerbros;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        Bundle b = new Bundle();
        b.putInt("x1", 10);
        // TODO: get all fields from layout and put them here (x1, y1, v1, x2, y2, v2)
        intent.putExtras(b);
        startActivity(intent);
    }
}
