package com.example.financialmanagerapp.model;

import java.io.Serializable;
import java.util.List;

public class Budget implements Serializable {
    protected int id;
    protected String _name;
    protected double _amount;
    protected String _color;
    protected String _period;
    protected List<BudgetDetail> budget_details;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String get_name() {
        return _name;
    }

    public double get_amount() {
        return _amount;
    }

    public String get_color() {
        return _color;
    }

    public String get_period() {
        return _period;
    }

    public List<BudgetDetail> getBudget_details() {
        return budget_details;
    }

    public Budget(Builder builder) {
        this._name = builder._name;
        this._amount = builder._amount;
        this._color = builder._color;
        this._period = builder._period;
        this.budget_details = builder.budget_details;
    }

    public static class Builder {
        private String _name;
        private double _amount;
        private String _color;
        private String _period;
        private List<BudgetDetail> budget_details;

        public Builder name(String name) {
            this._name = name;
            return this;
        }

        public Builder amount(double amount) {
            this._amount = amount;
            return this;
        }

        public Builder color(String color) {
            this._color = color;
            return this;
        }

        public Builder period(String period) {
            this._period = period;
            return this;
        }

        public Builder budgetDetails(List<BudgetDetail> budgetDetails) {
            this.budget_details = budgetDetails;
            return this;
        }
        public Budget build() {
            return new Budget(this);
        }
    }
}
