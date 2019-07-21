package com.index.medidor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.index.medidor.R;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estaciones;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;


import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InicioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio);

        final SharedPreferences myPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        try {
            getAllStations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int DURACION_SPLASH = 2000;
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos al Login
                if(myPreferences.getBoolean("sesion",false)){
                    irMain();
                }else{

                    Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
        }, DURACION_SPLASH);
    }

    private void irMain(){
        Intent intent = new Intent(InicioActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getAllStations() throws SQLException {

        DataBaseHelper helper = OpenHelperManager.getHelper(InicioActivity.this, DataBaseHelper.class);
        final Dao<Estaciones, Integer> dao = helper.getDaoEstaciones();
        if (!(dao.queryForAll().size() > 0)){

            Call<List<Estaciones>> callGetStations = MedidorApiAdapter.getApiService().getEstaciones();
            callGetStations.enqueue(new Callback<List<Estaciones>>() {
                @Override
                public void onResponse(@NonNull Call<List<Estaciones>> call, @NonNull Response<List<Estaciones>> response) {

                    if (response.isSuccessful()){
                        List<Estaciones> list = response.body();
                        if (list != null && list.size() > 0) {
                            for (Estaciones e: list) {
                                try {
                                    dao.create(e);
                                } catch (SQLException e1) {
                                    Toast.makeText(InicioActivity.this, "Error en la base de datos.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                        }
                    }else{
                        Toast.makeText(InicioActivity.this, "NO SE PUDIERON DESCARGAR LAS ESTACIONES INTENTALO MAS TARDE.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Estaciones>> call, @NonNull Throwable t) {
                    Toast.makeText(InicioActivity.this, "NO SE PUDIERON DESCARGAR LAS ESTACIONES INTENTALO MAS TARDE.", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}
