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
    private static Button btnSetBeginDate;
    private static Button btnSetBeginTime;
    private static Button btnSetEndDate;
    private static Button btnSetEndTime;

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
        btnSetBeginDate = (Button) findViewById(R.id.btnSetBeginDate);
        btnSetBeginTime = (Button) findViewById(R.id.btnSetBeginTime);
        btnSetEndDate = (Button) findViewById(R.id.btnSetEndDate);
        btnSetEndTime = (Button) findViewById(R.id.btnSetEndTime);
        mIntent = new Intent(this, MyService.class);

        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        updateTimes();
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
        btnSetBeginDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerDialog dialog = new DatePickerDialog(LocationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year - 1900;
                        mMonth = month;
                        mDay = dayOfMonth;
                        mHour = MyService.getBegDate().getHours();
                        mMinute = MyService.getBegDate().getMinutes();
                        MyService.setBegDate(new Date(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                        Log.d(TAG, "DatePickerListner: " + mYear + " " + mMonth + " " + mDay);
                        updateTimes();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        btnSetBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(LocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mYear = MyService.getBegDate().getYear();
                        mMonth = MyService.getBegDate().getMonth();
                        mDay = MyService.getBegDate().getDay();
                        mHour = hourOfDay;
                        mMinute = minute;
                        MyService.setBegDate(new Date(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                        updateTimes();
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE), true);
                dialog.show();
            }
        });
        btnSetEndDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerDialog dialog = new DatePickerDialog(LocationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year - 1900;
                        mMonth = month;
                        mDay = dayOfMonth;
                        mHour = MyService.getEndDate().getHours();
                        mMinute = MyService.getEndDate().getMinutes();
                        MyService.setEndDate(new Date(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                        Log.d(TAG, "DatePickerListner: " + mYear + " " + mMonth + " " + mDay);
                        updateTimes();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        btnSetEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(LocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mYear = MyService.getEndDate().getYear();
                        mMonth = MyService.getEndDate().getMonth();
                        mDay = MyService.getEndDate().getDay();
                        mHour = hourOfDay;
                        mMinute = minute;
                        MyService.setEndDate(new Date(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                        updateTimes();
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE), true);
                dialog.show();
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
    private void updateTimes(){
        Log.d(TAG, MyService.getBegDate().toString());
        btnSetBeginDate.setText(Integer.toString(MyService.getBegDate().getDate()) + "." +
                Integer.toString(MyService.getBegDate().getMonth() + 1) + "." +
                Integer.toString((MyService.getBegDate().getYear() + 1900)));
        Log.d(TAG, MyService.getEndDate().toString());
        btnSetEndDate.setText(Integer.toString(MyService.getEndDate().getDate()) + "." +
                Integer.toString(MyService.getEndDate().getMonth() + 1) + "." +
                Integer.toString((MyService.getEndDate().getYear() + 1900)));
        btnSetBeginTime.setText(MyService.getBegDate().getHours() + ":" +
                MyService.getBegDate().getMinutes());
        btnSetEndTime.setText(MyService.getEndDate().getHours() + ":" +
                MyService.getEndDate().getMinutes());
    }
}