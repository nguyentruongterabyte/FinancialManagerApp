package com.example.financialmanagerapp.model.DTO;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenDTO {
    @SerializedName("_refresh_token")
    protected String refreshToken;

    public RefreshTokenDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
