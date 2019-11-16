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
    @DatabaseField
    private String placa;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ModeloCarros modeloCarros;
    @DatabaseField
    private Boolean hasTwoTanks ;
    @DatabaseField
    private String tipoCombustible;
    @DatabaseField
    private String marca;
    @DatabaseField
    private String linea;
    @DatabaseField
    private String anio;

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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Boolean getHasTwoTanks() {
        return hasTwoTanks;
    }

    public void setHasTwoTanks(Boolean hasTwoTanks) {
        this.hasTwoTanks = hasTwoTanks;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }
}
