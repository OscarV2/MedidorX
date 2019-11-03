package com.index.medidor.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.index.medidor.activities.MainActivity;
import com.index.medidor.bluetooth.BluetoothHelper;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.UnidadRecorrido;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecorridoService {

    private Recorrido recorrido;
    private Context context;
    private Timer mTimer;
    private Handler mHandler;
    private boolean modelHasTwoTanks;
    private boolean recorridoStarted;
    private MainActivity mainActivity;
    private UnidadRecorrido unidadRecorrido;
    private long time;
    private DataBaseHelper helper;


    public RecorridoService() {
    }

    public RecorridoService(MainActivity mainActivity, boolean modelHasTwoTanks, DataBaseHelper helper) {
        this.mainActivity = mainActivity;
        this.modelHasTwoTanks = modelHasTwoTanks;
        recorrido = new Recorrido();
        recorrido.setDisanciaRecorrida(0.0);
        recorrido.setFechaInicio(new Date());
        recorrido.setUploaded(false);
        recorrido.setListTanqueadas(new ArrayList<>());
        recorrido.setListUnidadRecorrido(new ArrayList<>());
        time = 0;
        this.helper = helper;
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
        mTimer.schedule(task, Constantes.DELAY_RECORRIDO);
    }

    private void updateRecorrido() {

        unidadRecorrido = new UnidadRecorrido();
        unidadRecorrido.setLatitud(mainActivity.getInndexLocationService().getMyLocation().getLatitude());
        unidadRecorrido.setLongitud(mainActivity.getInndexLocationService().getMyLocation().getLongitude());
        unidadRecorrido.setTiempo(time);
        time += Constantes.DELAY_RECORRIDO;

        if (modelHasTwoTanks) {
            unidadRecorrido.setGalones(BluetoothHelper.getDato());
            unidadRecorrido.setGalones(BluetoothHelper.getDatoTanque2());
        } else {
            unidadRecorrido.setGalones(BluetoothHelper.getDato());
        }

        recorrido.getListUnidadRecorrido().add(unidadRecorrido);
    }

    public void pushTanqueada(Tanqueadas tanqueada) {

        recorrido.getListTanqueadas().add(tanqueada);
    }

    public void pararRecorrido() {

        recorrido.setFechaFin(new Date());
        recorridoStarted = false;
        guardarRecorrido();
        uploadRecorrido();
    }

    public void guardarRecorrido() {

        try {
            Dao<Recorrido, Integer> daoRecorrido = helper.getDaoRecorridos();
            daoRecorrido.create(recorrido);
            Toast.makeText(mainActivity, "Recorrido guardado de manera exitosa.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Error guardadndo reorrido.", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    public void uploadRecorrido() {

        Call<String> callRegisterRecorrido =
                MedidorApiAdapter.getApiService().postRegisterRecorrido(Constantes.CONTENT_TYPE_JSON, 1L, recorrido);

        callRegisterRecorrido.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.e("Recorrido", response.body());

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

}
