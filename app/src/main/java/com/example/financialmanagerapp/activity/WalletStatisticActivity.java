package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class WalletStatisticActivity extends BaseActivity {

    private ImageButton btnBack, btnForward, btnReturn;
    private TextView tvDate, tvOpeningBalance, tvEndingBalance,
            tvIncomeAmount, tvExpenseAmount, tvTotalAmount;
    private LinearLayout showMoreStructure, showMoreOverview;
    private PieChart mChart;

    protected int year;
    protected int month; // start at 0
    protected List<Transaction> transactions;
    protected double incomeAmount;
    protected double expenseAmount;
    protected double openingBalance;
    protected Wallet wallet;
    protected List<String> xData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_statistic);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle show more overview
        showMoreOverview.setOnClickListener(v -> {
            Intent overviewActivity = new Intent(WalletStatisticActivity.this, OverviewActivity.class);
            overviewActivity.putExtra("year", year);
            overviewActivity.putExtra("month", month);
            overviewActivity.putExtra("walletId", wallet.getId());
            startActivity(overviewActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // handle show more structure clicked
        showMoreStructure.setOnClickListener(v -> {
            Intent structureActivity = new Intent(WalletStatisticActivity.this, StructureActivity.class);
            structureActivity.putExtra("year", year);
            structureActivity.putExtra("month", month);
            structureActivity.putExtra("walletId", wallet.getId());
            startActivity(structureActivity);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (xData.size() == 0)
                    return;

                // Check if the selected Entry is a PieEntry
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    int index = (int) h.getX(); // Get the index of the selected entry

                    // Get the category name and amount
                    String categoryName = xData.get(index);
                    float totalAmount = pieEntry.getValue();

                    // Set the center text with the category name and total amount
                    @SuppressLint("DefaultLocale")
                    String centerText = categoryName + "\n"
                            + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), (double) -totalAmount);
                    mChart.setCenterText(centerText);
                }
            }

            @Override
            public void onNothingSelected() {

            }
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

        btnReturn.setOnClickListener(v -> {
            // Call super.onBackPressed() to handle default back press behavior
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });
    }

    private void handleMonthYearChanged() {
        handleCalculateEndingBalance(year, month);
        String symbol = Utils.currentUser.getCurrency().get_symbol();
        tvIncomeAmount.setText(MoneyFormatter.getText(symbol, incomeAmount));
        tvExpenseAmount.setText(MoneyFormatter.getText(symbol, expenseAmount));
        tvTotalAmount.setText(MoneyFormatter.getText(symbol, incomeAmount + expenseAmount));
        addDataset(mChart, year, month);
        mChart.setCenterText("Expense\n"
                + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), expenseAmount));
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
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);

        wallet = (Wallet) getIntent().getSerializableExtra("wallet");

        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));

        openingBalance = calculateOpeningBalance();
        tvOpeningBalance.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), openingBalance));
        transactions = new ArrayList<>();
        if (wallet != null) {
            transactions.addAll(wallet.getTransactions());

        }

        // handle calculate ending balance by month of year
        handleCalculateEndingBalance(year, month);
        String symbol = Utils.currentUser.getCurrency().get_symbol();
        tvIncomeAmount.setText(MoneyFormatter.getText(symbol, incomeAmount));
        tvExpenseAmount.setText(MoneyFormatter.getText(symbol, expenseAmount));
        tvTotalAmount.setText(MoneyFormatter.getText(symbol, incomeAmount + expenseAmount));

        // init mChart
        setChart();
        addDataset(mChart, year, month);
    }

    private void addDataset(PieChart pieChart, int year, int month) {
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);
        List<Transaction> expenseTransactions = Utils.filterTransactionsByType(transactionsByMonthOfYear, Utils.EXPENSE_TRANSACTION_ID);
        Map<String, List<Transaction>> transactionsByCategoryName = Utils.groupTransactionsByCategoryName(expenseTransactions);

        int maxCategories = 4; // Max number of categories to display separately
        float othersTotalAmount = 0;
        xData = new ArrayList<>();
        List<Float> yData = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>(); // Color list for pie chart slices

        // Create a list of pairs (categoryName, totalAmount) and sort it by totalAmount descending
        List<Map.Entry<String, Float>> categoryTotalAmounts = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : transactionsByCategoryName.entrySet()) {
            String categoryName = entry.getKey();
            List<Transaction> transactions = entry.getValue();
            float totalAmount = 0;
            for (Transaction transaction : transactions) {
                totalAmount += transaction.get_amount();
            }
            categoryTotalAmounts.add(new AbstractMap.SimpleEntry<>(categoryName, totalAmount));
        }

        // Sort the list by totalAmount in descending order
        categoryTotalAmounts.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        // Populate xData, yData, and colors with the top categories, and calculate "Others"
        for (int i = 0; i < categoryTotalAmounts.size(); i++) {
            if (i < maxCategories) {
                String categoryName = categoryTotalAmounts.get(i).getKey();
                xData.add(categoryName);
                yData.add(categoryTotalAmounts.get(i).getValue());

                // Get color from the first transaction in the category list
                List<Transaction> transactions = transactionsByCategoryName.get(categoryName);
                if (transactions != null && !transactions.isEmpty()) {
                    String colorHex = transactions.get(0).getCategory().get_color();
                    try {
                        int color = Color.parseColor(colorHex);
                        colors.add(color);
                    } catch (IllegalArgumentException e) {
                        colors.add(Color.GRAY); // Default color if parsing fails
                    }
                }
            } else {
                othersTotalAmount += categoryTotalAmounts.get(i).getValue();
            }
        }

        // If there are more than maxCategories categories, add "Others"
        if (categoryTotalAmounts.size() > maxCategories) {
            xData.add("Others");
            yData.add(othersTotalAmount);
            colors.add(Color.LTGRAY); // Default color for "Others"
        }

        // Create PieEntry list with values and labels
        for (float yDatum : yData) {
            yEntries.add(new PieEntry(yDatum, ""));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Expense Struct");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        // Hide value labels on the pie chart
        pieDataSet.setValueTextColor(Color.TRANSPARENT);

        // Set colors for the slices from the colors list
        pieDataSet.setColors(colors);

        // Calculate total sum
        float totalSum = 0;
        for (float value : yData) {
            totalSum += value;
        }

        // Set up the legend to display the xData beside the pie chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextSize(14);

        // Customize legend labels with percentages
        float finalTotalSum = totalSum;
        legend.setCustom(new ArrayList<LegendEntry>() {{
            int maxCategoryNameLength = 0;

            // First, find the maximum length of the category names
            for (String category : xData) {
                if (category.length() > maxCategoryNameLength) {
                    maxCategoryNameLength = category.length();
                }
            }

            // Iterate through the data and add LegendEntry items with formatted labels
            for (int i = 0; i < xData.size(); i++) {
                float percentage = (yData.get(i) / finalTotalSum) * 100;
                @SuppressLint("DefaultLocale")
                String formattedPercentage = String.format("(%.1f%%)", percentage);

                // Calculate the number of spaces needed to align the percentages
                String categoryName = xData.get(i);
                int spacesCount = maxCategoryNameLength - categoryName.length() + 1; // +1 for padding
                String spaces = new String(new char[spacesCount]).replace("\0", " ");

                // Combine the category name and percentage
                String label = categoryName + spaces + formattedPercentage;
                add(new LegendEntry(label, Legend.LegendForm.CIRCLE, 10f, 2f, null, colors.get(i)));
            }

            // Handle the case where there is no data
            if (xData.size() == 0) {
                String label = "_\t\t\t\t\t\t\t\t\t\t\t\t\t\t(0.0%)";
                colors.add(Color.RED);
                colors.add(Color.BLUE);
                colors.add(Color.YELLOW);
                colors.add(Color.GREEN);
                colors.add(Color.LTGRAY);
                for (int i = 0; i < 5; i++) {
                    add(new LegendEntry(label, Legend.LegendForm.CIRCLE, 10f, 2f, null, colors.get(i)));
                }
            }
        }});

        // If there is no data, set a default "No Data" slice
        if (xData.size() == 0) {
            yEntries.clear(); // Clear existing entries
            yEntries.add(new PieEntry(1f, "No Data")); // Single entry for "No Data"
            colors.clear(); // Clear existing colors
            colors.add(Color.GRAY); // Set color for "No Data"

            pieDataSet = new PieDataSet(yEntries, "No Data");
            pieDataSet.setColors(colors);
            pieDataSet.setValueTextColor(Color.TRANSPARENT); // Hide value labels
        }

        // Set the data
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void setChart() {
        mChart.setRotationEnabled(true);
        mChart.setDescription(new Description());
        mChart.setHoleRadius(60f);
        mChart.setTransparentCircleAlpha(0);
        mChart.setCenterText("Expense\n"
                + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), expenseAmount));
        mChart.setCenterTextSize(16);
        mChart.setDrawEntryLabels(true);
        mChart.getDescription().setText("");
    }

    private void handleCalculateEndingBalance(int year, int month) {
        incomeAmount = 0;
        expenseAmount = 0;

        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);

        for (Transaction transaction : transactionsByMonthOfYear) {
            if (transaction.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                incomeAmount += transaction.get_amount();
            if (transaction.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                expenseAmount -= transaction.get_amount();
        }

        double endingBalance = openingBalance + incomeAmount + expenseAmount;
        tvEndingBalance.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), endingBalance));
    }

    private double calculateOpeningBalance() {
        return wallet.get_initial_amount();
    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnForward = findViewById(R.id.btn_forward);
        btnReturn = findViewById(R.id.btn_return);

        tvDate = findViewById(R.id.tv_date);
        tvOpeningBalance = findViewById(R.id.tv_opening_balance);
        tvEndingBalance = findViewById(R.id.tv_ending_balance);
        tvIncomeAmount = findViewById(R.id.tv_income_amount);
        tvExpenseAmount = findViewById(R.id.tv_expense_amount);
        tvTotalAmount = findViewById(R.id.tv_total_amount);

        mChart = findViewById(R.id.pie_chart);
        showMoreStructure = findViewById(R.id.show_more_structure);
        showMoreOverview = findViewById(R.id.show_more_overview);
    }
}