package com.index.medidor.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Constantes {

    public static final String BASE_URL = "http://www.smartbill.us:8085/api/";
    //USUARIO
    public static final String POST_LOGIN_USER = "users/postLogin/";
    public static final String POST_REGISTER_USER = "users/postRegistrar";

    //ESTACIONES
    public static final String GET_ALL_ESTACIONES = "estaciones/getAll";
    //TANQUEADAS
    public static final String POST_REGISTRAR_TANQUEADA =  "tanqueadas/postRegistrar";
    public static final String GET_TANQUEADAS_BY_USER =  "tanqueadas/getByUser/";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String API_KEY_PLACES = "AIzaSyAyfqhsOG8cYzUEvLJqDpt54p9_vAN_mhs";
    public static final String API_KEY = "AIzaSyBVYoTTWhM_JZAlbUpJuTKTI5xcTJq7NFY";
//    public static final String API_KEY = "AIzaSyDqGc8jxOBTUju-yy_IfaaW1LUzn3Mbjf8";

    public static final String URL_PLACES ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={lat},{long}&radius=1200&type=gas_station&key=";
    public static final String URL_HTTP = "https://maps.googleapis.com/maps/api/directions/json?origin=";

    public static int ROTATION = 0;

    /**
     * MODELOS CARROS
     */
    public static final String GET_MODELOS_CARROS =  "tanqueadas/getByUser/";
    public static final String POST_REGISTER_MODELO_CARRO = "users/postRegistrar";

    /**
     * MARCAS CARROS
     */
    public static final String GET_MARCAS_CARROS =  "tanqueadas/getByUser/";

    public static float getDistance(LatLng myPosition, LatLng estacionLatLng){

        float[] results = {0};
        Location.distanceBetween(myPosition.latitude, myPosition.longitude, estacionLatLng.latitude, estacionLatLng.longitude, results);
        return results[0];
    }
}
