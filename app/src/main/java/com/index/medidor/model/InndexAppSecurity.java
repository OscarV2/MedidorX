package com.index.medidor.model;

import java.io.Serializable;

public class InndexAppSecurity implements Serializable {

    private int id;
    private String appPassword;

    public InndexAppSecurity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }
}
