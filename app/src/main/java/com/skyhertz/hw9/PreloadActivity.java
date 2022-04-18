package com.skyhertz.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

public class PreloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preload_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Timer timer = new Timer();
        TimerTask boot = new TimerTask() {
            public void run() {
                Intent intent = new Intent(PreloadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        timer.schedule(boot, 2000);
    }

}