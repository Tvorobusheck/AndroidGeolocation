package com.beginerdranch.android.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<ArrayList<Pair<Date, Pair<Double, Double>>>> listOfTracks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listOfTracks = MyService.getListOfTracks(MyService.getLocationList(getApplicationContext()));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng lastpos = new LatLng(0, 0);
        for(int i = 0; i < listOfTracks.size(); i++) {
            ArrayList<Pair<Date, Pair<Double, Double>>> locations = listOfTracks.get(i);
            if (locations.size() > 0) {
                mMap = googleMap;
                setUpMap();

                PolylineOptions options = new PolylineOptions().width(5).color(Color.RED);
                for (Pair<Date, Pair<Double, Double>> locPoint : locations) {
                    options.add(new LatLng(locPoint.second.first, locPoint.second.second));
                }
                lastpos = new LatLng(locations.get(locations.size() - 1).second.first,
                        locations.get(locations.size() - 1).second.second);
                mMap.addPolyline(options);
            }
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastpos, 14);
        mMap.animateCamera(cameraUpdate);
    }

    public void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
