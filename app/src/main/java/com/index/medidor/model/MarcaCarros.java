package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

@DatabaseTable(tableName = "marcas_carros")
public class MarcaCarros implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String nombre;

    private List<ModeloCarros> listModelos;

    public MarcaCarros() {
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

    public List<ModeloCarros> getListModelos() {
        return listModelos;
    }

    public void setListModelos(List<ModeloCarros> listModelos) {
        this.listModelos = listModelos;
    }
}
