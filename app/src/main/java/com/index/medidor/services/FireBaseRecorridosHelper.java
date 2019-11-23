package com.index.medidor.services;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.index.medidor.activities.MainActivity;

public class FireBaseRecorridosHelper {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private MainActivity mainActivity;
    private RecorridoService recorridoService;
    private String placa;

    public FireBaseRecorridosHelper(MainActivity mainActivity, String placa) {
        this.mainActivity = mainActivity;
        this.placa = placa;
    }

    public void init() {

        firebaseFirestore.collection("recorridos").document(placa).addSnapshotListener((documentSnapshot, e) -> {
            recorridoService = mainActivity.getRecorridoService();

            if(recorridoService != null) {
                recorridoService.uploadAllNotCompletedAndNotUploaded();
            }
        });
    }
}
