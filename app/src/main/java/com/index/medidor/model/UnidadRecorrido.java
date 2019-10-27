package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "unidad_recorrido")
public class UnidadRecorrido {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;
    @DatabaseField
    private double latitud;
    @DatabaseField
    private double longitud;
    @DatabaseField
    private long tiempo;
    @DatabaseField
    private Integer galones;
    @DatabaseField
    private Integer galonesT2;
    @DatabaseField
    private Integer valorBluetooh;
    @DatabaseField
    private Integer valorBluetoohT2;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Recorrido recorrido;

    public UnidadRecorrido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getGalones() {
        return galones;
    }

    public void setGalones(Integer galones) {
        this.galones = galones;
    }

    public Integer getGalonesT2() {
        return galonesT2;
    }

    public void setGalonesT2(Integer galonesT2) {
        this.galonesT2 = galonesT2;
    }

    public Integer getValorBluetooh() {
        return valorBluetooh;
    }

    public void setValorBluetooh(Integer valorBluetooh) {
        this.valorBluetooh = valorBluetooh;
    }

    public Integer getValorBluetoohT2() {
        return valorBluetoohT2;
    }

    public void setValorBluetoohT2(Integer valorBluetoohT2) {
        this.valorBluetoohT2 = valorBluetoohT2;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }
}
