package com.example.financialmanagerapp.model.request;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {
    @SerializedName("_refresh_token")
    protected String refreshToken;

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
