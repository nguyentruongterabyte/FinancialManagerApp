package com.example.financialmanagerapp.model.DTO;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class BudgetDTO {
    @SerializedName("_name")
    protected String _name;
    @SerializedName("_amount")
    protected double _amount;
    @SerializedName("_color")
    protected String _color;
    @SerializedName("_period")
    protected String _period;

    @SerializedName("_account_id")
    protected int _account_id;
    @SerializedName("_start_date")
    protected Timestamp _start_date;
    @SerializedName("_end_date")
    protected Timestamp _end_date;
    @SerializedName("budget_details")
    protected String budgetDetails;

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

    public void set_color(String _color) {
        this._color = _color;
    }
    public void set_period(String _period) {
        this._period = _period;
    }
    public void set_account_id(int _account_id) {
        this._account_id = _account_id;
    }

    public void set_start_date(Timestamp _start_date) {
        this._start_date = _start_date;
    }

    public void set_end_date(Timestamp _end_date) {
        this._end_date = _end_date;
    }

    public void setBudgetDetails(String budgetDetails) {
        this.budgetDetails = budgetDetails;
    }

    @NonNull

    @Override
    public String toString() {
        return "BudgetDTO{" +
                "_name='" + _name + '\'' +
                ", _amount=" + _amount +
                ", _color='" + _color + '\'' +
                ", _period='" + _period + '\'' +
                ", _account_id=" + _account_id +
                ", _start_date=" + _start_date +
                ", _end_date=" + _end_date +
                ", budgetDetails='" + budgetDetails + '\'' +
                '}';
    }
}
