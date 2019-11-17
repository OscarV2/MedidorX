package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.index.medidor.utils.Constantes;

public class StartRecorridoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(Constantes.START_RECORRIDO_INTENT_FILTER));
    }
}
