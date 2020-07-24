package com.index.medidor.fragments.adquisicion_datos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.BluetoothDeviceAdapter;
import com.index.medidor.bluetooth.BluetoothHelper;
import com.index.medidor.bluetooth.SpBluetoothDevice;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.pojo.Flujo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdquisicionDatos extends Fragment {

    private Spinner spMarca;
    private Spinner spAnio;
    private Spinner spCombustible;
    private Spinner spBluetoothDevices;

    private TextView tvDatoNivel;
    private TextView tvDatoFlujo;
    private TextView tvLastData;
    private TextView tvDataQuantity;
    private Button btnAddFluxData;

    private Button btnAdquisicion;
    private Button btnRegistrar;
    private EditText edtLinea;
    private RadioButton rbTieneDosTanques;

    private int estadoAdquicision;
    private int idMarca = 0, spBluetoothCheck = 0;
    private List<MarcaCarros> marcaCarrosList;
    private DataBaseHelper helper;
    private boolean acquisitionStarted;
    private boolean deviceConnected;

    private RelativeLayout rlAdqData;

    private List<Flujo> lFlujo;

    private MainActivity mainActivity;

    private ModeloCarros modeloCarros;

    private int nivel;
    private int volumen;
    private OnFragmentInteractionListener mListener;
    //private Timer mTimer1;
    //private Handler mHandler;
    private BluetoothHelper bluetoothHelper;
    private List<Integer> keyArraysAdq;
    private List<Integer> keyArraysFlux;

    public AdquisicionDatos() {
        // Required empty public constructor
    }

    public AdquisicionDatos(MainActivity mainActivity) {
        //this.bluetoothHelper = new BluetoothHelper();
        this.mainActivity = mainActivity;
        this.keyArraysAdq = new ArrayList<>();
        this.helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        this.lFlujo = new ArrayList<>();
        Flujo flujo = new Flujo(0, 0);
        lFlujo.add(flujo);
    }

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
        edtLinea = v.findViewById(R.id.edt_linea_adq_modelo);
        spMarca = v.findViewById(R.id.sp_marca_adq_modelo);
        spBluetoothDevices = v.findViewById(R.id.sp_select_bluetooth_device);
        rbTieneDosTanques = v.findViewById(R.id.rb_tiene_2_tanques);
        rlAdqData = v.findViewById(R.id.lay_gal_ingresados_modelo);
        rlAdqData.setVisibility(View.GONE);
        tvDatoNivel = v.findViewById(R.id.tv_adquisition_dato_1);
        tvDatoFlujo = v.findViewById(R.id.tv_adq_volumen);
        tvDataQuantity = v.findViewById(R.id.tv_adquisition_data_quantity);
        tvLastData = v.findViewById(R.id.tv_adquisition_last_saved_data);
        btnAdquisicion = v.findViewById(R.id.btn_datos_adq_correctamente);

        btnAddFluxData = v.findViewById(R.id.btn_add_adq_value);
        btnAddFluxData.setOnClickListener(v1 -> {
            addFluxData();
        });

        btnRegistrar = v.findViewById(R.id.btn_registrar_adq_correctamente);
        estadoAdquicision = 0;
        keyArraysFlux = new ArrayList<>();
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
                R.array.tipos_combustibles, android.R.layout.simple_spinner_dropdown_item));

        modeloCarros.setModelo(Constantes.getYearsModelsCars()[0]);
        modeloCarros.setTipoCombustible(spCombustible.getSelectedItem().toString());

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
                public void onNothingSelected(AdapterView<?> parent) {   }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
        edtLinea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }

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
            public void afterTextChanged(Editable s) {  }
        });
        btnAdquisicion.setOnClickListener(v -> {

            if (!deviceConnected)
            {
                Toast.makeText(mainActivity, "NO EXISTE DISPOSITIVO CONECTADO.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!acquisitionStarted){
                rlAdqData.setVisibility(View.VISIBLE);
                iniciarAdq();
                btnAdquisicion.setText(R.string.finalizarAdqModelo);
                estadoAdquicision = 1;

            }else if(estadoAdquicision == 1) {
                finalizarAdq();
            }
        });
        btnRegistrar.setOnClickListener(v -> {
            guardarAdq();
        });

        spAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modeloCarros.setModelo(Constantes.getYearsModelsCars()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                modeloCarros.setModelo(Constantes.getYearsModelsCars()[0]);
            }
        });

        spCombustible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modeloCarros.setTipoCombustible(spCombustible.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        Log.e("1","GUARDANDO ADQ");
        Gson gson = new Gson();

        modeloCarros.setFlujo( gson.toJson(lFlujo));
        if(this.rbTieneDosTanques.isChecked()) {
            modeloCarros.setHasTwoTanks(true);
        }
        modeloCarros.setLinea(edtLinea.getText().toString());
        modeloCarros.setHasTwoTanks(rbTieneDosTanques.isChecked());

        if (idMarca == 0){
            idMarca = 1;
        }
        modeloCarros.setIdMarca(this.idMarca);

        Call<ModeloCarros> callRegistrarModelo = MedidorApiAdapter.getApiService()
                .postRegisterModelo(Constantes.CONTENT_TYPE_JSON, modeloCarros);
        mainActivity.getmCustomProgressDialog().show("");
        callRegistrarModelo.enqueue(new Callback<ModeloCarros>() {
            @Override
            public void onResponse(Call<ModeloCarros> call, Response<ModeloCarros> response) {
                mainActivity.getmCustomProgressDialog().dismiss("");
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
                mainActivity.getmCustomProgressDialog().dismiss("");
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

                    Log.e("item","selected");
                    spBluetoothCheck++;
                    if(!acquisitionStarted && spBluetoothCheck > 1) {

                        Toast.makeText(mainActivity, "CONECTANDO...", Toast.LENGTH_SHORT).show();

                        mainActivity.initAdq(spBluetoothDevicesList.get(position).getAddress());

                        deviceConnected = true;

                    } else {
                        Log.e("item","selected else");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.e("Bluetooth","Nothing selected");
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

    public void getBluetoothData(int... dato) {

        if (acquisitionStarted) {
            nivel = dato[0];
            volumen = dato[1];

            tvDatoNivel.setText(String.format(mainActivity.getResources().getString(R.string.nivel_adquisition), dato[0]));
            tvDatoFlujo.setText(String.format(mainActivity.getResources().getString(R.string.flujo_adquisition), volumen));
        }
    }

    public void addFluxData() {

        Flujo flujo = new Flujo(nivel, volumen);
        lFlujo.add(flujo);

        tvLastData.setText(String.valueOf(flujo.getVolumen()));
        tvDataQuantity.setText(String.valueOf(lFlujo.size()));
    }

    public boolean isDeviceConnected() {
        return deviceConnected;
    }

    public void setDeviceConnected(boolean deviceConnected) {
        if (!deviceConnected) {
            spBluetoothDevices.setEnabled(true);
            Toast.makeText(mainActivity, "NO SE PUDO CONECTAR CON EL DISPOSITIVO.", Toast.LENGTH_SHORT).show();
        }
        this.deviceConnected = deviceConnected;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
