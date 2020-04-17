package com.index.medidor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.index.medidor.activities.MainActivity;

public class UploadRecorridoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ON","UPLOADING RECEIVER");
        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.upload();
    }
}
