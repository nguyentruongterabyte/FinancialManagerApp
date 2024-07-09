package com.example.financialmanagerapp.model;

import java.io.Serializable;

public class Currency implements Serializable {
    protected int id;
    protected String _ISO_code;
    protected String _symbol;
    protected String _currency;

    public int getId() {
        return id;
    }
    public String get_ISO_code() {
        return _ISO_code;
    }

    public String get_symbol() {
        return _symbol;
    }

    public String get_currency() {
        return _currency;
    }

}
