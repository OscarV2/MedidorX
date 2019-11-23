package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.index.medidor.activities.MainActivity;

public class StopRecorridoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("ONRECEIVE","STOP");

        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.resetRecorrido();
        //context.sendBroadcast(new Intent(Constantes.STOP_RECORRIDO_INTENT_FILTER ));
    }
}
