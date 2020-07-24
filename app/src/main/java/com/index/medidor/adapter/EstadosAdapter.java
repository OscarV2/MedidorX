package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estados;
import com.index.medidor.model.HistorialEstadoVehiculos;
import com.index.medidor.model.Vehiculo;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.CustomProgressDialog;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadosAdapter extends RecyclerView.Adapter<EstadosAdapter.EstadosViewHolder> {

    private List<Estados> items;
    private Context context;
    private MainActivity mainActivity;
    public int mSelectedItem = -1;
    private Integer selectesStatePosition;
    private CustomProgressDialog mCustomProgressDialog;
    private Dao<HistorialEstadoVehiculos, Integer> daoHistorial;

    public class EstadosViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStateName;
        private RadioButton rbState = null;
        private LinearLayout linearLayout;

        public EstadosViewHolder(View itemView) {
            super(itemView);
            tvStateName = itemView.findViewById(R.id.tv_state_name);
            rbState = itemView.findViewById(R.id.rb_state);
            linearLayout = itemView.findViewById(R.id.ll_states);
            Typeface light = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
            tvStateName.setTypeface(light);
            itemView.setOnClickListener(view -> {
            });
            View.OnClickListener l = v -> {
                mSelectedItem = getAdapterPosition();
                notifyItemRangeChanged(0, items.size());
                onChangeStateButtonPressed();
            };
            rbState.setOnClickListener(l);
        }
    }

    public EstadosAdapter(List<Estados> items, Context context, DataBaseHelper helper) {
        this.items = items;
        this.context = context;
        this.mainActivity = (MainActivity) context;
        mSelectedItem = getDefaultSatePosition();
        mCustomProgressDialog = new CustomProgressDialog(this.mainActivity);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);
        try {
            daoHistorial = helper.getDaoHistorialEstados();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public EstadosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_states, parent, false);
        return new EstadosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EstadosViewHolder holder, int position) {
        holder.rbState.setChecked(position == mSelectedItem);
        holder.tvStateName.setText(this.items.get(position).getNombre());
    }

    private int getDefaultSatePosition() {
        try {
            int idSelectedState = mainActivity.getMyPreferences().getInt(Constantes.DEFAULT_STATE_ID, 0);
            Estados selectedState = items.stream().filter(state -> state.getId() == idSelectedState).findFirst().get();
            return items.indexOf(selectedState);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void onChangeStateButtonPressed() {
        //idSelectedState = mainActivity.getMyPreferences().getInt(Constantes.DEFAULT_STATE_ID, 0);
        if (items != null && items.size() > 0) {
            if (mSelectedItem == getDefaultSatePosition()) {
                Toast.makeText(mainActivity, "DEBES SELECCIONAR UN ESTADO DIFERENTE PARA ACTUALIZAR.", Toast.LENGTH_SHORT).show();
                return;
            } else {

                HistorialEstadoVehiculos newStateHistoryRecord = new HistorialEstadoVehiculos();
                newStateHistoryRecord.setFechaInicio(Constantes.SDF_FOR_BACKEND.format(new Date()));

                Estados estados = items.get(mSelectedItem);
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(mainActivity.getMyPreferences().getLong(Constantes.DEFAULT_VEHICLE_ID, 0));

                newStateHistoryRecord.setEstado(estados);
                newStateHistoryRecord.setVehiculo(vehiculo);
                newStateHistoryRecord.setIdEstado(estados.getId());
                newStateHistoryRecord.setIdVehiculo(vehiculo.getId());

                Call<HistorialEstadoVehiculos> callUpdateState = MedidorApiAdapter.getApiService()
                        .postHistorialEstadosSave(Constantes.CONTENT_TYPE_JSON, newStateHistoryRecord);
                mCustomProgressDialog.show("");

                callUpdateState.enqueue(new Callback<HistorialEstadoVehiculos>() {
                    @Override
                    public void onResponse(Call<HistorialEstadoVehiculos> call, Response<HistorialEstadoVehiculos> response) {
                        mCustomProgressDialog.dismiss("");
                        Log.e("MEG", response.message());
                        Log.e("code", String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            Log.e("2","OK2");
                            updateStateOnMainActivity(estados);
                            newStateHistoryRecord.setUploaded(true);
                            try {
                                daoHistorial.create(newStateHistoryRecord);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else
                            Log.e("3","NO OK2");
                    }

                    @Override
                    public void onFailure(Call<HistorialEstadoVehiculos> call, Throwable t) {
                        mCustomProgressDialog.dismiss("");
                        newStateHistoryRecord.setUploaded(false);
                        try {
                            updateStateOnMainActivity(estados);
                            daoHistorial.create(newStateHistoryRecord);
                            /*Constraints constraints = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();
                            // ...then create a OneTimeWorkRequest that uses those constraints
                            OneTimeWorkRequest requestWork =
                                    new OneTimeWorkRequest.Builder(HistorialEstadoWorker.class)
                                            .setConstraints(constraints)
                                            .build();
                            WorkManager.getInstance(mainActivity).enqueue(requestWork);*/
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(mainActivity, "NO SE PUDO ACTUALIZAR LA INFORMACIÓN.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void updateStateOnMainActivity(Estados estados){
        Toast.makeText(mainActivity, "ESTADO ACTUALIZADO CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
        mainActivity.getMyPreferences().edit().putString(Constantes.DEFAULT_STATE, estados.getNombre()).apply();
        mainActivity.getMyPreferences().edit().putInt(Constantes.DEFAULT_STATE_ID, estados.getId()).apply();
        mainActivity.setDefaultState();
        mainActivity.getBtnBack().performClick();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public Integer getmSelectedItem() {
        return mSelectedItem;
    }

}
