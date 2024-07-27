package com.example.financialmanagerapp.retrofit;

import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.request.LoginRequest;
import com.example.financialmanagerapp.model.request.RegisterRequest;
import com.example.financialmanagerapp.model.response.AuthResponse;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.model.request.EmailCheckerRequest;
import com.example.financialmanagerapp.model.request.RefreshTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("api/auth/login")
    Call<ResponseObject<AuthResponse>> login(@Body LoginRequest request);

    @GET("api/auth/{id}")
    Call<ResponseObject<User>> get(@Path("id") int id);

    @POST("api/auth/password/reset-request")
    Call<ResponseObject<Void>> requestResetPassword(@Body LoginRequest request);

    @POST("api/auth/logout")
    Call<ResponseObject<Void>> logout(@Body RefreshTokenRequest request);
    // Currency
    @GET("api/currency")
    Call<ResponseObject<List<Currency>>> getCurrencies();

    @GET("api/currency/search")
    Call<ResponseObject<List<Currency>>> searchCurrency(@Query("key") String keyword);

    @GET("api/currency/{id}")
    Call<ResponseObject<Currency>> getCurrency(@Path("id") int id);

    // Wallet
    @POST("api/wallet")
    Call<ResponseObject<Wallet>> createWallet(@Body Wallet request);

    @PUT("api/wallet/update/{id}/{walletId}")
    Call<ResponseObject<Wallet>> updateWallet(
            @Body Wallet request,
            @Path("id") int userId,
            @Path("walletId") int walletId);

    // Category
    @GET("api/category")
    Call<ResponseObject<List<Category>>> getCategories();

    // Transaction
    @GET("api/transaction/{id}")
    Call<ResponseObject<List<Transaction>>> getTransactionByUserId
    (@Path("id") int userId, @Query("page") int page, @Query("amount") int amount);

    @POST("api/transaction")
    Call<ResponseObject<Transaction>> createTransaction(@Body Transaction request);

    @PUT("api/transaction/update/{id}/{transactionId}")
    Call<ResponseObject<Transaction>> updateTransaction(
            @Body Transaction request,
            @Path("id") int userId,
            @Path("transactionId") int transactionId);
}
