package com.example.financialmanagerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.Calendar;
import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    protected Context context;
    protected List<Transaction> transactions;
    protected int[] icons;
    protected int walletId;
    protected boolean displayTime;

    public TransactionAdapter(Context context, List<Transaction> transactions, int[] icons, int walletId, boolean displayTime) {
        this.context = context;
        this.transactions = transactions;
        this.icons = icons;
        this.walletId = walletId;
        this.displayTime = displayTime;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return transactions.get(position).getId();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_transaction, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.icon);
        TextView tvCategoryName = convertView.findViewById(R.id.tv_category_name);
        TextView tvWalletName = convertView.findViewById(R.id.tv_wallet_name);
        TextView tvAmount = convertView.findViewById(R.id.tv_amount);
        TextView tvTime = convertView.findViewById(R.id.tv_time);
        Transaction transaction = transactions.get(position);

        if (transaction.get_transaction_type_id() == Utils.TRANSFER_TRANSACTION_ID) {
            // set icon view and its background color
            iconView.setImageResource(R.drawable.ic_transaction);
            Utils.formattingImageBackground(context, iconView, "#006067");

            // set text view wallet
            Wallet fromWallet = transaction.getFrom_wallet();
            Wallet toWallet = transaction.getTo_wallet();
            tvWalletName.setText(String.format("%s-->%s", fromWallet.get_name(), toWallet.get_name()));
            tvCategoryName.setText(transaction.get_description() != null ? transaction.get_description() : "Transfer");
        } else {

            // set icon view and its background color
            iconView.setImageResource(icons[transaction.getCategory().get_icon()]);
            Utils.formattingImageBackground(context, iconView, transaction.getCategory().get_color());
            // set text view wallet
            Wallet wallet = transaction.getWallet();
            tvWalletName.setText(wallet.get_name());
            // set text view category
            tvCategoryName.setText(transaction.get_description() != null ? transaction.get_description() : transaction.getCategory().get_name());
        }
        // set icon view
        iconView.setColorFilter(ContextCompat.getColor(context, R.color.white));



        // set text view amount
        double amount;
        switch (transaction.get_transaction_type_id()) {
            case Utils.EXPENSE_TRANSACTION_ID:
                amount = -transaction.get_amount();
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_6));
                break;
            case Utils.INCOME_TRANSACTION_ID:
                amount = transaction.get_amount();
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.primary));
                break;
            case Utils.TRANSFER_TRANSACTION_ID:
                amount = transaction.get_amount();
                if (walletId == -1) {
                    tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else {
                    if (transaction.get_from_wallet_id() == walletId) {
                        tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), -amount));
                        tvAmount.setTextColor(ContextCompat.getColor(context, R.color.color_6));
                    } else {
                        tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
                        tvAmount.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    }
                }
                break;
        }

        // set text view time
        Calendar calendar = TimerFormatter.getCalendar(transaction.get_date());
        if (displayTime) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            tvTime.setText(TimerFormatter.convertTimeString(hour, minute));
        } else {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            tvTime.setText(TimerFormatter.convertDateString(year, month + 1, day));
        }
        return convertView;
    }
}
