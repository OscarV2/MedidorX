package com.index.medidor.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.index.medidor.utils.Constantes;

public class UploadRecorridoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("HOLA","ESTE ES EL REC DE UPLOADRECEIVER");
        context.sendBroadcast(new Intent(Constantes.RECORRIDO_INTENT_FILTER));
    }
}
