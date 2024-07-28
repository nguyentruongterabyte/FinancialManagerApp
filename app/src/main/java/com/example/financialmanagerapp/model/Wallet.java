package com.example.financialmanagerapp.model;

import java.io.Serializable;
import java.util.List;

public class Wallet implements Serializable {
    protected int id;
    protected String _name;
    protected double _initial_amount;
    protected double _amount;
    protected String _color;
    protected int _exclude;
    protected int _account_id;
    protected String _wallet_type_code;
    protected int _icon;
    protected List<String> errors;

    public Wallet() {
    }

    public static Wallet findById(int id, List<Wallet> wallets) {
        for (Wallet w : wallets) {
            if (w.getId() == id) {
                return w;
            }
        }
        return new Wallet();
    }

    public static boolean updateWalletInList(Wallet wallet, List<Wallet> wallets) {
        boolean isUpdated = false;

        for (int i = 0; i < wallets.size(); i++) {
            Wallet currentWallet = wallets.get(i);
            if (currentWallet.getId() == wallet.getId()) {
                wallets.set(i, wallet);
                isUpdated = true;
                break;
            }
        }

        return isUpdated;
    }

    public int get_account_id() {
        return _account_id;
    }

    public String get_wallet_type_code() {
        return _wallet_type_code;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

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

    public int get_icon() {
        return _icon;
    }

    public double get_amount() {
        return _amount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Wallet(Builder builder) {
        this._name = builder.name;
        this._color = builder.color;
        this._exclude = builder.exclude;
        this._account_id = builder.accountId;
        this._initial_amount = builder.initialAmount;
        this._wallet_type_code = builder.walletTypeCode;
        this._icon = builder.icon;
        this._amount = builder.amount;
    }

    public static class Builder {
        private String name;
        private double initialAmount;
        private String color;
        private int icon;
        private int exclude;
        private int accountId;
        private String walletTypeCode;
        private double amount;


        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder initialAmount(double initialAmount) {
            this.initialAmount = initialAmount;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder icon(int icon) {
            this.icon = icon;
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


        public Builder walletTypeCode(String walletTypeCode) {
            this.walletTypeCode = walletTypeCode;
            return this;
        }

        public Wallet build() {
            return new Wallet(this);
        }

    }

}
