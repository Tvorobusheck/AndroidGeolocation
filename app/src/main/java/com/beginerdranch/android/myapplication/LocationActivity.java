package com.beginerdranch.android.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
    private static TextView tvLocation;
    private static List<Pair<Date, Pair<Double, Double>>> listOfLocationPoints =
            Collections.emptyList();

    private void readFromFile(Context context) {
        try {
            InputStream inputStream = context.openFileInput(getString(R.string.locationTxt));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                int count = 0;
                Pair<Date, Pair<Double, Double>> location;
                Date date = new Date(0);
                double lat = 0;
                double lng = 0;
                listOfLocationPoints = new ArrayList<>();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    count++;
                    Log.d(TAG, "Recived string from file");
                    switch (count){
                        case 1:
                            date = new Date(Date.parse(receiveString));
                            break;
                        case 2:
                            lat = Double.parseDouble(receiveString);
                            break;
                        case 3:
                            lng = Double.parseDouble(receiveString);
                            location = Pair.create(date, Pair.create(lat, lng));
                            listOfLocationPoints.add(location);
                            count = 0;
                            break;
                        default:
                            Log.e(TAG, "Count has unexcepted value");
                    }
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d(TAG, "Size of list is " + listOfLocationPoints.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_location);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnStopUpdates = (Button) findViewById(R.id.btnStopUpdates);
        btnRestartUpdates = (Button) findViewById(R.id.btnRestartUpdates);
        btnFusedLocation = (Button) findViewById(R.id.btnShowLocation);
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readFromFile(getApplicationContext());
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
                startService(new Intent(LocationActivity.this, MyService.class));
            }
        });
        btnStopUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), MyService.class));
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