package com.index.medidor.services;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.UnidadRecorrido;
import com.index.medidor.model.Usuario;
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

public class RecorridoService {

    private Recorrido recorrido;
    private Timer mTimer;
    private boolean modelHasTwoTanks;
    private MainActivity mainActivity;
    private long time;
    private DataBaseHelper helper;
    private List<UnidadRecorrido> listUnidadRecorrido;
    private Dao<Recorrido, Integer> daoRecorrido;
    private Dao<UnidadRecorrido, Integer> daoUnidadRecorrido;

    private int idUsuario;
    private long idUsuarioModeloCarro;
    private short contUpdateRecorrido = 0;
    private Location location;
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
        this.idUsuario = (int) idUsuario;
        this.idUsuarioModeloCarro = idUsuarioModeloCarro;
        this.placa = placa;
        try {
            daoRecorrido = helper.getDaoRecorridos();
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

        Usuario user = new Usuario();
        user.setId((int) idUsuario);
        recorrido.setVehiculo(new Vehiculo(idUsuarioModeloCarro));
        recorrido.setUsuario(user);
        time = 0;
        this.helper = helper;
        this.listUnidadRecorrido = new ArrayList<>();
        try {
            daoRecorrido = helper.getDaoRecorridos();
            daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
            daoRecorrido.create(recorrido);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        contUpdateRecorrido = 0;
        this.idUsuario = (int) idUsuario;
        this.idUsuarioModeloCarro = idUsuarioModeloCarro;
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
        location = mainActivity.getInndexLocationService().getMyLocation();
        if (location != null) {
            unidadRecorrido.setLatitud(mainActivity.getInndexLocationService().getMyLocation().getLatitude());
            unidadRecorrido.setLongitud(mainActivity.getInndexLocationService().getMyLocation().getLongitude());
            unidadRecorrido.setAltitud(mainActivity.getInndexLocationService().getMyLocation().getAltitude());
        }
        unidadRecorrido.setDistancia(mainActivity.getInndexLocationService().getDistancia());
        unidadRecorrido.setHora(Constantes.SDF_HOUR_RECORRIDO.format(new Date()));
        //recorrido.setDisanciaRecorrida(mainActivity.getInndexLocationService().getDistancia_temp());
        unidadRecorrido.setEstado(mainActivity.getEstado());

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
        listUnidadRecorrido.add(unidadRecorrido);

        if (contUpdateRecorrido == 5) {
            updateRecorridoInDao();
            contUpdateRecorrido = 0;
        }
        contUpdateRecorrido++;
    }

    public void pararRecorrido() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        recorrido.setFechaFin(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorrido.setCompleted(true);
        Toast.makeText(mainActivity, "RECORRIDO COMPLETED", Toast.LENGTH_SHORT).show();
        updateRecorridoInDao();
    }

    private void updateRecorridoInDao() {
        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        try {
            //Log.e("UP COMPLE", String.valueOf(recorrido.isCompleted()));
            //Log.e("CODE",recorrido.getRecorridoCode());
            daoRecorrido.update(recorrido);

            for (UnidadRecorrido unidad : recorrido.getListUnidadRecorrido()) {
                if (unidad.getId() == null) {
                    unidad.setIdRecorrido(this.recorrido.getId());
                    this.daoUnidadRecorrido.create(unidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void uploadAllNotCompletedAndNotUploaded() {

        final List<Recorrido> recorridoList;

        try {
            QueryBuilder<Recorrido, Integer> queryBuilder =
                    daoRecorrido.queryBuilder();
            Where<Recorrido, Integer> where = queryBuilder.where();
            where.eq("uploaded", false);
            PreparedQuery<Recorrido> preparedQuery = queryBuilder.prepare();
            recorridoList = daoRecorrido.query(preparedQuery);

            if (recorridoList != null && recorridoList.size() > 0) {

                Usuario usuario = new Usuario();
                usuario.setId(idUsuario);
                Vehiculo uhmc = new Vehiculo();
                uhmc.setId(idUsuarioModeloCarro);

                Recorrido recorridoToUpload = recorridoList.get(0);

                QueryBuilder<UnidadRecorrido, Integer> qbUnidadRecorrido;
                Where<UnidadRecorrido, Integer> whereUR;
                PreparedQuery<UnidadRecorrido> pqUR;
                qbUnidadRecorrido =
                        daoUnidadRecorrido.queryBuilder();
                qbUnidadRecorrido.orderBy("id", true);
                //qbUnidadRecorrido.limit(LIMIT_UNIT_RECORRIDO);
                whereUR = qbUnidadRecorrido.where();
                whereUR.eq("idRecorrido", recorridoToUpload.getId());

                pqUR = qbUnidadRecorrido.prepare();

                List<UnidadRecorrido> lUnidadRecorrido = daoUnidadRecorrido.query(pqUR);
                totalvaluesForUpload = lUnidadRecorrido.size();
                //lUnidadRecorrido.sort(Comparator.comparing(UnidadRecorrido::getId));
                if(lUnidadRecorrido.size() > 1){

                    Log.e("SORT","First index " + lUnidadRecorrido.get(0).getId());
                    Log.e("SORT","Last index " + lUnidadRecorrido.get(lUnidadRecorrido.size() -1).getId());
                }

                if(totalvaluesForUpload > Constantes.LIMIT_UNIT_RECORRIDO)
                    recorridoToUpload.setListUnidadRecorrido(lUnidadRecorrido.subList(0, (int)Constantes.LIMIT_UNIT_RECORRIDO));
                else
                    recorridoToUpload.setListUnidadRecorrido(lUnidadRecorrido);

                recorridoToUpload.setUsuario(usuario);
                recorridoToUpload.setVehiculo(uhmc);
                Log.e("CODE", recorridoToUpload.getRecorridoCode());
                this.uploadSingleTrip(recorridoToUpload);
                //}
                //Log.e("size", "not uploaded "+recorridoList.size());
            } else {
                Log.e("ERR", "LA LISTA DE RECORRIDOS ES NULL O SU SIZE ES 0");
            }
        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    private void uploadSingleTrip(Recorrido recorridos) {
        Call<Recorrido> uploadAll = MedidorApiAdapter.getApiService().postRecorridosBulk(Constantes.CONTENT_TYPE_JSON,
                recorridos, placa);

        mainActivity.getmCustomProgressDialog().show("");
        uploadAll.enqueue(new Callback<Recorrido>() {
            @Override
            public void onResponse(Call<Recorrido> call, Response<Recorrido> response) {

                mainActivity.getmCustomProgressDialog().dismiss("");
                Toast.makeText(mainActivity, "RESPONSE CODE " + response.code(), Toast.LENGTH_LONG).show();

                if (response.isSuccessful() && response.body() != null) {

                    deleteUploadedUnidadRecorridos(recorridos.getListUnidadRecorrido());
                    if (recorridos.isCompleted() && (recorridos.getListUnidadRecorrido() == null
                            || recorridos.getListUnidadRecorrido().size() == 0)) {
                        Log.e("ID:", "IS COMPLETED AND READY FOR UPDATE " + recorridos.getId());
                        recorridos.setUploaded(true);
                        inUploadingProccess = false;
                        Log.e("ID:", "it is supposed It HAS BEEN UPloaded " + recorridos.getId());
                        try {
                            Log.e("ID", "INSIDE TRY CATCH");
                            daoRecorrido.update(recorridos);
                                        /*int c = daoRecorrido.delete(r);
                                        if(c > 0){
                                            Log.e("THIS", "WAS UPDATED " + r.getId());
                                        }else {
                                            Log.e("THIS", "WAS NOOOOTTTT UPDATED " + r.getId());
                                        }*/

                        } catch (SQLException e) {
                            Toast.makeText(mainActivity, "ERROR, ACTUALIZANDO RECORRIDOS EN LOCAL.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        if(totalvaluesForUpload > Constantes.LIMIT_UNIT_RECORRIDO){
                            uploadAllNotCompletedAndNotUploaded();
                            inUploadingProccess = true;
                        }
                        else
                            inUploadingProccess = false;
                    }
                } else {
                    Toast.makeText(mainActivity, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("RESPPONSE", "IS NOT SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<Recorrido> call, Throwable t) {
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

    public void continueCurrentRecorrido(Recorrido recorrido) {

        this.recorrido = recorrido;
        initTimmers();
        this.listUnidadRecorrido = new ArrayList<>();

        contUpdateRecorrido = 0;
        Toast.makeText(mainActivity, "CONTINUANDO RECORRIDO", Toast.LENGTH_SHORT).show();
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void deleteAllRecorridos() {
        try {
            List<Recorrido> recorridosList = daoRecorrido.queryForAll();
            if (recorridosList != null && recorridosList.size() > 0)
                daoRecorrido.delete(recorridosList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void uploadRecorrido() {

        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setListUnidadRecorrido(null);
        //recorrido.setId(null);

        Call<String> callRegisterRecorrido =
                MedidorApiAdapter.getApiService().postRegisterRecorrido(Constantes.CONTENT_TYPE_JSON, recorrido);

        callRegisterRecorrido.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                //se obtiene el id del recorrido
                if (response.body() != null) {
                    Long id = Long.parseLong(response.body());
                    recorrido.setId(id);
                } else {
                    Log.e("ERROR", "EL RESPONSE BODY ES NULO");
                }
                if (response.isSuccessful()) {
                    Toast.makeText(mainActivity, "RECORRIDO ENVIADO AL SERVIDOR DE MANERA EXITOSA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mainActivity, "NO SE PUDO SUBIR EL RECORRIDO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isModelHasTwoTanks() {
        return modelHasTwoTanks;
    }

    public void setModelHasTwoTanks(boolean modelHasTwoTanks) {
        this.modelHasTwoTanks = modelHasTwoTanks;
    }

    public void completeUnCompletedRecorridos() throws SQLException {

        List<Recorrido> lUnCompletedRecorridos = new ArrayList<>();
        QueryBuilder<Recorrido, Integer> queryBuilder =
                daoRecorrido.queryBuilder();
        Where<Recorrido, Integer> where = queryBuilder.where();
        where.eq("completed", false).and()
                .notIn("fecha", Constantes.SDF_DATE_ONLY.format(new Date()));
        PreparedQuery<Recorrido> preparedQuery = queryBuilder.prepare();
        lUnCompletedRecorridos = daoRecorrido.query(preparedQuery);

        if (lUnCompletedRecorridos != null && lUnCompletedRecorridos.size() > 0) {

            for (Recorrido r : lUnCompletedRecorridos
            ) {
                r.setCompleted(true);
                daoRecorrido.update(r);
            }
        }
    }

    public void pushTanqueada(Tanqueadas tanqueada) {
        recorrido.getListTanqueadas().add(tanqueada);
    }

    public boolean isInUploadingProccess() {
        return inUploadingProccess;
    }
}
