package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class BudgetDetailDTO {
    @SerializedName("_category_id")
    protected int _category_id;
    public void set_category_id(int _category_id) {
        this._category_id = _category_id;
    }
}
