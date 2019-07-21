package com.index.medidor.fragments.combustible;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.model.Estaciones;
import com.index.medidor.model.Tanqueadas;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import faranjit.currency.edittext.CurrencyEditText;

public class IngresadoFragment extends Fragment {

    private CombustibleTabs.OnFragmentInteractionListener mListener;

    private Button btnMedicion;
    private TextView tvGalones;
    private TextView tvTotal;
    private EditText edtCantDeseadaNum;
    private EditText etValor;
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
    //private CustomProgressDialog mCustomProgressDialog;
    private AlertDialog dialogCal;
    private AlertDialog.Builder dialog;
    private RatingBar calRatingBar;
    private TextView tvNombreEstacion, tvDirEstacion, tvTxtCal;
    Typeface light;
    Estaciones estacionMasCercana;

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

        init(view, bold);
        initDialog();

        return view;
    }

    private void iniciarMedicion(){

        combustibleInicial = mainActivity.getNivelCombustible();
        //validar precio galon
        if (valor <= 100){

            Toast.makeText(mainActivity, "Por favor ingrese un precio de galón válido.", Toast.LENGTH_SHORT).show();
            etValor.requestFocus();

        }else if(flagCantidadDeseada && cantDeseada <= 0){

            Toast.makeText(mainActivity, "Por favor ingrese una cantidad válida.", Toast.LENGTH_SHORT).show();
            edtCantDeseadaNum.requestFocus();

        }else if(!flagCantidadDeseada && galonesDeseados <=0 ){
            Toast.makeText(mainActivity, "Por favor ingrese una cantidad de galones válida.", Toast.LENGTH_SHORT).show();
            etValor.requestFocus();
        }

        else if (mainActivity.getBtSocket() == null){        //validar bluetooth

            Toast.makeText(mainActivity, "Por favor asegurese de que su conexión con el dispositivo es óptima", Toast.LENGTH_SHORT).show();

        }else{
            btnMedicion.setText(R.string.detener);

            mTimer1 = new Timer();

            TimerTask mTt1 = new TimerTask() {
                public void run() {
                    mHandler.post(() -> {
                        Log.e("Cantidad", "Combustible " + mainActivity.getNivelCombustible());
                        if (mainActivity.getNivelCombustible() > combustibleInicial){

                            combustible = mainActivity.getNivelCombustible() - combustibleInicial;
                        }
                        total = valor * combustible;
                        tvGalones.setText(String.format("%.1f", combustible) + " Gal. /");
                        tvTotal.setText("$ " + String.format("%.1f", total));
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
        }
        estacionMasCercana = mainActivity.getEstacionMasCercana();
        tvNombreEstacion.setText(estacionMasCercana.getMarca());
        tvDirEstacion.setText(estacionMasCercana.getDireccion());
        //mostrar Dialogo
        dialogCal.show();
    }

    private void init(View view,  Typeface bold){

//
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
                if (position == 0){
                    flagCantidadDeseada = true;
                    edtCantDeseadaNum.setText("");
                    cantDeseada = 0;
                }else{
                    flagCantidadDeseada = false;
                    edtCantDeseadaNum.setText("");
                    galonesDeseados = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                flagCantidadDeseada = true;
            }
        });

        btnMedicion.setTypeface(bold);
        btnMedicion.setOnClickListener(v -> {
            estado=!estado;
            if(estado){
                reset=true;
                iniciarMedicion();

            }else{
                detenerMedicion();
                btnMedicion.setText(R.string.iniciar);
            }
        });
        TextView tvcantDeseada = view.findViewById(R.id.tvCantDeseada);
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
        etValor = view.findViewById(R.id.etValor);
        etValor.setTypeface(light);
        edtCantDeseadaNum.setTypeface(light);

        edtCantDeseadaNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.e("cant", s.toString());
                if (flagCantidadDeseada){

                    try {
                        cantDeseada = Double.valueOf(s.toString());

                    }catch (NumberFormatException e){

                        cantDeseada = 0;
                    }
                }else{
                    try {
                        galonesDeseados = Double.valueOf(s.toString());

                    }catch (NumberFormatException e){

                        galonesDeseados = 0;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Texto", s.toString());
                try {

                    valor = Double.valueOf(s.toString());
                }catch (NumberFormatException e){

                    valor = 0;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton btnRefresh = view.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            if (!estado){
                tvGalones.setText("0.0 Gal. /");
                tvTotal.setText("$0");
            }

        });
        /*mCustomProgressDialog = new CustomProgressDialog(mainActivity);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);*/
        //initDialog();

    }

    private void guardarMedicion(Tanqueadas tanqueada){

        DataBaseHelper helper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
        try {
            Dao<Tanqueadas, Integer> dao = helper.getDaoTanqueadas();
            dao.create(tanqueada);
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

        calRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            tvTxtCal.setVisibility(View.VISIBLE);
            int ratingInt = (int)rating;
            Log.e("Rating",String.valueOf(ratingInt));

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
            guardarTanqueada();
            dialogCal.dismiss();
        });
        Button btnCancelar = dialogView.findViewById(R.id.btnCalCancelar);
        btnCancelar.setOnClickListener(v -> {
            guardarTanqueada();
            dialogCal.dismiss();

        });
        dialog.setView(dialogView);
        dialogCal = dialog.create();
    }

    private void guardarTanqueada(){

        Tanqueadas t = new Tanqueadas();

        t.setCalificacion((int)calRatingBar.getRating());

        if (calRatingBar.getRating() > 0){
            // TODO : UPDATE CALIFICACION DE ESTACION
        }

        t.setDireccion(estacionMasCercana.getDireccion());
        t.setNombre(estacionMasCercana.getNombre());
        t.setGalones(combustible);
        t.setTotal(total);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        t.setFecha(df.format(new Date()));
        t.setGalDeseados(galonesDeseados);
        t.setCantDeseada(cantDeseada);
        t.setFlagCantidadDeseada(flagCantidadDeseada);

        t.setLatitud(estacionMasCercana.getLatitud());
        t.setLongitud(estacionMasCercana.getLongitud());
        guardarMedicion(t);
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
