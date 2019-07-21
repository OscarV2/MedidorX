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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public BluetoothHelper(Context context) {
        recDataString = new StringBuilder();
        handlerState = 0;
        this.context = context;
        REQUEST_ENABLE_BT = 1;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDataReceiver = (BluetoothDataReceiver) context;
        bluetoothIn = new MyVeryOwnHandler();
    }

    private static class MyVeryOwnHandler extends Handler{

        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {

            if (msg.what == handlerState) {          //if message is what we want
                String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                recDataString.append(readMessage);              //keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                    int dataLength = dataInPrint.length();       //get length of data received

                    if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                    {

                        int dato = Integer.parseInt(recDataString.substring(1,endOfLineIndex));     //get sensor value from string between indices 1-5
                        bluetoothDataReceiver.getBluetoothData(dato);

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
                String address = "00:21:13:00:C2:B0";
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

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }
}
