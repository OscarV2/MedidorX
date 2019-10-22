package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "unidad_recorrido")
public class UnidadRecorrido {

    @DatabaseField
    private double latitud;
    @DatabaseField
    private double longitud;
    @DatabaseField
    private long tiempo;
    @DatabaseField
    private int galones;
    @DatabaseField
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
