package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.broadcast.LogoutReceiver;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends BaseActivity {

    private boolean doubleBackToExitPressedOnce = false;
    protected LogoutReceiver logoutReceiver;
    protected FinancialManagerAPI apiService;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutReceiver = new LogoutReceiver();
        IntentFilter filter = new IntentFilter("com.example.financialmanagerapp.LOGOUT");
        registerReceiver(logoutReceiver, filter);

        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void initData() {

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Check if current user is null
        if (Utils.currentUser == null) {
            Call<ResponseObject<User>> call = apiService.get(SharedPreferencesUtils.getUserId(this));
            call.enqueue(new Callback<ResponseObject<User>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        }
    }



    private void setControl() {
    }
}