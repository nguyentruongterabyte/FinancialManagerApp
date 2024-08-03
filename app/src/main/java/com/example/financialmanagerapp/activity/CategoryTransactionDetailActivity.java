package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.TransactionDateAdapter;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.TransactionDate;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;
import java.util.Objects;

public class CategoryTransactionDetailActivity extends BaseActivity {

    private ListView listView;
    private TextView tvCategoryName, tvTotalAmount, tvTitle;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_transaction_detail);
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


        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call super.onBackPressed() to handle default back press behavior
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        // get category name, walletId, transactions from intent
        String categoryName = getIntent().getStringExtra("categoryName");
        List<Transaction> transactions = (List<Transaction>) getIntent().getSerializableExtra("transactions");
        int walletId = getIntent().getIntExtra("walletId", -1);

        // set text, text color for text views
        tvCategoryName.setText(categoryName);
        assert categoryName != null;
        calculateAmount(categoryName, transactions, walletId);

        assert transactions != null;
        List<TransactionDate> transactionDates = Utils.groupTransactionsByDate(this, transactions);
        TransactionDateAdapter adapter = new TransactionDateAdapter(this, transactionDates, walletId);

        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);
    }

    @SuppressLint("SetTextI18n")
    private void calculateAmount(String categoryName, List<Transaction> transactionList, int walletId) {

        double amount = 0;

        if (categoryName.equals("Transfer")) {// transfer transactions
            for (Transaction transaction : Objects.requireNonNull(transactionList))
                if (transaction.get_from_wallet_id() == walletId)
                    amount -= transaction.get_amount();
                else
                    amount += transaction.get_amount();

        } else if (categoryName.equals("Others")) { // other transactions
            // having two type
            // -- others expense
            // -- others income
            for (Transaction transaction : Objects.requireNonNull(transactionList))
                // other transactions of income
                if (transaction.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID) {
                    amount += transaction.get_amount();
                } else { // other transactions of expense
                    // include: fee transactions, normal expense transactions
                    if (!transaction.isFeeTransaction()
                            ||
                            (transaction.isFeeTransaction()
                                    && transaction.getParent().get_from_wallet_id() == walletId)
                    ) {
                        amount -= transaction.get_amount();
                    }
                }

        } else { // Expense and Income transactions
            // if it is income transaction: amount += transactions amount
            if (transactionList.get(0).get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                for (Transaction t : transactionList)
                    amount += t.get_amount();
            else // else: amount -= transactions amount
                for (Transaction t : transactionList)
                    amount -= t.get_amount();

        }

        tvTitle.setText("Total income");
        tvTotalAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
        if (amount < 0) {
            tvTotalAmount.setTextColor(ContextCompat.getColor(this, R.color.color_6));
            tvTitle.setText("Total expense");
        }
        else if (amount == 0) tvTotalAmount.setTextColor(ContextCompat.getColor(this, R.color.black));
        else tvTotalAmount.setTextColor(ContextCompat.getColor(this, R.color.primary));

    }

    private void setControl() {
        listView = findViewById(R.id.list_view);

        tvCategoryName = findViewById(R.id.tv_category_name);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvTitle = findViewById(R.id.tv_title);

        btnBack = findViewById(R.id.btn_back);
    }
}