package com.example.financialmanagerapp.activity.fragment.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.DTO.RefreshTokenDTO;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountDialog extends BottomSheetDialogFragment {
    protected TextView tvName, tvBalance;
    protected FloatingActionButton btnSignOut;
    protected FinancialManagerAPI apiService;
    private double balance;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_bottom_sheet_layout, container, false);
        setControl(view);
        initData();
        setEvents();
        return view;
    }

    private void setEvents() {
        // handle button sign out clicked
        btnSignOut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Sign Out")
                    .setMessage("Are you sure?")
                    .setPositiveButton("OK", (dialog, which) ->
                        handleSignOut()
                    )
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // canceled
                    });
            // show it
            AlertDialog dialog = builder.create();
            dialog.show();
        });


    }

    private void handleSignOut() {

        RefreshTokenDTO request = new RefreshTokenDTO(SharedPreferencesUtils.getRefreshToken(getContext()));
        Call<ResponseObject<Void>> call = apiService.logout(request);
        call.enqueue(new Callback<ResponseObject<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Void>> call, @NonNull Response<ResponseObject<Void>> response) {

                // clear current user
                Utils.currentUser = null;

                // clear storage
                SharedPreferencesUtils.clearSharedPreferences(getContext());

                // return to Login Activity
                Intent logoutIntent = new Intent("com.example.financialManagerApp.LOGOUT");
                requireContext().sendBroadcast(logoutIntent);

            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Void>> call, @NonNull Throwable t) {
                // clear current user
                Utils.currentUser = null;

                // clear storage
                SharedPreferencesUtils.clearSharedPreferences(getContext());

                // return to Login Activity
                Intent logoutIntent = new Intent("com.example.financialManagerApp.LOGOUT");
                requireContext().sendBroadcast(logoutIntent);
            }
        });


    }

    private void initData() {
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, getContext());
        apiService = retrofit.create(FinancialManagerAPI.class);

        // init account
        if (Utils.currentUser != null) {
            tvName.setText(Utils.currentUser.get_name());
            initBalance();
            String symbol = Utils.currentUser.getCurrency().get_symbol();
            tvBalance.setText(MoneyFormatter.getText(symbol, balance));
        } else {
            Call<ResponseObject<User>> call = apiService.get(SharedPreferencesUtils.getUserId(getContext()));
            call.enqueue(new Callback<ResponseObject<User>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();
                        tvName.setText(Utils.currentUser.get_name());
                        initBalance();
                        String symbol = Utils.currentUser.getCurrency().get_symbol();
                        tvBalance.setText(MoneyFormatter.getText(symbol, balance));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        }
    }

    private void initBalance() {
        // Init balance
        balance = 0;
        for (int i = 0; i < Utils.currentUser.getWallets().size(); i++) {
            Wallet wallet = Utils.currentUser.getWallets().get(i);
            if (wallet.get_exclude() == 0 && wallet.get_amount() > 0) {
                balance += wallet.get_amount();
            }
        }

    }
    private void setControl(View view) {
        tvName = view.findViewById(R.id.tv_name);
        tvBalance = view.findViewById(R.id.tv_balance);
        btnSignOut = view.findViewById(R.id.btn_sign_out);
    }
}
