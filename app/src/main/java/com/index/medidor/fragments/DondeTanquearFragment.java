package com.index.medidor.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.index.medidor.R;
import com.index.medidor.activities.EstacionesActivity;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.EstacionesAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estaciones;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DondeTanquearFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DondeTanquearFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DondeTanquearFragment extends Fragment {

    private SharedPreferences myPreferences;
    private List<Estaciones> listEstaciones;
    private DataBaseHelper helper;

    private OnFragmentInteractionListener mListener;

    public DondeTanquearFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DondeTanquearFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DondeTanquearFragment newInstance(String param1, String param2) {
        DondeTanquearFragment fragment = new DondeTanquearFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_donde_tanquear, container, false);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        helper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);

        listEstaciones = new ArrayList<>();
        try {
            getAllStations();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RecyclerView rv = v.findViewById(R.id.rv_estaciones);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        RecyclerView.Adapter adapter = new EstacionesAdapter(listEstaciones, getActivity());
        rv.setAdapter(adapter);
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

    private void getAllStations() throws SQLException {

        final Dao<Estaciones, Integer> dao = helper.getDaoEstaciones();

        listEstaciones = dao.queryForAll();
    }
}
