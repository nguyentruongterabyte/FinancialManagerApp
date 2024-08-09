package com.example.financialmanagerapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.TransactionDetailActivity;
import com.example.financialmanagerapp.custom.ExpandedListView;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.TransactionDate;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;

public class TransactionDateAdapter extends BaseAdapter {

    protected Context context;
    protected List<TransactionDate> transactionDates;

    private TextView tvDayOfMonth,
            tvDayOfWeek,
            tvMonthYear,
            tvTotalAmount;

    private ExpandedListView listView;
    protected int walletId;

    public TransactionDateAdapter(Context context, List<TransactionDate> transactionDates, int walletId) {
        this.context = context;
        this.transactionDates = transactionDates;
        this.walletId = walletId;
    }

    @Override
    public int getCount() {
        return transactionDates.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionDates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return transactionDates.get(position).getTransactions().get(0).get_date().getTime();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_date, parent, false);
        }
        setControl(convertView);
        TransactionDate transactionDate = transactionDates.get(position);
        initData(transactionDate);
        setEvents();
        return convertView;
    }


    private double calculateTotalAmount(List<Transaction> transactions) {
        double totalAmount = 0.0;
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            switch (transaction.get_transaction_type_id()) {
                case Utils.EXPENSE_TRANSACTION_ID:
                    totalAmount -= transaction.get_amount();
                    break;
                case Utils.INCOME_TRANSACTION_ID:
                    totalAmount += transaction.get_amount();
                    break;
                case Utils.TRANSFER_TRANSACTION_ID:
                    if (walletId == -1) {
                        totalAmount -= transaction.get_fee();
                    } else {
                        if (transaction.get_from_wallet_id() == walletId) {
                            totalAmount -= transaction.get_amount();
                        } else {
                            totalAmount += transaction.get_amount();
                        }
                    }
                    break;
            }
        }
        return totalAmount;
    }

    private void setEvents() {
        // handle list view item (transaction) clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Transaction clickedTransaction = (Transaction) parent.getItemAtPosition(position);

            // starting transaction details
            Intent transactionDetailActivity = new Intent(context, TransactionDetailActivity.class);
            transactionDetailActivity.putExtra("transaction", clickedTransaction);

            // Cast context to Activity and start the activity
            if (context instanceof Activity) {
                context.startActivity(transactionDetailActivity);
                // Apply animation
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initData(TransactionDate transactionDate) {
        tvDayOfWeek.setText(TimerFormatter.getDayOfWeekText(transactionDate.getDayOfWeek()));
        tvDayOfMonth.setText(String.valueOf(transactionDate.getDayOfMonth()));
        tvMonthYear.setText(String.format("%s %d",
                TimerFormatter.getMonthOfYearText(transactionDate.getMonthOfYear()),
                transactionDate.getYear()));

        List<Transaction> transactions = transactionDate.getTransactions();


        if (walletId != -1)
            for (int i = transactions.size() - 1; i >= 0; i--)
                if (transactions.get(i).isFeeTransaction() && transactions.get(i).getParent().get_from_wallet_id() != walletId)
                    // remove transaction at i
                    // is fee transactions of other wallets
                    transactions.remove(i);


        // calculate total amount
        double totalAmount = calculateTotalAmount(transactions);
        tvTotalAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), totalAmount));

        // sort the list from latest to oldest time
        transactions.sort((t1, t2) -> {
            long time1 = t1.get_date().getTime();
            long time2 = t2.get_date().getTime();

            return Long.compare(time2, time1);
        });

        // set transaction adapter to list view
        TransactionAdapter adapter = new TransactionAdapter(context, transactions, Utils.categoriesIcons, walletId, true);

        listView.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(listView);
    }

    private void setControl(View convertView) {
        tvDayOfMonth = convertView.findViewById(R.id.tv_day_of_month);
        tvDayOfWeek = convertView.findViewById(R.id.tv_day_of_week);
        tvMonthYear = convertView.findViewById(R.id.tv_month_year);
        tvTotalAmount = convertView.findViewById(R.id.tv_total_amount);
        listView = convertView.findViewById(R.id.list_view);
    }

}
