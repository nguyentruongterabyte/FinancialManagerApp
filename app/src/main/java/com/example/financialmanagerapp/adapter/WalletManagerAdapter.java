package com.example.financialmanagerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.CreatingWalletActivity;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;

public class WalletManagerAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADD = 1;
    protected Context context;
    protected List<Wallet> wallets;
    protected int[] icons;

    public WalletManagerAdapter(Context context, List<Wallet> wallets, int[] icons) {
        this.context = context;
        this.wallets = wallets;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return wallets.size() + 1; // +1 for the "add" item
    }

    @Override
    public Object getItem(int position) {
        if (position == wallets.size()) {
            return null; // Special case for the "add" item
        }

        return wallets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == wallets.size()) {
            return TYPE_ADD;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Two types: regular item and "add" item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         int viewType = getItemViewType(position);

         if (viewType == TYPE_ITEM) {
             if (convertView == null) {
                 convertView = LayoutInflater.from(context).inflate(R.layout.list_item_wallet, parent, false);
             }

             ImageView iconView = convertView.findViewById(R.id.wallet_icon);
             TextView nameView = convertView.findViewById(R.id.wallet_name);
             TextView balanceView = convertView.findViewById(R.id.wallet_balance);
             CheckBox checkBox = convertView.findViewById(R.id.checkbox);
             checkBox.setVisibility(View.GONE);


             Wallet wallet = wallets.get(position);
             iconView.setImageResource(icons[wallet.get_icon()]);
             nameView.setText(wallet.get_name());
             balanceView.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), wallet.get_amount()));

             Utils.formattingImageBackground(context, iconView, wallet.get_color());
             return convertView;
         } else {
             if (convertView == null) {
                 convertView = LayoutInflater.from(context).inflate(R.layout.item_add, parent, false);

                 // on item add clicked
                 convertView.setOnClickListener(v -> {
                     Intent creatingWalletActivity = new Intent(context, CreatingWalletActivity.class);
                     if (context instanceof Activity) {
                         context.startActivity(creatingWalletActivity);
                         ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                     }
                 });
             }
         }
         return convertView;
    }
}
