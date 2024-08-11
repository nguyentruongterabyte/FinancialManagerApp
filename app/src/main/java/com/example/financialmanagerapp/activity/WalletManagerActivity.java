package com.example.financialmanagerapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.WalletAdapter;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WalletManagerActivity extends BaseActivity {

    private ImageButton btnBack, btnAddWallet;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_manager);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle list view item wallet clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Wallet wallet = (Wallet) parent.getItemAtPosition(position);
            Intent editingWalletActivity = new Intent(WalletManagerActivity.this, CreatingWalletActivity.class);
            editingWalletActivity.putExtra("wallet", wallet);
            startActivity(editingWalletActivity);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // handle button add wallet
        btnAddWallet.setOnClickListener(v -> {
            Intent creatingWalletActivity = new Intent(WalletManagerActivity.this, CreatingWalletActivity.class);
            startActivity(creatingWalletActivity);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // handle button back clicked
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


    }

    private void initData() {
        List<Wallet> wallets = new ArrayList<>();
        for (Wallet wallet : Utils.currentUser.getWallets()) {
            if (wallet.get_is_deleted() == 0) {
                wallets.add(wallet);
            }
        }

        WalletAdapter adapter = new WalletAdapter(this, wallets, Utils.walletIcons, false, null, true);
        listView.setAdapter(adapter);

    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnAddWallet = findViewById(R.id.btn_add_wallet);
        listView = findViewById(R.id.list_view);
    }
}