package com.example.financialmanagerapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Category implements Serializable {
    protected int id;
    protected String _name;
    protected int _icon;
    protected String _color;
    protected int _transaction_type_id;

    public Category(Builder builder) {
        this.id = builder.id;
        this._name = builder._name;
        this._color = builder._color;
        this._icon = builder._icon;
        this._transaction_type_id = builder._transaction_type_id;
    }

    public int getId() {
        return id;
    }

    public String get_name() {
        return _name;
    }

    public int get_icon() {
        return _icon;
    }

    public String get_color() {
        return _color;
    }

    public int get_transaction_type_id() {
        return _transaction_type_id;
    }

    public static class Builder {
        protected int id;
        protected String _name;
        protected int _icon;
        protected String _color;
        protected int _transaction_type_id;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this._name = name;
            return this;
        }

        public Builder icon(int icon) {
            this._icon = icon;
            return this;
        }
        public Builder color(String color) {
            this._color = color;
            return this;
        }
        public Builder transactionTypeId(int _transaction_type_id) {
            this._transaction_type_id = _transaction_type_id;
            return this;
        }

        public Category build() {
            return new Category(this);
        }

        @NonNull
        @Override
        public String toString() {
            return "Builder{" +
                    "id=" + id +
                    ", _name='" + _name + '\'' +
                    ", _icon=" + _icon +
                    ", _color='" + _color + '\'' +
                    ", _transaction_type_id=" + _transaction_type_id +
                    '}';
        }
    }
}
