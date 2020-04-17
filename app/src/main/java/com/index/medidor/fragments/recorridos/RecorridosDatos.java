package com.index.medidor.fragments.recorridos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.RecorridosDataAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class RecorridosDatos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MainActivity mainActivity;
    private DataBaseHelper helper;
    private Dao<Recorrido, Integer> daoRecorrido;
    private List<Recorrido> lRecorridos;

    private RecyclerView rvRecorridos;

    private OnFragmentInteractionListener mListener;

    public RecorridosDatos() {
    }

    public RecorridosDatos(MainActivity activity) {
        this.mainActivity = activity;
        this.helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        try {
            daoRecorrido = this.helper.getDaoRecorridos();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static RecorridosDatos newInstance(String param1, String param2) {
        RecorridosDatos fragment = new RecorridosDatos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_recorridos_datos, container, false);
        rvRecorridos = view.findViewById(R.id.rv_trips_data);

        try {
            lRecorridos = daoRecorrido.queryForAll();
            lRecorridos.forEach(r -> Log.e("fe", r.getFechaInicio().toString()));
            RecorridosDataAdapter adapter = new RecorridosDataAdapter( mainActivity, lRecorridos, helper);
            rvRecorridos.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRecorridos.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return view;
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
