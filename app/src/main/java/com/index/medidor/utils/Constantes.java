package com.index.medidor.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class Constantes {

    public static final String BASE_URL = "http://www.smartbill.us:8085/api/";
    //USUARIO
    public static final String POST_LOGIN_USER = "users/postLogin/";
    public static final String POST_REGISTER_USER = "users/postRegistrar";

    //ESTACIONES
    public static final String GET_ALL_ESTACIONES = "estaciones/getAll";
    public static final String POST_REGISTER_STATION = "estaciones/postRegistrar";
    //TANQUEADAS
    public static final String POST_REGISTRAR_TANQUEADA =  "tanqueadas/postRegistrar";
    public static final String GET_TANQUEADAS_BY_USER =  "tanqueadas/getByUser/";

    //USUARIO HAS MODELO CARROS
    public static final String POST_REGISTRAR_USUARIO_HAS_MODELO_CARRO =  "usuarioHasModeloCarros/";
    public static final String GET_USUARIO_HAS_MODELO_CARROS_BY_ID_USER =  "usuarioHasModeloCarros/getAllByUser/";

    public static final String API_KEY_PLACES = "AIzaSyAyfqhsOG8cYzUEvLJqDpt54p9_vAN_mhs";
    public static final String API_KEY = "AIzaSyBVYoTTWhM_JZAlbUpJuTKTI5xcTJq7NFY";
//    public static final String API_KEY = "AIzaSyDqGc8jxOBTUju-yy_IfaaW1LUzn3Mbjf8";

    public static final String URL_PLACES ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={lat},{long}&radius=1200&type=gas_station&key=";
    public static final String URL_HTTP = "https://maps.googleapis.com/maps/api/directions/json?origin=";

    /**
     * MODELOS CARROS
     */
    public static final String GET_MODELOS_CARROS =  "tanqueadas/getByUser/";
    public static final String POST_REGISTER_MODELO_CARRO = "modelos-carros/postRegistrar";

    /**
     * MARCAS CARROS
     */
    public static final String GET_MARCAS_CARROS =  "modelos-carros/getAll";

    public static int ROTATION = 0;

    public static int ARRAY_DATA_SIZE = 5;

    public static final String CONTENT_TYPE_JSON = "application/json";


    public static final double LAT_LNG_TOLERANCE = 0.002;

    public static float getDistance(LatLng myPosition, LatLng estacionLatLng){

        float[] results = {0};
        Location.distanceBetween(myPosition.latitude, myPosition.longitude, estacionLatLng.latitude, estacionLatLng.longitude, results);
        return results[0];
    }

    public static String[] getAllMarcasNames(DataBaseHelper helper) throws SQLException {

        final Dao<MarcaCarros, Integer> dao = helper.getDaoMarcas();

        List<MarcaCarros> listMarcas = dao.queryForAll();
        if (listMarcas!= null && listMarcas.size() > 0){

            Log.e("marcas",String.valueOf(listMarcas.size()));
        }
        String[] marcas = new String[listMarcas.size()];

        for (int i = 0; i < listMarcas.size(); i++ ) {

            marcas[i] = listMarcas.get(i).getNombre();
        }

        return  marcas;
    }



    public static String[] getYearsModelsCars(){

        int year = Calendar.getInstance().get(Calendar.YEAR);

        String[] years = new String[50];
        years[0] = String.valueOf(year);

        for (int i = 1; i < 50 ;i++){

            years[i] = String.valueOf( year-i );
        }

        return years;
    }
}
