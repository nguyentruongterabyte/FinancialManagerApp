package com.example.financialmanagerapp.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("_name")
    protected String name;

    @SerializedName("_email")
    protected String email;

    @SerializedName("_password")
    protected String password;

    @SerializedName("_password_confirmation")
    protected String passwordConfirmation;

    @SerializedName("_initial_currency_id")
    protected int initialCurrencyId;

    public RegisterRequest(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.passwordConfirmation = builder.passwordConfirmation;
        this.initialCurrencyId = builder.initialCurrencyId;
    }
    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String passwordConfirmation;
        private int initialCurrencyId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder passwordConfirmation(String passwordConfirmation) {
            this.passwordConfirmation = passwordConfirmation;
            return this;
        }

        public Builder initialCurrencyId(int initialCurrencyId) {
            this.initialCurrencyId = initialCurrencyId;
            return this;
        }

        public RegisterRequest build() { return new RegisterRequest(this); }
    }
}
