package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.Calendar;

public class TransactionDetailActivity extends BaseActivity {
    protected Transaction transaction = new Transaction();
    private ImageButton btnBack, btnEdit, btnDelete;
    private ImageView icon;
    private LinearLayout tvMemoContainer, tvFeeContainer;
    private TextView tvDescription, tvCategory, tvAmount, tvDate, tvWallet, tvType, tvMemo, tvFee;

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
    }

    private void handleDelete() {
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
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
            String formatTime = TimerFormatter.convertTimeString(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

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