package com.index.medidor.services;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.UnidadRecorrido;
import com.index.medidor.model.Vehiculo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.index.medidor.utils.Constantes.LIMIT_UNIT_RECORRIDO;

public class RecorridoService {

    private Recorrido recorrido;
    private Timer mTimer;
    private boolean modelHasTwoTanks;
    private MainActivity mainActivity;
    private long time;
    private DataBaseHelper helper;

    private Dao<UnidadRecorrido, Integer> daoUnidadRecorrido;

    private short contUpdateRecorrido = 0;
    private String placa;

    private boolean inUploadingProccess = false;
    private int totalvaluesForUpload = 0;
    public RecorridoService() {
    }

    public RecorridoService(MainActivity mainActivity, DataBaseHelper helper, long idUsuario,
                            long idUsuarioModeloCarro, String placa) {
        this.mainActivity = mainActivity;
        this.helper = helper;
        contUpdateRecorrido = 0;
        this.placa = placa;
        try {
            daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RecorridoService(MainActivity mainActivity, boolean modelHasTwoTanks,
                            DataBaseHelper helper, long idUsuario, long idUsuarioModeloCarro, String placa) {
        this.mainActivity = mainActivity;
        this.modelHasTwoTanks = modelHasTwoTanks;
        recorrido = new Recorrido();
        recorrido.setDisanciaRecorrida(0.0);
        recorrido.setGalonesPerdidos(0.0);
        recorrido.setFechaInicio(Constantes.SDF_FOR_BACKEND.format(new Date()));
        recorrido.setStFechaInicio(Constantes.SDF_FOR_BACKEND.format(new Date()));
        recorrido.setUploaded(false);
        recorrido.setListTanqueadas(new ArrayList<>());
        recorrido.setListUnidadRecorrido(new ArrayList<>());
        recorrido.setRecorridoCode(Constantes.generateRandomRecorridoCode());

        recorrido.setFecha(Constantes.SDF_DATE_ONLY.format(new Date()));

        recorrido.setVehiculo(new Vehiculo(idUsuarioModeloCarro));
        time = 0;
        this.helper = helper;
        try {
            daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contUpdateRecorrido = 0;
        this.placa = placa;

    }

    public void initTimmers() {
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

        UnidadRecorrido unidadRecorrido = new UnidadRecorrido();
        Location location = mainActivity.getInndexLocationService().getMyLocation();
        if (location != null) {
            unidadRecorrido.setLatitud(mainActivity.getInndexLocationService().getMyLocation().getLatitude());
            unidadRecorrido.setLongitud(mainActivity.getInndexLocationService().getMyLocation().getLongitude());
            unidadRecorrido.setAltitud(mainActivity.getInndexLocationService().getMyLocation().getAltitude());
        }
        unidadRecorrido.setDistancia(mainActivity.getInndexLocationService().getDistancia());
        unidadRecorrido.setHora(Constantes.SDF_HOUR_RECORRIDO.format(new Date()));
        unidadRecorrido.setEstado(mainActivity.getEstado());
        unidadRecorrido.setFecha(Constantes.SDF_DATE_ONLY.format(new Date()));

        time += Constantes.DELAY_RECORRIDO;

        if (modelHasTwoTanks) {
            unidadRecorrido.setGalones(mainActivity.getGalones());
            unidadRecorrido.setGalonesTankTwo(mainActivity.getGalonesT2());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
            unidadRecorrido.setValorBluetoothTankTwo(mainActivity.getValorBluetoohT2());
        } else {
            unidadRecorrido.setGalones(mainActivity.getGalones());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
        }

        try {
            this.daoUnidadRecorrido.create(unidadRecorrido);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pararRecorrido() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        Toast.makeText(mainActivity, "RECORRIDO COMPLETED", Toast.LENGTH_SHORT).show();
    }

    public void uploadAllNotCompletedAndNotUploaded() {

        try {
                QueryBuilder<UnidadRecorrido, Integer> qbUnidadRecorrido;
                Where<UnidadRecorrido, Integer> whereUR;
                PreparedQuery<UnidadRecorrido> pqUR;
                qbUnidadRecorrido =
                        daoUnidadRecorrido.queryBuilder();
                qbUnidadRecorrido.orderBy("id", true);
                qbUnidadRecorrido.limit(LIMIT_UNIT_RECORRIDO);
                whereUR = qbUnidadRecorrido.where();
                whereUR.gt("id",0);

                pqUR = qbUnidadRecorrido.prepare();
                QueryBuilder<UnidadRecorrido, Integer> qbUnidadRecorridoCount;

                qbUnidadRecorridoCount =
                    daoUnidadRecorrido.queryBuilder();
            qbUnidadRecorridoCount.setCountOf(true);
            PreparedQuery<UnidadRecorrido> pqURCount = qbUnidadRecorridoCount.prepare();
                Where<UnidadRecorrido, Integer> whereURCount = qbUnidadRecorrido.where();
                whereURCount.gt("id",0);
                List<UnidadRecorrido> lUnidadRecorrido = daoUnidadRecorrido.query(pqUR);
                totalvaluesForUpload = (int)daoUnidadRecorrido.countOf(pqURCount);
                //lUnidadRecorrido.sort(Comparator.comparing(UnidadRecorrido::getId));
                if(lUnidadRecorrido.size() > 1){

                    Log.e("SORT","First index " + lUnidadRecorrido.get(0).getId());
                    Log.e("SORT","Last index " + lUnidadRecorrido.get(lUnidadRecorrido.size() -1).getId());
                }

                //if(totalvaluesForUpload > LIMIT_UNIT_RECORRIDO)
                //    lUnidadRecorrido = lUnidadRecorrido.subList(0, (int) LIMIT_UNIT_RECORRIDO);

                this.uploadSingleTrip(lUnidadRecorrido);

        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadSingleTrip(List<UnidadRecorrido> listUnidadRecorrido) {
        Call<UnidadRecorrido> uploadAll = MedidorApiAdapter.getApiService().postRecorridosBulk(Constantes.CONTENT_TYPE_JSON,
                listUnidadRecorrido, placa);
        Toast.makeText(mainActivity, "Subiendo: " + listUnidadRecorrido.size(), Toast.LENGTH_SHORT).show();

        mainActivity.getmCustomProgressDialog().show("");
        uploadAll.enqueue(new Callback<UnidadRecorrido>() {
            @Override
            public void onResponse(Call<UnidadRecorrido> call, Response<UnidadRecorrido> response) {

                mainActivity.getmCustomProgressDialog().dismiss("");
                Toast.makeText(mainActivity, "RESPONSE CODE " + response.code(), Toast.LENGTH_LONG).show();

                if (response.isSuccessful() && response.body() != null) {
                    deleteUploadedUnidadRecorridos(listUnidadRecorrido);
                    if(totalvaluesForUpload > LIMIT_UNIT_RECORRIDO){
                        uploadAllNotCompletedAndNotUploaded();
                        inUploadingProccess = true;
                    }
                    else
                        inUploadingProccess = false;
                } else {
                    Toast.makeText(mainActivity, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("RESPPONSE", "IS NOT SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<UnidadRecorrido> call, Throwable t) {
                mainActivity.getmCustomProgressDialog().dismiss("");
                Toast.makeText(mainActivity, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR 3", t.getMessage());
                inUploadingProccess = false;
            }
        });
    }

    public Recorrido getCurrentUnCompletedRecorrido(String fecha) {
        Recorrido recorrido = null;
        try {
            Dao<Recorrido, Integer> dao = helper.getDaoRecorridos();
            List<Recorrido> recorridoList = dao.queryForEq("fecha", fecha);
            if (recorridoList != null && recorridoList.size() > 0) {
                recorrido = recorridoList.get(0);
                //if(recorrido.isCompleted()){
//                    recorrido = null;}
            }
        } catch (SQLException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return recorrido;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    private void deleteUploadedUnidadRecorridos(List<UnidadRecorrido> listUnidadRecorrido) {

        try {
            if (listUnidadRecorrido != null && listUnidadRecorrido.size() > 0) {
                for (int i = 0; i < listUnidadRecorrido.size(); i+=400) {
                    //daoUnidadRecorrido.delete(listUnidadRecorrido.get(i));
                    if((listUnidadRecorrido.size() - i) < 400){
                        if(listUnidadRecorrido.size() == 1){
                            daoUnidadRecorrido.delete(listUnidadRecorrido.get(0));
                        }else {
                            daoUnidadRecorrido.delete(listUnidadRecorrido.subList(i, listUnidadRecorrido.size()-1));
                        }
                    }else
                        daoUnidadRecorrido.delete(listUnidadRecorrido.subList(i, i+399));
                }
            }
        } catch (SQLException e) {
            Log.e("6.1", "EX " + e.getErrorCode() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isModelHasTwoTanks() {
        return modelHasTwoTanks;
    }

    public void setModelHasTwoTanks(boolean modelHasTwoTanks) {
        this.modelHasTwoTanks = modelHasTwoTanks;
    }

    public void pushTanqueada(Tanqueadas tanqueada) {
        recorrido.getListTanqueadas().add(tanqueada);
    }

    public boolean isInUploadingProccess() {
        return inUploadingProccess;
    }
}
