package com.index.medidor.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.bluetooth.SpBluetoothDevice;
import com.index.medidor.model.UsuarioHasModeloCarro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.EstacionesViewHolder> {

    private List<UsuarioHasModeloCarro> items;
    private Context context;

    private UsuarioHasModeloCarro usuarioHasModeloCarroSelected;

    public class EstacionesViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlaca;
        public TextView tvModelo;
        public TextView tvMarca;
        public TextView tvLinea;
        public TextView tvBlueTooth;

        public ImageButton btnChangeBlueTooth;

        public EstacionesViewHolder(View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tv_placa_mi_vehiculo);
            tvModelo = itemView.findViewById(R.id.tv_anio_mi_vehiculo);
            tvMarca = itemView.findViewById(R.id.tv_marca_mi_vehiculo);
            tvLinea =  itemView.findViewById(R.id.tv_linea_mi_vehiculo);
            tvBlueTooth = itemView.findViewById(R.id.tv_bluetooh_vehiculo);
            btnChangeBlueTooth = itemView.findViewById(R.id.img_change_bluetooth);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

            tvPlaca.setTypeface(light);
            tvMarca.setTypeface(light);
            tvModelo.setTypeface(light);
            tvLinea.setTypeface(light);
            tvBlueTooth.setTypeface(light);

            itemView.setOnClickListener(view -> {
            });

            btnChangeBlueTooth.setOnClickListener(v ->{

                showBlueToothList();
                int i = getLayoutPosition();
                usuarioHasModeloCarroSelected = items.get(i);
                Log.e("CLICK","ON BLUETOOTH NUmber " + i);

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

                    usuarioHasModeloCarroSelected.setBluetoothMac(spBluetoothDevicesList.get(position).getAddress());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    usuarioHasModeloCarroSelected.setBluetoothMac(spBluetoothDevicesList.get(0).getAddress());
                }
            });
        }

        Button btnUpdateBluetooth = dialog.findViewById(R.id.btn_update_bluetooth_selected);
        btnUpdateBluetooth.setOnClickListener(v -> {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            ((MainActivity)context).upateDefaultVehicle(usuarioHasModeloCarroSelected);

        });

        dialog.show();
    }

    public VehiculosAdapter(List<UsuarioHasModeloCarro> items, Context context) {
        this.items = items;
        this.context = context;
        usuarioHasModeloCarroSelected = null;
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
