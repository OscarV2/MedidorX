package com.index.medidor.services;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.index.medidor.activities.MainActivity;

import javax.annotation.Nullable;

public class FireBaseRecorridosHelper {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private MainActivity mainActivity;
    private RecorridoService recorridoService;
    private String placa;

    public FireBaseRecorridosHelper(MainActivity mainActivity, String placa) {
        this.mainActivity = mainActivity;
        this.placa = placa;
        documentReference = firebaseFirestore.collection("recorridos").document(placa);
    }

    public void init() {

        Log.e("1","1");
        if(placa != null && !placa.equals("")){
            Log.e("2","2");
            /*documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(e != null){
                        System.out.println("ocurrio una excepcion");
                        return;
                    } else {

                    }
                    Log.e("YES","HUBO CAMBIOOOOOOOO");
                }
            });*/

            firebaseFirestore.collection("recorridos").document("rrr123").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Toast.makeText(mainActivity, "ALGO PASO OK", Toast.LENGTH_LONG).show();
                }
            });

            /*firebaseFirestore.collection("recorridos")
                    .document(placa).addSnapshotListener((documentSnapshot, e) -> {
                recorridoService = mainActivity.getRecorridoService();
                Log.e("ON","SNAPSHOT LISTENER");
                if(recorridoService != null) {
                    Toast.makeText(mainActivity, "SUBIENDO RECORRIDO " + placa, Toast.LENGTH_SHORT).show();
                    recorridoService.uploadAllNotCompletedAndNotUploaded();
                }
            });*/
        }
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
