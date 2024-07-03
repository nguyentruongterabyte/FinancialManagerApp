package com.example.financialmanagerapp.model;

import java.io.Serializable;

public class Currency implements Serializable {
    protected int id;
    private String _ISO_code;
    private String _state_territory;
    private String _symbol;
    private String _currency;
    private String _fractional_unit;
    private int _number_to_basic;

    public int getId() {
        return id;
    }

    public String get_ISO_code() {
        return _ISO_code;
    }

    public void set_ISO_code(String _ISO_code) {
        this._ISO_code = _ISO_code;
    }

    public String get_state_territory() {
        return _state_territory;
    }

    public void set_state_territory(String _state_territory) {
        this._state_territory = _state_territory;
    }

    public String get_symbol() {
        return _symbol;
    }

    public void set_symbol(String _symbol) {
        this._symbol = _symbol;
    }

    public String get_currency() {
        return _currency;
    }

    public void set_currency(String _currency) {
        this._currency = _currency;
    }

    public String get_fractional_unit() {
        return _fractional_unit;
    }

    public void set_fractional_unit(String _fractional_unit) {
        this._fractional_unit = _fractional_unit;
    }

    public int get_number_to_basic() {
        return _number_to_basic;
    }

    public void set_number_to_basic(int _number_to_basic) {
        this._number_to_basic = _number_to_basic;
    }
}
