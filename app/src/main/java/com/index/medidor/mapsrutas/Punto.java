package com.index.medidor.mapsrutas;


public class Punto {

    private Double latitudInicial;
    private Double longitudInicial;
    private Double altitud;
    private Double latitudFinal;
    private Double longitudFinal;
    private String url;



    public Double getLatitudInicial() {
        return latitudInicial;
    }

    public void setLatitudInicial(Double latitudInicial) {
        this.latitudInicial = latitudInicial;
    }

    public Double getLongitudInicial() {
        return longitudInicial;
    }

    public void setLongitudInicial(Double longitudInicial) {this.longitudInicial = longitudInicial;}

    public Double getAltitud() {return altitud;}

    public void setAltitud(Double altitud) { this.altitud = altitud; }

    public Double getLatitudFinal() {
        return latitudFinal;
    }

    public void setLatitudFinal(Double latitudFinal) {
        this.latitudFinal = latitudFinal;
    }

    public Double getLongitudFinal() {
        return longitudFinal;
    }

    public void setLongitudFinal(Double longitudFinal) {
        this.longitudFinal = longitudFinal;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
