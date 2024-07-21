package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;

public class WalletAdapter extends BaseAdapter {
    protected Context context;
    protected List<Wallet> wallets;
    protected int[] icons;

    public WalletAdapter(Context context, List<Wallet> wallets, int[] icons) {
        this.context = context;
        this.wallets = wallets;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return wallets.size();
    }

    @Override
    public Object getItem(int position) {
        return wallets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return wallets.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_wallet, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.wallet_icon);
        TextView nameView = convertView.findViewById(R.id.wallet_name);
        TextView balanceView = convertView.findViewById(R.id.wallet_balance);

        Wallet wallet = wallets.get(position);
        iconView.setImageResource(icons[wallet.get_icon()]);
        nameView.setText(wallet.get_name());
        balanceView.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), wallet.get_amount()));

        int color = Color.parseColor(wallet.get_color());
        iconView.setBackgroundColor(color);
        return convertView;
    }
}
