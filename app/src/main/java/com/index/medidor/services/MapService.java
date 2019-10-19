package com.index.medidor.services;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.index.medidor.rutas.DirectionFinder;
import com.index.medidor.rutas.PasarUbicacion;
import com.index.medidor.rutas.Route;
import com.index.medidor.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

public class MapService implements PasarUbicacion, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker markerStation;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Location myLocation;
    private Context context;

    public MapService(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        this.context = context;
        UiSettings uiSettings = this.mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        this.mMap.setOnMarkerClickListener(this);
    }

    public void drawSationRoute(){

        if(markerStation == null){

            Toast.makeText(context, "DEBE SELECCIONAR UNA ESTACIÓN!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (myLocation != null){
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng destino = new LatLng(markerStation.getPosition().latitude, markerStation.getPosition().longitude);
            //DirectionFinder buscador = new DirectionFinder(this, latLng, destino, getString(R.string.google_maps_key));
            DirectionFinder buscador = new DirectionFinder(this, latLng, destino,
                    Constantes.API_KEY_PLACES);
            buscador.peticionRutas();
        }else{
            Log.e("UBICACION","NULA EN NEW RUTA");
        }
    }

    @Override
    public void trazarRutas(List<Route> rutas) {

        for (Route route : rutas) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        markerStation = marker;
        return false;
    }

    public void mostrarUbicacion() {
        //initLocation();
        if (myLocation != null){

            LatLng newPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 14));
        }else{
            Toast.makeText(context, "NO SE PUEDE MOSTRAR TU UBICACIÓN. INTENTALO MAS TARDE.", Toast.LENGTH_SHORT).show();
        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public Marker getMarkerStation() {
        return markerStation;
    }

    public void setMarkerStation(Marker markerStation) {
        this.markerStation = markerStation;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
}
