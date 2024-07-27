package com.example.financialmanagerapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WalletType implements Serializable {
    protected String _code;
    private String _name;

    public String get_code() {
        return _code;
    }

    public void set_code(String _code) {
        this._code = _code;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    @Override
    @NonNull
    public String toString() {
        return "WalletType{" +
                "_code='" + _code + '\'' +
                ", _name='" + _name + '\'' +
                '}';
    }
}
