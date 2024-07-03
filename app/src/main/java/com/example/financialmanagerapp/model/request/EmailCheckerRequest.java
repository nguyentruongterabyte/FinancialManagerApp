package com.example.financialmanagerapp.model.request;

import com.google.gson.annotations.SerializedName;

public class EmailCheckerRequest {
    @SerializedName("_email")
    protected String email;

    public EmailCheckerRequest(String email) {
        this.email = email;
    }

}
