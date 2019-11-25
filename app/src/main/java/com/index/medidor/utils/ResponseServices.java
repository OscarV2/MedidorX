package com.index.medidor.utils;

import java.io.Serializable;

public class ResponseServices implements Serializable {

    private String code;
    private String msg;

    public ResponseServices() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
