package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.model.Tanqueadas;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TanqueadasAdapter extends RecyclerView.Adapter<TanqueadasAdapter.TanqueadasViewHolder>{

    private List<Tanqueadas> items;
    private Context context;

    public class TanqueadasViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre;
        public TextView tvDireccion;
        public TextView tvGalones;
        public TextView tvValor;
        public TextView tvFecha;

        public TanqueadasViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvGalones = itemView.findViewById(R.id.tvGalones);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvFecha = itemView.findViewById(R.id.tvFecha);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");
            tvNombre.setTypeface(light);
            tvDireccion.setTypeface(light);
            tvGalones.setTypeface(light);
            tvValor.setTypeface(light);
            tvFecha.setTypeface(light);

        }
    }

    public TanqueadasAdapter(List<Tanqueadas>items, Context context){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public TanqueadasAdapter.TanqueadasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_tanqueadas,parent,false);
        return new TanqueadasAdapter.TanqueadasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TanqueadasAdapter.TanqueadasViewHolder holder, int position) {

        holder.tvNombre.setText(items.get(position).getNombre());
        holder.tvDireccion.setText(items.get(position).getDireccion());
        holder.tvGalones.setText(String.format(Locale.US,"%.1f",items.get(position).getGalones()) + " Gal. /");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("$ #,###.00", symbols);
 //       String prezzo = decimalFormat.format(number);
//        holder.tvValor.setText(" $" + String.format(Locale.US,"%.1f",items.get(position).getTotal()));
        holder.tvValor.setText(decimalFormat.format(items.get(position).getTotal()));

        //SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

        //SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.US);
        holder.tvFecha.setText(items.get(position).getFecha());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
