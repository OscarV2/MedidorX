package com.index.medidor.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothHelper {

    private MyVeryOwnHandler bluetoothIn;
    private static StringBuilder recDataString;
    private static int handlerState;
    private static BluetoothDataReceiver bluetoothDataReceiver;
    private Context context;
    private BluetoothSocket btSocket;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int REQUEST_ENABLE_BT;
    private BluetoothAdapter btAdapter;
    private static int dato;
    private static List<Integer> dataToAverage;
    private static boolean isAdq;
    private static JsonObject jsonValues;
    private static int[] arrayKeys;

    public BluetoothHelper(Context context, String valoresString) {
        recDataString = new StringBuilder();
        handlerState = 0;
        this.context = context;
        REQUEST_ENABLE_BT = 1;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDataReceiver = (BluetoothDataReceiver) context;
        bluetoothIn = new MyVeryOwnHandler();
        dataToAverage = new ArrayList<>();

        Gson gson = new Gson();

        JsonArray jsonArray = gson.fromJson(valoresString, JsonArray.class);

        jsonValues = jsonArray.get(0).getAsJsonObject();

        List<Integer> listKeys = new ArrayList<>();

        arrayKeys = new int[jsonValues.keySet().size()];
        int cont = 0;
        for (String key: jsonValues.keySet()) {

            listKeys.add(Integer.parseInt(key));
            cont++;
        }
        /*for (int i =0; i < listKeys.size(); i++){

            arrayKeys[i] = listKeys.get(i);
        }*/
    }

    private static class MyVeryOwnHandler extends Handler{

        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {

            if (msg.what == handlerState) {          //if message is what we want
                String readMessage = (String) msg.obj;  // msg.arg1 = bytes from connect thread
                readMessage = readMessage.replace(" ","");

                recDataString.append(readMessage);              //keep appending to string until ~

                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line

                if (endOfLineIndex > 0) {                                           // make sure there data before ~

                    if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                    {

                        dato = Integer.parseInt(recDataString.substring(1,endOfLineIndex ));     //get sensor value from string between indices 1-5
                        dataToAverage.add(dato);

                        if (!isAdq){

                            if (dataToAverage.size() == Constantes.ARRAY_DATA_SIZE){

                                int sum = 0;

                                for (int i = 0; i < dataToAverage.size(); i++){

                                    sum += dataToAverage.get(i);
                                }

                                sum = (sum / dataToAverage.size());

                                //dataToAverage.remove(dataToAverage.get(Constantes.ARRAY_DATA_SIZE - 1)) ;
                                //dataToAverage.set(Constantes.ARRAY_DATA_SIZE - 1, dato);

                                dataToAverage = new ArrayList<>();

                                JsonElement element = jsonValues.get(String.valueOf(sum));

                                if (element != null){

                                    //buscar el mas cercano

                                    Log.e("1KEY", String.valueOf(sum));
                                    Log.e("1Value", String.valueOf(element.getAsDouble()));

                                    bluetoothDataReceiver.getBluetoothData(element.getAsDouble());

                                }else{  // no esta en la lista, buscar el mas cercanp

                                    element = jsonValues.get(String.valueOf(sum));
                                    try{
                                        Log.e("2KEY", String.valueOf(sum));
                                        Log.e("2Value", String.valueOf(element.getAsDouble()));

                                        bluetoothDataReceiver.getBluetoothData(element.getAsDouble());

                                    }catch (NullPointerException ex){

                                        Log.e("EX","EXcepcion");
                                    }
                                }
                            }

                        }else{

                            bluetoothDataReceiver.getBluetoothData(dato);
                        }
                    }
                    recDataString.delete(0, recDataString.length());      //clear all string data
                }
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //creation of the connect thread

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (btSocket!=null && btSocket.isConnected()) {
                try {
                    //if (){
                    bytes = mmInStream.read(buffer);         //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    //}

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkBTState() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                        REQUEST_ENABLE_BT);
            }
        }
        if (btAdapter == null) {
            Toast.makeText(context, "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
                //String address = "00:21:13:00:C2:B0";
                String address = "00:18:E4:0A:00:01";
                BluetoothDevice device = btAdapter.getRemoteDevice(address);

                try {
                    btSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
                } catch (IOException e) {
                    Toast.makeText(context, "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    btSocket.connect();
                    ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                } catch (IOException e) {
                    try {
                        btSocket.close();
                        Toast.makeText(context, "No se pudo establecer conexion con el dispositivo", Toast.LENGTH_LONG).show();
                    } catch (IOException e2) {
                        //insert code to deal with this
                    }
                }
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("El Bluetooth esta desactivado").setCancelable(false).setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableBtIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        context.startActivity(enableBtIntent);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        }
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public static int getDato() {
        return dato;
    }

    public boolean isAdq() {
        return isAdq;
    }

    public void setAdq(boolean adq) {
        isAdq = adq;
    }
/*
    public static int getClosestValue(int sum){

        float smallestDiff = Math.abs(sum - arrayKeys[0]);
        int closest = 0; //index of the current closest number
        int currentDiff;
        for (int i = 1; i < arrayKeys.length; i++) {
            currentDiff = Math.abs(sum - arrayKeys[i]);
            if (currentDiff < smallestDiff) {
                smallestDiff = currentDiff;
                closest = i;
            }
        }

        return arrayKeys[closest];
    }
    */
}
