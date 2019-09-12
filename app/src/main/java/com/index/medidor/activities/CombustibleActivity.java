package com.index.medidor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.adapter.TabPagerAdapter;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.fragments.combustible.RendimientoFragment;
import com.index.medidor.model.Estaciones;
import com.index.medidor.places.EstacionesPlaces;
import com.index.medidor.utils.BluetoothDataReceiver;
import com.index.medidor.utils.BluetoothHelper;
import com.index.medidor.utils.RutasHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CombustibleActivity extends AppCompatActivity implements OnMapReadyCallback, BluetoothDataReceiver {

    private static ProgressBar pbCombustible;
    private ImageButton btnBack;
    private static TextView tvCombustible;
    private static double nivelCombustible = 0;
    private double porCombustible = 0;
    private double galones = 64.0 / 3.785;
    private AlertDialog alert = null;
    private DataBaseHelper helper;
    private Estaciones estacionMasCercana;
    GoogleMap mMap;
    private static SharedPreferences myPreferences;
    Location myLoc;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private int REQUEST_CHECK_SETTINGS = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng myLatLng;
    private Marker myMarker;
    private List<LatLng> listRutas;
    private boolean recorridoIniciado;
    LocationCallback locationCallback;
    private int idUsuario;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combustible);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nivelCombustible = Double.valueOf(myPreferences.getString("nivel", "0.0"));
        helper = OpenHelperManager.getHelper(CombustibleActivity.this, DataBaseHelper.class);
        this.idUsuario = myPreferences.getInt("idUsuario",0);
        Toolbar myToolbar = findViewById(R.id.medidor_toolbar);
        setSupportActionBar(myToolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MAPA", "NULO");
        }

        init();
        BluetoothHelper bluetoothHelper = new BluetoothHelper(CombustibleActivity.this, "");
        bluetoothHelper.checkBTState();
        try {
            getAllStations();
            EstacionesPlaces places = new EstacionesPlaces();
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            LatLng newPosition;
            newPosition = new LatLng(Double.valueOf(myPreferences.getString("latitud", "0")),
                    Double.valueOf(myPreferences.getString("longitud", "0")));
            estacionMasCercana = places.getEstacionMasCercana(newPosition, helper);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        listRutas = new ArrayList<>();
        recorridoIniciado = false;

    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public float getDistanciaRecorrida(){

        return RutasHelper.getDistanciaRecorrida(listRutas);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CombustibleActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            myLoc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER); // Obtener ultima ubicacion del Provider seleccionado
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {

                if (location != null) {
                    myLoc = location;
                    myLatLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
                    myMarker = mMap.addMarker(new MarkerOptions().position(myLatLng).title("Mi ubicación"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));

                } else {
                    myLatLng = new LatLng(Double.valueOf(myPreferences.getString("latitud", "0")), Double.valueOf(myPreferences.getString("longitud", "0")));
                    myMarker = mMap.addMarker(new MarkerOptions().position(myLatLng).title("Mi ubicación"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));

                }
            });
        }
    }

    @Override
    public void getBluetoothData(double dato) {
        pbCombustible.setProgress((int) nivelCombustible);
        tvCombustible.setText(getString(R.string.cant_gal, nivelCombustible));
    }

    public double getNivelCombustible() {
        return nivelCombustible;
    }

    public String getRutas(){
        Gson gson = new Gson();
        return gson.toJson(gson.toJsonTree(listRutas));
    }

    /*
    public static class TanquearActivity extends AppCompatActivity {

        private ProgressBar pbCombustible;
        private ImageButton btnBack;
        private TextView tvCombustible,tvTitulo;
        private RecyclerView recyclerView;
        private RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
        private double nivelCombustible=0;
        private double porCombustible=0;
        private double galones = 10.2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tanquear);

            Typeface light=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
            Typeface thin=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");

            btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


            tvCombustible = findViewById(R.id.tvCombustible);
            tvCombustible.setTypeface(light);
            pbCombustible = findViewById(R.id.pbCombustible);
            pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

            tvTitulo = findViewById(R.id.tvTitulo);
            tvTitulo.setTypeface(thin);

            List items = new ArrayList();
            Estaciones e1 =  new Estaciones("Esso",4.650492, -74.114369);
            e1.setDistancia((float)1100);
            e1.setDireccion("Cl 22 # 68d-20");
            e1.setHorario("Abierto 24 horas");
            e1.setClasificacion((float)4.2);
            items.add(e1);

            Estaciones e2 =  new Estaciones("Mobil",4.650492, -74.114369);
            e2.setDistancia((float)1800);
            e2.setDireccion("Kr 9 # 123-46");
            e2.setHorario("Abierto 24 horas");
            e2.setClasificacion((float)4.0);
            items.add(e2);
        }

    }
*/
    @SuppressLint("SetTextI18n")
    private void init() {
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.pager);

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new IngresadoFragment(), "Ingresado");
        adapter.addFragment(new RendimientoFragment(), "Rendimiento");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        tvCombustible = findViewById(R.id.tvCombustible);
        tvCombustible.setTypeface(light);
        pbCombustible = findViewById(R.id.pbCombustible);
        pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        tvCombustible.setText(String.format(Locale.US, "%.1f", nivelCombustible) + " Gal.");
        TextView tvTitulo1 = findViewById(R.id.tvTitulo2);
        tvTitulo1.setTypeface(thin);
        pbCombustible.setMax(20);
        pbCombustible.setProgress((int) nivelCombustible);
        TextView tvTitulo = findViewById(R.id.tvTitulo2);
        tvTitulo.setText("Medir Combustible");
        tvTitulo.setTypeface(light);
        ImageButton btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void getAllStations() throws SQLException {

        final Dao<Estaciones, Integer> dao = helper.getDaoEstaciones();

        List<Estaciones> estaciones = dao.queryForAll();
    }

    public Estaciones getEstacionMasCercana() {
        return estacionMasCercana;
    }

    public void iniciarRecorrido() {
        this.recorridoIniciado = true;
    }

    public void detenerRecorrido() {
        this.recorridoIniciado = false;
        mFusedLocationClient.removeLocationUpdates(locationCallback);

    }

    public void initLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("LOCATION","ON LOCATION RESULT");
                // do work here
                myLoc = locationResult.getLastLocation();
                myLatLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
                myMarker.setPosition(myLatLng);
                if (recorridoIniciado){
                    listRutas.add(myLatLng);
                }
            }
        };
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }else{
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(CombustibleActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}
