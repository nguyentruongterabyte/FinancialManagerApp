package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class LoginDTO {
    @SerializedName("_email")
    protected String email;

    @SerializedName("_password")
    protected String password;

    public LoginDTO(Builder builder) {
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

        public LoginDTO build() { return new LoginDTO(this); }
    }
}
