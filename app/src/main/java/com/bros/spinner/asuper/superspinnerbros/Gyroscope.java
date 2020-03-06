package com.bros.spinner.asuper.superspinnerbros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Gyroscope extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        readValues();
    }

    private void readValues() {
        Bundle b = this.getIntent().getExtras();
        if (b == null) {
            Log.e("bundle", "bundle is empty. reverting to default values");
            return;
        }

        int x1 = b.getInt("x1");
        Log.w("x1", String.format("x1 is: %d", x1));
    }
}
