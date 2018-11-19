package com.beginerdranch.android.locationtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;
    TextView mGpsField;
    TextView mLatitudeField;
    TextView mLongitudeField;
    TextView mCounterField;
    GoogleApiClient mGoogleApiClient;
    int requestCounter = 0;

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        createLocationRequest();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGpsField = findViewById(R.id.gpsField);
        mLatitudeField = findViewById(R.id.latitudeField);
        mLongitudeField = findViewById(R.id.longitudeField);
        mCounterField = findViewById(R.id.requestField);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mGpsField.setText(R.string.gps_not_works);
            return;
        }
        mGpsField.setText("Permission granted");
      //  startLocationUpdates();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mLatitudeField.setText(Double.toString(location.getLatitude()));
                            mLongitudeField.setText(Double.toString(location.getLongitude()));
                        }
                        else {
                            mLatitudeField.setText(R.string.null_location);
                            mLongitudeField.setText(R.string.null_location);
                        }
                    }
                });
        /*mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                requestCounter += 1;
                mCounterField.setText(Integer.toString(requestCounter));
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    mGpsField.setText(R.string.gps_works);


                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        mLatitudeField.setText(Double.toString(location.getLatitude()));
                        mLongitudeField.setText(Double.toString(location.getLongitude()));
                    }
                    else{
                        mLatitudeField.setText(R.string.null_location);
                        mLongitudeField.setText(R.string.null_location);
                    }
                }
            }

        };*/
    }
}
