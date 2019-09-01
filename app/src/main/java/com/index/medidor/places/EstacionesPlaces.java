package com.index.medidor.places;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estaciones;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.SortByDistance;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstacionesPlaces {

    public EstacionesPlaces() {
    }

    public void getEstacionesCercanas(Location location){

        String link = Constantes.URL_PLACES;
        link = link.replace("{lat}",String.valueOf(location.getLatitude()));
        link = link.replace("{long}",String.valueOf(location.getLongitude()));
        link = link + Constantes.API_KEY_PLACES;
        new DownloadRawData().execute(link);
    }

    public void dondeTanquear(){

    }

    public List<Estaciones> getEstacionesCercanas(LatLng myPosition, DataBaseHelper helper) throws SQLException {

        final Dao<Estaciones, Integer> daoEstaciones = helper.getDaoEstaciones();

        QueryBuilder<Estaciones, Integer> queryBuilder =
                daoEstaciones.queryBuilder();

        queryBuilder.where().between("latitud",myPosition.latitude - Constantes.LAT_LNG_TOLERANCE,
                myPosition.latitude + Constantes.LAT_LNG_TOLERANCE)
        .and().between("longitud", myPosition.longitude - Constantes.LAT_LNG_TOLERANCE,
                myPosition.longitude + Constantes.LAT_LNG_TOLERANCE);

        PreparedQuery<Estaciones> preparedQuery = queryBuilder.prepare();

        List<Estaciones> listEstaciones = daoEstaciones.query(preparedQuery);
        float distancia;
        if(listEstaciones.size() > 0){

            for (int i = 0; i < listEstaciones.size(); i++) {

                distancia = Constantes.getDistance(myPosition, listEstaciones.get(i).getCoordenadas());
                listEstaciones.get(i).setDistancia(distancia);
            }

            Collections.sort(listEstaciones, (e1, e2) -> new SortByDistance().compare(e1,e2));

            return  listEstaciones;

        }else{
            return null;
        }

    }

    public Estaciones getEstacionMasCercana(LatLng myPosition, DataBaseHelper helper) throws SQLException {

        final Dao<Estaciones, Integer> dao = helper.getDaoEstaciones();

        List<Estaciones> listEstaciones = dao.queryForAll();
        List<Float> distancias = new ArrayList<>();
        if(listEstaciones.size() > 0){
            for (Estaciones estacion: listEstaciones) {

                distancias.add(getDistance(myPosition, estacion.getCoordenadas()));

            }
            int minIndex = distancias.indexOf(Collections.min(distancias));
            return  listEstaciones.get(minIndex);

        }else{
            return null;
        }


    }

    private static class DownloadRawData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                InputStream is = url.openConnection().getInputStream();
                Log.e("se abrio la ","conexion");

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                Log.e("json: ",buffer.toString());


            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String res) {

                Log.e("ir a parse","JSON");
        }
    }

    public float getDistance(LatLng myPosition, LatLng estacionLatLng){

        float[] results = {0};
        Location.distanceBetween(myPosition.latitude, myPosition.longitude, estacionLatLng.latitude, estacionLatLng.longitude, results);
        return results[0];
    }
}
