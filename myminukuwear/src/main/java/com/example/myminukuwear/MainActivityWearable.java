package com.example.myminukuwear;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivityWearable extends Activity
        //implements SensorEventListener
{


    private Context mContext;
    private TextView mTextView;
    private TextView mSensorText;
    private SensorManager mSensorManager;
    private int mLastGyroCount = 0;
    private int mGyroCount = 0;
    private int mLastAccelCount = 0;
    private int mAccelCount = 0;
    private Timer mTimerUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wearable);
        //final WatchViewStub stub = (WatchViewStub) findViewById(R.id.);
        //stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            //@Override
            //public void onLayoutInflated(WatchViewStub stub) {
              //  mTextView = (TextView) stub.findViewById(R.id.text);
                // mSensorText=(TextView) stub.findViewById(R.id.sensorText);
            //}
       // });

    }
}


