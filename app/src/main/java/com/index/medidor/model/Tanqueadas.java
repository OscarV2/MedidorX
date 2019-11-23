package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tanqueadas")
public class Tanqueadas implements Serializable {

    @DatabaseField
    private String nombre;
    @DatabaseField
    private String direccion;
    @DatabaseField
    private double galones;
    @DatabaseField
    private double total;
    @DatabaseField
    private String fecha;
    @DatabaseField
    private double latitud;
    @DatabaseField
    private double longitud;
    @DatabaseField
    private double cantDeseada;
    @DatabaseField
    private double galDeseados;
    @DatabaseField
    private boolean flagCantidadDeseada;
    @DatabaseField
    private int calificacion;
    @DatabaseField
    private String comentarios;

    @DatabaseField
    private double precioGalon;

    @DatabaseField
    private int idEstacion;

    @DatabaseField
    private int idUsuario;

    @DatabaseField
    private int idRecorrido;

    public Tanqueadas() {
    }

    public Tanqueadas(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getGalones() {
        return galones;
    }

    public void setGalones(double galones) {
        this.galones = galones;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(int idEstacion) {
        this.idEstacion = idEstacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public double getCantDeseada() {
        return cantDeseada;
    }

    public void setCantDeseada(double cantDeseada) {
        this.cantDeseada = cantDeseada;
    }

    public double getGalDeseados() {
        return galDeseados;
    }

    public void setGalDeseados(double galDeseados) {
        this.galDeseados = galDeseados;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public boolean isFlagCantidadDeseada() {
        return flagCantidadDeseada;
    }

    public void setFlagCantidadDeseada(boolean flagCantidadDeseada) {
        this.flagCantidadDeseada = flagCantidadDeseada;
    }

    public double getPrecioGalon() {
        return precioGalon;
    }

    public void setPrecioGalon(double precioGalon) {
        this.precioGalon = precioGalon;
    }

    public int getIdRecorrido() {
        return idRecorrido;
    }

    public void setIdRecorrido(int idRecorrido) {
        this.idRecorrido = idRecorrido;
    }
}
