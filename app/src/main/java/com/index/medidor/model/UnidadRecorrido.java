package com.index.medidor.model;

public class UnidadRecorrido {

    private double latitud;
    private double longitud;
    private long tiempo;
    private int galones;
    private int valorBluetooh;

    public UnidadRecorrido() {
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public int getGalones() {
        return galones;
    }

    public void setGalones(int galones) {
        this.galones = galones;
    }

    public int getValorBluetooh() {
        return valorBluetooh;
    }

    public void setValorBluetooh(int valorBluetooh) {
        this.valorBluetooh = valorBluetooh;
    }
}
