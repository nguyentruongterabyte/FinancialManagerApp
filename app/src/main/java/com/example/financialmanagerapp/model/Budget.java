package com.example.financialmanagerapp.model;

import java.sql.Timestamp;
import java.util.List;

public class Budget {
    protected int id;
    protected String _name;
    protected double _amount;
    protected Timestamp _start_date;
    protected Timestamp _end_date;
    protected String _color;
    protected String _period;
    protected List<BudgetDetail> budget_details;

    public int getId() {
        return id;
    }

    public String get_name() {
        return _name;
    }

    public double get_amount() {
        return _amount;
    }

    public Timestamp get_start_date() {
        return _start_date;
    }

    public Timestamp get_end_date() {
        return _end_date;
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
        this._start_date = builder._start_date;
        this._end_date = builder._end_date;
        this._color = builder._color;
        this._period = builder._period;
        this.budget_details = builder.budget_details;
    }

    public static class Builder {
        private String _name;
        private double _amount;
        private Timestamp _start_date;
        private Timestamp _end_date;
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

        public Builder startDate(Timestamp startDate) {
            this._start_date = startDate;
            return this;
        }

        public Builder endDate(Timestamp endDate) {
            this._end_date = endDate;
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
