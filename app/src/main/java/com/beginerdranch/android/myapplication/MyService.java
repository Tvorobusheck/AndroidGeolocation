package com.beginerdranch.android.myapplication;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyService extends Service  implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long OPTIM_POINTS = 5;
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000 * 30;
    private static LocationRequest mLocationRequest;
    private static GoogleApiClient mGoogleApiClient;
    private static Location mCurrentLocation;

    private static Date begDate = new Date(70, 01, 01, 0, 0, 0);
    private static Date endDate = new Date(170, 01, 01, 0, 0, 0);

    public static Pair<Date, Date> getDateBorders(){
        return Pair.create(begDate, endDate);
    }
    public static void setBegDate(Date date){
        Log.d(TAG, "Your begin date is: " + date.toString());
        begDate = date;
    }
    public static void setEndDate(Date date){

        Log.d(TAG, "Your end date is: " + date.toString());
        endDate = date;
    }
    public static Date getBegDate(){
        return begDate;
    }
    public static Date getEndDate(){
        return endDate;
    }
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
    private void addLocationPoint(String lat, String lng, String time) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(this.openFileOutput(getString(R.string.locationTxt),
                            Context.MODE_APPEND));
            outputStreamWriter.write(time + "\n" +
                                        lat + "\n" +
                                        lng + "\n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static ArrayList<Pair<Date, Pair<Double, Double>>> readFromFile(Context context) {
        ArrayList<Pair<Date, Pair<Double, Double>>> listOfLocationPoints = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(context.getString(R.string.locationTxt));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                int count = 0;
                Pair<Date, Pair<Double, Double>> location;
                Date date = new Date(0);
                double lat = 0;
                double lng = 0;
                listOfLocationPoints = new ArrayList<>();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    count++;
                    Log.d(TAG, "Recived string from file");
                    switch (count) {
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
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d(TAG, "Size of list is " + listOfLocationPoints.size());
        return listOfLocationPoints;
    }
    private static double getDist(Pair<Double, Double> fi,
                                  Pair<Double, Double> se){
        return Math.sqrt((fi.first - se.first) * (fi.first - se.first) +
                (fi.second - se.second) * (fi.second - se.second));
    }
    private static boolean missedPoint(Pair<Double, Double> fi,
                                       Pair<Double, Double> se,
                                       Pair<Double, Double> main){
        if(getDist(fi, main) + getDist(se, main) > 2 * getDist(fi, se))
            return true;
        else
            return false;
    }
    private static ArrayList<Pair<Date, Pair<Double, Double>>> optimizeList(ArrayList<Pair<Date, Pair<Double, Double>>> locations){
        ArrayList<Pair<Date, Pair<Double, Double>>> result = new ArrayList<>();
        for(int i = 0; i < locations.size(); i++){
            Pair<Date, Pair<Double, Double>> point;
            if(locations.size() > i + 1 && i - 1 >= 0 && missedPoint(locations.get(i - 1).second,
                                                            locations.get(i + 1).second,
                                                            locations.get(i).second)) {
                int cnt = 0;
                double summ_x = 0;
                double summ_y = 0;
                for (int j = i - 1; j >= i - OPTIM_POINTS; j--) {
                    if (j < 0)
                        break;
                    summ_x += locations.get(j).second.first;
                    summ_y += locations.get(j).second.second;
                    cnt++;
                }
                for (int j = i + 1; j <= i + OPTIM_POINTS; j++) {
                    if (j >= locations.size())
                        break;
                    summ_x += locations.get(j).second.first;
                    summ_y += locations.get(j).second.second;
                    cnt++;
                }
                point = Pair.create(locations.get(i).first,
                        Pair.create(summ_x / cnt, summ_y / cnt));
            }
            else
                point = locations.get(i);
            Log.d(TAG, "Two dates are: " + "\n" +
                            begDate.toString() + "\n" +
                            point.first.toString());
            if(begDate.compareTo(point.first) <= 0 && endDate.compareTo(point.first) >= 0)
               result.add(point);
        }
        return result;
    }
    public static ArrayList<Pair<Date, Pair<Double, Double>>> getLocationList (Context context){
        return optimizeList(readFromFile(context));
    }
    private void updateLocations() {
        Log.d(LOG_TAG, "UI update initiated");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            try {
                addLocationPoint(lat, lng, Calendar.getInstance().getTime().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                Log.d(LOG_TAG, "Location update resumed");
            } else {
                Log.d(LOG_TAG, "Location update not started");
            }
        }
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
        return new LocalBinder();
    }
    public class LocalBinder extends Binder{
        public MyService getInstance(){
            return MyService.this;
        }
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