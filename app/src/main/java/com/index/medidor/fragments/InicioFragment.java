package com.index.medidor.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.CombustibleActivity;
import com.index.medidor.activities.MainActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InicioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MainActivity mainActivity;
    private Typeface bold ;
    private FloatingActionButton fabRuta;

    private Button btnRecorrido;
    private TextView tvGalones,tvDistancia;
    boolean estado = false;
    private Timer mTimer1;
    private Handler mHandler;
    private double combustibleInicial, galonesPerdidos;
    private Recorridos recorrido;

    public InicioFragment() {
        // Required empty public constructor
    }
    public InicioFragment(Typeface bold, MainActivity mainActivity) {

        this.mainActivity = mainActivity;
        this.bold = bold;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void drawRouteToStation(){

        mainActivity.getMapService().drawSationRoute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inicio, container, false);

        //Button btnTanquear = v.findViewById(R.id.btnTanquear);

        Typeface light=Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(),"fonts/Roboto-Light.ttf");
        Typeface bold=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");

        tvGalones = v.findViewById(R.id.tvGalones);
        tvGalones.setTypeface(light);
        tvGalones.setText(getString(R.string.txt_galones, 0.0));

        tvDistancia = v.findViewById(R.id.tvDistancia);
        tvDistancia.setTypeface(light);
        tvDistancia.setText(getString(R.string.txt_distancia, 0.0));
/*
        btnRecorrido = v.findViewById(R.id.btnRecorrido);
        btnRecorrido.setTypeface(bold);
        btnRecorrido.setOnClickListener(view -> {
            estado = !estado;
            if(estado){
                btnRecorrido.setText(R.string.detener2);

                combustibleActivity.initLocationSettings();
                combustibleActivity.iniciarRecorrido();
                iniciarMedicion();
            }else{
                btnRecorrido.setText(R.string.iniciar2);

                combustibleActivity.detenerRecorrido();
                detenerMedicion();
            }
        });
*/
        recorrido = new Recorridos();
//        combustibleInicial = combustibleActivity.getNivelCombustible();
        galonesPerdidos = 0;
        mHandler = new Handler();

        //btnTanquear.setTypeface(bold);
        //btnTanquear.setOnClickListener(v1 -> {
            //newRuta();
        //    mainActivity.irDondeTanquear();
        //});
        FloatingActionButton fabUbicacion = v.findViewById(R.id.fabUbicacion);
        fabUbicacion.setOnClickListener(v2 -> this.mainActivity.getMapService().mostrarUbicacion());

        fabRuta = v.findViewById(R.id.fabRuta);

        fabRuta.setOnClickListener(v1 -> this.drawRouteToStation());

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setBold(Typeface bold) {
        this.bold = bold;
    }

    public FloatingActionButton getFabRuta() {
        return fabRuta;
    }

    private void iniciarMedicion(){
        mTimer1 = new Timer();

        TimerTask mTt1 = new TimerTask() {

            public void run() {
                mHandler.post(() -> {
                    Log.e("Cantidad", "Combustible " + mainActivity.getNivelCombustible());
                    double combustibleActual = mainActivity.getNivelCombustible();

                    if (combustibleActual < combustibleInicial){

                        galonesPerdidos = galonesPerdidos + (combustibleInicial - mainActivity.getNivelCombustible());
                        combustibleInicial = combustibleActual;
                        tvGalones.setText(getString(R.string.txt_galones, galonesPerdidos));
                        //tvDistancia.setText(getString(R.string.txt_distancia, mainActivity.getDistanciaRecorrida()));
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
        //recorrido.setRutas(mainActivity.getRutas());
        //recorrido.setIdUsuario(mainActivity.getIdUsuario());
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
