package com.index.medidor.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.index.medidor.R;
import com.index.medidor.utils.CustomProgressDialog;

public class RegistroActivity extends AppCompatActivity {
    private String url = null ;
    private int valor;
    private CustomProgressDialog mCustomProgressDialog;
    private EditText etEmail, etNombres, etApellidos, etPassword, etIdDispositivo;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);

        etEmail = findViewById(R.id.etEmail);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etPassword = findViewById(R.id.etPassword);
        etIdDispositivo = findViewById(R.id.etIdDispositivo);
        btnRegistrar =  findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomProgressDialog.show("");
                valor = 0;
                String email = etEmail.getText().toString().toLowerCase();
                String nombres = etNombres.getText().toString();
                String apellidos = etApellidos.getText().toString();
                String password = etPassword.getText().toString();
                String id_dispositivo = etIdDispositivo.getText().toString();
                url = "https://www.inndextechnology.com/inndex/app/registarUsuario.php?email="+email+"&nombres="+nombres+"&apellidos="+apellidos+"&password="+password+"&id_dispositivo="+id_dispositivo;
                if (!email.isEmpty() && !password.isEmpty() && !nombres.isEmpty() && !apellidos.isEmpty() && !id_dispositivo.isEmpty()){
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo !=null && networkInfo.isConnected()){
                        //new Registro().execute();
                    }else{
                        Toast.makeText(getBaseContext(),"No hay conexion a internet",Toast.LENGTH_LONG).show();
                        mCustomProgressDialog.dismiss("");
                    }
                }else{
                    Toast.makeText(getBaseContext(),"No ha llenado todos los datos",Toast.LENGTH_LONG).show();
                    mCustomProgressDialog.dismiss("");
                }
            }
        });

    }

}
