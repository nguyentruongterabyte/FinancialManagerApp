package com.example.financialmanagerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Wallet {

    protected int id;

    @SerializedName("_name")
    protected String _name;
    @SerializedName("_initial_amount")
    protected double _initial_amount;
    @SerializedName("_color")
    protected String _color;

    @SerializedName("_exclude")
    protected int _exclude;
    @SerializedName("_account_id")
    protected int _account_id;

    @SerializedName("_wallet_type_code")
    protected String _wallet_type_code;

    protected WalletType wallet_type;

    protected List<String> errors;

    public int getId() {
        return id;
    }

    public String get_name() {
        return _name;
    }

    public double get_initial_amount() {
        return _initial_amount;
    }

    public String get_color() {
        return _color;
    }

    public int get_exclude() {
        return _exclude;
    }

    public int get_account_id() {
        return _account_id;
    }

    public WalletType getWallet_type() {
        return wallet_type;
    }


    public String get_wallet_type_code() {
        return _wallet_type_code;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Wallet(Builder builder) {
        this._name = builder.name;
        this._color = builder.color;
        this._exclude = builder.exclude;
        this._account_id = builder.accountId;
        this.wallet_type = builder.walletType;
        this._initial_amount = builder.initialAmount;
        this._wallet_type_code = builder.walletTypeCode;
    }

    public static class Builder {
        private String name;
        private double initialAmount;
        private String color;
        private int exclude;
        private int accountId;
        private String walletTypeCode;
        private WalletType walletType;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder initialAmount(double initialAmount) {
            this.initialAmount = initialAmount;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder exclude(int exclude) {
            this.exclude = exclude;
            return this;
        }

        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder walletType(WalletType walletType) {
            this.walletType = walletType;
            return this;
        }

        public Builder walletTypeCode(String walletTypeCode) {
            this.walletTypeCode = walletTypeCode;
            return this;
        }
        public Wallet build() {
            return new Wallet(this);
        }

    }
}
