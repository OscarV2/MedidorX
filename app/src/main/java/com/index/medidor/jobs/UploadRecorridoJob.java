package com.index.medidor.jobs;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.index.medidor.services.UploadRecorridosService;
import com.index.medidor.utils.Constantes;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class UploadRecorridoJob extends JobService {

    private UploadRecorridosService uploadRecorridosService;
    private Context context;
    String placa;

    @Override
    public boolean onStartJob(JobParameters params) {
        context = getApplicationContext();
        init();
        if (this.uploadRecorridosService != null)
            this.uploadRecorridosService.uploadAllNotCompletedAndNotUploaded();
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void init() {
        if (placa == null){
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            placa = myPreferences.getString(Constantes.DEFAULT_PLACA, "");
        }
        //if (uploadRecorridosService == null)
         //   uploadRecorridosService = new UploadRecorridosService(helper, this.context, placa);
    }
}
