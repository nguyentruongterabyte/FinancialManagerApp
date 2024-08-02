package com.example.financialmanagerapp.activity.fragment.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.WalletDetailActivity;
import com.example.financialmanagerapp.adapter.WalletManagerAdapter;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalletTabFragment extends Fragment {

    private ListView listView;
    private TextView tvManager;
    protected FinancialManagerAPI apiService;

    public WalletTabFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControl(view);
        initData();
        setEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void setEvents() {
        // handle on item wallet clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Wallet wallet = (Wallet) parent.getItemAtPosition(position);
            Intent walletDetailActivity = new Intent(requireContext(), WalletDetailActivity.class);
            walletDetailActivity.putExtra("wallet", wallet);
            if (requireContext() instanceof Activity) {
                requireContext().startActivity(walletDetailActivity);
                ((Activity) requireContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initData() {
        // Check if current user is null
        if (Utils.currentUser == null) {
            Call<ResponseObject<User>> call = apiService.get(SharedPreferencesUtils.getUserId(getContext()));
            call.enqueue(new Callback<ResponseObject<User>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();
                        WalletManagerAdapter adapter = new WalletManagerAdapter(requireContext(), Utils.currentUser.getWallets(), Utils.walletIcons);
                        listView.setAdapter(adapter);
                        tvManager.setText(String.format("Manager(%d)", Utils.currentUser.getWallets().size()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            WalletManagerAdapter adapter = new WalletManagerAdapter(requireContext(), Utils.currentUser.getWallets(), Utils.walletIcons);
            listView.setAdapter(adapter);

            tvManager.setText(String.format("Manager(%d)", Utils.currentUser.getWallets().size()));
        }
    }

    private void setControl(View view) {
        listView = view.findViewById(R.id.list_view);

        tvManager = view.findViewById(R.id.tv_manager);

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, getContext());
        apiService = retrofit.create(FinancialManagerAPI.class);
    }
}
