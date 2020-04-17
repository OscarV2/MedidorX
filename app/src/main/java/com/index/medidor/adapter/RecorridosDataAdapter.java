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
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.UnidadRecorrido;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;


public class RecorridosDataAdapter extends RecyclerView.Adapter<RecorridosDataAdapter.RecorridosViewHolder>{

    private Context context;
    private List<Recorrido> recorridosList;
    private Dao<UnidadRecorrido, Integer> daoUnidadRecorrido;

    public RecorridosDataAdapter(Context context, List<Recorrido> recorridosList, DataBaseHelper helper) {
        this.context = context;
        this.recorridosList = recorridosList;
        try {
            this.daoUnidadRecorrido = helper.getDaoUnidadRecorrido();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecorridosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_recorrido_data, parent,false);

        return new RecorridosDataAdapter.RecorridosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecorridosViewHolder holder, int i) {

        if (recorridosList != null){
            Recorrido recorrido = recorridosList.get(i);
            holder.tvRecorridoCode.setText(recorrido.getRecorridoCode());
            holder.tvRecorridoFechaInicio.setText(recorrido.getFechaInicio());
            holder.tvRecorridoFechaFin.setText(recorrido.getFechaFin());

            if (recorrido.isUploaded())
                holder.tvRecorridoUploaded.setText("Si");
            else
                holder.tvRecorridoUploaded.setText("No");
            if(recorrido.isCompleted())
                holder.tvRecorridoCompleted.setText("Si");
            else
                holder.tvRecorridoCompleted.setText("No");

            List<UnidadRecorrido> lUnidadRecorrido;
            try {
                lUnidadRecorrido = daoUnidadRecorrido.queryForEq("idRecorrido", recorrido.getId());
                if (lUnidadRecorrido != null)
                    holder.tvRecorridoDataPorSubir.setText("Faltan por subir : " + lUnidadRecorrido.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {

        return recorridosList.size();
        //return 3;
    }

    public class RecorridosViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRecorridoCode;
        private TextView tvRecorridoFechaInicio;
        private TextView tvRecorridoFechaFin;
        private TextView tvRecorridoCompleted;
        private TextView tvRecorridoUploaded;
        private TextView tvRecorridoDataPorSubir;

        public RecorridosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecorridoCode = itemView.findViewById(R.id.tv_recorrido_data_code);
            tvRecorridoFechaInicio = itemView.findViewById(R.id.tv_recorrido_data_fechaInicio);
            tvRecorridoFechaFin = itemView.findViewById(R.id.tv_recorrido_data_fechaFin);
            tvRecorridoCompleted = itemView.findViewById(R.id.tv_recorrido_data_completed1);
            tvRecorridoUploaded = itemView.findViewById(R.id.tv_recorrido_data_uploaded1);
            tvRecorridoDataPorSubir = itemView.findViewById(R.id.tv_recorrido_data_falta_por_subir);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

            tvRecorridoCode.setTypeface(light);
            tvRecorridoFechaInicio.setTypeface(light);
            tvRecorridoCompleted.setTypeface(light);
            tvRecorridoFechaFin.setTypeface(light);
            tvRecorridoUploaded.setTypeface(light);
            tvRecorridoDataPorSubir.setTypeface(light);
        }
    }
}
