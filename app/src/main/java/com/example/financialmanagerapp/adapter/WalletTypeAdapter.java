package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.WalletType;

import java.util.List;

public class WalletTypeAdapter extends BaseAdapter {

    protected Context context;
    protected List<WalletType> walletTypes;

    public WalletTypeAdapter(Context context, List<WalletType> walletTypes) {
        this.context = context;
        this.walletTypes = walletTypes;
    }

    @Override
    public int getCount() {
        return walletTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return walletTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_wallet_type, parent, false);
        }

        WalletType walletType = walletTypes.get(position);

        TextView tvName = convertView.findViewById(R.id.tv_name);

        tvName.setText(walletType.get_name());
        return convertView;
    }
}
