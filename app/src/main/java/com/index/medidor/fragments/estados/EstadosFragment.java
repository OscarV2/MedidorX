package com.index.medidor.fragments.estados;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.EstadosAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estados;
import com.index.medidor.model.HistorialEstadoVehiculos;
import com.index.medidor.model.Vehiculo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.CustomProgressDialog;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EstadosFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MainActivity mainActivity;
    private DataBaseHelper helper;
    private List<Estados> listEstados;
    private Dao<Estados, Integer> daoEstados;
    //private Button btnChangeState;
    private RecyclerView rvVehiculos;
    private EstadosAdapter adapter;
    int idSelectedState;
    private CustomProgressDialog mCustomProgressDialog;

    public EstadosFragment() { }

    public EstadosFragment(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
        this.helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        try {
            this.daoEstados = this.helper.getDaoEstados();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCustomProgressDialog = new CustomProgressDialog(this.mainActivity);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);
    }

    public static EstadosFragment newInstance(String param1, String param2) {
        EstadosFragment fragment = new EstadosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estados, container, false);
        //btnChangeState = view.findViewById(R.id.btn_update_state);
        //btnChangeState.setOnClickListener(v -> {
          //  onChangeStateButtonPressed();
        //});
        rvVehiculos = view.findViewById(R.id.rv_states);
        try {
            listEstados = daoEstados.queryForAll();

            adapter = new EstadosAdapter(listEstados, mainActivity, helper);
            rvVehiculos.setLayoutManager(new LinearLayoutManager(getContext()));
            rvVehiculos.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void onChangeStateButtonPressed(){
        idSelectedState = mainActivity.getMyPreferences().getInt(Constantes.DEFAULT_STATE_ID, 0);
        if(listEstados != null && listEstados.size() > 0){
            if(this.adapter.getmSelectedItem().equals(getDefaultSatePosition())){
                Toast.makeText(mainActivity, "DEBES SELECCIONAR UN ESTADO DIFERENTE PARA ACTUALIZAR.", Toast.LENGTH_SHORT).show();
                return;
            } else{

                HistorialEstadoVehiculos newStateHistoryRecord = new HistorialEstadoVehiculos();
                newStateHistoryRecord.setFechaInicio(Constantes.SDF_FOR_BACKEND.format(new Date()));

                Estados estados = listEstados.get(adapter.mSelectedItem);
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(mainActivity.getMyPreferences().getLong(Constantes.DEFAULT_VEHICLE_ID, 0));

                newStateHistoryRecord.setEstado(estados);
                newStateHistoryRecord.setVehiculo(vehiculo);

                Call<HistorialEstadoVehiculos> callUpdateState = MedidorApiAdapter.getApiService()
                        .postHistorialEstadosSave(Constantes.CONTENT_TYPE_JSON, newStateHistoryRecord);
                mCustomProgressDialog.show("");

                callUpdateState.enqueue(new Callback<HistorialEstadoVehiculos>() {
                    @Override
                    public void onResponse(Call<HistorialEstadoVehiculos> call, Response<HistorialEstadoVehiculos> response) {
                        mCustomProgressDialog.dismiss("");

                        if (response.isSuccessful()){

                            Toast.makeText(mainActivity, "ESTADO ACTUALIZADO CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
                            mainActivity.getMyPreferences().edit().putString(Constantes.DEFAULT_STATE, estados.getNombre()).apply();
                            mainActivity.getMyPreferences().edit().putInt(Constantes.DEFAULT_STATE_ID, estados.getId()).apply();

                            mainActivity.setDefaultState();
                        }
                    }

                    @Override
                    public void onFailure(Call<HistorialEstadoVehiculos> call, Throwable t) {
                        mCustomProgressDialog.dismiss("");
                        Toast.makeText(mainActivity, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private int getDefaultSatePosition(){
        try {
            Estados selectedState = listEstados.stream().filter(state -> state.getId() == idSelectedState).findFirst().get();
            return listEstados.indexOf(selectedState);
        }catch (Exception ex){
            return 0;
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
