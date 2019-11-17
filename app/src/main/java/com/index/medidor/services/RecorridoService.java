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
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
    private Dao<Recorrido, Integer> daoRecorrido;

    public RecorridoService() {
    }

    public RecorridoService(MainActivity mainActivity, boolean modelHasTwoTanks,
                            DataBaseHelper helper, int idUsuario, int idUsuarioMOdeloCarro) {
        this.mainActivity = mainActivity;
        this.modelHasTwoTanks = modelHasTwoTanks;
        recorrido = new Recorrido();
        recorrido.setDisanciaRecorrida(0.0);
        recorrido.setGalonesPerdidos(0.0);
        recorrido.setFechaInicio(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorrido.setUploaded(false);
        recorrido.setListTanqueadas(new ArrayList<>());
        recorrido.setListUnidadRecorrido(new ArrayList<>());
        recorrido.setRecorridoCode(Constantes.generateRndomRecorridoCode());
        Usuario user = new Usuario();
        user.setId(idUsuario);
        recorrido.setUsuarioHasModeloCarro(new UsuarioHasModeloCarro((long)idUsuarioMOdeloCarro));
        recorrido.setUsuario(user);
        time = 0;
        this.helper = helper;
        this.listUnidadRecorrido = new ArrayList<>();
        try {
            daoRecorrido = helper.getDaoRecorridos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            unidadRecorrido.setGalones(mainActivity.getGalones());
            unidadRecorrido.setGalonesT2(mainActivity.getGalones());

            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetoohT2());

        } else {
            unidadRecorrido.setGalones(mainActivity.getGalones());
            unidadRecorrido.setValorBluetooh(mainActivity.getValorBluetooh());
        }
        listUnidadRecorrido.add(unidadRecorrido);
        updateRecorridoInDao();
    }

    public void pushTanqueada(Tanqueadas tanqueada) {
        recorrido.getListTanqueadas().add(tanqueada);
    }

    public void pararRecorrido() {
        recorrido.setFechaFin(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorrido.setCompleted(true);

        recorridoStarted = false;
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            recorridoStarted = false;
        }
        guardarRecorrido();
    }

    private void guardarRecorrido() {

        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setJsonListUnidadRecorrido();

        try {
            Dao<UnidadRecorrido, Integer> daoUnidadRecorrido= helper.getDaoUnidadRecorrido();

            recorrido.setId((long)daoRecorrido.create(recorrido));
            daoUnidadRecorrido.create(recorrido.getListUnidadRecorrido());
            Gson gson = new Gson();
            Log.e("RECORRIDO", "DESPUES DE GUARDADO.");
            Log.e("", gson.toJson(recorrido));

        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Error guardando reorrido.", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    private void updateRecorridoInDao() {

        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setJsonListUnidadRecorrido();

        try {
            daoRecorrido.update(recorrido);
            Gson gson = new Gson();
            //Log.e("RECORRIDO", "DESPUES DE ACTUALIZADO.");
            //Log.e("", gson.toJson(recorrido));

        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Error guardando reorrido.", Toast.LENGTH_SHORT).show();

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

    public void uploadAllNotCompletedAndNotUploaded() {

        final List<Recorrido> recorridoList;

// the name field must be equal to "foo"
        try {

            QueryBuilder<Recorrido, Integer> queryBuilder =
                    daoRecorrido.queryBuilder();
            Where<Recorrido, Integer> where = queryBuilder.where();
            where.eq("uploaded", false);
            PreparedQuery<Recorrido> preparedQuery = queryBuilder.prepare();

            recorridoList = daoRecorrido.query(preparedQuery);

            if (recorridoList.size() > 0) {

                Call<String> uploadAll =   MedidorApiAdapter.getApiService().postRecorridosBulk(Constantes.CONTENT_TYPE_JSON ,
                        recorridoList);

                uploadAll.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful()) {

                            for (Recorrido r: recorridoList) {
                                if (r.isCompleted()){
                                    r.setUploaded(true);
                                    try {
                                        daoRecorrido.update(r);
                                    } catch (SQLException e) {
                                        Toast.makeText(mainActivity, "ERROR, ACTUALIZANDO RECORRIDOS EN LOCAL.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(mainActivity, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN.", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", t.getMessage());
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
