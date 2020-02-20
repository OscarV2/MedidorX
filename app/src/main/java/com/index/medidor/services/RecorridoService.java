package com.index.medidor.services;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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

    private int idUsuario ;
    private long idUsuarioModeloCarro;
    private short contUpdateRecorrido = 0;
    private Location location;
    private String placa;

    public RecorridoService() { }

    public RecorridoService(MainActivity mainActivity, DataBaseHelper helper, long idUsuario, long idUsuarioModeloCarro, String placa) {
        this.mainActivity = mainActivity;
        this.helper = helper;
        contUpdateRecorrido = 0;
        this.idUsuario = (int)idUsuario;
        this.idUsuarioModeloCarro = idUsuarioModeloCarro;
        this.placa = placa;
    }

    public RecorridoService(MainActivity mainActivity, boolean modelHasTwoTanks,
                            DataBaseHelper helper, long idUsuario, long idUsuarioModeloCarro, String placa) {
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
        user.setId((int)idUsuario);
        recorrido.setVehiculo(new Vehiculo(idUsuarioModeloCarro));
        recorrido.setUsuario(user);
        time = 0;
        recorrido.setFecha(Constantes.SDF_DATE_ONLY.format(new Date()));
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
        this.idUsuario = (int)idUsuario;
        this.idUsuarioModeloCarro = idUsuarioModeloCarro;
        this.placa = placa;

    }

    public void iniciarRecorrido() {
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateRecorrido();
            }
        };
        mTimer.schedule(task, 1000, Constantes.DELAY_RECORRIDO);
        Toast.makeText(mainActivity, "RECORRIDO INICIADO", Toast.LENGTH_SHORT).show();
    }

    private void updateRecorrido() {

        UnidadRecorrido unidadRecorrido = new UnidadRecorrido();
        location = mainActivity.getInndexLocationService().getMyLocation();
        if(location != null)
        {
            unidadRecorrido.setLatitud(mainActivity.getInndexLocationService().getMyLocation().getLatitude());
            unidadRecorrido.setLongitud(mainActivity.getInndexLocationService().getMyLocation().getLongitude());
            unidadRecorrido.setAltitud(mainActivity.getInndexLocationService().getMyLocation().getAltitude());
        }
        unidadRecorrido.setTiempo(time / 1000);

        unidadRecorrido.setDistancia(mainActivity.getInndexLocationService().getDistancia());
        unidadRecorrido.setHora(Constantes.SDF_HOUR_RECORRIDO.format(new Date()));
        //recorrido.setDisanciaRecorrida(mainActivity.getInndexLocationService().getDistancia_temp());

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

        if(contUpdateRecorrido == 5){
            updateRecorridoInDao();
            contUpdateRecorrido = 0;
        }
        contUpdateRecorrido++;
    }

    public void pararRecorrido() {

        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        recorrido.setFechaFin(Constantes.SDF_DATE_RECORRIDO.format(new Date()));
        recorrido.setCompleted(true);
        Toast.makeText(mainActivity, "RECORRIDO COMPLETED", Toast.LENGTH_SHORT).show();
        updateRecorridoInDao();
    }

    // guarda un recorrido completado
 /*   private void guardarRecorrido() {

        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        //recorrido.setJsonListUnidadRecorrido();
        try {
            Dao<UnidadRecorrido, Integer> daoUnidadRecorrido= helper.getDaoUnidadRecorrido();
            daoUnidadRecorrido.create(recorrido.getListUnidadRecorrido());
            Gson gson = new Gson();
            Log.e("RECORRIDO", "DESPUES DE GUARDADO.");
            Log.e("", gson.toJson(recorrido));

        } catch (SQLException e) {
            Toast.makeText(mainActivity, "Error guardando reorrido.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
*/
    private void updateRecorridoInDao() {
        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        try {
            //Log.e("UP COMPLE", String.valueOf(recorrido.isCompleted()));
            //Log.e("CODE",recorrido.getRecorridoCode());
            daoRecorrido.update(recorrido);

            for (UnidadRecorrido unidad: recorrido.getListUnidadRecorrido()             ) {

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
            //recorridoList = daoRecorrido.queryForAll();

            if (recorridoList != null && recorridoList.size() > 0) {

                Usuario usuario = new Usuario();
                usuario.setId(idUsuario);
                Vehiculo uhmc = new Vehiculo();
                uhmc.setId(idUsuarioModeloCarro);

                QueryBuilder<UnidadRecorrido, Integer> qbUnidadRecorrido;
                Where<UnidadRecorrido, Integer> whereUR;
                PreparedQuery<UnidadRecorrido> pqUR;
                for (Recorrido r: recorridoList) {

                    qbUnidadRecorrido =
                            daoUnidadRecorrido.queryBuilder();
                    whereUR = qbUnidadRecorrido.where();
                    whereUR.eq("idRecorrido", r.getId());
                    pqUR = qbUnidadRecorrido.prepare();

                    r.setListUnidadRecorrido(daoUnidadRecorrido.query(pqUR));

                    if(r.getListUnidadRecorrido() != null && r.getListUnidadRecorrido().size() > 0){
                        Log.e("1", "List u reco OK");
                        Log.e("2", "Size " +r.getListUnidadRecorrido().size());

                        Log.e("3", "First record " +  r.getListUnidadRecorrido().get(0).getLatitud() );

                    } else {
                        Log.e("4", "SORRY THIS IS NULL");

                    }


                    Log.e("UPLOADED", String.valueOf(r.isUploaded()));
                    Log.e("COMPLETED", String.valueOf(r.isCompleted()));
//                    Log.e("json", r.getJsonListUnidadRecorrido());
                    Log.e("fechaIni", r.getFechaInicio());
                    Log.e("code", r.getRecorridoCode());
                    Log.e("fecha", r.getFecha());


                    if(r.getFechaFin() != null)
                        Log.e("FECHAFIN", r.getFechaFin());
                    else
                        Log.e("FECHAFIN", "NULL");
                    r.setUsuario(usuario);
                    r.setVehiculo(uhmc);
                }

                Log.e("size", "not uploaded "+recorridoList.size());
                Call<Void> uploadAll = MedidorApiAdapter.getApiService().postRecorridosBulk(Constantes.CONTENT_TYPE_JSON ,
                        recorridoList, placa);

                uploadAll.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.isSuccessful()) {

                            for (Recorrido r: recorridoList) {
                                deleteUploadedUnidadRecorridos(r.getListUnidadRecorrido());
                                if (r.isCompleted()){
                                    Log.e("ID:","IS COMPLETED AND READY FOR UPDATE " + r.getId());
                                    r.setUploaded(true);
                                    Log.e("ID:","it is supposed It HAS BEEN UPDATED " + r.getId());
                                    try {
                                        Log.e("ID","INSIDE TRY CATCH");
                                        int c = daoRecorrido.delete(r);
                                        if(c > 0){
                                            Log.e("THIS", "WAS UPDATED " + r.getId());
                                        }else {
                                            Log.e("THIS", "WAS NOOOOTTTT UPDATED " + r.getId());
                                        }

                                    } catch (SQLException e) {
                                        Toast.makeText(mainActivity, "ERROR, ACTUALIZANDO RECORRIDOS EN LOCAL.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }else {
                                    Log.e("THIS", "IS NOT COMPLETED " + r.getId());
                                }
                            }
                        }else {
                            Log.e("RESPPONSE", "IS NOT SUCCESSFUL");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(mainActivity, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN.", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR 3", t.getMessage());
                    }
                });

            } else {
                Log.e("ERR","LA LISTA DE RECORRIDOS ES NULL O SU SIZE ES 0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Recorrido getCurrentUnCompleedRecorrido(String fecha) {

        Recorrido recorrido = null;

        try {
            Dao<Recorrido, Integer> dao = helper.getDaoRecorridos();

            List<Recorrido> recorridoList = dao.queryForEq("fecha", fecha);

            if (recorridoList != null && recorridoList.size() > 0)
                recorrido = recorridoList.get(0);

        } catch (SQLException | IndexOutOfBoundsException e) {

            e.printStackTrace();
        }
        return recorrido;
    }

    public void continueCurrentRecorrido(Recorrido recorrido){

        this.recorrido = recorrido;
        iniciarRecorrido();
        this.listUnidadRecorrido = new ArrayList<>();
        try {
            daoRecorrido = helper.getDaoRecorridos();
            daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
     //       daoRecorrido.create(recorrido);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contUpdateRecorrido = 0;

        Toast.makeText(mainActivity, "CONTINUANDO RECORRIDO", Toast.LENGTH_SHORT).show();

    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void deleteAllRecorridos(){

        try {
            List<Recorrido> recorridosList = daoRecorrido.queryForAll();
            if (recorridosList != null && recorridosList.size() > 0)
                daoRecorrido.delete(recorridosList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUploadedUnidadRecorridos(List<UnidadRecorrido> listUnidadRecorrido){

        try {
            Log.e("5", "deleting u recorridos");
            this.daoUnidadRecorrido.delete(listUnidadRecorrido);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void uploadRecorrido() {

        Gson gson = new Gson();
        recorrido.setListUnidadRecorrido(listUnidadRecorrido);
        recorrido.setListUnidadRecorrido(null);
        //recorrido.setId(null);

        Call<String> callRegisterRecorrido =
                MedidorApiAdapter.getApiService().postRegisterRecorrido(Constantes.CONTENT_TYPE_JSON, recorrido);

        callRegisterRecorrido.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                //se obtiene el id del recorrido
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

    public boolean isModelHasTwoTanks() {
        return modelHasTwoTanks;
    }

    public void setModelHasTwoTanks(boolean modelHasTwoTanks) {
        this.modelHasTwoTanks = modelHasTwoTanks;
    }

    public void pushTanqueada(Tanqueadas tanqueada) {
        recorrido.getListTanqueadas().add(tanqueada);
    }
}
