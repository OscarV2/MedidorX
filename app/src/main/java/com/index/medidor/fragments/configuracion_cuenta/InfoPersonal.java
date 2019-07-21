package com.index.medidor.fragments.configuracion_cuenta;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoPersonal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoPersonal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoPersonal extends Fragment {

    private EditText edtNombresApellidos;
    private EditText edtCorreo;
    private EditText edtTelefono;
    private EditText edtPaisCiudad;
    private EditText edtPassword;

    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    public InfoPersonal() {
        // Required empty public constructor
    }

    public InfoPersonal(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoPersonal.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoPersonal newInstance(String param1, String param2) {

        return new InfoPersonal();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_personal, container, false);

        edtCorreo = v.findViewById(R.id.edt_ip_correo);
        edtNombresApellidos = v.findViewById(R.id.edt_ip_nombres_apellidos);
        edtPaisCiudad = v.findViewById(R.id.edt_ip_pais_ciudad);
        edtPassword = v.findViewById(R.id.edt_ip_password);
        edtTelefono = v.findViewById(R.id.edt_ip_numero_telefono);

        if (mainActivity != null){
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);

            edtCorreo.setText(myPreferences.getString("email",""));
            edtTelefono.setText(myPreferences.getString("celular",""));
            String nombres = myPreferences.getString("nombres","");
            String apellidos = myPreferences.getString("apellidos","");

            edtNombresApellidos.setText(nombres + " " + apellidos);

        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
