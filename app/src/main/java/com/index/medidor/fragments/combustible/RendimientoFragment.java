package com.index.medidor.fragments.combustible;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.CombustibleActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorridos;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RendimientoFragment extends Fragment {

    private Button btnRecorrido;
    private TextView tvGalones,tvDistancia;
    boolean estado = false;
    private CombustibleActivity combustibleActivity;
    private Timer mTimer1;
    private Handler mHandler;
    private double combustibleInicial, galonesPerdidos;
    private Recorridos recorrido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_rendimiento, container, false);
        //combustibleActivity = (CombustibleActivity) getActivity();

        Typeface light=Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(),"fonts/Roboto-Light.ttf");
        Typeface bold=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");

        tvGalones = view.findViewById(R.id.tvGalones);
        tvGalones.setTypeface(light);
        tvGalones.setText(getString(R.string.txt_galones, 0.0));

        tvDistancia = view.findViewById(R.id.tvDistancia);
        tvDistancia.setTypeface(light);
        tvDistancia.setText(getString(R.string.txt_distancia, 0.0));

        btnRecorrido = view.findViewById(R.id.btnRecorrido);
        btnRecorrido.setTypeface(bold);
        btnRecorrido.setOnClickListener(v -> {
            estado = !estado;
            if(estado){
                btnRecorrido.setText(R.string.detener2);
                /*
                combustibleActivity.initLocationSettings();
                combustibleActivity.iniciarRecorrido();
                iniciarMedicion();*/
            }else{
                btnRecorrido.setText(R.string.iniciar2);
                /*
                combustibleActivity.detenerRecorrido();
                detenerMedicion();*/
            }
        });

        recorrido = new Recorridos();
//        combustibleInicial = combustibleActivity.getNivelCombustible();
        galonesPerdidos = 0;
        mHandler = new Handler();
        return view;
    }

    private void iniciarMedicion(){
        mTimer1 = new Timer();

        TimerTask mTt1 = new TimerTask() {

            public void run() {
                mHandler.post(() -> {
                    Log.e("Cantidad", "Combustible " + combustibleActivity.getNivelCombustible());
                    double combustibleActual = combustibleActivity.getNivelCombustible();

                    if (combustibleActual < combustibleInicial){

                        galonesPerdidos = galonesPerdidos + (combustibleInicial - combustibleActivity.getNivelCombustible());
                        combustibleInicial = combustibleActual;
                        tvGalones.setText(getString(R.string.txt_galones, galonesPerdidos));
                        tvDistancia.setText(getString(R.string.txt_distancia, combustibleActivity.getDistanciaRecorrida()));
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        recorrido.setHoraInicio(df.format(new Date()));
    }

    private void detenerMedicion(){

        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
        recorrido.setGalonesPerdidos(galonesPerdidos);
        recorrido.setRutas(combustibleActivity.getRutas());
        recorrido.setIdUsuario(combustibleActivity.getIdUsuario());
        recorrido.setPosiciones();
        recorrido.setDistancia();
        Gson gson = new Gson();
        Log.e("Rendimi", gson.toJson(gson.toJsonTree(recorrido)));
        guardarRecorrido();
    }

    private void guardarRecorrido(){
        DataBaseHelper helper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);

        try {
            Dao<Recorridos, Integer> daoRecorridos = helper.getDaoRecorridos();
            daoRecorridos.create(recorrido);
            recorrido = new Recorridos();
            //Toast.makeText(combustibleActivity, "Recorrido registrado de manera exitosa.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
