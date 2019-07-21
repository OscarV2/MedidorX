package com.index.medidor.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.index.medidor.R;

public class ConfigActivity extends AppCompatActivity {
    private ProgressBar pbCombustible;
    private ImageButton btnBack;
    private TextView tvCombustible,tvTitulo;
    private double nivelCombustible=0;
    private double porCombustible=0;
    private double galones = 15.2;
    private ImageView ivFoto,ivAgregar;
    private EditText etNombre,etTel,etCombustible;
    private Button btnPassword,btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Typeface light=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface thin=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
        //Typeface bold=Typeface.createFromAsset(getAssets(),"fonts/Reboto-Bold.ttf");*/

        tvCombustible = (TextView) findViewById(R.id.tvCombustible);
        tvCombustible.setTypeface(light);
        pbCombustible = (ProgressBar) findViewById(R.id.pbCombustible);
        pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvTitulo.setTypeface(thin);



        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        ivAgregar = (ImageView) findViewById(R.id.ivAgregar);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etTel = (EditText) findViewById(R.id.etTel);
        etCombustible = (EditText) findViewById(R.id.etCombustible);
        btnPassword = (Button) findViewById(R.id.btnPassword);
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this,PasswordActivity.class);
            }
        });
        btnGuardar = (Button) findViewById(R.id.btnGuardar);

    }

}
