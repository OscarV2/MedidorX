package com.index.medidor.pojo;

import java.io.Serializable;

public class Flujo implements Serializable {

    private int nivel;
    private double volumen;

    public Flujo() {
    }

    public Flujo(int nivel, double volumen) {
        this.nivel = nivel;
        this.volumen = volumen;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }
}
