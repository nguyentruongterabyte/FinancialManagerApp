package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class WalletDTO {
    @SerializedName("_name")
    protected String _name;
    @SerializedName("_initial_amount")
    protected double _initial_amount;
    @SerializedName("_amount")
    protected double _amount;
    @SerializedName("_color")
    protected String _color;
    @SerializedName("_exclude")
    protected int _exclude;
    @SerializedName("_account_id")
    protected int _account_id;

    @SerializedName("_wallet_type_code")
    protected String _wallet_type_code;
    @SerializedName("_icon")
    protected int _icon;

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_initial_amount(double _initial_amount) {
        this._initial_amount = _initial_amount;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

    public void set_color(String _color) {
        this._color = _color;
    }

    public void set_exclude(int _exclude) {
        this._exclude = _exclude;
    }

    public void set_account_id(int _account_id) {
        this._account_id = _account_id;
    }

    public void set_wallet_type_code(String _wallet_type_code) {
        this._wallet_type_code = _wallet_type_code;
    }

    public void set_icon(int _icon) {
        this._icon = _icon;
    }
}
