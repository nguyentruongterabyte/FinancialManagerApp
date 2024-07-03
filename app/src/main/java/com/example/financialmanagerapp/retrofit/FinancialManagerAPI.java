package com.example.financialmanagerapp.retrofit;

import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.request.RegisterRequest;
import com.example.financialmanagerapp.model.response.AuthResponse;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.model.request.EmailCheckerRequest;
import com.example.financialmanagerapp.model.request.PasswordCheckerRequest;
import com.example.financialmanagerapp.model.request.RefreshTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FinancialManagerAPI {

    // Auth
    @POST("api/auth/refresh")
    Call<ResponseObject<String>> refreshToken(@Body RefreshTokenRequest request);

    @POST("api/auth/checker/email")
    Call<ResponseObject<List<String>>> emailChecker(@Body EmailCheckerRequest request);

    @POST("api/auth/register")
    Call<ResponseObject<AuthResponse>> register(@Body RegisterRequest request);

    // Currency
    @GET("api/currency")
    Call<ResponseObject<List<Currency>>> getCurrencies();

    @GET("api/currency/search")
    Call<ResponseObject<List<Currency>>> searchCurrency(@Query("key") String keyword);

    @GET("api/currency/{id}")
    Call<ResponseObject<Currency>> getCurrency(@Path("id") int id);
}
