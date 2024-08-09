package com.example.financialmanagerapp.model;

public class BudgetDetail {
    protected int id;
    protected Category category;
    protected int _category_id;

    public BudgetDetail(int _category_id) {
        this._category_id = _category_id;
    }

    public int get_category_id() {
        return _category_id;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

}