package com.index.medidor.utils;

import com.index.medidor.model.Estaciones;

import java.util.Comparator;

public class SortByDistance implements Comparator<Estaciones> {
    @Override
    public int compare(Estaciones e1, Estaciones e2) {
        return Float.compare(e2.getDistancia(), e1.getDistancia());
    }
}
