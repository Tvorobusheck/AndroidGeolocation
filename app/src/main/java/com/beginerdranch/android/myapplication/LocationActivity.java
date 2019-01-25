package com.beginerdranch.android.myapplication;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class LocationActivity extends Activity{

    Button btnFusedLocation;
    TextView tvLocation;
    String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        setContentView(R.layout.activity_location);

        tvLocation = (TextView) findViewById(R.id.tvLocation);

        btnFusedLocation = (Button) findViewById(R.id.btnShowLocation);
        startService(new Intent(LocationActivity.this, MyService.class));
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Pair<Location, Date> lastPos;
                if(MyService.getLocationList().isEmpty())
                    tvLocation.setText("Location unknown");
                else {
                    lastPos = MyService.getLocationList().get(MyService.getLocationList().size() - 1);
                    tvLocation.setText("Latitude: " + lastPos.first.getLatitude() + "\n" +
                            "Longitude: " + lastPos.first.getLongitude() + "\n" +
                            "Date: " + lastPos.second.toString() + "\n" +
                            "Amount of updates: " + MyService.getLocationList().size());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
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