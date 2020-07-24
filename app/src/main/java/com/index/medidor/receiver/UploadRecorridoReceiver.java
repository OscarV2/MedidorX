package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.index.medidor.database.DataBaseHelper;
import com.index.medidor.services.UploadRecorridosService;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class UploadRecorridoReceiver extends BroadcastReceiver {

    private DataBaseHelper helper;
    private UploadRecorridosService uploadRecorridosService;
    private Context context;
    String placa;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        init();
        if (this.uploadRecorridosService != null)
            this.uploadRecorridosService.uploadAllNotCompletedAndNotUploaded();
        //MainActivity mainActivity = MainActivity.getInstance();
        //mainActivity.upload();
    }

    private void init() {
        if (placa == null){
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            placa = myPreferences.getString(Constantes.DEFAULT_PLACA, "");
            Log.e("placa", placa);
        }


        if (uploadRecorridosService == null)
            uploadRecorridosService = new UploadRecorridosService(this.context, placa);
    }



}
