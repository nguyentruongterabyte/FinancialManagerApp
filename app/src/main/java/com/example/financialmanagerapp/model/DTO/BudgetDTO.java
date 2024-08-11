package com.example.financialmanagerapp.model.DTO;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

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
                ", budgetDetails='" + budgetDetails + '\'' +
                '}';
    }
}
