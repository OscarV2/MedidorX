package com.index.medidor.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estados;
import com.index.medidor.model.HistorialEstadoVehiculos;
import com.index.medidor.model.UnidadRecorrido;
import com.index.medidor.model.Vehiculo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.index.medidor.utils.Constantes.LIMIT_UNIT_RECORRIDO;

public class UploadRecorridosService {

    private Dao<HistorialEstadoVehiculos, Integer> daoHistorial;
    private Dao<UnidadRecorrido, Integer> daoUnidadRecorrido;
    private DataBaseHelper helper;
    private Context context;
    private boolean inUploadingProccess = false;
    private int totalvaluesForUpload = 0;
    private String placa;
    //private CustomProgressDialog mCustomProgressDialog;

    public UploadRecorridosService(Context context, String placa) {
        this.context = context;
        helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        this.placa = placa;
        try {
            if(daoUnidadRecorrido == null)
                daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
            if(daoHistorial == null)
                daoHistorial = helper.getDaoHistorialEstados();
            //     if(helper == null)
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            whereUR.gt("id", 0);

            pqUR = qbUnidadRecorrido.prepare();
            QueryBuilder<UnidadRecorrido, Integer> qbUnidadRecorridoCount;

            qbUnidadRecorridoCount =
                    daoUnidadRecorrido.queryBuilder();
            qbUnidadRecorridoCount.setCountOf(true);
            PreparedQuery<UnidadRecorrido> pqURCount = qbUnidadRecorridoCount.prepare();
            Where<UnidadRecorrido, Integer> whereURCount = qbUnidadRecorrido.where();
            whereURCount.gt("id", 0);
            List<UnidadRecorrido> lUnidadRecorrido = daoUnidadRecorrido.query(pqUR);
            totalvaluesForUpload = (int) daoUnidadRecorrido.countOf(pqURCount);
            //lUnidadRecorrido.sort(Comparator.comparing(UnidadRecorrido::getId));

            if(lUnidadRecorrido != null && lUnidadRecorrido.size() > 0)
                this.uploadSingleTrip(lUnidadRecorrido);

            List<HistorialEstadoVehiculos> lHistorialEstados = daoHistorial.queryForEq("uploaded", false);
            if (lHistorialEstados != null && lHistorialEstados.size() > 0) {
                HistorialEstadoVehiculos newStateHistoryRecord = lHistorialEstados.get(0);
                newStateHistoryRecord.setVehiculo(new Vehiculo(newStateHistoryRecord.getIdVehiculo()));
                newStateHistoryRecord.setEstado(new Estados(newStateHistoryRecord.getIdEstado()));
                uploadHistorialEstado(newStateHistoryRecord);
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadSingleTrip(List<UnidadRecorrido> listUnidadRecorrido) {
        Call<UnidadRecorrido> uploadAll = MedidorApiAdapter.getApiService().postRecorridosBulk(Constantes.CONTENT_TYPE_JSON,
                listUnidadRecorrido, placa);
        //Toast.makeText(context, "Subiendo PLACA: " + listUnidadRecorrido.size(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Subiendo PLACA: " + this.placa, Toast.LENGTH_LONG).show();

        Log.e("PLACA",this.placa);
        //mCustomProgressDialog.show("");
        uploadAll.enqueue(new Callback<UnidadRecorrido>() {
            @Override
            public void onResponse(Call<UnidadRecorrido> call, Response<UnidadRecorrido> response) {
                //Log.e("RESPONSE", String.valueOf(response.code()));
                if (response.isSuccessful() && response.body() != null) {
                    deleteUploadedUnidadRecorridos(listUnidadRecorrido);
                    if (totalvaluesForUpload > LIMIT_UNIT_RECORRIDO) {
                        uploadAllNotCompletedAndNotUploaded();
                        inUploadingProccess = true;
                    } else
                        inUploadingProccess = false;
                } else {
                    Toast.makeText(context, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("RESPPONSE", "IS NOT SUCCESSFUL " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UnidadRecorrido> call, Throwable t) {
                //mCustomProgressDialog.dismiss("");
                Toast.makeText(context, "ERROR, NO SE PUDO SUBIR TODA LA INFORMACIÓN " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR 3", t.getMessage());
                inUploadingProccess = false;
            }
        });
    }

    private void uploadHistorialEstado(HistorialEstadoVehiculos newStateHistoryRecord){
        Call<HistorialEstadoVehiculos> callUpdateState = MedidorApiAdapter.getApiService()
                .postHistorialEstadosSave(Constantes.CONTENT_TYPE_JSON, newStateHistoryRecord);

        callUpdateState.enqueue(new Callback<HistorialEstadoVehiculos>() {
            @Override
            public void onResponse(Call<HistorialEstadoVehiculos> call, Response<HistorialEstadoVehiculos> response) {
                if (response.isSuccessful()) {
                    newStateHistoryRecord.setUploaded(true);
                    try {
                        daoHistorial.update(newStateHistoryRecord);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<HistorialEstadoVehiculos> call, Throwable t) {
                Log.e("UPLOAD","historial response failed " + t.getMessage());
            }
        });
    }

    private void deleteUploadedUnidadRecorridos(List<UnidadRecorrido> listUnidadRecorrido) {

        try {
            if (listUnidadRecorrido != null && listUnidadRecorrido.size() > 0) {
                for (int i = 0; i < listUnidadRecorrido.size(); i += 400) {
                    //daoUnidadRecorrido.delete(listUnidadRecorrido.get(i));
                    if ((listUnidadRecorrido.size() - i) < 400) {
                        if (listUnidadRecorrido.size() == 1) {
                            daoUnidadRecorrido.delete(listUnidadRecorrido.get(0));
                        } else {
                            daoUnidadRecorrido.delete(listUnidadRecorrido.subList(i, listUnidadRecorrido.size() - 1));
                        }
                    } else
                        daoUnidadRecorrido.delete(listUnidadRecorrido.subList(i, i + 399));
                }
            }
        } catch (SQLException e) {
            Log.e("6.1", "EX " + e.getErrorCode() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
