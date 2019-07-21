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


public class ValidacionActivity extends AppCompatActivity {
    private EditText etCodigo;
    private Button btnConfirmar;
    private String email, url = null;
    private int valor;
    private CustomProgressDialog mCustomProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);

        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        mCustomProgressDialog.setCancelable(false);

        email = getIntent().getStringExtra("email");
        etCodigo = findViewById(R.id.etcodigo);

        btnConfirmar = findViewById(R.id.btnConfirmar);
        //url = "http://dartsis.com.co/ja/inndex/app/enviarEmail.php?email="+email;
        //new Validar().execute();

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomProgressDialog.show("");
                valor = 0;
                String codigo = etCodigo.getText().toString();
                url = "https://www.inndextechnology.com/inndex/app/validarInfo.php?email="+email+"&codigo="+codigo;
                if (!codigo.isEmpty()){
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo !=null && networkInfo.isConnected()){
                        //new Validar().execute();
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
