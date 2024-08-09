package com.example.financialmanagerapp.activity.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.TransactionDateAdapter;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.TransactionDate;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransactionTabFragment extends Fragment {

    private final static int TRANSACTIONS_PER_PAGE = 200; // 200 transactions a page
    private boolean isLoading = false;
    private int currentPage = 1;
    private ImageView btnBalance, btnHideBalance;
    private TextView tvBalance;
    private LinearLayout noRecord;
    private ListView listView;

    protected List<TransactionDate> transactionDates;

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

    @Override
    public void onResume() {
        super.onResume();
        isLoading = false;
        initData();
    }

    private void initData() {
        setBalance();
        getTransactions(1);
    }

    private void getTransactions(int page) {
        if (Utils.currentUser == null) return;
        if (isLoading) return;
        isLoading = true;

        if (Utils.transactions.size() == 0) {
            // set invisible list view
            noRecord.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Call<ResponseObject<List<Transaction>>> call = apiService.getTransactionByUserId(
                    Utils.currentUser.getId(), page, TRANSACTIONS_PER_PAGE);
            call.enqueue(new Callback<ResponseObject<List<Transaction>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<Transaction>>> call,
                                       @NonNull Response<ResponseObject<List<Transaction>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {

                            List<Transaction> newTransactions = response.body().getResult();

                            if (newTransactions.size() > 0) {
                                if (page == 1) {
                                    Utils.transactions = newTransactions;
                                } else {
                                    Utils.transactions.addAll(newTransactions);
                                }

                                renderTransactions();
                            }

                        } else {
                            Log.e("API_ERROR", "API call failed with status: " + response.body().getStatus() + ", message: " + response.body().getMessage());
                        }
                    } else {
                        Log.e("API_ERROR", "API call failed: " + response.message());
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<Transaction>>> call, @NonNull Throwable t) {

                    // Detailed logging
                    if (t instanceof IOException) {
                        Log.e("API_ERROR", "Network or conversion error: " + t.getMessage());
                    } else {
                        Log.e("API_ERROR", "Unexpected error: " + t.getMessage());
                    }
                    isLoading = false;
                }
            });
        } else {
            // set invisible list view
            noRecord.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            renderTransactions();
        }


    }

    private void renderTransactions() {
        // set visible list view
        noRecord.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        // remove all fee transaction
        Utils.transactions.removeIf(Transaction::isFeeTransaction);

        // Add fee transactions
        Utils.addFeeTransactions(Utils.transactions);

        syncWalletsInTransactions(Utils.transactions);

        transactionDates = Utils.groupTransactionsByDate(requireContext(), Utils.transactions);
        TransactionDateAdapter adapter = new TransactionDateAdapter(requireContext(), transactionDates, -1);
        listView.setAdapter(adapter);

        // find the position of the latest updated_at
        int latestPosition = findLastedTransactionPosition(transactionDates);

        if (latestPosition != -1) {
            listView.post(() ->
                listView.setSelection(latestPosition)
            );
        }
    }

    private void syncWalletsInTransactions(List<Transaction> transactions) {
        List<Wallet> wallets = Utils.currentUser.getWallets();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction currentTransaction = transactions.get(i);
            if (currentTransaction.getTo_wallet() != null)
                currentTransaction.setTo_wallet(Wallet.findById(currentTransaction.getTo_wallet().getId(), wallets));

            if (currentTransaction.getFrom_wallet() != null)
                currentTransaction.setFrom_wallet(Wallet.findById(currentTransaction.getFrom_wallet().getId(), wallets));

            if (currentTransaction.getWallet() != null)
                currentTransaction.setWallet(Wallet.findById(currentTransaction.getWallet().getId(), wallets));

            transactions.set(i, currentTransaction);
        }
    }

    private int findLastedTransactionPosition(List<TransactionDate> transactionDates) {
        Timestamp latestTimestamp = null;
        int latestPosition = -1;

        for (int i = 0; i < transactionDates.size(); i++) {
            List<Transaction> transactions = transactionDates.get(i).getTransactions();
            for (Transaction transaction : transactions) {
                if (latestTimestamp == null || transaction.getUpdated_at().after(latestTimestamp)) {
                    latestTimestamp = transaction.getUpdated_at();
                    latestPosition = i;
                }
            }
        }
        return latestPosition;
    }

    private void setBalance() {
        // set balance
        balance = 0;
        List<Wallet> wallets = Utils.currentUser.getWallets();
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            if (wallet.get_exclude() == 0 && wallet.get_amount() > 0) {
                balance += wallet.get_amount();
            }
        }

        // Init symbol
        symbol = Utils.currentUser.getCurrency().get_symbol();

        // Set text on text view
        tvBalance.setText(MoneyFormatter.getText(symbol, balance));

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
            if (Utils.currentUser == null)
                return;
            Intent creatingRecordActivity = new Intent("com.example.financialManagerApp.CREATING_RECORD");
            requireContext().sendBroadcast(creatingRecordActivity);
        });

        // handle list view scroll
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No - op
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && !isLoading) {
                    currentPage++;
                    getTransactions(currentPage);
                }
            }
        });
    }

    private void setControl(View view) {
        btnBalance = view.findViewById(R.id.btn_balance);
        btnHideBalance = view.findViewById(R.id.btn_hide_balance);
        btnCreateRecord = view.findViewById(R.id.btn_create_record);

        tvBalance = view.findViewById(R.id.tv_balance);

        noRecord = view.findViewById(R.id.no_record);

        listView = view.findViewById(R.id.list_view);

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, getContext());
        apiService = retrofit.create(FinancialManagerAPI.class);
    }
}
