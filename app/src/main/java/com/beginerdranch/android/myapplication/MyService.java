package com.beginerdranch.android.myapplication;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyService extends Service  implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    private static LocationRequest mLocationRequest;
    private static GoogleApiClient mGoogleApiClient;
    private static Location mCurrentLocation;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(LOG_TAG, "Location update started");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Firing onLocationChanged");
        mCurrentLocation = location;
        updateLocations();
    }
    private void addLocationPoint(String lat, String lng, String time){
        try {
            /** TODO
             * It rewrites file, change on append
             */
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(this.openFileOutput(getString(R.string.locationTxt),
                            Context.MODE_PRIVATE));
            outputStreamWriter.append(time + "\n" +
                                        lat + "\n" +
                                        lng);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void updateLocations() {
        Log.d(LOG_TAG, "UI update initiated");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            addLocationPoint(lat, lng, Calendar.getInstance().getTime().toString());
            Log.d(LOG_TAG, "Latitude is: " + lat + "\n" +
                    "Longitude is: " + lng);
        } else {
            Log.d(LOG_TAG, "location is null");
        }
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(LOG_TAG, "Location update stopped");
    }

    final String LOG_TAG = "ServiceLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        if(isGooglePlayServicesAvailable()) {
            Toast.makeText(getApplicationContext(), "GooglePlay works",
                    Toast.LENGTH_SHORT).show();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                Log.d(LOG_TAG, "Location update resumed");
            } else {
                Log.d(LOG_TAG, "Location update not started");
            }
        }
        else
            Toast.makeText(getApplicationContext(), "GooglePlay unavaible",
                    Toast.LENGTH_SHORT).show();

        Log.d(LOG_TAG, "Service works!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        Log.d(LOG_TAG, "onConnected - isConnected: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection failed: " + connectionResult.toString());
    }
}