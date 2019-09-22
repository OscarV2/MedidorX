package com.index.medidor.fragments.configuracion_cuenta;

        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;

        import androidx.annotation.RequiresApi;
        import androidx.fragment.app.Fragment;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.index.medidor.R;
        import com.index.medidor.activities.MainActivity;
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

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

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
    private List<MarcaCarros> listMarcas;
    private List<ModeloCarros> listModeloCarros;
    private DataBaseHelper helper;
    private String[] marcas;
    private UsuarioHasModeloCarro nuevoUsuarioHasModeloCarro;
    private TextView tvAgregarVehiculo;
    private ModeloCarros modeloCarros;

    int idMarca;
    private String linea = "";

    private Dao<MarcaCarros, Integer> daoMarcaCarros;
    private Dao<UsuarioHasModeloCarro, Integer> daoUsuarioModeloCarros;

    // TODO: Rename and change types of parameters
    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    public MiVehiculo() {
        // Required empty public constructor
    }

    public MiVehiculo(MainActivity mainActivity) {

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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mi_vehiculo, container, false);
        spAnio = v.findViewById(R.id.sp_anio_mi_vehiculo_nuevo);
        edtPlaca = v.findViewById(R.id.edt_placa_mi_vehiculo_nuevo);
        spLinea = v.findViewById(R.id.sp_linea_mi_vehiculo_nuevo);
        spMarca = v.findViewById(R.id.sp_marca_mi_vehiculo_nuevo);
        tvAgregarVehiculo = v.findViewById(R.id.tv_agregar_vehiculo);

        tvAgregarVehiculo.setOnClickListener(v1 -> {

            guardarVehiculo();
        });

        spAnio.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                Constantes.getYearsModelsCars()));

        try {
            spMarca.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
            nuevoUsuarioHasModeloCarro.setBluetoothMac("00:21:13:00:BF:AD");
            nuevoUsuarioHasModeloCarro.setBluetoothNombre("INNDEX");

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

    private void checkForInndexDevice(){


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
