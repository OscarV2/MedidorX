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
    private double altitud;
    @DatabaseField
    private long tiempo;
    @DatabaseField
    private Double galones;
    @DatabaseField
    private Double galonesT2;
    @DatabaseField
    private Integer valorBluetooh;
    @DatabaseField
    private Integer valorBluetoohT2;
    @DatabaseField
    private Double distancia;
    @DatabaseField
    private String hora;
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

    public Double getGalones() {
        return galones;
    }

    public void setGalones(Double galones) {
        this.galones = galones;
    }

    public Double getGalonesT2() {
        return galonesT2;
    }

    public void setGalonesT2(Double galonesT2) {
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

    public double getAltitud() {
        return altitud;
    }

    public void setAltitud(double altitud) {
        this.altitud = altitud;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }
    public Double getDistancia() {
        return distancia;
    }
    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
}
