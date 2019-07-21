package com.index.medidor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.index.medidor.R;
import com.index.medidor.model.Usuario;
import com.index.medidor.retrofit.MedidorApiAdapter;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.CustomProgressDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etPassword, etEmail;
    private TextView tvRecordar;
    private Button btnIngresar, btnRegistrar;
    private String email ;

    private CustomProgressDialog mCustomProgressDialog;
    private SharedPreferences.Editor infoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface light=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface thin=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
        Typeface bold=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);

        etEmail =  findViewById(R.id.etEmail);
        etEmail.setTypeface(light);
        etPassword =  findViewById(R.id.etPassword);
        etPassword.setTypeface(light);
        btnIngresar =  findViewById(R.id.btnIngresar);
        btnIngresar.setTypeface(bold);

        btnIngresar.setOnClickListener(view -> {

            email = etEmail.getText().toString().toLowerCase();
            String password = etPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty() && isEmailValid(email)){
                Usuario user = new Usuario();
                user.setEmail(email);
                user.setPassword(password);
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()){
                    //login(user);
                    mCustomProgressDialog.show("");
                    login(user);
                    //Toast.makeText(LoginActivity.this, "OK", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),"No hay conexion a internet", Toast.LENGTH_LONG).show();

                }
            }else{
                Toast.makeText(getBaseContext(),"Por favor ingrese Correo y Contraseña válidos.", Toast.LENGTH_LONG).show();
            }
        });
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setTypeface(bold);
        btnRegistrar.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        tvRecordar = findViewById(R.id.tvRecordar);
        tvRecordar.setTypeface(bold);
        tvRecordar.setOnClickListener(view -> {


        });

    }

    private void login(Usuario user){

        Call<Usuario> login = MedidorApiAdapter.getApiService().postLogin(Constantes.CONTENT_TYPE_JSON, user);
        login.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                mCustomProgressDialog.dismiss("");
                Log.e("1", response.message());
                if (response.isSuccessful()){

                    if (response.body() != null){
                        irMain(response.body());
                    }else{
                        Toast.makeText(LoginActivity.this, "Error: ESTE USUARIO NO EXISTE.", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                mCustomProgressDialog.dismiss("");
                Log.e("2", t.getMessage());

                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void irMain(Usuario user){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        infoUsuario = myPreferences.edit();
        infoUsuario.putBoolean("sesion",true);
        infoUsuario.putString("email", user.getEmail());
        infoUsuario.putString("nombres", user.getNombres());
        infoUsuario.putInt("idUsuario", user.getId());
        infoUsuario.putString("celular", user.getCelular());
        infoUsuario.putString("apellidos", user.getApellidos());
        infoUsuario.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isEmailValid(String email){
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }
}