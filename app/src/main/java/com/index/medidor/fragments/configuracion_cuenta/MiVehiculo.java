package com.index.medidor.fragments.configuracion_cuenta;

        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;

        import androidx.annotation.RequiresApi;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

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
        import com.index.medidor.adapter.VehiculosAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MiVehiculo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MiVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiVehiculo extends Fragment {


    // TODO: Rename and change types of parameters
    private MainActivity mainActivity;

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

        RecyclerView rvVehiculos = v.findViewById(R.id.rv_mis_vehiculos);
        DataBaseHelper helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);
        List<UsuarioHasModeloCarro> usuarioHasModeloCarroList;
        try {
            Dao<UsuarioHasModeloCarro, Integer> daoUsuarioHasMOdeloCarro = helper.getDaoUsuarioHasModeloCarros();
            usuarioHasModeloCarroList = daoUsuarioHasMOdeloCarro.queryForAll();

            Log.e("SIZE", String.valueOf(usuarioHasModeloCarroList.size()));

            usuarioHasModeloCarroList.forEach(a -> {

                Log.e("1", String.valueOf(a.getHasTwoTanks()));

            });

            VehiculosAdapter adapter = new VehiculosAdapter(usuarioHasModeloCarroList, mainActivity);

            rvVehiculos.setLayoutManager(new LinearLayoutManager(getContext()));
            rvVehiculos.setAdapter(adapter);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
