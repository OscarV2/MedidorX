package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "recorrido")
public class Recorrido implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;
    @DatabaseField
    private int idUsuario;
    @DatabaseField
    private Date fecha;
    @DatabaseField
    private transient boolean uploaded;
    @ForeignCollectionField
    private Collection<UnidadRecorrido> listUnidadRecorrido;

    public Recorrido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Collection getListUnidadRecorrido() {
        return listUnidadRecorrido;
    }

    public void setListUnidadRecorrido(Collection listUnidadRecorrido) {
        this.listUnidadRecorrido = listUnidadRecorrido;
    }
}
