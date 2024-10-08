package com.example.financialmanagerapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    protected int id;
    protected String _name;
    protected String _email;
    protected String _password;
    protected String _password_confirmation;
    protected Currency currency;
    protected List<Wallet> wallets;
    protected List<Budget> budgets;

    public User() {
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public int getId() {
        return id;
    }

    public String get_name() {
        return _name;
    }

    public String get_email() {
        return _email;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String get_password() {
        return _password;
    }

    public String get_password_confirmation() {
        return _password_confirmation;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public User(Builder builder) {
        this.id = builder.id;
        this._email = builder._email;
        this._name = builder._name;
        this._password = builder._password;
        this._password_confirmation = builder._password_confirmation;
        this.currency = builder.currency;
    }

    public static class Builder{
        private int id;
        private String _name;
        private String _email;
        private String _password;
        private String _password_confirmation;
        private Currency currency;

        public Builder id(int id) {
            this.id = id;
            return this;
        }
        public Builder name(String _name) {
            this._name = _name;
            return this;
        }

        public Builder email(String _email) {
            this._email = _email;
            return this;
        }

        public Builder password(String _password) {
            this._password = _password;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder passwordConfirmation(String _password_confirmation) {
            this._password_confirmation = _password_confirmation;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", _name='" + _name + '\'' +
                ", _email='" + _email + '\'' +
                ", _password='" + _password + '\'' +
                ", _password_confirmation='" + _password_confirmation + '\'' +
                ", currency=" + currency +
                '}';
    }
}
