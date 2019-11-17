package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.index.medidor.utils.Constantes;

public class StopRecorridoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("HOLA","ESTE ES EL REC DE UPLOADRECEIVER");
        context.sendBroadcast(new Intent(Constantes.STOP_RECORRIDO_INTENT_FILTER ));
    }
}
