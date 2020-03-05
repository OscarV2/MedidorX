package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estados;
import com.index.medidor.utils.Constantes;

import java.util.List;

public class EstadosAdapter extends RecyclerView.Adapter<EstadosAdapter.EstadosViewHolder> {

    private List<Estados> items;
    private Context context;
    private MainActivity mainActivity;
    public int mSelectedItem = -1;
    private Integer selectesStatePosition;


    public class EstadosViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStateName;
        private RadioButton rbState = null;
        private LinearLayout linearLayout;

        public EstadosViewHolder(View itemView) {
            super(itemView);
            tvStateName = itemView.findViewById(R.id.tv_state_name);
            rbState = itemView.findViewById(R.id.rb_state);
            linearLayout = itemView.findViewById(R.id.ll_states);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");
            tvStateName.setTypeface(light);
            itemView.setOnClickListener(view -> {
            });
            View.OnClickListener l = v -> {
                mSelectedItem = getAdapterPosition();
                notifyItemRangeChanged(0, items.size());
            };
            rbState.setOnClickListener(l);
        }
    }

    public EstadosAdapter(List<Estados> items, Context context, DataBaseHelper helper) {
        this.items = items;
        this.context = context;
        this.mainActivity = (MainActivity)context;
        mSelectedItem = getDefaultSatePosition();

    }

    @Override
    public EstadosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_states,parent,false);
        return new EstadosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EstadosViewHolder holder, int position) {
        holder.rbState.setChecked(position == mSelectedItem);
        holder.tvStateName.setText(this.items.get(position).getNombre());
    }

    private int getDefaultSatePosition(){
        try {
            int idSelectedState = mainActivity.getMyPreferences().getInt(Constantes.DEFAULT_STATE_ID, 0);
            Estados selectedState = items.stream().filter(state -> state.getId() == idSelectedState).findFirst().get();
            return items.indexOf(selectedState);
        }catch (Exception ex){
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Integer getmSelectedItem() {
        return mSelectedItem;
    }

}
