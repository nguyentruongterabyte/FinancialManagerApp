package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class EmailCheckerDTO {
    @SerializedName("_email")
    protected String email;

    public EmailCheckerDTO(String email) {
        this.email = email;
    }

}
