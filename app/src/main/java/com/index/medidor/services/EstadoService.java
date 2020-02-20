package com.index.medidor.services;

import android.content.Context;
import android.widget.Toast;

import com.index.medidor.model.HistorialEstadoVehiculos;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadoService {

    private Context context;

    public EstadoService() {
    }

    public void updateCurrentState(HistorialEstadoVehiculos currenState) {

        Call<HistorialEstadoVehiculos> callUpdateCurrentState = MedidorApiAdapter.getApiService()
                .postHistorialEstadosSave(Constantes.CONTENT_TYPE_JSON, currenState);
        callUpdateCurrentState.enqueue(new Callback<HistorialEstadoVehiculos>() {
            @Override
            public void onResponse(Call<HistorialEstadoVehiculos> call, Response<HistorialEstadoVehiculos> response) {

                if (response.isSuccessful()) {

                    saveNewHistorial(null);

                } else {
                    Toast.makeText(context, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<HistorialEstadoVehiculos> call, Throwable t) {
                Toast.makeText(context, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewHistorial(HistorialEstadoVehiculos newHistorialEstdo) {

        Call<HistorialEstadoVehiculos> callUpdateCurrentState = MedidorApiAdapter.getApiService()
                .postHistorialEstadosSave(Constantes.CONTENT_TYPE_JSON, newHistorialEstdo);
        callUpdateCurrentState.enqueue(new Callback<HistorialEstadoVehiculos>() {
            @Override
            public void onResponse(Call<HistorialEstadoVehiculos> call, Response<HistorialEstadoVehiculos> response) {

                if (response.isSuccessful()) {


                } else {
                    Toast.makeText(context, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<HistorialEstadoVehiculos> call, Throwable t) {
                Toast.makeText(context, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
