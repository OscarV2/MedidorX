package com.index.medidor.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.index.medidor.R;
import com.index.medidor.adapter.TabPagerAdapter;
import com.index.medidor.fragments.historial.RecorridosFragment;
import com.index.medidor.fragments.historial.TanqueadasFragment;

public class HistorialActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pager;
    private TabPagerAdapter adapter;
    private ProgressBar pbCombustible;
    private ImageButton btnBack;
    private TextView tvCombustible,tvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        Typeface light=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface thin=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
        Toolbar myToolbar = findViewById(R.id.medidor_toolbar);
        setSupportActionBar(myToolbar);
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);

        adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TanqueadasFragment(),"Tanqueadas");
        adapter.addFragment(new RecorridosFragment(),"Recorridos");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(v -> {
            Log.e("Historial","back pressed");
            Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
            startActivity(intent);
            //finish();
        });


        tvCombustible = findViewById(R.id.tvCombustible);
        tvCombustible.setTypeface(light);
        pbCombustible = findViewById(R.id.pbCombustible);
        pbCombustible.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

        tvTitulo = findViewById(R.id.tvTitulo2);
        tvTitulo.setTypeface(thin);
        tvTitulo.setText(getString(R.string.historial));
    }

}
