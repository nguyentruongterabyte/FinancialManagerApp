package com.example.financialmanagerapp.model;

public class Category {
    protected int id;
    protected String _name;
    protected int _icon;
    protected String _color;
    protected int _transaction_type_id;

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
}
