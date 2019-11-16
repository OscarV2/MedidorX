package com.index.medidor.fragments.configuracion_cuenta;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.BluetoothDeviceAdapter;
import com.index.medidor.bluetooth.SpBluetoothDevice;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoVehiculo extends Fragment {

    private Spinner spMarca;
    private Spinner spLinea;
    private Spinner spAnio;
    private Spinner spBluetoothDevices;
    private EditText edtPlaca;
    private List<MarcaCarros> listMarcas;
    private List<ModeloCarros> listModeloCarros;
    private DataBaseHelper helper;
    private String[] marcas;
    private UsuarioHasModeloCarro nuevoUsuarioHasModeloCarro;
    private UsuarioHasModeloCarro usuarioHasModeloCarroUpdate;
    private TextView tvAgregarVehiculo;
    private TextView tvEditarBluetooth;
    private Button btnUpdateUhmc;
    private ModeloCarros modeloCarros;

    int idMarca;
    private String linea = "";

    private Dao<MarcaCarros, Integer> daoMarcaCarros;
    private Dao<UsuarioHasModeloCarro, Integer> daoUsuarioModeloCarros;

    private MainActivity mainActivity;
    private OnFragmentInteractionListener mListener;

    public NuevoVehiculo() {
        // Required empty public constructor
    }

    public NuevoVehiculo(MainActivity mainActivity){

        this.mainActivity = mainActivity;
        this.listModeloCarros = new ArrayList<>();
        helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        try {
            daoMarcaCarros = helper.getDaoMarcas();
            daoUsuarioModeloCarros = helper.getDaoUsuarioHasModeloCarros();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        nuevoUsuarioHasModeloCarro = new UsuarioHasModeloCarro();
        usuarioHasModeloCarroUpdate = new UsuarioHasModeloCarro();
    }

    public static NuevoVehiculo newInstance(String param1, String param2) {
        NuevoVehiculo fragment = new NuevoVehiculo();
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
        View v = inflater.inflate(R.layout.fragment_nuevo_vehiculo, container, false);

        spAnio = v.findViewById(R.id.sp_anio_mi_vehiculo_nuevo);
        edtPlaca = v.findViewById(R.id.edt_placa_mi_vehiculo_nuevo);
        spLinea = v.findViewById(R.id.sp_linea_mi_vehiculo_nuevo);
        spMarca = v.findViewById(R.id.sp_marca_mi_vehiculo_nuevo);
        tvAgregarVehiculo = v.findViewById(R.id.tv_agregar_vehiculo);
        tvEditarBluetooth = v.findViewById(R.id.tv_edit_bluetooh);
        spBluetoothDevices = v.findViewById(R.id.sp_edit_bluetooth_device);
        btnUpdateUhmc = v.findViewById(R.id.btn_edit_bluetooth_aceptar);
        initSpBluetoothDevices();

        tvAgregarVehiculo.setOnClickListener(v1 -> guardarVehiculo() );

        tvEditarBluetooth.setOnClickListener(v2 ->    {
            spBluetoothDevices.setVisibility(View.VISIBLE);
            btnUpdateUhmc.setVisibility(View.VISIBLE);
        });

        spAnio.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                Constantes.getYearsModelsCars()));

        btnUpdateUhmc.setOnClickListener(v12 -> updateVehiculo());

        spBluetoothDevices.setVisibility(View.GONE);
        btnUpdateUhmc.setVisibility(View.GONE);

        try {
            spMarca.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item,
                    getAllMarcasNames()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        spMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //buscar id de marca por nombre
                //int idMarca = listMarcas.stream().filter( m -> m.getNombre().equals(parent.getSelectedItem().toString()) ).findFirst().get().getId();
                QueryBuilder<MarcaCarros, Integer> queryBuilder = daoMarcaCarros.queryBuilder();
                try {
                    queryBuilder.where().eq("nombre", marcas[position]);
                    List<MarcaCarros> modeloCarrosList = queryBuilder.query();

                    if(modeloCarrosList != null && modeloCarrosList.size() > 0){

                        idMarca = modeloCarrosList.get(0).getId();

                        getModelosByMarca(idMarca);

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spLinea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                linea = listModeloCarros.get(position).getLinea();
                nuevoUsuarioHasModeloCarro.setModelosCarrosId(listModeloCarros.get(position).getId());
                //nuevoUsuarioHasModeloCarro.setValoresAdq(listModeloCarros.get(position).getValoresAdq());
                modeloCarros = listModeloCarros.get(position);
                nuevoUsuarioHasModeloCarro.setModeloCarros(modeloCarros);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;
    }

    private String[] getAllMarcasNames() throws SQLException {

        listMarcas = daoMarcaCarros.queryForAll();
        if (listMarcas!= null && listMarcas.size() > 0){

            Log.e("marcas",String.valueOf(listMarcas.size()));
        }
        marcas = new String[listMarcas.size()];

        for (int i = 0; i < listMarcas.size(); i++ ) {

            marcas[i] = listMarcas.get(i).getNombre();
        }

        return  marcas;
    }

    public void getModelosByMarca(int idMarca){

        Call<List<ModeloCarros>> callListModeloCarros = MedidorApiAdapter.getApiService()
                .getModelosCarrosByMarca(String.valueOf(idMarca));

        callListModeloCarros.enqueue(new Callback<List<ModeloCarros>>() {
            @Override
            public void onResponse(Call<List<ModeloCarros>> call, Response<List<ModeloCarros>> response) {

                if (response.isSuccessful()){

                    listModeloCarros = response.body();
                    if(listModeloCarros != null && listModeloCarros.size() > 0){

                        spLinea.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                                getAllLineasNames(listModeloCarros)));
                        modeloCarros = listModeloCarros.get(0);

                    }else{

                        spLinea.setAdapter(null);
                    }

                }else {

                    spLinea.setAdapter(null);
                }

            }

            @Override
            public void onFailure(Call<List<ModeloCarros>> call, Throwable t) {

                Toast.makeText(mainActivity, "NO SE PUDO OBTENER TODOAS LAS LÍNEAS ASOCIADASA A ESTA MARCA.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void guardarVehiculo(){

        String placa =  edtPlaca.getText().toString();

        if(placa.equals("") || placa.length() < 6){

            edtPlaca.requestFocus();
            Toast.makeText(mainActivity, "La placa no es válida.", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = mainActivity.getMyPreferences().getInt("idUsuario", 0);

        if(idUsuario != 0){

            nuevoUsuarioHasModeloCarro.setUsuariosId(idUsuario);
            nuevoUsuarioHasModeloCarro.setBluetoothMac(mainActivity.getMyPreferences().getString(Constantes.DEFAULT_BLUETOOTH_MAC,""));
            nuevoUsuarioHasModeloCarro.setBluetoothNombre("INNDEX");
            nuevoUsuarioHasModeloCarro.setPlaca(placa);

            Call<ResponseBody> callRegisterUsuariosHasModeloCarro = MedidorApiAdapter.getApiService()
                    .postRegisterUsuarioHasModeloCarro(Constantes.CONTENT_TYPE_JSON ,
                            String.valueOf(idMarca), linea,
                            nuevoUsuarioHasModeloCarro);

            callRegisterUsuariosHasModeloCarro.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.e("responseU", response.message());

                    if(response.isSuccessful()){

                        try {
                            daoUsuarioModeloCarros.create(nuevoUsuarioHasModeloCarro);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(mainActivity, "VEHÍCULO REGISTRADO DE MANERA EXITOSA.", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR EL VEHÍCULO.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR EL VEHÍCULO." + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("reg beh", t.getMessage());
                }
            });
        }else{

            Toast.makeText(mainActivity, "ESTE USUARIO NO EXISTE", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateVehiculo() {

        try {
            Integer id = (int)(mainActivity.getMyPreferences().getLong(Constantes.DEFAULT_UHMC_ID, 0));
            Dao<UsuarioHasModeloCarro, Integer> dao = helper.getDaoUsuarioHasModeloCarros();
            UsuarioHasModeloCarro uhmc = dao.queryForId(id);
            //uhmc.setId(mainActivity.getMyPreferences().getLong(Constantes.DEFAULT_UHMC_ID, 0));

            ModeloCarros modeloCarros = new ModeloCarros();
            modeloCarros.setId((int)mainActivity.getMyPreferences().getLong("defaultModeloCarroId",0));
            Gson gson = new Gson();
            usuarioHasModeloCarroUpdate.setId(uhmc.getId());
            usuarioHasModeloCarroUpdate.setBluetoothNombre(uhmc.getBluetoothNombre());
            usuarioHasModeloCarroUpdate.setModeloCarros(modeloCarros);
            usuarioHasModeloCarroUpdate.setUsuariosId(uhmc.getUsuariosId());
            Log.e("UHMC", gson.toJson(usuarioHasModeloCarroUpdate));

            Call<UsuarioHasModeloCarro> callUpdateUsuariosHasModeloCarro = MedidorApiAdapter.getApiService()
                    .putUpdateUsuarioHasModeloCarro(Constantes.CONTENT_TYPE_JSON ,
                            usuarioHasModeloCarroUpdate);

            callUpdateUsuariosHasModeloCarro.enqueue(new Callback<UsuarioHasModeloCarro>() {
                @Override
                public void onResponse(Call<UsuarioHasModeloCarro> call, Response<UsuarioHasModeloCarro> response) {

                    if(response.isSuccessful()) {

                        mainActivity.resetAll();
                        Toast.makeText(mainActivity, "VEHÍCULO ACTUALIZADO DE MANERA EXITOSA.", Toast.LENGTH_SHORT).show();
                        spBluetoothDevices.setVisibility(View.GONE);
                        btnUpdateUhmc.setVisibility(View.GONE);
                        mainActivity.getMyPreferences().edit().putString(Constantes.DEFAULT_BLUETOOTH_MAC, usuarioHasModeloCarroUpdate.getBluetoothMac()).apply();
                    }

                }

                @Override
                public void onFailure(Call<UsuarioHasModeloCarro> call, Throwable t) {
                    Log.e("ERR", t.getLocalizedMessage());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void initSpBluetoothDevices() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<SpBluetoothDevice> spBluetoothDevicesList = new ArrayList<>();
        SpBluetoothDevice spBluetoothDevice;
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

                    usuarioHasModeloCarroUpdate.setBluetoothMac(spBluetoothDevicesList.get(position).getAddress());

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    usuarioHasModeloCarroUpdate.setBluetoothMac(spBluetoothDevicesList.get(0).getAddress());
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
    private String[] getAllLineasNames(List<ModeloCarros> listModeloCarros) {

        if (listModeloCarros!= null && listModeloCarros.size() > 0){

            Log.e("marcas",String.valueOf(listModeloCarros.size()));
        }
        String[] lineas = new String[listModeloCarros.size()];

        for (int i = 0; i < listModeloCarros.size(); i++ ) {

            lineas[i] = listModeloCarros.get(i).getLinea();
        }

        return  lineas;
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
