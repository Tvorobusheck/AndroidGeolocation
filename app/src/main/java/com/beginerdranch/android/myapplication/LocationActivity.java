package com.beginerdranch.android.myapplication;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
                ArrayList<Pair<Date, Pair<Double, Double>>> listOfLocationPoints =
                        MyService.getLocationList(getApplicationContext());
                if(listOfLocationPoints.isEmpty())
                    tvLocation.setText("Location unknown");
                else{
                    Date date = listOfLocationPoints.get(listOfLocationPoints.size() - 1).first;
                    double lat = listOfLocationPoints.get(listOfLocationPoints.size() - 1).second.first;
                    double lng = listOfLocationPoints.get(listOfLocationPoints.size() - 1).second.second;
                    tvLocation.setText("Amount of updates: " + listOfLocationPoints.size() + "\n" +
                                    "Time of last update: " + date.toString() + "\n" +
                                    "Latitude: " + Double.toString(lat) + "\n" +
                                    "Longitude: " + Double.toString(lng));
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