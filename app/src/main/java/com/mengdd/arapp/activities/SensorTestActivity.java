package com.mengdd.arapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mengdd.arapp.R;

public class SensorTestActivity extends Activity {

    private TextView mHelloWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);
        mHelloWorld = (TextView) findViewById(R.id.sensor_test);
    }
}
