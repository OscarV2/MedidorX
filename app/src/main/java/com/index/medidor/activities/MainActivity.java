package com.index.medidor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.index.medidor.R;
import com.index.medidor.bluetooth.interfaces.IBluetoothState;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.fragments.adquisicion_datos.AdquisicionDatos;
import com.index.medidor.fragments.DondeTanquearFragment;
import com.index.medidor.fragments.configuracion_cuenta.ConfiguracionTabs;
import com.index.medidor.fragments.configuracion_cuenta.NuevoVehiculo;
import com.index.medidor.fragments.dondetanquear.DondeTanquearTabs;
import com.index.medidor.fragments.historial.HistorialTabs;
import com.index.medidor.fragments.combustible.IngresadoFragment;
import com.index.medidor.fragments.InicioFragment;
import com.index.medidor.model.Estaciones;
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.index.medidor.places.EstacionesPlaces;
import com.index.medidor.bluetooth.interfaces.BluetoothDataReceiver;
import com.index.medidor.bluetooth.BluetoothHelper;
import com.index.medidor.receiver.StartRecorridoReceiver;
import com.index.medidor.receiver.StopRecorridoReceiver;
import com.index.medidor.services.InndexLocationService;
import com.index.medidor.services.MapService;
import com.index.medidor.services.RecorridoService;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.CustomProgressDialog;
import com.index.medidor.utils.NavTypeFace;
import com.index.medidor.utils.SetArrayValuesForInndex;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, SetArrayValuesForInndex,
        BluetoothDataReceiver, AdquisicionDatos.OnFragmentInteractionListener, IBluetoothState, DondeTanquearTabs.OnFragmentInteractionListener,
        InicioFragment.OnFragmentInteractionListener, HistorialTabs.OnFragmentInteractionListener, DondeTanquearFragment.OnFragmentInteractionListener,
        ConfiguracionTabs.OnFragmentInteractionListener, IngresadoFragment.OnFragmentInteractionListener, NuevoVehiculo.OnFragmentInteractionListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private BluetoothSocket btSocket;
    private ProgressBar pbCombustible, pbTanque2;
    private TextView tvCombustible, tvCombustibleTank2;
    private TextView tvDefaultPlaca;
    private AlertDialog alert = null;
    private CustomProgressDialog mCustomProgressDialog;
    private SharedPreferences myPreferences;
    private double nivelCombustible, nivelCombustibleTank2;
    private List<Estaciones> estaciones;
    private DataBaseHelper helper;
    private ImageButton btnBack, btnMenu;
    private TextView tvTitulo;
    private Typeface bold;
    private View viewMap;
    private Estaciones estacionMasCercana;
    private EstacionesPlaces estacionesPlaces;
    private Fragment miFragment;

    private Timer timerInndexDeviceListener;
    private BluetoothHelper bluetoothHelper;
    private Integer tipoUsuario;
    private RecorridoService recorridoService;
    private int idUsuario, idUsuarioModeloCarro;

    private MapService mapService;
    private InndexLocationService inndexLocationService;
    private boolean modelHasTwoTanks;
    private String values;

    private double galones;
    private double galonesT2;

    private Integer valorBluetooh;
    private Integer valorBluetoohT2;

    //private Timer mTimer;
    private boolean newDevice;

    private long timeMillisToStartRecorrido;
    private long timeMillisToStopRecorrido;

    BroadcastReceiver stopRecorridoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("MESS", "MESSAGE RECEIVED STOPPING TRIP");
            resetRecorrido();
        }
    };

    BroadcastReceiver startRecorridoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("MESS", "MESSAGE RECEIVED STARTING TRIP");
            initRecorrido();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        helper = OpenHelperManager.getHelper(MainActivity.this, DataBaseHelper.class);

        bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        viewMap = findViewById(R.id.map);

        try {
            getAllStations();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mapService = new MapService(this);

        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        inndexLocationService = new InndexLocationService(MainActivity.this);
        inndexLocationService.init();
        checkGPSState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        idUsuarioModeloCarro = myPreferences.getInt(Constantes.DEFAULT_UHMC_ID, 0);
        idUsuario = myPreferences.getInt(Constantes.DEFAULT_USER_ID, 0);

        values = myPreferences.getString(Constantes.DEFAULT_BLUETOOTH_VALUE_ARRAY, "");
        modelHasTwoTanks = myPreferences.getBoolean( Constantes.MODEL_HAS_TWO_TANKS, false);

        tipoUsuario = myPreferences.getInt("tipoUsuario", 8);


        pbCombustible = findViewById(R.id.pbCombustible);
        tvCombustible = findViewById(R.id.tvCombustible);
        pbTanque2 = findViewById(R.id.pbCombustibleTank2);
        tvCombustibleTank2 = findViewById(R.id.tvCombustibleTank2);
        pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        pbTanque2.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        if (values != null && !values.equals("")) {
            bluetoothHelper = new BluetoothHelper(MainActivity.this, values);
            bluetoothHelper.checkBTState();
            btSocket = bluetoothHelper.getBtSocket();

            initCombustibleProgresBars();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//            this.registerReceiver(mReceiver, filter);
        }
        Fragment fragment = new InicioFragment(bold, this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
//        ((InicioFragment) fragment).getFabRuta().setVisibility(View.GONE);
        initControls();
        try {
            estacionesPlaces = new EstacionesPlaces();
            LatLng newPosition;
            newPosition = new LatLng(Double.valueOf(myPreferences.getString("latitud", "0")),
                    Double.valueOf(myPreferences.getString("longitud", "0")));
            estacionMasCercana = estacionesPlaces.getEstacionMasCercana(newPosition, helper);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        initRecorrido();
        initReceivers();
    }

    private void initReceivers() {

        Calendar calendarStop = Calendar.getInstance();
        calendarStop.set(Calendar.HOUR_OF_DAY, 23);
        calendarStop.set(Calendar.MINUTE, 59);
        calendarStop.set(Calendar.SECOND, 0);

        AlarmManager stopRecorridoAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intentStopRecorrido = new Intent(MainActivity.this, StopRecorridoReceiver.class);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(MainActivity.this, 0, intentStopRecorrido, 0);
        stopRecorridoAlarmManager.setRepeating(AlarmManager.RTC,  calendarStop.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntentStop);
        registerReceiver(stopRecorridoReceiver, new IntentFilter(Constantes.STOP_RECORRIDO_INTENT_FILTER ));

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Calendar.HOUR_OF_DAY, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.SECOND, 2);

        AlarmManager startRecorridoAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intentStartRrecorrido = new Intent(MainActivity.this, StartRecorridoReceiver.class);
        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(MainActivity.this, 0, intentStartRrecorrido, 0);
        startRecorridoAlarmManager.setRepeating(AlarmManager.RTC,  calendarStart.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntentStart);
        registerReceiver(startRecorridoReceiver, new IntentFilter(Constantes.START_RECORRIDO_INTENT_FILTER));

    }

    private void initCombustibleProgresBars() {

        Integer maxValue = getMaxValueFromAdquisitionArray();

        pbCombustible.setMax(maxValue);
        pbTanque2.setMax(maxValue);

        nivelCombustible = Double.valueOf(Objects.requireNonNull(myPreferences.getString("nivel", "0.0")));
        tvCombustible.setText(String.format(Locale.US,"%.1f", nivelCombustible)+" Gal.");
        pbCombustible.setProgress((int)nivelCombustible);

        if(!modelHasTwoTanks) {         // NO tiene dos tanques
            pbTanque2.setVisibility(View.GONE);
            tvCombustibleTank2.setVisibility(View.GONE);

        }else {
            pbTanque2.setVisibility(View.VISIBLE);
            tvCombustibleTank2.setVisibility(View.VISIBLE);
            nivelCombustibleTank2 = Double.valueOf(Objects.requireNonNull(myPreferences.getString("nivelTank2", "0.0")));
            tvCombustibleTank2.setText(String.format(Locale.US,"%.1f", nivelCombustibleTank2)+" Gal.");
        }
    }

    private Integer getMaxValueFromAdquisitionArray() {

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(values, JsonArray.class);

        JsonObject jsonValues = jsonArray.get(0).getAsJsonObject();
        List<Integer> listValues = new ArrayList<>();

        for (String key: jsonValues.keySet()) {
            listValues.add((int)jsonValues.get(key).getAsDouble());
        }

        if(listValues.size() > 0) {

            return Collections.max(listValues);
        } else {
            return 10;
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new NavTypeFace("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //mNewTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, mNewTitle.length(), 0); Use this if you want to center the items
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(this, "BACK BUTTON PRESSED", Toast.LENGTH_SHORT).show();
        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        miFragment = null;
        boolean fragmentSeleccionado = false;

        if (id == R.id.nav_combustible) {

            miFragment = new IngresadoFragment(this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Medir Combustible");
            btnMenu.setVisibility(View.GONE);

        } else if (id == R.id.nav_historial) {
            miFragment = new HistorialTabs();
            fragmentSeleccionado = true;
            tvTitulo.setText("Historial");
            viewMap.setVisibility(View.GONE);
            btnMenu.setVisibility(View.GONE);

        } else if (id == R.id.nav_config) {
            miFragment = new ConfiguracionTabs(MainActivity.this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Configuración de cuenta");
            viewMap.setVisibility(View.GONE);
            btnMenu.setVisibility(View.GONE);

        }else if (id == R.id.nav_adq_datos) {
            miFragment = new AdquisicionDatos(MainActivity.this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Adquisición de datos");
            viewMap.setVisibility(View.GONE);
            btnMenu.setVisibility(View.GONE);

        }else if (id == R.id.nav_recorrido) {

            if(this.inndexLocationService.getMyLocation() == null) {
                Toast.makeText(this, "Ubicación actual desconocida.", Toast.LENGTH_SHORT).show();
                return true;
            }

            if(bluetoothHelper == null ) {
                Toast.makeText(this, "No se pudo conectar con el dispositivo.", Toast.LENGTH_SHORT).show();
                return true;
            }

            recorridoService = new RecorridoService(MainActivity.this, true, this.helper, idUsuario, idUsuarioModeloCarro);
            inndexLocationService.setDistancia(0);
            recorridoService.iniciarRecorrido();
            Toast.makeText(this, "RECORRIDO INICIADO", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_recorrido_stop) {

            if(recorridoService != null) {
                recorridoService = null;
            }
        }
        else if (id == R.id.logout) {
            //logout();
        }

        if (fragmentSeleccionado){
            btnBack.setVisibility(View.VISIBLE);
            tvTitulo.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, miFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapService.setmMap(googleMap);

        if(inndexLocationService.getMyLocation() != null) {

            mapService.setMyLocation(inndexLocationService.getMyLocation());
            mapService.mostrarUbicacion();
        }

        if (estaciones.size() > 0){
            for (Estaciones estacion:
                    estaciones) {
                LatLng latLng = new LatLng(estacion.getLatitud(), estacion.getLongitud());
                mapService.getmMap().addMarker(new MarkerOptions().position(latLng).title(estacion.getMarca()).snippet(estacion.getDireccion()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapService.getmMap().setMyLocationEnabled(true);

            inndexLocationService.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
            //locationManager.req
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            mapService.setMyLocation(inndexLocationService.getLocationManager().getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
            if (mapService.getMyLocation() != null){
                LatLng newPosition = new LatLng(mapService.getMyLocation().getLatitude(), mapService.getMyLocation().getLongitude());
                SharedPreferences.Editor editor = myPreferences.edit();
                editor.putString("latitud",String.valueOf(mapService.getMyLocation().getLatitude()));
                editor.putString("longitud",String.valueOf(mapService.getMyLocation().getLongitude()));
                editor.apply();
                mapService.getmMap().animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 14));
                //mMap.addMarker(new MarkerOptions().position(newPosition).flat(true).title("Mi ubicación"));
                EstacionesPlaces places = new EstacionesPlaces();
                mapService.getmMap().setMyLocationEnabled(true);
                try {
                    Estaciones estacionMasCercana = places.getEstacionMasCercana(newPosition, helper);
                    Gson gson = new Gson();
                    Log.e("Estacion","MAS CERCANA");
                    Log.e("Estacion",gson.toJson(estacionMasCercana));

                } catch (SQLException e) {
                    Log.e("EXCEPCION","No se pudo obtener la estacion mas cercana");
                    e.printStackTrace();
                }

            }
        }else{
            Log.e("PERMISOS","HACE FALTA ALGUN PERMISO");
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    InndexLocationService.LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        inndexLocationService.setLocationManager(null);

        if (alert != null) {
            alert.dismiss();
        }
    }

    @Override
    public void getBluetoothData(double... dato) {

        if(miFragment instanceof AdquisicionDatos && BluetoothHelper.isAdqProcess()){
            (((AdquisicionDatos) miFragment)).getBluetoothData((int)dato[0], (int)dato[1]);
            return;
        }

        if (dato.length == 1) {
            nivelCombustible = dato[0];
            pbCombustible.setProgress((int)dato[0]);
            galones = dato[0];
            tvCombustible.setText(getString(R.string.cant_gal, nivelCombustible));
            myPreferences.edit().putString("nivel", String.valueOf(nivelCombustible)).apply();
        } else if (dato.length > 1) {

            nivelCombustible = dato[0];
            nivelCombustibleTank2 = dato[1];
            pbCombustible.setProgress((int)dato[0]);
            pbTanque2.setProgress((int)dato[1]);
            galones = dato[0];
            galonesT2 = dato[1];
            tvCombustible.setText(getString(R.string.cant_gal, nivelCombustible));
            tvCombustibleTank2.setText(getString(R.string.cant_gal, nivelCombustibleTank2));
            myPreferences.edit().putString("nivel", String.valueOf(nivelCombustible)).apply();
            myPreferences.edit().putString("nivelTank2", String.valueOf(nivelCombustibleTank2)).apply();
        }
    }

    @Override
    public void getArrayBlueToothValues(Integer... data) {

        if(data.length > 1) {
            valorBluetooh = data[0];
            valorBluetoohT2 = data[1];
        } else {
            valorBluetooh = data[0];
        }
    }

    private void logout() {
        drawer.closeDrawers(); // Cerrar drawer
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.logout));
        dialog.setMessage("¿Seguro que quiere Cerrar Sesión?");
        dialog.setPositiveButton("Aceptar", (dialogInterface, i) -> {

        });
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.cancel());
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    private void initControls(){
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(view -> {
            this.getDrawer().openDrawer(this.getNavigationView());
        });

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);

        TextView tvUsuario = headerLayout.findViewById(R.id.tvUsuario);
        tvDefaultPlaca = headerLayout.findViewById(R.id.tvDefaultPlaca);
        tvDefaultPlaca.setText(myPreferences.getString(Constantes.DEFAULT_PLACA, ""));
        tvUsuario.setText(myPreferences.getString("nombres", "") + myPreferences.getString("apellidos", ""));
        navigationView.setNavigationItemSelectedListener(this);
        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);

            if (tipoUsuario == 1){

                if (mi.getItemId() == R.id.nav_adq_datos){

                    mi.setVisible(false);
                }
            }
        }

        btnBack = findViewById(R.id.btnBack2);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(v -> {
            Fragment fragment = new InicioFragment(bold, this);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            btnBack.setVisibility(View.GONE);
            tvTitulo.setVisibility(View.GONE);
            viewMap.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.VISIBLE);
        });
        tvTitulo = findViewById(R.id.tvTitulo2);
        tvTitulo.setTypeface(thin);
        tvTitulo.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case InndexLocationService.LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("PERMISOS","FUERON HABILITADOS");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getAllStations() throws SQLException {
        final Dao<Estaciones, Integer> dao = helper.getDaoEstaciones();
        estaciones = dao.queryForAll();
    }

    public void irDondeTanquear(){

        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

        miFragment = new DondeTanquearTabs(this);
        fragmentSeleccionado = true;
        tvTitulo.setText("¿Donde Tanquear?");
        btnBack.setVisibility(View.VISIBLE);
        tvTitulo.setVisibility(View.VISIBLE);
        viewMap.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, miFragment).commit();
        /*
        tvTitulo.setVisibility(View.VISIBLE);
        tvTitulo.setText("¿Donde Tanquear?");
        viewMap.setVisibility(View.GONE);

        Fragment miFragment = new DondeTanquearFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, miFragment).commit();
        btnBack.setVisibility(View.VISIBLE);*/

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateLocation(Location myLocation){
        //myLocation = location;
        mapService.setMyLocation(null);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("latitud",String.valueOf(myLocation.getLatitude()));
        editor.putString("longitud",String.valueOf(myLocation.getLongitude()));
        editor.apply();
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public double getNivelCombustible() {
        return nivelCombustible;
    }

    public Estaciones getEstacionMasCercana() {
        return estacionMasCercana;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public List<Estaciones> getEstaciones() {
        return estaciones;
    }

    public SharedPreferences getMyPreferences() {
        return myPreferences;
    }

    public void irCambiarContrasena(){
    }

    public BluetoothHelper getBluetoothHelper() {
        return bluetoothHelper;
    }

    public void setBluetoothHelper(BluetoothHelper bluetoothHelper) {
        this.bluetoothHelper = bluetoothHelper;
    }

    public List<Estaciones> getEstacionesCercanas() throws SQLException {

        return estacionesPlaces.getEstacionesCercanas(new LatLng(Double.valueOf(myPreferences.getString("latitud", "0")),
                Double.valueOf(myPreferences.getString("longitud", "0"))), helper);
    }

    @Override
    public void setValues(String values) { }

    public void addNewStationToMap(Estaciones estacion){
        estaciones.add(estacion);

        LatLng latLng = new LatLng(estacion.getLatitud(), estacion.getLongitud());
        mapService.getmMap().addMarker(new MarkerOptions().position(latLng).title(estacion.getMarca()).snippet(estacion.getDireccion()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
    }

    @Override
    public void onPairedDeviceOff() {

        if(newDevice){
            return;
        }

        timerInndexDeviceListener = new Timer();
        //handlerInndexDeviceListener = new Handler();
        TimerTask tTInndexDeviceListener = new TimerTask() {
            @Override
            public void run() {

                if (!btSocket.isConnected()){

                    bluetoothHelper.checkBTState();
                }
            }
        };
        timerInndexDeviceListener.schedule(tTInndexDeviceListener, 1,3000);
    }

    public void cancelTimers(){
        if (timerInndexDeviceListener != null) {
            timerInndexDeviceListener.cancel();
            timerInndexDeviceListener.purge();
        }
    }

    public void initAdq(String bluetoothMac){
        myPreferences.edit().putString(Constantes.DEFAULT_BLUETOOTH_MAC, bluetoothMac).apply();
        bluetoothHelper = new BluetoothHelper(MainActivity.this);
        bluetoothHelper.checkBTState();
        btSocket = bluetoothHelper.getBtSocket();
    }

    @Override
    public void couldNotConnectToDevice() {
        //if(miFragment instanceof AdquisicionDatos)
        if(miFragment instanceof AdquisicionDatos)
            ((AdquisicionDatos) miFragment).setDeviceConnected(false);

        Toast.makeText(getApplicationContext(), "No se pudo establecer conexion con el dispositivo", Toast.LENGTH_LONG).show();
    }

    public ImageButton getBtnBack() {
        return btnBack;
    }

    public MapService getMapService() {
        return mapService;
    }

    public void setMapService(MapService mapService) {
        this.mapService = mapService;
    }

    public InndexLocationService getInndexLocationService() {
        return inndexLocationService;
    }

    private void checkGPSState() {

        if (!inndexLocationService.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS esta desactivado").setCancelable(false).setPositiveButton("Activar", (dialog, which) -> {
                Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableGPSIntent);
            });
            alert = builder.create();
            alert.show();
        }
    }

    public Integer getValorBluetooh() {
        return valorBluetooh;
    }

    public Integer getValorBluetoohT2() {
        return valorBluetoohT2;
    }

    //Método que detiene el recorrido actual, lo sube al servidor y empieza un nuevo recorrido
    private void resetRecorrido() {

        Log.e("ERR","RESETING RECORRIDO");
        if(this.inndexLocationService.getMyLocation() == null) {
            Toast.makeText(this, "Ubicación actual desconocida.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(bluetoothHelper == null ) {
            Toast.makeText(this, "No se pudo conectar con el dispositivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (recorridoService != null) {

            recorridoService.pararRecorrido();
            inndexLocationService.setDistancia(0);
            recorridoService = new RecorridoService(MainActivity.this, true,
                    this.helper, idUsuario, idUsuarioModeloCarro);
            recorridoService.iniciarRecorrido();
            Toast.makeText(this, "RECORRIDO INICIADO", Toast.LENGTH_SHORT).show();

            return;
        }

        recorridoService = new RecorridoService(MainActivity.this, true, this.helper, idUsuario, idUsuarioModeloCarro);
        inndexLocationService.setDistancia(0);
        recorridoService.iniciarRecorrido();
        Toast.makeText(this, "RECORRIDO INICIADO", Toast.LENGTH_SHORT).show();
    }

    private void initRecorrido() {

        if(this.inndexLocationService.getMyLocation() == null) {
            Toast.makeText(this, "Ubicación actual desconocida.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(bluetoothHelper == null ) {
            Toast.makeText(this, "No se pudo conectar con el dispositivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        recorridoService = new RecorridoService(MainActivity.this, true, this.helper, idUsuario, idUsuarioModeloCarro);
        inndexLocationService.setDistancia(0);
        recorridoService.iniciarRecorrido();
        Toast.makeText(this, "RECORRIDO INICIADO", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentRecorrido() {

        if(recorridoService != null && recorridoService.getRecorrido() != null)
            recorridoService.uploadRecorrido();
    }

    public void upateDefaultVehicle(UsuarioHasModeloCarro uhmc) {

        if(this.bluetoothHelper != null) {

            try {
                newDevice = true;
                this.bluetoothHelper.getBtSocket().close();
                this.bluetoothHelper = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        uhmc.setModeloCarros(null);
        Log.e("UHMC", gson.toJson(uhmc));
        myPreferences.edit().putString(Constantes.DEFAULT_BLUETOOTH_VALUE_ARRAY, uhmc.getValoresAdq()).apply();
        //myPreferences.edit().putInt(Constantes.DEFAULT_GAL_CANT, (int)uhmc.getModeloCarros().getGalones()).apply();
        myPreferences.edit().putString(Constantes.DEFAULT_BLUETOOTH_MAC, uhmc.getBluetoothMac()).apply();
        myPreferences.edit().putBoolean(Constantes.MODEL_HAS_TWO_TANKS, uhmc.getHasTwoTanks()).apply();
        myPreferences.edit().putLong(Constantes.DEFAULT_UHMC_ID, uhmc.getId()).apply();
//        myPreferences.edit().putLong("defaultModeloCarroId", uhmc.getModeloCarros().getId()).apply();
        myPreferences.edit().putString(Constantes.DEFAULT_PLACA, uhmc.getPlaca()).apply();

        values = uhmc.getValoresAdq();
        modelHasTwoTanks = uhmc.getHasTwoTanks();

        if(modelHasTwoTanks){
            tvCombustibleTank2.setVisibility(View.VISIBLE);
            pbTanque2.setVisibility(View.VISIBLE);
        }
        tvDefaultPlaca.setText(myPreferences.getString(Constantes.DEFAULT_PLACA, ""));

        if (values != null && !values.equals("")) {
            bluetoothHelper = new BluetoothHelper(MainActivity.this, values);
            bluetoothHelper.checkBTState();
            btSocket = bluetoothHelper.getBtSocket();

            initCombustibleProgresBars();

            IntentFilter filter = new IntentFilter();
            newDevice = false;
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//            this.registerReceiver(mReceiver, filter);
        }

        initCombustibleProgresBars();
    }

    public void resetAll() {

//        recorridoService.stopTimmers();
//        recorridoService = null;
//        bluetoothHelper = null;
    }

    public double getGalones() {
        return galones;
    }

    public double getGalonesT2() {
        return galonesT2;
    }
}
