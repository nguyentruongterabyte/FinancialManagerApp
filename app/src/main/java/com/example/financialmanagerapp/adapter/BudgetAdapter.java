package com.example.financialmanagerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.BudgetDetail;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetAdapter extends BaseAdapter {
    protected Context context;
    protected List<Budget> budgets;
    protected List<Transaction> transactions;
    protected List<Transaction> expenseTransactions;

    private TextView tvName, tvRemainAmount;
    private ProgressBar progressBar;

    public BudgetAdapter(Context context, List<Budget> budgets, List<Transaction> transactions) {
        this.context = context;
        this.budgets = budgets;
        this.transactions = transactions;
        Utils.addFeeTransactions(transactions);
        this.expenseTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                expenseTransactions.add(transaction);
        }
    }

    @Override
    public int getCount() {
        return budgets.size();
    }

    @Override
    public Object getItem(int position) {
        return budgets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return budgets.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_budget, parent, false);
        }

        setControl(convertView);
        initData(position);
        setEvents();
        return convertView;
    }

    private void setEvents() {
    }

    @SuppressLint("SetTextI18n")
    private void initData(int position) {
        Budget budget = budgets.get(position);

        tvName.setText(budget.get_name());

        // get start date and end date
        Calendar start = TimerFormatter.getCalendar(budget.get_start_date());
        Calendar end = TimerFormatter.getCalendar(budget.get_end_date());

        // get category ids from budget
        List<Integer> categoryIds = new ArrayList<>();
        for (BudgetDetail budgetDetail : budget.getBudget_details()) {
            categoryIds.add(budgetDetail.getCategory().getId());
        }

        // filter transaction in the budget
        List<Transaction> budgetTransactions = new ArrayList<>();
        for (Transaction transaction : expenseTransactions) {
            Calendar createdAt = TimerFormatter.getCalendar(transaction.get_date());
            if (categoryIds.contains(transaction.get_category_id())
                    && !createdAt.before(start) && !createdAt.after(end))
                budgetTransactions.add(transaction);
        }

        // calculate spent amount
        double spentAmount = 0;
        for (Transaction transaction : budgetTransactions) {
            spentAmount += transaction.get_amount();
        }

        if (spentAmount > budget.get_amount()) {
            tvRemainAmount.setText("Overspent " +
                    MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), spentAmount));
            int percentage = 100;
            Log.d("ProgressBar", "Setting progress to: " + percentage);
            progressBar.setProgress(percentage);
        } else {
            double budgetAmount = budget.get_amount();
            double remainAmount = budgetAmount - spentAmount;
            tvRemainAmount.setText("Remain " +
                    MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), remainAmount));

            Log.d("ProgressBar", "Budget Amount: " + budgetAmount);
            Log.d("ProgressBar", "Spent Amount: " + spentAmount);

            double percentage = spentAmount / budgetAmount * 100f;

            Log.d("ProgressBar", "Calculated Percentage: " + percentage);
            progressBar.setProgress((int) percentage);
        }
    }


    private void setControl(View convertView) {
        tvName = convertView.findViewById(R.id.tv_name);
        tvRemainAmount = convertView.findViewById(R.id.tv_remain_amount);

        progressBar = convertView.findViewById(R.id.progress_bar);
    }
}
