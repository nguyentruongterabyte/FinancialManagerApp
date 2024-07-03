package com.example.financialmanagerapp.retrofit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.model.request.RefreshTokenRequest;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;
    protected static FinancialManagerAPI financialManagerAPI;

    public static Retrofit getInstance(String baseUrl, Context context) {

        if (instance == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String accessToken = SharedPreferencesUtils.getAccessToken(context);
                        String refreshToken = SharedPreferencesUtils.getRefreshToken(context);
                        Request.Builder requestBuilder = original.newBuilder()
                                .method(original.method(), original.body());

                        requestBuilder.addHeader("Authorization", "Bearer " + accessToken);

                        Request request = requestBuilder.build();
                        Response response = chain.proceed(request);

                        if (response.code() == 401 && refreshToken != null) {
                            synchronized (RetrofitClient.class) {
                                try {
                                    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
                                    ResponseObject<String> newTokenResponse = refreshToken(refreshTokenRequest);
                                    if (newTokenResponse != null && newTokenResponse.getStatus() == 200) {
                                        accessToken = newTokenResponse.getResult();
                                        SharedPreferencesUtils.saveAccessToken(context, accessToken);
                                        // Retry the request with the new token
                                        Request newRequest = original.newBuilder()
                                                .header("Authorization", "Bearer " + accessToken)
                                                .build();
                                        return chain.proceed(newRequest);
                                    } else {
                                        Log.e("myLog", "Failed to refresh token, response: " + newTokenResponse);
                                    }
                                } catch (IOException e) {
                                    Log.e("myLog", "IOException while refreshing token", e);
                                }
                            }
                        }

                        if (response.code() == 403) {
                            // return to Login Activity
                            Intent logoutIntent = new Intent("com.example.financialmanagerapp.LOGOUT");
                            context.sendBroadcast(logoutIntent);
                        }
                        return response;
                    })
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request compressedRequest = originalRequest.newBuilder()
                                .header("Accept-Encoding", "gzip")
                                .build();
                        return chain.proceed(compressedRequest);
                    })
                    .addInterceptor(interceptor)
                    .cache(new Cache(context.getCacheDir(), 10 * 1024 * 1024)) // 10 MB cache
                    .build();
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return instance;
    }

    private static ResponseObject<String> refreshToken(RefreshTokenRequest request) throws IOException {
        Call<ResponseObject<String>> call = financialManagerAPI.refreshToken(request);

        retrofit2.Response<ResponseObject<String>> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new IOException("Failed to refresh token: " + response.message());
        }
    }
}
