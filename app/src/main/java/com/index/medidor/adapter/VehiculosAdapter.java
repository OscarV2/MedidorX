package com.index.medidor.adapter;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.bluetooth.SpBluetoothDevice;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.model.Vehiculo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.EstacionesViewHolder> {

    private List<Vehiculo> items;
    private Context context;
    private String defaultPlaca;
    private MainActivity mainActivity;
    private DataBaseHelper helper;

    private Vehiculo vehiculoSelected;

    public class EstacionesViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlaca;
        public TextView tvModelo;
        public TextView tvMarca;
        public TextView tvLinea;
        public TextView tvBlueTooth;
        public TextView tvHasTwoTanks;
        private RelativeLayout relativeLayout;
        public ImageButton btnChangeBlueTooth;

        public EstacionesViewHolder(View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tv_placa_mi_vehiculo);
            tvModelo = itemView.findViewById(R.id.tv_anio_mi_vehiculo);
            tvMarca = itemView.findViewById(R.id.tv_marca_mi_vehiculo);
            tvLinea =  itemView.findViewById(R.id.tv_linea_mi_vehiculo);
            tvBlueTooth = itemView.findViewById(R.id.tv_bluetooh_vehiculo);
            tvHasTwoTanks = itemView.findViewById(R.id.tv_has_tow_tanks_mi_vehiculo);
            relativeLayout = itemView.findViewById(R.id.rel_mi_vehiculo);
            btnChangeBlueTooth = itemView.findViewById(R.id.img_change_bluetooth);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

            tvPlaca.setTypeface(light);
            tvMarca.setTypeface(light);
            tvModelo.setTypeface(light);
            tvLinea.setTypeface(light);
            tvBlueTooth.setTypeface(light);
            tvHasTwoTanks.setTypeface(light);

            itemView.setOnClickListener(view -> {
            });

            btnChangeBlueTooth.setOnClickListener(v ->{
                showBlueToothList();
                int i = getLayoutPosition();
                vehiculoSelected = items.get(i);
                Gson gson = new Gson();
                Log.e("11", gson.toJson(vehiculoSelected));
            });
        }
    }

    private void showBlueToothList( ) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_pick_bluetooth);
        dialog.setTitle("Actualizar bluetooth");

        Spinner spBluetoothDevices = dialog.findViewById(R.id.sp_pick_bluetooth);
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

            BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(context, spBluetoothDevicesList);
            spBluetoothDevices.setAdapter(adapter);

            spBluetoothDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    vehiculoSelected.setBluetoothMac(spBluetoothDevicesList.get(position).getAddress());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    vehiculoSelected.setBluetoothMac(spBluetoothDevicesList.get(0).getAddress());
                }
            });
        }

        Button btnUpdateBluetooth = dialog.findViewById(R.id.btn_update_bluetooth_selected);
        btnUpdateBluetooth.setOnClickListener(v -> {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            updateVehiculo();

        });
        dialog.show();
    }

    public VehiculosAdapter(List<Vehiculo> items, Context context, DataBaseHelper helper) {
        this.items = items;
        this.context = context;
        this.mainActivity = (MainActivity)context;
        this.helper = helper;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        vehiculoSelected = null;
        defaultPlaca = sharedPreferences.getString(Constantes.DEFAULT_PLACA, "");
    }

    @Override
    public EstacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_vehiculo,parent,false);
        return new EstacionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EstacionesViewHolder holder, int position) {
        holder.tvLinea.setText(items.get(position).getLinea());
        holder.tvMarca.setText(items.get(position).getMarca());
        holder.tvModelo.setText(items.get(position).getAnio());
        holder.tvBlueTooth.setText(items.get(position).getBluetoothMac());
        holder.tvPlaca.setText(items.get(position).getPlaca());

        if(items.get(position).getHasTwoTanks()){

            holder.tvHasTwoTanks.setText("#Tanques: 2");
        }else {
            holder.tvHasTwoTanks.setText("#Tanques: 1");
        }

        if(items.get(position).getPlaca().equals(defaultPlaca)){
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow_soft));
        }
    }

    private void updateVehiculo() {

        try {
            Integer id = (int)(mainActivity.getMyPreferences().getLong(Constantes.DEFAULT_UHMC_ID, 0));
            Dao<Vehiculo, Integer> dao = helper.getDaoUsuarioHasModeloCarros();
            Vehiculo uhmc = dao.queryForId(id);

            ModeloCarros modeloCarros = new ModeloCarros();
            modeloCarros.setId((int)mainActivity.getMyPreferences().getLong("defaultModeloCarroId",0));
            Gson gson = new Gson();
            vehiculoSelected.setId(uhmc.getId());
            vehiculoSelected.setBluetoothNombre(uhmc.getBluetoothNombre());
            vehiculoSelected.setModeloCarros(modeloCarros);
            vehiculoSelected.setUsuariosId(uhmc.getUsuariosId());
            Log.e("UHMC", gson.toJson(vehiculoSelected));

            Call<Vehiculo> callUpdateUsuariosHasModeloCarro = MedidorApiAdapter.getApiService()
                    .putUpdateUsuarioHasModeloCarro(Constantes.CONTENT_TYPE_JSON ,
                            vehiculoSelected);

            callUpdateUsuariosHasModeloCarro.enqueue(new Callback<Vehiculo>() {
                @Override
                public void onResponse(Call<Vehiculo> call, Response<Vehiculo> response) {

                    if(response.isSuccessful()) {

                        ((MainActivity)context).upateDefaultVehicle(vehiculoSelected);
                        notifyDataSetChanged();
                        Toast.makeText(mainActivity, "VEHÍCULO ACTUALIZADO DE MANERA EXITOSA.", Toast.LENGTH_SHORT).show();
                        //btnUpdateUhmc.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<Vehiculo> call, Throwable t) {
                    Toast.makeText(mainActivity, "NO SE PUDO ACTUALIZAR EL VEHÍCULO.", Toast.LENGTH_SHORT).show();
                    Log.e("ERR", t.getLocalizedMessage());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
