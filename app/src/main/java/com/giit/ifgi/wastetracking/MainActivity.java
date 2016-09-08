package com.giit.ifgi.wastetracking;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button StartService;
    private Button StopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartService = (Button) findViewById(R.id.startservice);
        StopService = (Button) findViewById(R.id.stopservice);

        final Intent i= new Intent(getApplicationContext(), LocationService.class);

        StartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(i);
            }

        });

        StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(i);
            }

        });



    }
}
