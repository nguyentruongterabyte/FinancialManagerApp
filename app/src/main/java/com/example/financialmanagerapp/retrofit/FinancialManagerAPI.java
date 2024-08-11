package com.example.financialmanagerapp.retrofit;

import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.DTO.BudgetDTO;
import com.example.financialmanagerapp.model.DTO.EmailCheckerDTO;
import com.example.financialmanagerapp.model.DTO.LoginDTO;
import com.example.financialmanagerapp.model.DTO.RefreshTokenDTO;
import com.example.financialmanagerapp.model.DTO.RegisterDTO;
import com.example.financialmanagerapp.model.DTO.TransactionDTO;
import com.example.financialmanagerapp.model.DTO.WalletDTO;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.WalletType;
import com.example.financialmanagerapp.model.response.AuthResponse;
import com.example.financialmanagerapp.model.response.ResponseObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FinancialManagerAPI {

    // Auth
    @POST("api/auth/refresh")
    Call<ResponseObject<String>> refreshToken(@Body RefreshTokenDTO request);

    @POST("api/auth/checker/email")
    Call<ResponseObject<List<String>>> emailChecker(@Body EmailCheckerDTO request);

    @POST("api/auth/register")
    Call<ResponseObject<AuthResponse>> register(@Body RegisterDTO request);

    @POST("api/auth/login")
    Call<ResponseObject<AuthResponse>> login(@Body LoginDTO request);

    @GET("api/auth/{id}")
    Call<ResponseObject<User>> get(@Path("id") int id);

    @POST("api/auth/password/reset-request")
    Call<ResponseObject<Void>> requestResetPassword(@Body LoginDTO request);

    @POST("api/auth/logout")
    Call<ResponseObject<Void>> logout(@Body RefreshTokenDTO request);

    // Currency
    @GET("api/currency")
    Call<ResponseObject<List<Currency>>> getCurrencies();

    @GET("api/currency/search")
    Call<ResponseObject<List<Currency>>> searchCurrency(@Query("key") String keyword);

    @GET("api/currency/{id}")
    Call<ResponseObject<Currency>> getCurrency(@Path("id") int id);

    // Wallet
    @POST("api/wallet")
    Call<ResponseObject<Wallet>> createWallet(@Body WalletDTO request);

    @PUT("api/wallet/update/{id}/{walletId}")
    Call<ResponseObject<Wallet>> updateWallet(
            @Body WalletDTO request,
            @Path("id") int userId,
            @Path("walletId") int walletId);

    // Wallet type
    @GET("api/wallet-type")
    Call<ResponseObject<List<WalletType>>> getWalletTypes();

    // Category
    @GET("api/category")
    Call<ResponseObject<List<Category>>> getCategories();

    // Transaction
    @GET("api/transaction/{id}")
    Call<ResponseObject<List<Transaction>>> getTransactionByUserId
    (@Path("id") int userId, @Query("page") int page, @Query("amount") int amount);

    @POST("api/transaction")
    Call<ResponseObject<Transaction>> createTransaction(@Body TransactionDTO request);

    @PUT("api/transaction/{id}/{transactionId}")
    Call<ResponseObject<Transaction>> updateTransaction(
            @Body TransactionDTO request,
            @Path("id") int userId,
            @Path("transactionId") int transactionId);

    @DELETE("api/transaction/{id}/{transactionId}")
    Call<ResponseObject<Void>> deleteTransaction(
            @Path("id") int userId,
            @Path("transactionId") int transactionId
    );

    // Budget
    @POST("api/budget")
    Call<ResponseObject<Budget>> createBudget(@Body BudgetDTO request);

    @PUT("api/budget/{id}/{budgetId}")
    Call<ResponseObject<Budget>> updateBudget(
            @Body BudgetDTO request,
            @Path("id") int userId,
            @Path("budgetId") int budgetId
    );

    @DELETE("api/budget/{id}/{budgetId}")
    Call<ResponseObject<Void>> deleteBudget(
            @Path("id") int userId,
            @Path("budgetId") int budgetId
    );
}
