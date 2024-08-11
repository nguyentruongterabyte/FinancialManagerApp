package com.example.financialmanagerapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.DTO.WalletDTO;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.mapper.WalletMapper;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalletAdapter extends BaseAdapter {
    protected Context context;
    protected List<Wallet> wallets;
    protected int[] icons;
    protected boolean chooseMany;
    protected Map<Integer, Boolean> checkBoxStateMap;
    protected List<Integer> checkedIdList;
    protected boolean isEnableDelete;
    protected FinancialManagerAPI apiService;

    public WalletAdapter(
            Context context,
            List<Wallet> wallets,
            int[] icons,
            boolean chooseMany,
            List<Integer> checkedIdList,
            boolean isEnableDelete
    ) {
        this.context = context;
        this.wallets = wallets;
        this.icons = icons;
        this.chooseMany = chooseMany;
        this.checkBoxStateMap = new HashMap<>();
        this.checkedIdList = checkedIdList;
        this.isEnableDelete = isEnableDelete;
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
        ImageView btnDelete = convertView.findViewById(R.id.btn_delete);
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, context);
        apiService = retrofit.create(FinancialManagerAPI.class);


        if (isEnableDelete)
            btnDelete.setVisibility(View.VISIBLE);
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


        // set event button delete clicked
        btnDelete.setOnClickListener(v -> {

            if (wallets.size() == 1)
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Transaction")
                    .setMessage("Do you really want to delete this transaction?")
                    .setPositiveButton("DELETE", (dialog, which) ->
                            handleDeleteWallet(position)
                    )
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return convertView;
    }

    private void handleDeleteWallet(int position) {
        Wallet wallet = wallets.get(position);
        wallet.set_is_deleted(1);
        WalletDTO walletDTO = WalletMapper.toWalletDTO(wallet);

        Call<ResponseObject<Wallet>> call = apiService.updateWallet(
                walletDTO, Utils.currentUser.getId(), wallet.getId());

        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        for (int i = 0; i < wallets.size(); i++) {
                            if (wallets.get(i).getId() == wallet.getId()) {
                                wallets.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < Utils.currentUser.getWallets().size(); i++) {
                            if (Utils.currentUser.getWallets().get(i).getId() == wallet.getId()) {
                                Utils.currentUser.getWallets().remove(i);
                                break;
                            }
                        }
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
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
