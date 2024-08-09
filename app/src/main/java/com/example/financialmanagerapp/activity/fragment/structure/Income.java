package com.example.financialmanagerapp.activity.fragment.structure;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.CategoryTransactionAdapter;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Income extends Fragment {
    private SharedViewModel sharedViewModel;
    private PieChart mChart;
    private ListView listView;
    protected List<Transaction> transactions;
    protected int year;
    protected int month;
    protected List<String> xData;
    protected double incomeAmount;

    public Income() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_structure, container, false);

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

        // handle on chart value selected
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
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

        // observe live data
        sharedViewModel.getMonth().observe(getViewLifecycleOwner(), this::handleMonthChanged);
        sharedViewModel.getYear().observe(getViewLifecycleOwner(), this::handleYearChanged);
    }

    private void handleYearChanged(Integer y) {
        year = y;
        // handle calculate income amount;
        handleCalculateIncomeAmount();
        // add dataset
        addDataset(mChart, year, month);
        // set adapter
        setAdapter(year, month);
        mChart.setCenterText("Income\n"
                + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), incomeAmount));
    }

    private void handleMonthChanged(Integer m) {
        month = m;
        // handle calculate income amount;
        handleCalculateIncomeAmount();
        // add dataset
        addDataset(mChart, year, month);
        // set adapter
        setAdapter(year, month);
        mChart.setCenterText("Income\n"
                + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), incomeAmount));
    }

    private void initData() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        transactions = Utils.getAllTransactionsFromWallets();
        transactions = Utils.filterTransactionsByType(transactions, Utils.INCOME_TRANSACTION_ID);
        // handle calculate income amount;
        handleCalculateIncomeAmount();
        // init mChart
        setChart();
        // add dataset
        addDataset(mChart, year, month);
        // set list view

        setAdapter(year, month);
    }

    private void setAdapter(int year, int month) {
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);
        CategoryTransactionAdapter adapter = new CategoryTransactionAdapter(requireContext(), transactionsByMonthOfYear, -1, Utils.categoriesIcons, true);
        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);

    }

    //    private void handleCalculateIncomeAmount() {
    private void handleCalculateIncomeAmount() {
        incomeAmount = 0;
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);

        for (Transaction transaction : transactionsByMonthOfYear) {
            incomeAmount += transaction.get_amount();
        }
    }

    private void setChart() {
        mChart.setRotationEnabled(true);
        mChart.setDescription(new Description());
        mChart.setHoleRadius(60f);
        mChart.setTransparentCircleAlpha(0);
        mChart.setCenterText("Income\n"
                + MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), incomeAmount));
        mChart.setCenterTextSize(16);
        mChart.setDrawEntryLabels(true);
        mChart.getDescription().setText("");

    }

    private void addDataset(PieChart pieChart, int year, int month) {
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        List<Transaction> transactionsByMonthOfYear = Utils.groupTransactionsByMonthOfYear(transactions, year, month);
        List<Transaction> incomeTransactions = Utils.filterTransactionsByType(transactionsByMonthOfYear, Utils.INCOME_TRANSACTION_ID);
        Map<String, List<Transaction>> transactionsByCategoryName = Utils.groupTransactionsByCategoryName(incomeTransactions);


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
        }

        // Create PieEntry list with values and labels
        for (float yDatum : yData) {
            yEntries.add(new PieEntry(yDatum, ""));
        }


        PieDataSet pieDataSet = new PieDataSet(yEntries, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        // Display value labels on the pie chart
        pieDataSet.setValueTextColors(colors);


        // Set colors for the slices from the colors list
        pieDataSet.setColors(colors);

        // Display value labels outside the pie chart
        pieDataSet.setValueLinePart1OffsetPercentage(80f); // Offset for the first part of the line
        pieDataSet.setValueLinePart1Length(0.3f); // Length of the first part of the line
        pieDataSet.setValueLinePart2Length(0.4f); // Length of the second part of the line
        pieDataSet.setValueLineColor(Color.BLACK); // Color of the value lines
        pieDataSet.setValueLineVariableLength(true); // Allow variable length lines

        // Set colors for the value text
        pieDataSet.setValueTextColors(colors);

        // Set colors for the slices from the colors list
        pieDataSet.setColors(colors);

        // Enable drawing of lines and labels outside the chart
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        // Set the data
        PieData pieData = new PieData(pieDataSet);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);

        pieData.setValueFormatter(new PercentFormatter(pieChart)); // Displays percentage values
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(10, 10, 10, 10);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    private void setControl(View view) {
        mChart = view.findViewById(R.id.pie_chart);
        listView = view.findViewById(R.id.list_view);
    }
}