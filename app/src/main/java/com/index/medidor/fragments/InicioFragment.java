package com.index.medidor.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inicio, container, false);

        Button btnTanquear = v.findViewById(R.id.btnTanquear);
        ImageButton btnMenu = v.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(view -> {
            if (mainActivity == null) Log.e("Click","BOTON MENU, MAIN ACTIVITY NULA");
            if (mainActivity.getDrawer() == null) Log.e("Click","BOTON MENU , DRAWER NULO");
            if (mainActivity.getNavigationView() == null) Log.e("Click","BOTON MENU NAVIGATION VIEW NULA");

            mainActivity.getDrawer().openDrawer(mainActivity.getNavigationView());
        });
        btnTanquear.setTypeface(bold);
        btnTanquear.setOnClickListener(v1 -> {
            //newRuta();
            mainActivity.irDondeTanquear();
        });
        FloatingActionButton fabUbicacion = v.findViewById(R.id.fabUbicacion);
        fabUbicacion.setOnClickListener(v2 -> this.mainActivity.mostrarUbicacion());

        fabRuta = v.findViewById(R.id.fabRuta);

        //fabRuta.setOnClickListener(v -> {});

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
}
