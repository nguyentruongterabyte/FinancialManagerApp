package com.example.financialmanagerapp.activity;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;

import com.example.financialmanagerapp.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {


        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call super.onBackPressed() to handle default back press behavior
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void setControl() {
    }

    private void initData() {
    }
}