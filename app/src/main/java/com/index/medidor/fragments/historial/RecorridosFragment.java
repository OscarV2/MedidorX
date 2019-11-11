package com.index.medidor.fragments.historial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.adapter.RecorridosAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.UnidadRecorrido;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecorridosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recorridos, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_recorridos);

        List<Recorrido> listRecorridos = new ArrayList<>();
        Collection<UnidadRecorrido> listUnidades= new ArrayList<>();

        DataBaseHelper helper = OpenHelperManager.getHelper(getContext(), DataBaseHelper.class);

        //if(listRecorridos.size() > 0) {
        //}

        try {
            Dao<Recorrido, Integer> recorridoDao = helper.getDaoRecorridos();
            Dao<UnidadRecorrido, Integer> unidadRecorridos = helper.getDaoUnidadRecorrido();

            listRecorridos = recorridoDao.queryForAll();

            Log.e("SIZE","RECORRIDOS " + listRecorridos.size());
            Log.e("SIZEU","UNIDAD RECORRIDOS " + listUnidades.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        RecyclerView.Adapter adapter = new RecorridosAdapter(getActivity(), listRecorridos);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
