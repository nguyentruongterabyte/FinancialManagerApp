package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.mapper.TransactionMapper;
import com.example.financialmanagerapp.model.mapper.WalletMapper;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.sql.Timestamp;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransactionDetailActivity extends BaseActivity {
    private Transaction transaction = new Transaction();
    private ImageButton btnBack, btnEdit, btnDelete;
    private ImageView icon;
    private LinearLayout tvMemoContainer, tvFeeContainer;
    private TextView tvDescription, tvCategory, tvAmount, tvDate, tvWallet, tvType, tvMemo, tvFee;
    private FinancialManagerAPI apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle button back clicked
        btnBack.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            Timestamp updatedAt = new Timestamp(calendar.getTimeInMillis());
            transaction.setUpdated_at(updatedAt);
            Transaction.updateTransactionInList(transaction, Utils.transactions);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        // handle button edit clicked
        btnEdit.setOnClickListener(v -> {
            Intent editingTransactionActivity = new Intent(this, EditingTransactionActivity.class);
            editingTransactionActivity.putExtra("transaction", transaction);
            startActivity(editingTransactionActivity);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // handle button delete clicked
        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Transaction")
                    .setMessage("Do you really want to delete this transaction?")
                    .setPositiveButton("DELETE", (dialog, which) ->
                            handleDelete()
                    )
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Calendar calendar = Calendar.getInstance();
                Timestamp updatedAt = new Timestamp(calendar.getTimeInMillis());
                transaction.setUpdated_at(updatedAt);
                Transaction.updateTransactionInList(transaction, Utils.transactions);
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
    }

    private void handleDelete() {
        if (transaction.isFeeTransaction()) {

            // handle fee transaction, set fee equals 0
            double fee = transaction.get_amount();
            Transaction parent = transaction.getParent();
            parent.set_fee(0);
            Call<ResponseObject<Transaction>> call = apiService.updateTransaction(
                    TransactionMapper.toTransactionDTO(parent),
                    Utils.currentUser.getId(),
                    parent.getId());
            call.enqueue(new Callback<ResponseObject<Transaction>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Response<ResponseObject<Transaction>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            // Update fee
                            transaction = response.body().getResult();
                            Transaction.updateTransactionInList(transaction, Utils.transactions);
                            // update amount
                            Wallet wallet = transaction.getFrom_wallet();
                            handleUpdateWalletAmount(wallet, wallet.get_amount() + fee);
                        } else {
                            Toast.makeText(TransactionDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TransactionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            // handle delete other transactions
            Call<ResponseObject<Void>> call = apiService.deleteTransaction(
                    Utils.currentUser.getId(),
                    transaction.getId());

            call.enqueue(new Callback<ResponseObject<Void>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<Void>> call, @NonNull Response<ResponseObject<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 204) {
                            // Delete transaction in transactions utils
                            for (int i = 0; i < Utils.transactions.size(); i++) {
                                Transaction currentTransaction = Utils.transactions.get(i);
                                if (currentTransaction.getId() == transaction.getId()) {
                                    Utils.transactions.remove(i);
                                    break;
                                }
                            }

                            // handle update wallet amount
                            double amount;
                            Wallet wallet;
                            Wallet fromWallet = transaction.getFrom_wallet();
                            switch (transaction.get_transaction_type_id()) {
                                case Utils.INCOME_TRANSACTION_ID:
                                    wallet = transaction.getWallet();
                                    amount = wallet.get_amount() - transaction.get_amount();
                                    handleUpdateWalletAmount(wallet, amount);
                                    break;
                                case Utils.EXPENSE_TRANSACTION_ID:
                                    wallet = transaction.getWallet();
                                    amount = wallet.get_amount() + transaction.get_amount();
                                    handleUpdateWalletAmount(wallet, amount);
                                    break;
                                case Utils.TRANSFER_TRANSACTION_ID:
                                    wallet = transaction.getTo_wallet();
                                    amount = wallet.get_amount() - transaction.get_amount();
                                    double fromWalletAmount = fromWallet.get_amount() + transaction.get_amount() + transaction.get_fee();
                                    handleUpdateWalletAmount(wallet, fromWallet, amount, fromWalletAmount);
                                    break;
                            }

                        } else {
                            Toast.makeText(TransactionDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TransactionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<Void>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        }
    }

    private void handleUpdateWalletAmount(Wallet wallet, Wallet fromWallet, double amount, double fromWalletAmount) {
        fromWallet.set_amount(fromWalletAmount);
        Call<ResponseObject<Wallet>> call = apiService.updateWallet(WalletMapper.toWalletDTO(fromWallet), Utils.currentUser.getId(), fromWallet.getId());
        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Wallet updatedWallet = response.body().getResult();
                        boolean isUpdated = Wallet.updateWalletInList(updatedWallet, Utils.currentUser.getWallets());
                        if (isUpdated) {
                            handleUpdateWalletAmount(wallet, amount);
                        }
                    } else {
                        Toast.makeText(TransactionDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                Log.d("API_ERROR", "API call failed: " + t.getMessage());
            }
        });

    }

    private void handleUpdateWalletAmount(Wallet wallet, double amount) {
        wallet.set_amount(amount);
        Call<ResponseObject<Wallet>> call = apiService.updateWallet(WalletMapper.toWalletDTO(wallet), Utils.currentUser.getId(), wallet.getId());
        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {

                        Wallet updatedWallet = response.body().getResult();
                        boolean isUpdated = Wallet.updateWalletInList(updatedWallet, Utils.currentUser.getWallets());
                        if (isUpdated) {
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                            finish();
                        } else {
                            Toast.makeText(TransactionDetailActivity.this, "An error occur", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(TransactionDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                Log.d("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // get the transaction from the intent
        transaction = (Transaction) getIntent()
                .getSerializableExtra("transaction");

        if (transaction != null) {
            String description = transaction.get_description() != null ?
                    transaction.get_description() :
                    transaction.get_transaction_type_id() == Utils.TRANSFER_TRANSACTION_ID ?
                            "Transfer"
                            : transaction.getCategory().get_name();
            tvDescription.setText(description);

            if (transaction.get_transaction_type_id() == Utils.TRANSFER_TRANSACTION_ID) {
                tvCategory.setText("Transfer");
                tvWallet.setText(transaction.getFrom_wallet().get_name() + " --> " + transaction.getTo_wallet().get_name());
                if (transaction.get_fee() > 0) {
                    tvFeeContainer.setVisibility(View.VISIBLE);
                    tvFee.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), -transaction.get_fee()));
                }

                icon.setImageResource(R.drawable.ic_transaction);
                icon.setBackgroundColor(ContextCompat.getColor(this, R.color.color_2));

            } else {
                tvCategory.setText(transaction.getCategory().get_name());
                tvWallet.setText(transaction.getWallet().get_name());
                tvFeeContainer.setVisibility(View.GONE);
                // set icon view and its background color
                icon.setImageResource(Utils.categoriesIcons[transaction.getCategory().get_icon()]);
                int color = Color.parseColor(transaction.getCategory().get_color());
                icon.setBackgroundColor(color);
            }
            icon.setColorFilter(ContextCompat.getColor(this, R.color.white));

            double amount = transaction.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID ? -transaction.get_amount() : transaction.get_amount();
            tvAmount.setText(MoneyFormatter.getText(
                    Utils.currentUser.getCurrency().get_symbol(),
                    amount));
            switch (transaction.get_transaction_type_id()) {
                case Utils.INCOME_TRANSACTION_ID:
                    tvAmount.setTextColor(ContextCompat.getColor(this, R.color.primary));
                    tvType.setText("Income");
                    break;
                case Utils.EXPENSE_TRANSACTION_ID:
                    tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_6));
                    tvType.setText("Expense");
                    break;
                case Utils.TRANSFER_TRANSACTION_ID:
                    tvAmount.setTextColor(ContextCompat.getColor(this, R.color.black));
                    tvType.setText("Transfer");
                    break;
            }

            Calendar calendar = TimerFormatter.getCalendar(transaction.get_date());

            String formatDate = TimerFormatter.convertDateString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            String formatTime = TimerFormatter.convertTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            tvDate.setText(formatDate + " " + formatTime);

            if (transaction.get_memo() != null) {
                tvMemoContainer.setVisibility(View.VISIBLE);
                tvMemo.setText(transaction.get_memo());
            } else {
                tvMemoContainer.setVisibility(View.GONE);
            }
        }
    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        icon = findViewById(R.id.icon);

        tvDescription = findViewById(R.id.tv_description);
        tvCategory = findViewById(R.id.tv_category);
        tvAmount = findViewById(R.id.tv_amount);
        tvDate = findViewById(R.id.tv_date);
        tvWallet = findViewById(R.id.tv_wallet);
        tvType = findViewById(R.id.tv_type);
        tvMemo = findViewById(R.id.tv_memo);
        tvFee = findViewById(R.id.tv_fee);

        tvMemoContainer = findViewById(R.id.tv_memo_container);
        tvFeeContainer = findViewById(R.id.tv_fee_container);
    }
}