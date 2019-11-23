package com.index.medidor.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    public static final String PUT_UPDATE_USUARIO_HAS_MODELO_CARRO =  "usuarioHasModeloCarros/update";
    public static final String GET_USUARIO_HAS_MODELO_CARROS_BY_ID_USER =  "usuarioHasModeloCarros/getAllByUser/";

    //API KEY DE PONTON
    public static final String API_KEY_PLACES = "AIzaSyBHLkx2pOIKyoiTYjw-c78ValF4iHktcjc";
    //public static final String API_KEY = "AIzaSyBVYoTTWhM_JZAlbUpJuTKTI5xcTJq7NFY";

    //RECORRIDOS
    public static final String POST_REGISTRAR_RECORRIDO = "recorridos/register";
    public static final String POST_RECORRIDOS_BULK = "recorridos/bulk";

    public static final String GET_RECORRIDOS_BY_USER= "recorridos/idUsuario";

    public static final String URL_PLACES ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={lat},{long}&radius=1200&type=gas_station&key=";
    public static final String URL_HTTP = "https://maps.googleapis.com/maps/api/directions/json?origin=";

    /**
     * MODELOS CARROS
     */
    public static final String GET_MODELOS_CARROS_BY_MARCA =  "modelos-carros/getByMarca/";
    public static final String POST_REGISTER_MODELO_CARRO = "modelos-carros/postRegistrar";

    /**
     * MARCAS CARROS
     */
    public static final String GET_MARCAS_CARROS =  "marcas-carros/getAll";
    public static final String DEFAULT_USER_ID = "idUsuario";


    public static int ROTATION = 0;

    public static int ARRAY_DATA_SIZE = 5;

    public static final String DEFAULT_BLUETOOTH_VALUE_ARRAY = "defaultValuesBluetoothArray";
    public static final String DEFAULT_BLUETOOTH_MAC = "defaultBluetoothMac";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String DEFAULT_GAL_CANT = "defaultGalCant";
    public static final String MODEL_HAS_TWO_TANKS = "modelHasTwoTanks";
    public static final String STOP_RECORRIDO_INTENT_FILTER = "RECORRIDO_INTENT_FILTER";
    public static final String START_RECORRIDO_INTENT_FILTER = "START_RECORRIDO_INTENT_FILTER";
    public static final String DEFAULT_UHMC_ID = "defaultUhmcId";
    public static final String DEFAULT_PLACA = "defaultPlaca";
    public static final String SESION_ACTIVE = "sesionMedidor";

    public static final double LAT_LNG_TOLERANCE = 0.002;

    public static final long DELAY_RECORRIDO = 1000;

    public static final SimpleDateFormat SDF_FOR_BACKEND = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static final SimpleDateFormat SDF_DATE_ONLY = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static final SimpleDateFormat SDF_DATE_RECORRIDO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static final SimpleDateFormat SDF_HOUR_RECORRIDO = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public static String generateRndomRecorridoCode(){

        SimpleDateFormat sdfRandom = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);

        return sdfRandom.format(new Date()) + UUID.randomUUID().toString();
    }

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

    /**
     * Constants for FetchAddressIntentService
     */
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static String obtenerDireccionesURL(Double latitud_origen, Double longitud_origen, Double latitud_destino,
                                                Double longitud_destino, String mapsApiKey) {

        String str_key = "key=" + mapsApiKey;
        String str_origin = "origin=" + latitud_origen + "," + longitud_origen;
        String str_dest = "destination=" + latitud_destino + "," + longitud_destino;
        String sensor = "sensor=false";
        String parameters = str_key + "&" + str_origin + "&" + str_dest + "&" + sensor;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        return url;
    }
}
