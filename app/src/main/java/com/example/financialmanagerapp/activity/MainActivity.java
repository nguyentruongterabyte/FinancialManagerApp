package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.broadcast.LogoutReceiver;

public class MainActivity extends BaseActivity {

    protected LogoutReceiver logoutReceiver;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutReceiver = new LogoutReceiver();
        IntentFilter filter = new IntentFilter("com.example.financialmanagerapp.LOGOUT");
        registerReceiver(logoutReceiver, filter);
    }
}