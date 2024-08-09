package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletAdapter extends BaseAdapter {
    protected Context context;
    protected List<Wallet> wallets;
    protected int[] icons;
    protected boolean chooseMany;
    protected Map<Integer, Boolean> checkBoxStateMap;
    protected List<Integer> checkedIdList;

    public WalletAdapter(
            Context context,
            List<Wallet> wallets,
            int[] icons,
            boolean chooseMany,
            List<Integer> checkedIdList
    ) {
        this.context = context;
        this.wallets = wallets;
        this.icons = icons;
        this.chooseMany = chooseMany;
        this.checkBoxStateMap = new HashMap<>();
        this.checkedIdList = checkedIdList;

        // Initialize checkBoxStateMap based on checkedIdList
        if (checkedIdList != null) {
            for (Integer id : checkedIdList) {
                checkBoxStateMap.put(id, true);
            }
        }
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
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);

        // set enable checkbox on each item list
        if (chooseMany)
            checkBox.setVisibility(View.VISIBLE);
        else
            checkBox.setVisibility(View.GONE);

        Wallet wallet = wallets.get(position);
        iconView.setImageResource(icons[wallet.get_icon()]);
        nameView.setText(wallet.get_name());
        balanceView.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), wallet.get_amount()));

        Utils.formattingImageBackground(context, iconView, wallet.get_color());

        // Set checked checkbox state from checkBoxStateMap
        checkBox.setOnCheckedChangeListener(null); // Clear listener before setting checked state
        checkBox.setChecked(Boolean.TRUE.equals(
                checkBoxStateMap.getOrDefault(wallet.getId(), false)));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                checkBoxStateMap.put(wallet.getId(), isChecked));
        return convertView;
    }

    public Map<Integer, Boolean> getCheckBoxStateMap() {
        return checkBoxStateMap;
    }

    public void updateWallets(List<Wallet> newWallets, List<Integer> newCheckedIdList) {
        this.wallets = newWallets;
        this.checkedIdList = newCheckedIdList;

        // Update checkBoxStateMap
        this.checkBoxStateMap.clear();
        if (newCheckedIdList != null) {
            for (Integer id : newCheckedIdList) {
                checkBoxStateMap.put(id, true);
            }
        }

        notifyDataSetChanged();
    }
}
