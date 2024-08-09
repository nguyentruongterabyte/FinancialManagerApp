package com.example.financialmanagerapp.activity.fragment.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.CreatingBudgetActivity;
import com.example.financialmanagerapp.activity.WalletDetailActivity;
import com.example.financialmanagerapp.adapter.BudgetAdapter;
import com.example.financialmanagerapp.adapter.WalletManagerAdapter;
import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;

import retrofit2.Retrofit;

public class WalletTabFragment extends Fragment {

    private ListView listView;
    private ListView listViewBudget;
    private TextView tvManager;
    private LinearLayout btnAddBudget;

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

        // handle button add budget clicked
        btnAddBudget.setOnClickListener(v -> {
            Intent creatingBudgetActivity = new Intent(requireContext(), CreatingBudgetActivity.class);
            if (requireContext() instanceof Activity) {
                requireContext().startActivity(creatingBudgetActivity);
                ((Activity) requireContext()).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initData() {
        WalletManagerAdapter adapter = new WalletManagerAdapter(requireContext(), Utils.currentUser.getWallets(), Utils.walletIcons);
        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);
        tvManager.setText(String.format("Manager(%d)", Utils.currentUser.getWallets().size()));

        List<Budget> budgets = Utils.currentUser.getBudgets();
        List<Transaction> transactions = Utils.getAllTransactionsFromWallets();
        BudgetAdapter budgetAdapter = new BudgetAdapter(requireContext(), budgets, transactions);
        listViewBudget.setAdapter(budgetAdapter);
        Utils.setListViewHeightBasedOnItems(listViewBudget);

    }

    private void setControl(View view) {
        listView = view.findViewById(R.id.list_view);
        listViewBudget = view.findViewById(R.id.list_view_budget);

        tvManager = view.findViewById(R.id.tv_manager);

        btnAddBudget = view.findViewById(R.id.btn_add_budget);

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, getContext());
        apiService = retrofit.create(FinancialManagerAPI.class);
    }
}
