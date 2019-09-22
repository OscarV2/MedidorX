package com.index.medidor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuarios_has_modelos_carros")
public class UsuarioHasModeloCarro {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private int modelosCarrosId;
    @DatabaseField
    private int usuariosId;
    @DatabaseField
    private String bluetoothNombre;
    @DatabaseField
    private String bluetoothMac;
    @DatabaseField
    private String valoresAdq;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ModeloCarros modeloCarros;

    public UsuarioHasModeloCarro() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getModelosCarrosId() {
        return modelosCarrosId;
    }

    public void setModelosCarrosId(int modelosCarrosId) {
        this.modelosCarrosId = modelosCarrosId;
    }

    public int getUsuariosId() {
        return usuariosId;
    }

    public void setUsuariosId(int usuariosId) {
        this.usuariosId = usuariosId;
    }

    public String getBluetoothNombre() {
        return bluetoothNombre;
    }

    public void setBluetoothNombre(String bluetoothNombre) {
        this.bluetoothNombre = bluetoothNombre;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public void setBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }

    public String getValoresAdq() {
        return valoresAdq;
    }

    public void setValoresAdq(String valoresAdq) {
        this.valoresAdq = valoresAdq;
    }

    public ModeloCarros getModeloCarros() {
        return modeloCarros;
    }

    public void setModeloCarros(ModeloCarros modeloCarros) {
        this.modeloCarros = modeloCarros;
    }
}
