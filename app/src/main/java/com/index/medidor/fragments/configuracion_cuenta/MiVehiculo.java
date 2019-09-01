package com.index.medidor.fragments.configuracion_cuenta;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MiVehiculo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MiVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiVehiculo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Spinner spMarca;
    private Spinner spLinea;
    private Spinner spAnio;
    private EditText edtPlaca;
    // TODO: Rename and change types of parameters
    private MainActivity mainActivity;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MiVehiculo() {
        // Required empty public constructor
    }

    public MiVehiculo(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MiVehiculo.
     */
    // TODO: Rename and change types and number of parameters
    public static MiVehiculo newInstance(String param1, String param2) {
        MiVehiculo fragment = new MiVehiculo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mi_vehiculo, container, false);
        spAnio = v.findViewById(R.id.sp_anio_mi_vehiculo_nuevo);
        edtPlaca = v.findViewById(R.id.edt_placa_mi_vehiculo_nuevo);
        spLinea = v.findViewById(R.id.sp_linea_mi_vehiculo_nuevo);
        spMarca = v.findViewById(R.id.sp_marca_mi_vehiculo_nuevo);

        spAnio.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                Constantes.getYearsModelsCars()));

        try {
            spMarca.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                    getAllMarcasNames()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private String[] getAllMarcasNames() throws SQLException {

        DataBaseHelper helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        final Dao<MarcaCarros, Integer> dao = helper.getDaoMarcas();

        List<MarcaCarros> listMarcas = dao.queryForAll();
        if (listMarcas!= null && listMarcas.size() > 0){

            Log.e("marcas",String.valueOf(listMarcas.size()));
        }
        String[] marcas = new String[listMarcas.size()];

        for (int i = 0; i < listMarcas.size(); i++ ) {

            marcas[i] = listMarcas.get(i).getNombre();
        }

        return  marcas;
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
