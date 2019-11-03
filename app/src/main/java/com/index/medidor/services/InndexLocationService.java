package com.index.medidor.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.index.medidor.activities.MainActivity;

import static android.content.Context.LOCATION_SERVICE;

public class InndexLocationService implements LocationListener {

    private MainActivity mainActivity;
    private LocationManager locationManager;
    public static final int LOCATION_REQUEST_CODE = 1;
    private Location myLocation;
    private double distancia_temp = 0;

    private double distancia = 0;

    public InndexLocationService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void init() {

        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(locationManager == null) {
                locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
            }

        } else {
             ActivityCompat.requestPermissions(
                    mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (myLocation != null) {
            Log.e("location", "NOT NULL");

            distancia_temp = myLocation.distanceTo(location);

            if(distancia_temp > 15) {

                Log.e("location", "distance greater than 10");

                mainActivity.getMapService().updateMyPosition();
                myLocation = location;
                distancia += distancia_temp;
            }else {

                Log.e("location", "distance less than 10");

            }

        } else {
            myLocation = location;
            mainActivity.getMapService().setMyLocation(myLocation);
            //coordenada = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mainActivity.getMapService().updateMyPosition();
        }

        String ubi = "distancia recorrida " + distancia;
        Toast.makeText(mainActivity,ubi,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public double getDistancia_temp() {
        return distancia_temp;
    }

    public void setDistancia_temp(double distancia_temp) {
        this.distancia_temp = distancia_temp;
    }
}
