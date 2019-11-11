package com.index.medidor.services;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.UnidadRecorrido;
import com.index.medidor.model.Usuario;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.dao.Dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecorridoService implements Serializable {

    private Recorrido recorrido;
    private Timer mTimer;
    private boolean modelHasTwoTanks;
    private boolean recorridoStarted;
    private MainActivity mainActivity;
    private long time;
    private DataBaseHelper helper;
    private Collection<UnidadRecorrido> listUnidadRecorrido;

    public RecorridoService() {
    }

    public RecorridoService(MainActivity mainActivity, boolean modelHasTwoTanks, DataBaseHelper helper) {
        this.mainActivity = mainActivity;
        this.modelHasTwoTanks = modelHasTwoTanks;
        recorrido = new Recorrido();
        recorrido.setDisanciaRecorrida(0.0);
        recorrido.setGalonesPerdidos(0.0);
        recorrido.setFechaInicio(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorrido.setUploaded(false);
        recorrido.setListTanqueadas(new ArrayList<>());
        recorrido.setListUnidadRecorrido(new ArrayList<>());
        Usuario user = new Usuario();
        user.setId(1);
        recorrido.setUsuario(user);
        time = 0;
        this.helper = helper;
        this.listUnidadRecorrido = new ArrayList<>();
        Gson gson = new Gson();

    }

    public void iniciarRecorrido() {

        recorridoStarted =  true;
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateRecorrido();
            }
        };
        mTimer.schedule(task, 1000, Constantes.DELAY_RECORRIDO);
    }

    private void updateRecorrido() {

        //Log.e("RERR", "UPDATING RECORRIDO");
        UnidadRecorrido unidadRecorrido = new UnidadRecorrido();
        unidadRecorrido.setLatitud(mainActivity.getInndexLocationService().getMyLocation().getLatitude());
        unidadRecorrido.setLongitud(mainActivity.getInndexLocationService().getMyLocation().getLongitude());
        unidadRecorrido.setAltitud(mainActivity.getInndexLocationService().getMyLocation().getAltitude());
        unidadRecorrido.setTiempo(time / 1000);

        unidadRecorrido.setDistancia(mainActivity.getInndexLocationService().getDistancia());
        unidadRecorrido.setHora(Constantes.SDF_HOUR_RECORRIDO.format(new Date()));
        //recorrido.setDisanciaRecorrida(mainActivity.getInndexLocationService().getDistancia_temp());

        time += Constantes.DELAY_RECORRIDO;

        if (modelHasTwoTanks) {
            unidadRecorrido.setGalones(mainActivity.getBluetoothHelper().getDato());
            unidadRecorrido.setGalonesT2(mainActivity.getBluetoothHelper().getDatoTanque2());

            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetoohT2());

        } else {
            unidadRecorrido.setGalones(mainActivity.getBluetoothHelper().getDato());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
        }
        listUnidadRecorrido.add(unidadRecorrido);
    }

    public void pushTanqueada(Tanqueadas tanqueada) {
        recorrido.getListTanqueadas().add(tanqueada);
    }

    public void pararRecorrido() {
        recorrido.setFechaFin(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorridoStarted = false;
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            recorridoStarted = false;
        }
        guardarRecorrido();
        uploadRecorrido();
    }

    private void guardarRecorrido() {

        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setJsonListUnidadRecorrido();

        try {
            Dao<Recorrido, Integer> daoRecorrido = helper.getDaoRecorridos();
            Dao<UnidadRecorrido, Integer> daoUnidadRecorrido= helper.getDaoUnidadRecorrido();

            daoRecorrido.create(recorrido);
            daoUnidadRecorrido.create(recorrido.getListUnidadRecorrido());
            Gson gson = new Gson();
            Log.e("RECORRIDO", "DESPUES DE GUARDADO.");
            Log.e("", gson.toJson(recorrido));
            Toast.makeText(mainActivity, "Recorrido guardado de manera exitosa.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Error guardadndo reorrido.", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    public void uploadRecorrido() {

        Gson gson = new Gson();
        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setJsonListUnidadRecorrido();
        recorrido.setListUnidadRecorrido(null);
        //recorrido.setId(null);

        Call<String> callRegisterRecorrido =
                MedidorApiAdapter.getApiService().postRegisterRecorrido(Constantes.CONTENT_TYPE_JSON, recorrido);

        callRegisterRecorrido.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                //se obtiene el id del recorrido
                Log.e("Recorrido", response.body());
                if(response.body() != null) {

                    Long id = Long.parseLong(response.body());
                    recorrido.setId(id);
                } else {
                    Log.e("ERROR", "EL RESPONSE BODY ES NULO");
                }
                if(response.isSuccessful()) {
                    Toast.makeText(mainActivity, "RECORRIDO ENVIADO AL SERVIDOR DE MANERA EXITOSA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mainActivity, "NO SE PUDO SUBIR EL RECORRIDO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void stopTimmers() {
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
    }
}
