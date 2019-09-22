package com.index.medidor.fragments.combustible;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estaciones;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.CustomProgressDialog;
import com.index.medidor.utils.ResponseServices;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.abhinay.input.CurrencyEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngresadoFragment extends Fragment {

    private CombustibleTabs.OnFragmentInteractionListener mListener;

    private Button btnMedicion;
    private TextView tvGalones;
    private TextView tvTotal, tvSignoPeso;
    private CurrencyEditText edtCantDeseadaNum;
    private CurrencyEditText edtValor;          //precio combustible
    private Spinner spOtraEstacion;
    private boolean estado=false;
    private boolean flagCantidadDeseada; // true = cantidad deseada en dinero, false = galones
    private boolean reset = false;
    private double combustible=0.0, combustibleInicial;
    private double valor=0;
    private double total=0;
    private MainActivity mainActivity;
    private Timer mTimer1;
    private Handler mHandler;
    private double galonesDeseados, cantDeseada;
    private CustomProgressDialog mCustomProgressDialog;
    private AlertDialog dialogCal;
    private AlertDialog.Builder dialog;
    private RatingBar calRatingBar;
    private TextView tvNombreEstacion, tvDirEstacion, tvTxtCal;
    private Typeface light;
    private List<Estaciones> estacionesCercanas;
    private Estaciones estacionTanquea;
    private Tanqueadas t;

    public IngresadoFragment(MainActivity m){
        this.mainActivity = m;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingresado, container, false);

        light=Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(),"fonts/Roboto-Light.ttf");
        Typeface bold=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");
        estado=true;
        init(view, bold);
        initDialog();

        return view;
    }

    private void iniciarMedicion() {

        combustibleInicial = mainActivity.getNivelCombustible();
        valor = edtValor.getCleanDoubleValue();
        cantDeseada = edtCantDeseadaNum.getCleanDoubleValue();
        //validar precio galon
        if (valor <= 100){

            Toast.makeText(mainActivity, "Por favor ingrese un precio de galón válido.", Toast.LENGTH_SHORT).show();
            edtValor.requestFocus();
        }else if(flagCantidadDeseada && cantDeseada <= 0){

            Toast.makeText(mainActivity, "Por favor ingrese una cantidad válida.", Toast.LENGTH_SHORT).show();
            edtCantDeseadaNum.requestFocus();

        }else if(!flagCantidadDeseada && galonesDeseados <=0 ){
            Toast.makeText(mainActivity, "Por favor ingrese una cantidad de galones válida.", Toast.LENGTH_SHORT).show();
            edtValor.requestFocus();

        }else if (mainActivity.getBtSocket() == null){        //validar bluetooth

            Toast.makeText(mainActivity, "Por favor asegurese de que su conexión con el dispositivo es óptima", Toast.LENGTH_SHORT).show();

        }else{
            estado=false;

            btnMedicion.setText(R.string.detener);

            mTimer1 = new Timer();

            TimerTask mTt1 = new TimerTask() {
                public void run() {
                    mHandler.post(() -> {
                        if (mainActivity.getNivelCombustible() > combustibleInicial){

                            combustible = mainActivity.getNivelCombustible() - combustibleInicial;
                        }
                        total = valor * combustible;
                        tvGalones.setText(String.format("%.1f", combustible) + " Gal. /");

                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setGroupingSeparator(',');
                        symbols.setDecimalSeparator('.');

                        DecimalFormat decimalFormat = new DecimalFormat("$ #,###.00", symbols);

//                        tvTotal.setText("$ " + String.format("%.1f", total));
                        tvTotal.setText(decimalFormat.format(total));

                    });
                }
            };

            mTimer1.schedule(mTt1, 1, 1000);
        }
    }

    private void detenerMedicion(){
        //mCustomProgressDialog.show("");
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
            estado=true;
        }

        try {
            estacionesCercanas = mainActivity.getEstacionesCercanas();
            if (estacionesCercanas != null && estacionesCercanas.size() > 0){

                estacionTanquea = estacionesCercanas.get(0);
                tvNombreEstacion.setText(estacionTanquea.getMarca());
                tvDirEstacion.setText(estacionTanquea.getDireccion());
                //mostrar Dialogo
                dialogCal.show();

                String[] estacionesMarcas = new String[estacionesCercanas.size()];
                for (int i = 0; i < estacionesCercanas.size(); i++ ) {

                    estacionesMarcas[i] = estacionesCercanas.get(i).getMarca();
                }

                if(spOtraEstacion != null){
                    spOtraEstacion.setAdapter(new ArrayAdapter<>(mainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            estacionesMarcas));
                }else{
                    Log.e("ESA","VAINA ES NULA");
                }


                
            }else{

                tvNombreEstacion.setText("");
                tvDirEstacion.setText("No se encontró ninguna estación registrada en esta ubicación, por favor seleccione la marca de la estación a registrar.");

                spOtraEstacion.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.marcas_estaciones)));

                dialogCal.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void init(View view,  Typeface bold){

        mHandler = new Handler();
        btnMedicion = view.findViewById(R.id.btnMedicion);
        Spinner spCantDeseada = view.findViewById(R.id.spCantDeseada);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mainActivity, R.array.optCantDeseada,
                R.layout.my_spinner);
        spCantDeseada.setAdapter(adapter);
        edtCantDeseadaNum = view.findViewById(R.id.edtCantDeseadaNum);

        spCantDeseada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){             //cop
                    flagCantidadDeseada = true;
                    edtCantDeseadaNum.setText("");
                    cantDeseada = 0;
                    tvSignoPeso.setVisibility(View.VISIBLE);

                }else{                          //cantidad
                    flagCantidadDeseada = false;
                    edtCantDeseadaNum.setText("");
                    galonesDeseados = 0;
                    tvSignoPeso.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                flagCantidadDeseada = true;
            }
        });

        btnMedicion.setTypeface(bold);
        btnMedicion.setOnClickListener(v -> {

        if(estado){                 //si estado es tru inicia medicion
            reset=true;
            iniciarMedicion();

        }else{
            detenerMedicion();
            btnMedicion.setText(R.string.iniciar);
        }

        });
        TextView tvcantDeseada = view.findViewById(R.id.tvCantDeseada);
        tvSignoPeso = view.findViewById(R.id.tv_signo_peso);
        tvSignoPeso.setTypeface(light);
        tvcantDeseada.setTypeface(light);
        TextView tv1 = view.findViewById(R.id.tv1);
        tv1.setTypeface(light);
        TextView tv2 = view.findViewById(R.id.tv2);
        tv2.setTypeface(light);
        TextView tv3 = view.findViewById(R.id.tv3);
        tv3.setTypeface(light);

        tvGalones = view.findViewById(R.id.tvGalones);
        tvGalones.setTypeface(light);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvTotal.setTypeface(light);
        edtValor = view.findViewById(R.id.etValor);
        edtValor.setTypeface(light);
        edtCantDeseadaNum.setTypeface(light);

        edtValor.setDelimiter(false);
        edtValor.setSpacing(false);
        edtValor.setDecimals(false);

        edtCantDeseadaNum.setDelimiter(false);
        edtCantDeseadaNum.setSpacing(false);
        edtCantDeseadaNum.setDecimals(false);

        ImageButton btnRefresh = view.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            if (!estado){
                tvGalones.setText("0.0 Gal. /");
                tvTotal.setText("$0");
            }

        });

    }

    private void guardarMedicion(Tanqueadas tanqueada){

        DataBaseHelper helper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
        try {
            Dao<Tanqueadas, Integer> dao = helper.getDaoTanqueadas();
            dao.create(tanqueada);
            Toast.makeText(mainActivity, "TANQUEADA REGISTRADA DE MANERA EXITOSA.", Toast.LENGTH_SHORT).show();

            //mCustomProgressDialog.dismiss("");
            //Toast.makeText(mainActivity, "Medición registrada de forma exitosa.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            //mCustomProgressDialog.dismiss("");
            e.printStackTrace();
        }

    }

    private void setCantidadDeseada(){
        if (flagCantidadDeseada){
            cantDeseada = Double.valueOf(edtCantDeseadaNum.getText().toString());
        }else{
           galonesDeseados = Double.valueOf(edtCantDeseadaNum.getText().toString());
        }
    }

    private void initDialog(){

        dialog = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.calification_dialog, null);

        calRatingBar = dialogView.findViewById(R.id.cal_ratingbar);
        tvDirEstacion = dialogView.findViewById(R.id.rank_dialog_title2);
        tvDirEstacion.setTypeface(light);
        tvNombreEstacion = dialogView.findViewById(R.id.rank_dialog_title1);
        tvNombreEstacion.setTypeface(light);
        tvTxtCal = dialogView.findViewById(R.id.txtCal);
        tvTxtCal.setVisibility(View.GONE);

        EditText edtComentarios = dialogView.findViewById(R.id.edt_comentarios);
        spOtraEstacion = dialogView.findViewById(R.id.sp_otra_estacion);

        calRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            tvTxtCal.setVisibility(View.VISIBLE);
            int ratingInt = (int)rating;

            switch (ratingInt){
                case 1:
                    tvTxtCal.setText("Pésimo");
                    break;
                case 2:
                    tvTxtCal.setText("Malo");
                    break;
                case 3:
                    tvTxtCal.setText("Regular");
                    break;
                case 4:
                    tvTxtCal.setText("Bueno");
                    break;
                case 5:
                    tvTxtCal.setText("Excelente");
                    break;
            }
        });

        Button btnAceptar = dialogView.findViewById(R.id.btnCalAceptar);
        btnAceptar.setOnClickListener(v -> {
            setTanqueada();
            dialogCal.dismiss();
        });
        Button btnCancelar = dialogView.findViewById(R.id.btnCalCancelar);
        btnCancelar.setOnClickListener(v -> {
            setTanqueada();
            dialogCal.dismiss();

        });
        dialog.setView(dialogView);
        dialogCal = dialog.create();
    }

    private void setTanqueada(){

        t = new Tanqueadas();

        t.setCalificacion((int)calRatingBar.getRating());

        if (calRatingBar.getRating() > 0){
            // TODO : UPDATE CALIFICACION DE ESTACION
        }

        if(estacionTanquea == null){
            String marca = spOtraEstacion.getSelectedItem().toString();
            if(marca.equals("")){

                spOtraEstacion.requestFocus();
                Toast.makeText(mainActivity, "POR FAVOR SELECCIONES UNA ESTACIÓN", Toast.LENGTH_SHORT).show();
                return;
            }

            estacionTanquea = new Estaciones();
            estacionTanquea.setNombre(marca);
            estacionTanquea.setDireccion("Cll 10 b # 30-55");
            estacionTanquea.setCertificada(0);
            estacionTanquea.setMarca(spOtraEstacion.getSelectedItem().toString());

            estacionTanquea.setLatitud(mainActivity.getMyLocation().getLatitude());
            estacionTanquea.setLongitud(mainActivity.getMyLocation().getLongitude());

            guardarNuevaEstacion(estacionTanquea);
        }else{

            guardarTanqueada();

        }

    }

    private void guardarTanqueada(){

        t.setDireccion(estacionTanquea.getDireccion());
        t.setNombre(estacionTanquea.getMarca());
        t.setGalones(combustible);
        t.setTotal(total);
        t.setFecha(Constantes.SDF_FOR_BACKEND.format(new Date()));
        t.setGalDeseados(galonesDeseados);
        t.setCantDeseada(cantDeseada);
        t.setFlagCantidadDeseada(flagCantidadDeseada);
        t.setPrecioGalon(this.valor);

        t.setIdUsuario(mainActivity.getMyPreferences().getInt("idUsuario", 0));

        t.setLatitud(estacionTanquea.getLatitud());
        t.setLongitud(estacionTanquea.getLongitud());

        t.setIdEstacion(estacionTanquea.getId());
        //guardarMedicion(t);
        Log.e("Tanq", new Gson().toJson(t));
        enviarTanqueada(t);
    }

    private void guardarNuevaEstacion(Estaciones nuevaEstacion)  {

        Call<ResponseServices> callRegisterStation = MedidorApiAdapter.getApiService()
                .postRegisterStation(Constantes.CONTENT_TYPE_JSON, nuevaEstacion);
        Log.e("lat", String.valueOf(nuevaEstacion.getLatitud()));
        Log.e("long", String.valueOf(nuevaEstacion.getLongitud()));

        callRegisterStation.enqueue(new Callback<ResponseServices>() {
            @Override
            public void onResponse(Call<ResponseServices> call, Response<ResponseServices> response) {

                if(response.isSuccessful()){

                    try {
                        DataBaseHelper helper = OpenHelperManager.getHelper(mainActivity, DataBaseHelper.class);

                        Dao<Estaciones, Integer> daoEsatciones = helper.getDaoEstaciones();

                        daoEsatciones.create(nuevaEstacion);

                        Toast.makeText(mainActivity, "Estación registrada de manera exitosa.", Toast.LENGTH_SHORT).show();

                        guardarTanqueada();

                        mainActivity.addNewStationToMap(nuevaEstacion);

                    } catch (SQLException e) {
                        e.printStackTrace();
                        return;
                    }
                }else{
                    Log.e("ON ELSE", response.message());
                    Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR LA ESTACIÓN", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<ResponseServices> call, Throwable t) {

                Log.e("FAILURE", "FALLOOOO");

                Log.e("FAILURE", t.getMessage());
                Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR LA ESTACIÓN", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void enviarTanqueada(Tanqueadas tanqueada){

        Call<ResponseServices> callRegistrarTanqueada = MedidorApiAdapter.getApiService()
                .postRegisterTanqueada(Constantes.CONTENT_TYPE_JSON, tanqueada);

        callRegistrarTanqueada.enqueue(new Callback<ResponseServices>() {
            @Override
            public void onResponse(Call<ResponseServices> call, Response<ResponseServices> response) {

                if (response.isSuccessful()){

                    guardarMedicion(t);

                }else {

                    Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR LA TANQUEADA.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseServices> call, Throwable t) {

                Toast.makeText(mainActivity, "NO SE PUDO REGISTRAR LA TANQUEADA.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
