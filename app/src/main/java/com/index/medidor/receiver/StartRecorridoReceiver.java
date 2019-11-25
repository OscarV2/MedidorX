package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.index.medidor.activities.MainActivity;
import com.index.medidor.utils.Constantes;

public class StartRecorridoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("ONRECEIVE","START");
        //MainActivity mainActivity = MainActivity.getInstance();
        //mainActivity.initRecorrido();
        context.sendBroadcast(new Intent(Constantes.START_RECORRIDO_INTENT_FILTER));
    }
}
