package com.index.medidor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.index.medidor.R;
import com.index.medidor.bluetooth.SpBluetoothDevice;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends ArrayAdapter<SpBluetoothDevice> {

    public BluetoothDeviceAdapter(@NonNull Context context, @NonNull ArrayList<SpBluetoothDevice> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, @NonNull ViewGroup parent){

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spin_bluetooth_devices, parent,
                    false);
        }

        TextView tvNombre = convertView.findViewById(R.id.tv_device_name);
        TextView tvAddress = convertView.findViewById(R.id.tv_device_address);

        SpBluetoothDevice device = getItem(position);

        if(device != null){

            tvAddress.setText(device.getAddress());
            tvNombre.setText(device.getName());
        }

        return convertView;

    }
}
