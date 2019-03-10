package com.beginerdranch.android.myapplication;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationActivity extends Activity{

    private static Button btnFusedLocation;
    private static Button btnStopUpdates;
    private static Button btnRestartUpdates;
    private static Button btnShowMap;
    private static TextView tvLocation;
    private static Intent mIntent;

    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_location);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnStopUpdates = (Button) findViewById(R.id.btnStopUpdates);
        btnRestartUpdates = (Button) findViewById(R.id.btnRestartUpdates);
        btnFusedLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowMap = (Button) findViewById(R.id.btnShowMap);
        mIntent = new Intent(this, MyService.class);

        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<ArrayList<Pair<Date, Pair<Double, Double>>>> listOfLocationPoints =
                        MyService.getListOfTracks(MyService.getLocationList(getApplicationContext()));
                if(listOfLocationPoints.isEmpty())
                    tvLocation.setText("Location unknown");
                else{
                    tvLocation.setText("Amount of tracks: " + listOfLocationPoints.size() + "\n" +
                                        "Amount of points: " + MyService.getLocationList(getApplicationContext()).size());
                }

            }
        });
        btnRestartUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(mIntent);
            }
        });
        btnStopUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(mIntent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}