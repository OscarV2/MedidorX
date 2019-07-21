package com.index.medidor.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.index.medidor.places.EstacionesPlaces;

import java.util.List;

public class RutasHelper {

    private Context context;


    private void drawLine(){

    }

    public static final float getDistanciaRecorrida(List<LatLng> listRutas){

        EstacionesPlaces places = new EstacionesPlaces();
        if (listRutas.size() > 1){

            return places.getDistance(listRutas.get(0), listRutas.get(listRutas.size()-1));
        }else{
            return 0;
        }
    }
}
