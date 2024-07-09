package com.example.financialmanagerapp.model.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("_email")
    protected String email;

    @SerializedName("_password")
    protected String password;

    public LoginRequest(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
    }

    public static class Builder {

        private String email;
        private String password;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequest build() { return new LoginRequest(this); }
    }
}
