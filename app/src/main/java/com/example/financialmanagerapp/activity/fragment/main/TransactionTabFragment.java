package com.example.financialmanagerapp.activity.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransactionTabFragment extends Fragment {

    private ImageView btnBalance, btnHideBalance;
    private TextView tvBalance;
    protected LinearLayout noRecord;
    private FloatingActionButton btnCreateRecord;
    private double balance;
    private String symbol;
    protected FinancialManagerAPI apiService;

    public TransactionTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControl(view);
        initData();
        setEvents();

    }

    private void initData() {

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, getContext());
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Check if current user is null
        if (Utils.currentUser == null) {
            Call<ResponseObject<User>> call = apiService.get(SharedPreferencesUtils.getUserId(getContext()));
            call.enqueue(new Callback<ResponseObject<User>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();

                        initBalance();

                        // Init symbol
                        symbol = Utils.currentUser.getCurrency().get_symbol();

                        // Set text on text view
                        tvBalance.setText(MoneyFormatter.getText(symbol, balance));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            initBalance();

            // Init symbol
            symbol = Utils.currentUser.getCurrency().get_symbol();

            // Set text on text view
            tvBalance.setText(MoneyFormatter.getText(symbol, balance));
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

    private void setEvents() {

        // handle button show balance clicked
        btnBalance.setOnClickListener(v -> {
            tvBalance.setText("****");
            btnHideBalance.setVisibility(View.VISIBLE);
            btnBalance.setVisibility(View.GONE);
        });

        // handle button hide balance clicked
        btnHideBalance.setOnClickListener(v -> {
            tvBalance.setText(MoneyFormatter.getText(symbol, balance));
            btnBalance.setVisibility(View.VISIBLE);
            btnHideBalance.setVisibility(View.GONE);
        });

        // handle button create transaction clicked
        btnCreateRecord.setOnClickListener(v -> {
            Intent creatingRecordActivity = new Intent("com.example.financialManagerApp.CREATING_RECORD");
            requireContext().sendBroadcast(creatingRecordActivity);
        });
    }

    private void setControl(View view) {
        btnBalance = view.findViewById(R.id.btn_balance);
        btnHideBalance = view.findViewById(R.id.btn_hide_balance);
        btnCreateRecord = view.findViewById(R.id.btn_create_record);

        tvBalance = view.findViewById(R.id.tv_balance);

        noRecord = view.findViewById(R.id.no_record);

        btnBalance.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
    }
}
