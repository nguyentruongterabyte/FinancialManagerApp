package com.example.financialmanagerapp.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Transaction {
    protected int id;
    @SerializedName("_amount")
    protected Double _amount;
    @SerializedName("_description")
    protected String _description;
    @SerializedName("_wallet_id")
    protected int _wallet_id;
    @SerializedName("_category_id")
    protected Integer _category_id;
    @SerializedName("_memo")
    protected String _memo;
    @SerializedName("_from_wallet_id")
    protected Integer _from_wallet_id;
    @SerializedName("_to_wallet_id")
    protected Integer _to_wallet_id;
    @SerializedName("_fee")
    protected double _fee;
    @SerializedName("_transaction_type_id")
    protected int _transaction_type_id;
    @SerializedName("_date")
    protected Timestamp _date;

    public Transaction() {
    }

    public Timestamp get_date() {
        return _date;
    }

    public void set_date(Timestamp _date) {
        this._date = _date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double get_amount() {
        return _amount;
    }

    public void set_amount(Double _amount) {
        this._amount = _amount;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public int get_wallet_id() {
        return _wallet_id;
    }

    public void set_wallet_id(int _wallet_id) {
        this._wallet_id = _wallet_id;
    }

    public int get_category_id() {
        return _category_id;
    }

    public void set_category_id(int _category_id) {
        this._category_id = _category_id;
    }

    public String get_memo() {
        return _memo;
    }

    public void set_memo(String _memo) {
        this._memo = _memo;
    }

    public Integer get_from_wallet_id() {
        return _from_wallet_id;
    }

    public void set_from_wallet_id(int _from_wallet_id) {
        this._from_wallet_id = _from_wallet_id;
    }

    public int get_to_wallet_id() {
        return _to_wallet_id;
    }

    public void set_to_wallet_id(int _to_wallet_id) {
        this._to_wallet_id = _to_wallet_id;
    }

    public double get_fee() {
        return _fee;
    }

    public void set_fee(double _fee) {
        this._fee = _fee;
    }

    public int get_transaction_type_id() {
        return _transaction_type_id;
    }

    public void set_transaction_type_id(int _transaction_type_id) {
        this._transaction_type_id = _transaction_type_id;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", _amount=" + _amount +
                ", _description='" + _description + '\'' +
                ", _wallet_id=" + _wallet_id +
                ", _category_id=" + _category_id +
                ", _memo='" + _memo + '\'' +
                ", _from_wallet_id=" + _from_wallet_id +
                ", _to_wallet_id=" + _to_wallet_id +
                ", _fee=" + _fee +
                ", _transaction_type_id=" + _transaction_type_id +
                ", _date=" + _date +
                '}';
    }
}
