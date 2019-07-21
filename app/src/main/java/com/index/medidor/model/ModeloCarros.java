package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "modelos_carros")
public class ModeloCarros implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String nombre;
    @DatabaseField
    private String muestreo;
    @DatabaseField
    private int id_marca;

    public ModeloCarros() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMuestreo() {
        return muestreo;
    }

    public void setMuestreo(String muestreo) {
        this.muestreo = muestreo;
    }

    public int getId_marca() {
        return id_marca;
    }

    public void setId_marca(int id_marca) {
        this.id_marca = id_marca;
    }
}
