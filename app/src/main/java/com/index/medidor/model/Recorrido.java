package com.index.medidor.model;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@DatabaseTable(tableName = "recorrido")
public class Recorrido implements Serializable {

    @DatabaseField(columnName = "id", id = true)
    private Long id;
    @DatabaseField
    private String fechaInicio;
    @DatabaseField
    private String fechaFin;
    @DatabaseField
    private Double distanciaRecorrida;
    @DatabaseField
    private Double galonesPerdidos;
    @DatabaseField
    private transient boolean uploaded;
    @ForeignCollectionField
    private Collection<UnidadRecorrido> listUnidadRecorrido;
    @ForeignCollectionField
    private Collection<Tanqueadas> listTanqueadas;

    private Usuario usuario;

    private String jsonListUnidadRecorrido;

    public Recorrido() { }

    public Recorrido(int idUsuario,  Double galonesPerdidos) {
        Usuario user = new Usuario();
        user.setId(idUsuario);
        //this.fechaInicio = new Date();
        this.distanciaRecorrida = 0.0;
        this.galonesPerdidos = galonesPerdidos;
        this.uploaded = false;
        this.listUnidadRecorrido = new ArrayList<>();
        this.listTanqueadas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public void setDisanciaRecorrida(Double distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public Double getGalonesPerdidos() {
        return galonesPerdidos;
    }

    public void setGalonesPerdidos(Double galonesPerdidos) {
        this.galonesPerdidos = galonesPerdidos;
    }

    public Collection<Tanqueadas> getListTanqueadas() {
        return listTanqueadas;
    }

    public void setListTanqueadas(Collection<Tanqueadas> listTanqueadas) {
        this.listTanqueadas = listTanqueadas;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Collection<UnidadRecorrido> getListUnidadRecorrido() {
        return listUnidadRecorrido;
    }

    public void setListUnidadRecorrido(Collection<UnidadRecorrido> listUnidadRecorrido) {
        this.listUnidadRecorrido = listUnidadRecorrido;
    }

    public String getJsonListUnidadRecorrido() {
        return jsonListUnidadRecorrido;
    }

    public void setJsonListUnidadRecorrido() {
        Gson gson = new Gson();
        this.jsonListUnidadRecorrido = gson.toJson(listUnidadRecorrido);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
