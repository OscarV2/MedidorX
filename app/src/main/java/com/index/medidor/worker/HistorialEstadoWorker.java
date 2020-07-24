package com.index.medidor.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.HistorialEstadoVehiculos;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialEstadoWorker extends Worker {

    public HistorialEstadoWorker(@NonNull Context context,
                                 @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        DataBaseHelper helper = OpenHelperManager.getHelper(getApplicationContext(), DataBaseHelper.class);
        try {
            Log.e("DOING","MY work");
            Dao<HistorialEstadoVehiculos, Integer> daoHistorial = helper.getDaoHistorialEstados();
            List<HistorialEstadoVehiculos> lHistorialEstados = daoHistorial.queryForEq("uploaded", false);

            if (lHistorialEstados != null && lHistorialEstados.size() > 0) {
                HistorialEstadoVehiculos newStateHistoryRecord = lHistorialEstados.get(0);
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

                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Result.success();
    }
}
