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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.index.medidor.activities.MainActivity;

import static android.content.Context.LOCATION_SERVICE;

public class InndexLocationService {

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int locationRequestCode = 1000;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private MainActivity mainActivity;
    private LocationManager locationManager;
    public static final int LOCATION_REQUEST_CODE = 1;
    private Location myLocation;
    private double distancia_temp = 0;

    private double distancia;

    public InndexLocationService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.mainActivity);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(15 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    Log.e("LOCTION","RESULT NUUULLLL");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        myLocation = location;
                        mainActivity.updateLocation(myLocation);
                        distancia_temp = myLocation.distanceTo(location);
                        mainActivity.getMapService().updateMyPosition();
                        mainActivity.getMapService().setMyLocation(myLocation);
                        if(distancia_temp > 15) {

                            myLocation = location;
                            distancia += distancia_temp;
                        }
                    }
                }
            }
        };
    }

    public void init() {

        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            Toast.makeText(mainActivity, "LOCATION PERMISSIONS GRANTED", Toast.LENGTH_LONG).show();
            // already permission granted

            mFusedLocationClient.getLastLocation().addOnSuccessListener(mainActivity, location -> {

                if(location != null) {
                    myLocation = location;
                    mainActivity.updateLocation(myLocation);
                }
                else
                    Toast.makeText(mainActivity, "ESA VAINA ES NULL", Toast.LENGTH_LONG).show();
            });
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            //Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            /*if(lastKnownLocationGPS != null) {
                myLocation = lastKnownLocationGPS;
                mainActivity.updateLocation(myLocation);

            } else {
                Log.e("LOC2","SORRY TRY IT NEXT TIME");
            }*/
        }

/*
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Manifest.permission.ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED
            if(locationManager == null) {
                locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
                Log.e("PRO","enabled " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocationGPS != null) {
                    myLocation = lastKnownLocationGPS;
                    mainActivity.updateLocation(myLocation);

                } else {
                    Log.e("LOC2","SORRY TRY IT NEXT TIME");
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                    mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        */
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

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
