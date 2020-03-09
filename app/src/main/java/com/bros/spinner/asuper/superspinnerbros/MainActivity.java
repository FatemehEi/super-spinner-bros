package com.bros.spinner.asuper.superspinnerbros;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button gravityBTN;
    private Button gyroscopeBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gravityBTN = findViewById(R.id.gravity_btn);
        gravityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        gyroscopeBTN = findViewById(R.id.gyroscope_btn);
        gyroscopeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    Bundle b = new Bundle();
                    b.putInt("x1", x1num);
                    b.putInt("y1", y1num);
                    b.putInt("vx1", vx1num);
                    b.putInt("vy1", vy1num);
                    b.putInt("x2", x2num);
                    b.putInt("y2", y2num);
                    b.putInt("vx2", vx2num);
                    b.putInt("vy2", vy2num);
                    //Log.w("x1", String.format("%d",x1num));
                    goToGyroscopeActivity(b);
                }catch (Exception e) {
                    Log.e("Error","invalid input");
                }
            }
        });
    }

    private void goToGyroscopeActivity(Bundle b) {
        Intent intent = new Intent(MainActivity.this, Gyroscope.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
