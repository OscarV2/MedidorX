package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "modelos_carros")
public class ModeloCarros implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String linea;
    @DatabaseField
    private String muestreo;
    @DatabaseField
    private int idMarca;
    @DatabaseField
    private double galones;
    @DatabaseField
    private String tipo_combustuble;
    @DatabaseField
    private String modelo;
    @DatabaseField
    private String valoresAdq;

    public ModeloCarros() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getMuestreo() {
        return muestreo;
    }

    public void setMuestreo(String muestreo) {
        this.muestreo = muestreo;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public double getGalones() {
        return galones;
    }

    public void setGalones(double galones) {
        this.galones = galones;
    }

    public String getTipo_combustuble() {
        return tipo_combustuble;
    }

    public void setTipo_combustuble(String tipo_combustuble) {
        this.tipo_combustuble = tipo_combustuble;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getValoresAdq() {
        return valoresAdq;
    }

    public void setValoresAdq(String valoresAdq) {
        this.valoresAdq = valoresAdq;
    }
}
