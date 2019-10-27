package com.index.medidor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.index.medidor.R;
import com.index.medidor.model.Recorrido;

import java.util.List;


public class RecorridosAdapter extends RecyclerView.Adapter<RecorridosAdapter.RecorridosViewHolder>{

    private Context context;
    private List<Recorrido> recorridosList;

    public RecorridosAdapter(Context context, List<Recorrido> recorridosList) {
        this.context = context;
        this.recorridosList = recorridosList;
    }

    @NonNull
    @Override
    public RecorridosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_recorrido,parent,false);

        return new RecorridosAdapter.RecorridosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecorridosViewHolder recorridosViewHolder, int i) {

        if (recorridosList != null){

            Recorrido recorrido = recorridosList.get(i);
            recorridosViewHolder.bindView(recorrido);
        }

    }

    @Override
    public int getItemCount() {

        //return recorridosList.size();
        return 3;

    }

    public class RecorridosViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        private TextView tvFechaRecorrido;
        private TextView tvGalRecorrido;
        private TextView tvDistancia;

        MapView mapView;
        GoogleMap map;

        public RecorridosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDistancia = itemView.findViewById(R.id.tv_distancia_recorrido);
            tvFechaRecorrido = itemView.findViewById(R.id.tv_fecha_recorrido);
            tvGalRecorrido = itemView.findViewById(R.id.tv_gal_recorrido);
            mapView = itemView.findViewById(R.id.mapView);

            Typeface light=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

            tvDistancia.setTypeface(light);
            tvGalRecorrido.setTypeface(light);
            tvFechaRecorrido.setTypeface(light);
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            initMap();
        }

        private void initMap() {

            if (map == null) return;

            Recorrido recorrido = (Recorrido)mapView.getTag();

            if (recorrido == null) return;

            //recorrido.setPosiciones();
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLACK);
            options.width(3f);

            /*
            if (recorrido.getPosiciones().size() >= 2){

                int rSize = recorrido.getPosiciones().size();
                map.addMarker(new MarkerOptions().position(recorrido.getPosiciones().get(0)).title("Inicio"));
                map.addMarker(new MarkerOptions().position(recorrido.getPosiciones().get(rSize - 1)).title("Fin"));

                for (int i = 1; i <= recorrido.getPosiciones().size() -1 ;i++  ) {

                            options.add(recorrido.getPosiciones().get(i));

                }

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(recorrido.getPosiciones().get(rSize - 1), 12f));

            }else{

                recorrido.getPosiciones().add(new LatLng(4.650148, -74.158272));
                recorrido.getPosiciones().add(new LatLng(4.639155, -74.144990));
                recorrido.getPosiciones().add(new LatLng(4.630806, -74.130001));

                int r = recorrido.getPosiciones().size();
                map.addMarker(new MarkerOptions().position(recorrido.getPosiciones().get(0)).title("Inicio"));
                map.addMarker(new MarkerOptions().position(recorrido.getPosiciones().get(r - 1)).title("Fin"));

                for (int i = 1; i <= recorrido.getPosiciones().size() -1 ;i++  ) {


                    options.add(recorrido.getPosiciones().get(i));

                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(recorrido.getPosiciones().get(r - 1), 12f));
            }
            map.addPolyline(options);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            */
        }

        public void bindView(Recorrido recorrido){

            /*
            mapView.setTag(recorrido);
            initMap();
            tvDistancia.setText(recorrido.getDistancia());
            tvGalRecorrido.setText(String.valueOf(recorrido.getGalonesPerdidos()));
            tvFechaRecorrido.setText(recorrido.getHoraInicio());
            */
        }

    }
}
