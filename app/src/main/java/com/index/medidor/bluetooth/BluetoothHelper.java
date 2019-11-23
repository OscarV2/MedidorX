package com.index.medidor.bluetooth;

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
import com.index.medidor.activities.MainActivity;
import com.index.medidor.bluetooth.interfaces.BluetoothDataReceiver;
import com.index.medidor.bluetooth.interfaces.IBluetoothState;
import com.index.medidor.utils.Constantes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothHelper {

    private MyVeryOwnHandler bluetoothIn;
    private static StringBuilder recDataString = new StringBuilder();
    private static int handlerState;
    private static BluetoothDataReceiver bluetoothDataReceiver;
    private Context context;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int REQUEST_ENABLE_BT;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private BluetoothDevice bluetoothDevice;
    private int dato, datoFlujo, datoTanque2;
    private static List<Integer> dataToAverage;
    private static List<Integer> dataToAverageTank2;
    public static boolean adqProcess;
    private static JsonObject jsonValues;
    private SharedPreferences preferences;
    private ConnectedThread mConnectedThread;
    private IBluetoothState iBluetoothState;
    private static boolean modelHasTwoTanks;


    /**
     * Constructor for any car model, even those with two tanks
     * @param context
     * @param valoresString
     */
    public BluetoothHelper(Context context, String valoresString) {

        this.context = context;
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(valoresString, JsonArray.class);
        dataToAverage = new ArrayList<>();
        dataToAverageTank2 = new ArrayList<>();
        init();
        jsonValues = jsonArray.get(0).getAsJsonObject();
        List<Integer> listKeys = new ArrayList<>();
        modelHasTwoTanks = preferences.getBoolean(Constantes.MODEL_HAS_TWO_TANKS, false);

        for (String key: jsonValues.keySet()) {
            listKeys.add(Integer.parseInt(key));
        }
    }

    /**
     * Constructor for adquisition
     * @param context
     */
    public BluetoothHelper(Context context) {
        this.context = context;
        adqProcess = true;
        modelHasTwoTanks = false;

        init();
    }

    private class MyVeryOwnHandler extends Handler{

        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {

            if (msg.what == handlerState) {             //if message is what we want
                String readMessage = (String) msg.obj;      // msg.arg1 = bytes from connect thread
                readMessage = readMessage.replaceAll(" ","");
                if(readMessage.length() > 20){
                    readMessage = "";
                }
                recDataString.append(readMessage);              //keep appending to string until A
                //Log.e("message", readMessage);

                int endOfLineIndex = recDataString.indexOf("A");                    // determine the end-of-line

                if(endOfLineIndex > 0) {
                    if (recDataString.charAt(0) == '#') {

                        String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string

                        String[] datos = dataInPrint.split("~");

                        if(datos.length > 0) {

                            try {
                                dato = Integer.parseInt(datos[0]);
                            } catch (NumberFormatException ex) {
                                ex.getStackTrace();
                            }
                            if(adqProcess && datos.length > 1) {  // en proceso de adquisición
                                datoFlujo = Integer.valueOf(datos[1]);
                                bluetoothDataReceiver.getBluetoothData(dato, datoFlujo);
                                recDataString.delete(0, recDataString.length());      //clear all string data
                                return;
                            }

                            dataToAverage.add(dato);

                            if(!modelHasTwoTanks) {

                                if (dataToAverage.size() == Constantes.ARRAY_DATA_SIZE) {
                                    sendDataForOneTankModel();
                                    } else {
                                    Log.e("IF","1");
                                    bluetoothDataReceiver.getBluetoothData(dato);
                                }
                            }else {                     //This model has two tanks

                                try {
                                    datoTanque2 = Integer.valueOf(datos[1]);
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {

                                    datoTanque2 = 0;
                                    ex.getStackTrace();
                                }

                                dataToAverageTank2.add(datoTanque2);
                                sendDataForTwoTanksModel();
                            }
                        }
                    }
                    recDataString.delete(0, recDataString.length());      //clear all string data
                } else {

                    //Log.e("redcata",recDataString.toString());
                    if (recDataString.length() > 30) {
                        recDataString.delete(0, recDataString.length());      //clear all string data
                    }
                }
            }
        }
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (btSocket != null && btSocket.isConnected()) {
                try {
                    bytes = mmInStream.read(buffer);         //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();

                } catch (IOException e) {

//                    Toast.makeText(context, "EL DISPOSITIVO SE HA DESCONECTADO.", Toast.LENGTH_SHORT).show();
                    try {
                        btSocket.close();
                        //El dicpositivo bluetooth se ha desconectado7
                        ((MainActivity)context).onPairedDeviceOff();
                        this.interrupt();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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
                return;
            }
        }

        if (btAdapter == null) {

            Toast.makeText(context, "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {

            btAdapter.cancelDiscovery();

            if (btAdapter.isEnabled()) {
                String address = preferences.getString(Constantes.DEFAULT_BLUETOOTH_MAC, "");
                bluetoothDevice = btAdapter.getRemoteDevice(address);

                try {

                    btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);

                    if (btSocket != null){

                        btSocket.connect();
                        ((MainActivity)context).cancelTimers();
                        mConnectedThread = new ConnectedThread(btSocket);
                        mConnectedThread.start();

                    }else {

                      //  iBluetoothState.couldNotConnectToDevice();
                      //  ((MainActivity)context).couldNotConnectToDevice();
                    }

                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //iBluetoothState.couldNotConnectToDevice();
                    //((MainActivity)context).couldNotConnectToDevice();
                }

                // Establish the Bluetooth socket connection.
                /*try {

                } /*catch (IOException e) {
                    try {
                        btSocket.close();
                        Toast.makeText(context, "No se pudo establecer conexion con el dispositivo", Toast.LENGTH_LONG).show();
                    } catch (IOException e2) {
                        //insert code to deal with this
                    }
                }*/
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

    public int getDato() {
        return dato;
    }

    public static boolean isAdqProcess() {
        return adqProcess;
    }

    public static void setAdqProcess(boolean adqProcess) {
        BluetoothHelper.adqProcess = adqProcess;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void init() {

        recDataString = new StringBuilder();
        handlerState = 0;

        REQUEST_ENABLE_BT = 1;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDataReceiver = (BluetoothDataReceiver) context;
        bluetoothIn = new MyVeryOwnHandler();
        btSocket = null;
        iBluetoothState = (MainActivity)context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getDatoTanque2() {
        return datoTanque2;
    }

    static void sendDataForOneTankModel() {

        int sum = 0;

        for (int i = 0; i < dataToAverage.size(); i++) {
            sum += dataToAverage.get(i);
        }
        sum = (sum / dataToAverage.size());

        dataToAverage = new ArrayList<>();
        JsonElement element = jsonValues.get(String.valueOf(sum));

        if (element != null) {
            //buscar el mas cercano
            bluetoothDataReceiver.getBluetoothData(element.getAsDouble());

        } else {  // no esta en la lista, buscar el mas cercanp
            element = jsonValues.get(String.valueOf(sum));
            try {
                bluetoothDataReceiver.getBluetoothData(element.getAsDouble());
            } catch (NullPointerException ex) {
                Log.e("EX", ex.getMessage());
            }
        }

    }

    static void sendDataForTwoTanksModel() {

        int sumTank1 = 0;
        int sumTank2 = 0;

        for (int i = 0; i < dataToAverage.size(); i++) {
            sumTank1 += dataToAverage.get(i);
        }
        sumTank1 = (sumTank1 / dataToAverage.size());

        for (int i = 0; i < dataToAverageTank2.size(); i++) {
            sumTank2 += dataToAverageTank2.get(i);
        }
        sumTank2 = (sumTank2 / dataToAverageTank2.size());

        dataToAverage = new ArrayList<>();
        dataToAverageTank2 = new ArrayList<>();

        JsonElement elementTank1 = jsonValues.get(String.valueOf(sumTank1));
        JsonElement elementTank2 = jsonValues.get(String.valueOf(sumTank2));

        if (elementTank1 != null && elementTank2 != null) {
            //buscar el mas cercano
            bluetoothDataReceiver.getBluetoothData(elementTank1.getAsDouble(), elementTank2.getAsDouble());
            bluetoothDataReceiver.getArrayBlueToothValues(sumTank1, sumTank2);

        } else {  // no están en la lista, buscar el mas cercano

            elementTank1 = jsonValues.get(String.valueOf(sumTank1));
            elementTank2 = jsonValues.get(String.valueOf(sumTank2));
            bluetoothDataReceiver.getArrayBlueToothValues(sumTank1, sumTank2);
            try {
                bluetoothDataReceiver.getBluetoothData(elementTank1.getAsDouble(), elementTank2.getAsDouble());
            } catch (NullPointerException ex) {
                Log.e("EX", ex.getMessage());
            }
        }
    }

}

