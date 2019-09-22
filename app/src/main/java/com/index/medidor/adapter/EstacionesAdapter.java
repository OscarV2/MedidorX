package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.model.Estaciones;

import java.util.List;

public class EstacionesAdapter  extends RecyclerView.Adapter<EstacionesAdapter.EstacionesViewHolder> {

    private List<Estaciones>items;
    private Context context;

    public class EstacionesViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre;
        public TextView tvDistancia;
        public TextView tvDireccion;
        public TextView tvHorario;
        public TextView tvCalificacion;
        public RatingBar rbClasificacion;

        public EstacionesViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDistancia = itemView.findViewById(R.id.tvDistancia);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvHorario =  itemView.findViewById(R.id.tvHorario);
            tvCalificacion = itemView.findViewById(R.id.tvClasificacion);
            rbClasificacion =  itemView.findViewById(R.id.rbClasificacion);
            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

            tvCalificacion.setTypeface(light);
            tvDireccion.setTypeface(light);
            tvDistancia.setTypeface(light);
            tvHorario.setTypeface(light);
            tvNombre.setTypeface(light);

        }
    }

    public EstacionesAdapter(List<Estaciones> items, Context context) {
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
        holder.tvNombre.setText(items.get(position).getNombre());
        float distancia = items.get(position).getDistancia();
        if (distancia>=1000){
            distancia /=1000;
            holder.tvDistancia.setText(String.format("%.1f",distancia)+" Km");
        }else{
            holder.tvDistancia.setText(String.format("%.1f",distancia)+" m");
        }

        holder.tvDireccion.setText(items.get(position).getDireccion());
        holder.tvHorario.setText(items.get(position).getHorario());
        holder.tvCalificacion.setText(String.valueOf(items.get(position).getCalificacion()));
//        holder.rbClasificacion.setRating(items.get(position).getClasificacion());
        holder.rbClasificacion.setRating(2.2F);
        holder.tvHorario.setText("Abierto 24 horas");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
