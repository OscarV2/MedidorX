package com.index.medidor.pojo;

import java.io.Serializable;

public class Flujo implements Serializable {

    private int nivel;
    private int volumen;

    public Flujo() {
    }

    public Flujo(int nivel, int volumen) {
        this.nivel = nivel;
        this.volumen = volumen;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getVolumen() {
        return volumen;
    }

    public void setVolumen(int volumen) {
        this.volumen = volumen;
    }
}
