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
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.index.medidor.R;
import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.fragments.adquisicion_datos.AdquisicionDatos;
import com.index.medidor.fragments.combustible.CombustibleTabs;
import com.index.medidor.fragments.DondeTanquearFragment;
import com.index.medidor.fragments.configuracion_cuenta.ConfiguracionTabs;
import com.index.medidor.fragments.dondetanquear.DondeTanquearTabs;
import com.index.medidor.fragments.historial.HistorialTabs;
import com.index.medidor.fragments.combustible.IngresadoFragment;
import com.index.medidor.fragments.InicioFragment;
import com.index.medidor.model.Estaciones;
import com.index.medidor.places.EstacionesPlaces;
import com.index.medidor.rutas.PasarUbicacion;
import com.index.medidor.rutas.Route;
import com.index.medidor.utils.BluetoothDataReceiver;
import com.index.medidor.utils.BluetoothHelper;
import com.index.medidor.utils.CustomProgressDialog;
import com.index.medidor.utils.NavTypeFace;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, PasarUbicacion, BluetoothDataReceiver, AdquisicionDatos.OnFragmentInteractionListener,
        InicioFragment.OnFragmentInteractionListener, HistorialTabs.OnFragmentInteractionListener, DondeTanquearFragment.OnFragmentInteractionListener,
        CombustibleTabs.OnFragmentInteractionListener, ConfiguracionTabs.OnFragmentInteractionListener,LocationListener, IngresadoFragment.OnFragmentInteractionListener, DondeTanquearTabs.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;
    private DrawerLayout drawer;
    NavigationView navigationView;
    private LatLng newPosition;

    private BluetoothSocket btSocket;
    private LocationManager locationManager;
    private ProgressBar pbCombustible;
    private TextView tvCombustible;
    private AlertDialog alert = null;
    private Location myLocation;
    private CustomProgressDialog mCustomProgressDialog;
    private SharedPreferences myPreferences;
    private double nivelCombustible;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<Estaciones> estaciones;
    private DataBaseHelper helper;
    ImageButton btnBack;
    TextView tvTitulo;
    Typeface bold;
    View viewMap;
    private Estaciones estacionMasCercana;
    private EstacionesPlaces estacionesPlaces;
    Fragment miFragment;

    private BluetoothHelper bluetoothHelper;

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

        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkGPSState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        bluetoothHelper = new BluetoothHelper(MainActivity.this);
        bluetoothHelper.checkBTState();
        btSocket = bluetoothHelper.getBtSocket();

        Fragment fragment = new InicioFragment(bold, this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
//        ((InicioFragment) fragment).getFabRuta().setVisibility(View.GONE);

        initControls();
        try {

            estacionesPlaces = new EstacionesPlaces();
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            LatLng newPosition;
            newPosition = new LatLng(Double.valueOf(myPreferences.getString("latitud", "0")),
                    Double.valueOf(myPreferences.getString("longitud", "0")));
            estacionMasCercana = estacionesPlaces.getEstacionMasCercana(newPosition, helper);

        } catch (SQLException e) {
            e.printStackTrace();
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

            miFragment = new CombustibleTabs(this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Medir Combustible");

            // Handle the camera action
            /*try {
                if (btSocket != null && btSocket.isConnected()){

                    btSocket.close();
                    btSocket = null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //Intent intent = new Intent(getApplicationContext(), CombustibleActivity.class);
            //startActivity(intent);
            //finish();
        } else if (id == R.id.nav_historial) {
            miFragment = new HistorialTabs();
            fragmentSeleccionado = true;
            tvTitulo.setText("Historial");
            viewMap.setVisibility(View.GONE);
            //Intent intent = new Intent(getApplicationContext(), HistorialActivity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_config) {
            //Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
            //startActivity(intent);
            miFragment = new ConfiguracionTabs(MainActivity.this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Configuración de cuenta");
            viewMap.setVisibility(View.GONE);
        }else if (id == R.id.nav_adq_datos) {
            miFragment = new AdquisicionDatos(MainActivity.this);
            fragmentSeleccionado = true;
            tvTitulo.setText("Adquisición de datos");
            viewMap.setVisibility(View.GONE);
            //logout();
        }else if (id == R.id.logout) {
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
        mMap = googleMap;

        if (estaciones.size() > 0){
            for (Estaciones estacion:
                    estaciones) {
                LatLng latLng = new LatLng(estacion.getLatitud(), estacion.getLongitud());
                mMap.addMarker(new MarkerOptions().position(latLng).title(estacion.getMarca()).snippet(estacion.getDireccion()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));

            }
        }

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Log.e("onMapReady","Dentro de segundo if");
            if (myLocation != null){
                Log.e("onMapReady","Dentro de segundo if, mostrando ubicacion");
                mostrarUbicacion();
            }else{
                Log.e("onMapReady","Dentro de segundo if, mulocation es NULL");

            }

        } else {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
              */  ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            //}
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        //newMarker(10.468854, -73.257013);
        //newMarker(8.60, -74.08);
        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MAIN","on RESUME");
    }

    @Override
    public void onPause() {
        Log.e("MAIN","on pause");

        if (btSocket != null ){
            Log.e("MAIN","EL btSocket NOOOOO ES NULO");

            if (btSocket.isConnected()){
                Log.e("MAIN","EL btSocket ESTA CONECTADO");

                try {
                    btSocket.close();
                    Log.e("MAIN","BLUR¿E SOCKET CLOSED");
                } catch (IOException e) { }
            }else{
                Log.e("MAIN","EL btSocket NOOOO ESTA CONECTADO");
            }
        }else{
            Log.e("MAIN","EL btSocket ES NULO");

        }
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MAIN","on RESUME");

        locationManager = null;
        /*if (btSocket != null ){

            if (btSocket.isConnected()){
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }*/
        if (alert != null) {
            alert.dismiss();
        }
    }

    private void checkGPSState() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS esta desactivado").setCancelable(false).setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(enableGPSIntent);
                }
            });
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng marcadores = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        return false;
    }

    @Override
    public void trazarRutas(List<Route> rutas) {

        Log.e("Trazar rutas","trazando rutas");
        for (Route route : rutas) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public void getBluetoothData(int dato) {
        nivelCombustible = (double) (20*dato)/1023;
        pbCombustible.setProgress((int)nivelCombustible);
        tvCombustible.setText(getString(R.string.cant_gal,nivelCombustible));
        myPreferences.edit().putString("nivel", String.valueOf(nivelCombustible)).apply();

        if(miFragment instanceof AdquisicionDatos){

            (((AdquisicionDatos) miFragment)).getBluetoothData(dato);
        }
    }

    public void mostrarUbicacion() {
        //initLocation();
        if (myLocation != null){

            newPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 14));
        }else{
            Toast.makeText(this, "NO SE PUEDE MOSTRAR TU UBICACIÓN. INTENTALO MAS TARDE.", Toast.LENGTH_SHORT).show();
        }

    }

    void newRuta() {
        if (myLocation != null){
            EstacionesPlaces estacionesPlaces = new EstacionesPlaces();
            estacionesPlaces.getEstacionesCercanas(myLocation);
            /*LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng destino = new LatLng(4.657843, -74.099556);
            DirectionFinder buscador = new DirectionFinder(this, latLng, destino);
            buscador.peticionRutas();*/
        }else{
            Log.e("UBICACION","NULA EN NEW RUTA");
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

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);

        //TextView tvEmail = headerLayout.findViewById(R.id.tvEmail);
        TextView tvUsuario = headerLayout.findViewById(R.id.tvUsuario);
        //tvEmail.setText(myPreferences.getString("email", ""));
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
        }

        pbCombustible = findViewById(R.id.pbCombustible);
        pbCombustible.setMax(20);
        tvCombustible = findViewById(R.id.tvCombustible);
        btnBack = findViewById(R.id.btnBack2);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(v -> {
            Fragment fragment = new InicioFragment(bold, this);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            btnBack.setVisibility(View.GONE);
            tvTitulo.setVisibility(View.GONE);
            viewMap.setVisibility(View.VISIBLE);

        });
        nivelCombustible = Double.valueOf(Objects.requireNonNull(myPreferences.getString("nivel", "0.0")));
        tvCombustible.setText(String.format(Locale.US,"%.1f",nivelCombustible)+" Gal.");
        tvCombustible.setTypeface(light);
        pbCombustible.setProgress((int)nivelCombustible);
        pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        tvTitulo = findViewById(R.id.tvTitulo2);
        tvTitulo.setTypeface(thin);
        tvTitulo.setVisibility(View.GONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("PERMISOS","FUERON HABILITADOS");
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        //locationManager.req
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if (myLocation != null){
                            newPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                            SharedPreferences.Editor editor = myPreferences.edit();
                            editor.putString("latitud",String.valueOf(myLocation.getLatitude()));
                            editor.putString("longitud",String.valueOf(myLocation.getLongitude()));
                            editor.apply();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 14));
                            mMap.addMarker(new MarkerOptions().position(newPosition).flat(true).title("Mi ubicación"));
                            EstacionesPlaces places = new EstacionesPlaces();
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
                                LOCATION_REQUEST_CODE);
                    }
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
        Log.e("EstacionesActivity",String.valueOf(estaciones.size()));
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

    private void updateLocation(){
        //myLocation = location;
        myLocation = null;
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("latitud",String.valueOf(myLocation.getLatitude()));
        editor.putString("longitud",String.valueOf(myLocation.getLongitude()));
        editor.apply();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    public Location getMyLocation() {
        return myLocation;
    }
}
