package com.index.medidor.fragments.adquisicion_datos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.model.ModeloCarros;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdquisicionDatos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdquisicionDatos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdquisicionDatos extends Fragment {

    private EditText edtMarca;
    private EditText edtLinea;
    private EditText edtAnio;
    private EditText edtCombustible;
    private EditText edtGalIngresados;

    private Button btnAdquisicion;
    private Button btnRegistrar;

    private MainActivity mainActivity;

    private ModeloCarros modeloCarros;

    private OnFragmentInteractionListener mListener;

    public AdquisicionDatos() {
        // Required empty public constructor
    }

    public AdquisicionDatos(MainActivity mainActivity) {

        this.mainActivity = mainActivity;

    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdquisicionDatos.
     */
    // TODO: Rename and change types and number of parameters
    public static AdquisicionDatos newInstance(String param1, String param2) {
        AdquisicionDatos fragment = new AdquisicionDatos();
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
        View v = inflater.inflate(R.layout.fragment_adquisicion_datos, container, false);
        edtAnio = v.findViewById(R.id.edt_anio_adq_modelo);
        edtCombustible = v.findViewById(R.id.edt_combustible_adq_modelo);
        edtGalIngresados = v.findViewById(R.id.edt_gal_ingresados_modelo);
        edtLinea = v.findViewById(R.id.edt_linea_adq_modelo);
        edtMarca = v.findViewById(R.id.edt_marca_adq_modelo);

        btnAdquisicion = v.findViewById(R.id.btn_datos_adq_correctamente);
        btnRegistrar = v.findViewById(R.id.btn_registrar_adq_correctamente);

        modeloCarros = new ModeloCarros();

        return v;
    }

    private void iniciarAdq(){



    }

    private void finalizarAdq(){

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
}
