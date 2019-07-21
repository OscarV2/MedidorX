package com.index.medidor.fragments.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.adapter.TanqueadasAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Tanqueadas;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TanqueadasFragment extends Fragment {


    public TanqueadasFragment(){

    }

    private List<Tanqueadas> items;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tanqueadas, container, false);

        items = new ArrayList<>();
        try {
            getAllTanqueadas();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvTanqueadas);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        RecyclerView.Adapter adapter = new TanqueadasAdapter(items, getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getAllTanqueadas() throws SQLException {

        DataBaseHelper helper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
        final Dao<Tanqueadas, Integer> dao = helper.getDaoTanqueadas();

        items = dao.queryForAll();
    }

}
