package com.index.medidor.threads;

import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadUsuarioHasModeloCarro extends Thread {

    private int idUsuario;
    private DataBaseHelper helper;

    public DownloadUsuarioHasModeloCarro(int idUsuario, DataBaseHelper helper) {

        this.idUsuario = idUsuario;
        this.helper = helper;

    }

    @Override
    public void run() {

        Call<List<UsuarioHasModeloCarro>> callGetAllUsuarioHasModelos  = MedidorApiAdapter.getApiService().getUsuarioHasModeloCarros(String.valueOf(idUsuario));
        callGetAllUsuarioHasModelos.enqueue(new Callback<List<UsuarioHasModeloCarro>>() {
            @Override
            public void onResponse(Call<List<UsuarioHasModeloCarro>> call, Response<List<UsuarioHasModeloCarro>> response) {

                try {
                    Dao<UsuarioHasModeloCarro, Integer> dao = helper.getDaoUsuarioHasModeloCarros();

                    if(response.body() != null && response.body().size() > 0) {

                        dao.create(response.body());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioHasModeloCarro>> call, Throwable t) {

            }
        });
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public DataBaseHelper getHelper() {
        return helper;
    }

    public void setHelper(DataBaseHelper helper) {
        this.helper = helper;
    }
}
