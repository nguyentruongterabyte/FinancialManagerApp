package com.example.financialmanagerapp.model;

import java.io.Serializable;

public class WalletType implements Serializable {
    protected String _code;
    protected String _name;

    public String get_code() {
        return _code;
    }
    public String get_name() {
        return _name;
    }
}
