package com.example.financialmanagerapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.WalletAdapter;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

public class WalletDetailActivity extends BaseActivity {
    private ListView listView;
    private ImageButton btnBack, btnEdit, btnStatistic;
    private ImageView walletImage;
    private TextView tvWalletName, tvWalletAmount, tvWalletInitialAmount,
            tvIncomeTransactionsQuantity,
            tvExpenseTransactionsQuantity,
            tvTransferTransactionsQuantity,
            btnViewAll;
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // Handle button back clicked
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void initData() {
//      Test
        WalletAdapter adapter = new WalletAdapter(
                this,
                Utils.currentUser.getWallets(),
                Utils.walletIcons);
        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);

        // get the wallet from extra
        wallet = (Wallet) getIntent().getSerializableExtra("wallet");

        if (wallet != null) {
            // set image resource and background for wallet image
            walletImage.setImageResource(Utils.walletIcons[wallet.get_icon()]);
            Utils.formattingImageBackground(this, walletImage, wallet.get_color());

            // set wallet name
            tvWalletName.setText(wallet.get_name());

            // set wallet amount
            tvWalletAmount.setText(MoneyFormatter.getText(
                    Utils.currentUser.getCurrency().get_symbol(),
                    wallet.get_amount()));

            // set initial amount
            tvWalletInitialAmount.setText(MoneyFormatter.getText(
                    Utils.currentUser.getCurrency().get_symbol(),
                    wallet.get_initial_amount()));

            // handle counting transaction types
            handleCountingTransactionTypes();
        }
    }

    private void handleCountingTransactionTypes() {
        int incomeTransactionsQuantity = 0,
                expenseTransactionsQuantity = 0,
                transferTransactionsQuantity = 0;
        // calculate transaction by transaction types
        for (Transaction transaction : wallet.getTransactions()) {
            switch (transaction.get_transaction_type_id()) {
                case Utils.INCOME_TRANSACTION_ID:
                    incomeTransactionsQuantity += 1;
                    break;
                case Utils.EXPENSE_TRANSACTION_ID:
                    expenseTransactionsQuantity += 1;
                    break;
                case Utils.TRANSFER_TRANSACTION_ID:
                    transferTransactionsQuantity += 1;
                    break;
            }
        }

        // set text view
        tvIncomeTransactionsQuantity.setText(
                incomeTransactionsQuantity < 2
                        ? incomeTransactionsQuantity + " transaction"
                        : incomeTransactionsQuantity + " transactions");

        tvExpenseTransactionsQuantity.setText(
                expenseTransactionsQuantity < 2
                        ? expenseTransactionsQuantity + " transaction"
                        : expenseTransactionsQuantity + " transactions");

        tvTransferTransactionsQuantity.setText(
                transferTransactionsQuantity < 2
                        ? transferTransactionsQuantity + " transaction"
                        : transferTransactionsQuantity + " transactions");

    }

    private void setControl() {
        listView = findViewById(R.id.list_view);

        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        btnStatistic = findViewById(R.id.btn_statistic);
        btnStatistic.setColorFilter(ContextCompat.getColor(this, R.color.black));
        btnViewAll = findViewById(R.id.btn_view_all);

        walletImage = findViewById(R.id.wallet_image);

        tvWalletName = findViewById(R.id.tv_wallet_name);
        tvWalletAmount = findViewById(R.id.tv_wallet_amount);
        tvWalletInitialAmount = findViewById(R.id.tv_wallet_initial_amount);
        tvIncomeTransactionsQuantity = findViewById(R.id.tv_income_transactions_quantity);
        tvExpenseTransactionsQuantity = findViewById(R.id.tv_expense_transactions_quantity);
        tvTransferTransactionsQuantity = findViewById(R.id.tv_transfer_transactions_quantity);

    }
}