package com.index.medidor.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.index.medidor.activities.MainActivity;

public class InndexLocationService implements LocationListener {

    private MainActivity mainActivity;
    private LocationManager locationManager;
    public static final int LOCATION_REQUEST_CODE = 1;

    public InndexLocationService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void init() {

        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(locationManager == null) {

                locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                mainActivity.getMapService().setMyLocation(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
                mainActivity.getMapService().getmMap().setMyLocationEnabled(true);

                if (mainActivity.getMapService().getMyLocation() != null){
                    mainActivity.getMapService().mostrarUbicacion();
                }else{
                    Log.e("onMapReady","Dentro de segundo if, mulocation es NULL");
                }
            }

        } else {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
              */  ActivityCompat.requestPermissions(
                    mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

            //}
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

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
}
