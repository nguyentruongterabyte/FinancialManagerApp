package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class PasswordCheckerDTO {
    @SerializedName("_password")
    protected String password;

    @SerializedName("_password_confirmation")
    protected String passwordConfirmation;

    public PasswordCheckerDTO(String password, String passwordConfirmation) {
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
