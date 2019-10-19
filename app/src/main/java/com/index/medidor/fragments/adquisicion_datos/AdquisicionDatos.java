package com.index.medidor.fragments.adquisicion_datos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.BluetoothDeviceAdapter;
import com.index.medidor.bluetooth.SpBluetoothDevice;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.bluetooth.BluetoothHelper;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdquisicionDatos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdquisicionDatos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdquisicionDatos extends Fragment {

    private Spinner spMarca;
    private EditText edtLinea;
    private Spinner spAnio;
    private Spinner spCombustible;
    private Spinner spBluetoothDevices;

    private EditText edtGalIngresados;
    private int estadoAdquicision;
    private int idMarca = 0, spBluetoothCheck = 0;
    private List<MarcaCarros> marcaCarrosList;
    private DataBaseHelper helper;
    private boolean acquisitionStarted;

    private Button btnAdquisicion;
    private Button btnRegistrar;

    private MainActivity mainActivity;

    private ModeloCarros modeloCarros;

    private JsonObject jsonMuestreo;

    private OnFragmentInteractionListener mListener;
    private Timer mTimer1;
    private Handler mHandler;
    private BluetoothHelper bluetoothHelper;
    private double galIngresados;
    private List<Integer> keyArraysAdq;

    public AdquisicionDatos() {
        // Required empty public constructor
    }

    public AdquisicionDatos(MainActivity mainActivity) {

        //this.bluetoothHelper = new BluetoothHelper();
        this.mainActivity = mainActivity;
        this.keyArraysAdq = new ArrayList<>();
        this.helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
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
        spAnio = v.findViewById(R.id.sp_anio_adq_modelo);
        spCombustible = v.findViewById(R.id.edt_combustible_adq_modelo);
        edtGalIngresados = v.findViewById(R.id.edt_gal_ingresados_modelo);
        edtLinea = v.findViewById(R.id.edt_linea_adq_modelo);
        spMarca = v.findViewById(R.id.sp_marca_adq_modelo);
        spBluetoothDevices = v.findViewById(R.id.sp_select_bluetooth_device);

        btnAdquisicion = v.findViewById(R.id.btn_datos_adq_correctamente);
        btnRegistrar = v.findViewById(R.id.btn_registrar_adq_correctamente);
        estadoAdquicision = 0;
        init();
        initSpBluetoothDevices();
        acquisitionStarted = false;

        return v;
    }

    private void init() {

        btnRegistrar.setEnabled(false);
        btnRegistrar.setVisibility(View.GONE);
        btnAdquisicion.setEnabled(false);

        modeloCarros = new ModeloCarros();

        spAnio.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                Constantes.getYearsModelsCars()));

        spCombustible.setAdapter( ArrayAdapter.createFromResource(mainActivity,
                R.array.tipos_combustibles, android.R.layout.simple_spinner_item));

        try {

            spMarca.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                    Constantes.getAllMarcasNames(helper)));

            marcaCarrosList = helper.getDaoMarcas().queryForAll();
            idMarca = 1;
            spMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    idMarca = marcaCarrosList.get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
        edtLinea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (estadoAdquicision == 0){

                    if (s.length() > 0){
                        btnAdquisicion.setEnabled(true);
                        btnAdquisicion.setBackgroundColor(mainActivity.getResources().getColor(R.color.colorPrimaryDark));
                    }else{
                        btnAdquisicion.setEnabled(false);
                        btnAdquisicion.setBackgroundColor(mainActivity.getResources().getColor(R.color.colorPrimaryDark));

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnAdquisicion.setOnClickListener(v -> {

            if (!acquisitionStarted){

                iniciarAdq();

                btnAdquisicion.setText(R.string.finalizarAdqModelo);
                estadoAdquicision = 1;

            }else if(estadoAdquicision == 1 && acquisitionStarted) {

                finalizarAdq();

            }

        });
        btnRegistrar.setOnClickListener(v -> {

            String val = edtGalIngresados.getText().toString();

            if(val.length() == 0){

                Toast.makeText(mainActivity, "Debe ingresar un valor de galones ingresados válido", Toast.LENGTH_SHORT).show();

                edtGalIngresados.requestFocus();

            }else{
                galIngresados = Double.valueOf(val);

                if (galIngresados < 1){
                    Toast.makeText(mainActivity, "Debe ingresar un valor de galones ingresados válido", Toast.LENGTH_SHORT).show();

                    edtGalIngresados.requestFocus();
                }else{
                        guardarAdq();
                }
            }
        });
    }

    private void iniciarAdq(){

        if (mainActivity.getBtSocket() != null){

            acquisitionStarted = true;
            BluetoothHelper.setAdqProcess(true);

        }else{
            Toast.makeText(mainActivity, "ASEGURESE DE QUE EL DISPOSITIVO INNDEX ESTA CONECTADO.", Toast.LENGTH_SHORT).show();
        }

    }

    private void finalizarAdq(){

        btnAdquisicion.setText(R.string.datosAdqModeloOk);
        btnAdquisicion.setEnabled(false);
        btnRegistrar.setEnabled(true);
        btnRegistrar.setBackgroundColor(mainActivity.getResources().getColor(R.color.colorPrimaryDark));
        btnRegistrar.setVisibility(View.VISIBLE);
        estadoAdquicision = 0;
        acquisitionStarted = false;

    }

    private void guardarAdq() {

        Gson gson = new Gson();

        JsonObject jsonArrayKeys = new JsonObject();

        jsonArrayKeys.add("galones", gson.toJsonTree(keyArraysAdq));

        modeloCarros.setMuestreo( gson.toJson(jsonArrayKeys));
        modeloCarros.setLinea(edtLinea.getText().toString());
        modeloCarros.setGalones(this.galIngresados);
        if (idMarca == 0){
            idMarca = 1;
        }
        modeloCarros.setIdMarca(this.idMarca);

        Call<ModeloCarros> callRegistrarModelo = MedidorApiAdapter.getApiService()
                .postRegisterModelo(Constantes.CONTENT_TYPE_JSON, modeloCarros);

        callRegistrarModelo.enqueue(new Callback<ModeloCarros>() {
            @Override
            public void onResponse(Call<ModeloCarros> call, Response<ModeloCarros> response) {

                if(response.isSuccessful()){

                    DataBaseHelper helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);

                    try {
                        Dao<ModeloCarros, Integer> daoModeloCarros = helper.getDaoModelos();
                        daoModeloCarros.create(modeloCarros);
                        resetAdq();

                        Toast.makeText(mainActivity, "Modelo registrado exitosamente", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{

                    Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR LA ADQUISICIÓN", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModeloCarros> call, Throwable t) {

                Toast.makeText(mainActivity, "ERROR " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void resetAdq(){

        btnAdquisicion.setEnabled(false);
        btnRegistrar.setEnabled(false);
        //btnRegistrar.setVisibility(View.GONE);
        btnRegistrar.setBackgroundColor(mainActivity.getResources().getColor(R.color.textSecond));
        btnAdquisicion.setBackgroundColor(mainActivity.getResources().getColor(R.color.textSecond));

    }

    private void initSpBluetoothDevices() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<SpBluetoothDevice> spBluetoothDevicesList = new ArrayList<>();
        SpBluetoothDevice spBluetoothDevice = null;
        if (bluetoothAdapter.isEnabled()){

            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                spBluetoothDevice = new SpBluetoothDevice(device.getName(), device.getAddress());

                spBluetoothDevicesList.add(spBluetoothDevice);

            }
        }

        if (spBluetoothDevicesList.size() > 0){

            BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(mainActivity, spBluetoothDevicesList);
            spBluetoothDevices.setAdapter(adapter);

            spBluetoothDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    /*TODO: si ya se esta haciendo adquisicion no se puede escoger otro bluetooth
                     */

                    spBluetoothCheck++;
                    if(!acquisitionStarted && spBluetoothCheck > 1) {

                        Toast.makeText(mainActivity, "CONECTANDO...", Toast.LENGTH_SHORT).show();

                        mainActivity.initAdq(spBluetoothDevicesList.get(position).getAddress());

                        spBluetoothDevices.setEnabled(false);

                    } else {


                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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


    public void getBluetoothData(int dato) {

        if (acquisitionStarted){

            Log.e("tu adqDato", String.valueOf(dato));
            keyArraysAdq.add(dato);

        }
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
