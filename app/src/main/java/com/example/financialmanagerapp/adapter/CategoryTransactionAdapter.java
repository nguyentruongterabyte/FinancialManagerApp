package com.example.financialmanagerapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.CategoryTransactionDetailActivity;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategoryTransactionAdapter extends BaseAdapter {
    protected Context context;
    protected List<Transaction> transactions;
    protected Map<String, List<Transaction>> transactionsByCategory;
    protected List<String> categoryKeys;
    protected int[] icons;
    protected int walletId;
    protected double amount;
    protected double totalAmount;
    protected boolean displayPercentage;

    private ImageView icon;
    private TextView tvCategoryName, tvQuantity, tvAmount, tvPercentage;

    public CategoryTransactionAdapter(Context context, List<Transaction> transactions, int walletId, int[] icons, boolean displayPercentage) {
        this.context = context;
        this.transactions = transactions;
        this.walletId = walletId;
        this.icons = icons;
        this.transactionsByCategory = Utils.groupTransactionsByCategoryName(transactions);
        this.categoryKeys = new ArrayList<>(transactionsByCategory.keySet());
        this.totalAmount = handleCalculateTotalAmount(this.transactions);
        this.displayPercentage = displayPercentage;
    }

    private double handleCalculateTotalAmount(List<Transaction> transactions) {
        double totalSum = 0;

        for (Transaction transaction : transactions)
            totalSum += transaction.get_amount();

        return totalSum;
    }

    @Override
    public int getCount() {
        return categoryKeys.size();
    }

    @Override
    public Object getItem(int position) {
        String categoryName = categoryKeys.get(position);
        return transactionsByCategory.get(categoryName);
    }

    @Override
    public long getItemId(int position) {
        String categoryName = categoryKeys.get(position);
        return categoryName.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_category_transaction, parent, false);
        }

        setControl(convertView);
        initData(position);
        setEvents(convertView, position);
        return convertView;
    }

    private void setEvents(View convertView, int position) {
        convertView.setOnClickListener(v -> {
            String categoryName = categoryKeys.get(position);
            List<Transaction> transactionList = transactionsByCategory.get(categoryName);

            Intent categoryTransactionDetailActivity = new Intent(context, CategoryTransactionDetailActivity.class);
            categoryTransactionDetailActivity.putExtra("transactions", (Serializable) transactionList);
            categoryTransactionDetailActivity.putExtra("categoryName", categoryName);
            categoryTransactionDetailActivity.putExtra("walletId", walletId);
            if (context instanceof Activity) {
                context.startActivity(categoryTransactionDetailActivity);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void initData(int position) {
        String categoryName = categoryKeys.get(position);
        List<Transaction> transactionList = transactionsByCategory.get(categoryName);
        amount = 0;
        int transactionsQuantity;

        if (categoryName.equals("Transfer")) {// transfer transactions
            icon.setImageResource(R.drawable.ic_transaction);
            Utils.formattingImageBackground(context, icon, "#006067");

            // set transaction quantity
            transactionsQuantity = Objects.requireNonNull(transactionList).size();

            for (Transaction transaction : Objects.requireNonNull(transactionList))
                if (transaction.get_from_wallet_id() == walletId)
                    amount -= transaction.get_amount();
                else
                    amount += transaction.get_amount();

        } else if (categoryName.equals("Others")) { // other transactions
            // having two type
            // -- others expense
            // -- others income

            transactionsQuantity = 0;

            for (Transaction transaction : Objects.requireNonNull(transactionList))
                // other transactions of income
                if (transaction.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID) {
                    amount += transaction.get_amount();
                    transactionsQuantity += 1;
                } else { // other transactions of expense
                    // include: fee transactions, normal expense transactions
                    if (!transaction.isFeeTransaction()
                            ||
                            (transaction.isFeeTransaction()
                                    && transaction.getParent().get_from_wallet_id() == walletId)
                    ) {
                        transactionsQuantity += 1;
                        amount -= transaction.get_amount();
                    }
                }


            icon.setImageResource(R.drawable.ic_others);
            if (amount < 0) Utils.formattingImageBackground(context, icon, "#E3432F");
            else Utils.formattingImageBackground(context, icon, "#2167F3");
        } else { // Expense and Income transactions
            Transaction transaction = Objects.requireNonNull(transactionList).get(0);
            icon.setImageResource(icons[transaction.getCategory().get_icon()]);
            Utils.formattingImageBackground(context, icon, transaction.getCategory
                    ().get_color());

            transactionsQuantity = Objects.requireNonNull(transactionList).size();

            // if it is income transaction: amount += transactions amount
            if (transactionList.get(0).get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                for (Transaction t : transactionList)
                    amount += t.get_amount();
            else // else: amount -= transactions amount
                for (Transaction t : transactionList)
                    amount -= t.get_amount();

        }

        // set transaction quantity
        tvQuantity.setText(transactionsQuantity < 2
                ? transactionsQuantity + " transaction"
                : transactionsQuantity + " transactions"
        );

        // set icon to white
        icon.setColorFilter(ContextCompat.getColor(context, R.color.white));

        if (amount < 0)
            tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_6));
            // amount equals 0
        else if (amount == 0) tvAmount.setTextColor(ContextCompat.getColor(context, R.color.black));
        else
            tvAmount.setTextColor(ContextCompat.getColor(context, R.color.primary));


        tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));

        tvCategoryName.setText(categoryName);

        if (displayPercentage) {
            double percentage = Math.abs(amount) / totalAmount * 100;
            @SuppressLint("DefaultLocale")
            String formattedPercentage = String.format("%.1f%%", percentage);
            tvPercentage.setText(formattedPercentage);
            tvPercentage.setVisibility(View.VISIBLE);
        }
    }

    private void setControl(View convertView) {
        icon = convertView.findViewById(R.id.icon);
        tvCategoryName = convertView.findViewById(R.id.tv_category_name);
        tvQuantity = convertView.findViewById(R.id.tv_quantity);
        tvAmount = convertView.findViewById(R.id.tv_amount);
        tvPercentage = convertView.findViewById(R.id.tv_percentage);
    }
}
