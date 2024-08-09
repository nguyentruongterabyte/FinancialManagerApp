package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.TransactionDateAdapter;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.TransactionDate;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.Calendar;
import java.util.List;

public class OverviewActivity extends BaseActivity {
    private ImageButton btnReturn, btnBack, btnForward;
    private TextView tvDate, tvIncomeAmount, tvExpenseAmount, tvTotalAmount;
    private ListView listView;
    private ScrollView scrollView;
    private LinearLayout noRecord;
    protected int year, month;
    protected List<Transaction> transactions;
    protected double incomeAmount;
    protected double expenseAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle back
        btnReturn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // handle decrease month year
        btnBack.setOnClickListener(v -> {
            handleDecreaseMonthYear();
            handleMonthYearChanged();
        });

        // handle increase month year
        btnForward.setOnClickListener(v -> {
            handleIncreaseMonthYear();
            handleMonthYearChanged();
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

    @SuppressLint("DefaultLocale")
    private void handleMonthYearChanged() {
        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));
        // handle calculate income, expense, total amounts
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);
        handleCalculateAmount(transactionsByMonthOfYear);
        handleRenderListview(transactionsByMonthOfYear);
    }

    @SuppressLint("DefaultLocale")
    private void handleIncreaseMonthYear() {
        if (month > 10) {
            month = 0;
            year++;
        } else {
            month++;
        }
        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));
    }
    @SuppressLint("DefaultLocale")
    private void handleDecreaseMonthYear() {
        if (month < 1) {
            month = 11;
            year--;
        } else {
            month--;
        }
        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));
    }
    @SuppressLint("DefaultLocale")
    private void initData() {
        Calendar calendar = Calendar.getInstance();
        year = getIntent().getIntExtra("year", calendar.get(Calendar.YEAR));
        month = getIntent().getIntExtra("month", calendar.get(Calendar.MONTH));

        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));

        transactions = Utils.getAllTransactionsFromWallets();
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);

        // handle calculate income, expense, total amounts
        handleCalculateAmount(transactionsByMonthOfYear);

        // handle render list view
        handleRenderListview(transactionsByMonthOfYear);
    }

    private void handleRenderListview(List<Transaction> transactionList) {
        List<TransactionDate> transactionDates = Utils.groupTransactionsByDate(this, transactionList);
        TransactionDateAdapter adapter = new TransactionDateAdapter(this, transactionDates, -1);
        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);
    }

    private void handleCalculateAmount(List<Transaction> transactionList) {
        incomeAmount = 0;
        expenseAmount = 0;

        // set visibility if transactions is empty
        if (transactionList.size() == 0) {
            scrollView.setVisibility(View.GONE);
            noRecord.setVisibility(View.VISIBLE);
            return;
        } else {
            scrollView.setVisibility(View.VISIBLE);
            noRecord.setVisibility(View.GONE);
        }

        for (Transaction transaction : transactionList) {
            if (transaction.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                incomeAmount += transaction.get_amount();
            if (transaction.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                expenseAmount -= transaction.get_amount();

        }

        // set text view
        String symbol = Utils.currentUser.getCurrency().get_symbol();
        tvIncomeAmount.setText(MoneyFormatter.getText(symbol, incomeAmount));
        tvExpenseAmount.setText(MoneyFormatter.getText(symbol, expenseAmount));
        tvTotalAmount.setText(MoneyFormatter.getText(symbol, incomeAmount + expenseAmount));
    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnForward = findViewById(R.id.btn_forward);
        btnReturn = findViewById(R.id.btn_return);

        tvIncomeAmount = findViewById(R.id.tv_income_amount);
        tvExpenseAmount = findViewById(R.id.tv_expense_amount);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvDate = findViewById(R.id.tv_date);

        listView = findViewById(R.id.list_view);

        scrollView = findViewById(R.id.scroll_view);

        noRecord = findViewById(R.id.no_record);
    }
}