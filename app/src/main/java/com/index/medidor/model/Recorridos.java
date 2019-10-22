package com.index.medidor.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.index.medidor.utils.Constantes;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

@DatabaseTable(tableName = "recorridos")
public class Recorridos implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    /**
     * Json string con un list de LatLng
     */
    @DatabaseField(canBeNull = false)
    private String rutas;
    @DatabaseField(canBeNull = false)
    private int idUsuario;
    @DatabaseField
    private double galonesPerdidos;
    @DatabaseField
    private String horaInicio;
    @DatabaseField
    private int distancia;

    private transient List<LatLng> posiciones;

    @ForeignCollectionField
    private List<UnidadRecorrido> unidadRecorridos;

    public String getRutas() {
        return rutas;
    }

    public void setRutas(String rutas) {
        this.rutas = rutas;
    }

    public List<LatLng> getPosiciones() {
        return posiciones;
    }

    public void setPosiciones() {
        Gson gson = new Gson();
        TypeToken<List<LatLng>> token = new TypeToken<List<LatLng>>() {
        };
        this.posiciones = gson.fromJson(this.rutas, token.getType());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getGalonesPerdidos() {
        return galonesPerdidos;
    }

    public void setGalonesPerdidos(double galonesPerdidos) {
        this.galonesPerdidos = galonesPerdidos;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setPosiciones(List<LatLng> posiciones) {
        this.posiciones = posiciones;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia() {

        this.distancia = 0;
        this.setPosiciones();
        if (posiciones.size() >= 2){
            for(int i = 1; i <= posiciones.size() - 1 ;i++){

                float dFloat = Constantes.getDistance(posiciones.get(i), posiciones.get(i-1));
                this.distancia = this.distancia + (int)dFloat;

            }
        }
    }

    public List<UnidadRecorrido> getUnidadRecorridos() {
        return unidadRecorridos;
    }

    public void setUnidadRecorridos(List<UnidadRecorrido> unidadRecorridos) {
        this.unidadRecorridos = unidadRecorridos;
    }
}
