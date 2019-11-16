package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.model.Estaciones;
import com.index.medidor.model.UsuarioHasModeloCarro;

import java.util.List;

public class VehículosAdapter extends RecyclerView.Adapter<VehículosAdapter.EstacionesViewHolder> {

    private List<UsuarioHasModeloCarro> items;
    private Context context;

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

            btnChangeBlueTooth.setOnClickListener(v -> {
                showBlueToothList();                
            });
        }
    }

    private void showBlueToothList() {
    }

    public VehículosAdapter(List<UsuarioHasModeloCarro> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public EstacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_estaciones,parent,false);
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
