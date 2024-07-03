package com.example.financialmanagerapp.model.request;

import com.google.gson.annotations.SerializedName;

public class PasswordCheckerRequest {
    @SerializedName("_password")
    protected String password;

    @SerializedName("_password_confirmation")
    protected String passwordConfirmation;

    public PasswordCheckerRequest(String password, String passwordConfirmation) {
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
