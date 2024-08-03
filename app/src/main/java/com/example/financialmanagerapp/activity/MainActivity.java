package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.fragment.account.AccountBottomSheetDialogFragment;
import com.example.financialmanagerapp.adapter.ViewPagerAdapter;
import com.example.financialmanagerapp.broadcast.CreatingRecordReceiver;
import com.example.financialmanagerapp.broadcast.LogoutReceiver;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends BaseActivity {

    private boolean doubleBackToExitPressedOnce = false;
    protected LogoutReceiver logoutReceiver;
    protected CreatingRecordReceiver creatingRecordReceiver;
    protected TextView tvName;
    protected ImageButton btnSearch, btnTransactionList;
    protected LinearLayout btnAccount;
    private TabLayout tabLayout;
    protected ViewPager2 viewPager;
    protected ViewPagerAdapter viewPagerAdapter;
    protected FinancialManagerAPI apiService;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutReceiver = new LogoutReceiver();
        creatingRecordReceiver = new CreatingRecordReceiver();

        IntentFilter filter = new IntentFilter("com.example.financialManagerApp.LOGOUT");
        registerReceiver(logoutReceiver, filter);

        IntentFilter filter1 = new IntentFilter("com.example.financialManagerApp.CREATING_RECORD");
        registerReceiver(creatingRecordReceiver, filter1);
        setControl();
        initData();
        setEvents();

    }


    private void setEvents() {

        // handle button account clicked
        btnAccount.setOnClickListener(v -> {
            AccountBottomSheetDialogFragment accountBottomSheetDialogFragment = new AccountBottomSheetDialogFragment();
            accountBottomSheetDialogFragment.show(getSupportFragmentManager(), accountBottomSheetDialogFragment.getTag());
        });

        // set active or inactive tab when click tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setActiveTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setInactiveTab(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }



    private void initData() {
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // init text view name
        tvName.setText("");
        if (Utils.currentUser != null) {
            tvName.setText(Utils.currentUser.get_name());
        } else {
            Call<ResponseObject<User>> call = apiService.get(SharedPreferencesUtils.getUserId(this));
            call.enqueue(new Callback<ResponseObject<User>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();
                        tvName.setText(Utils.currentUser.get_name());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        }
    }



    @SuppressLint("SetTextI18n")
    private void setControl() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        btnSearch = findViewById(R.id.btn_search);
        btnTransactionList = findViewById(R.id.btn_transaction_list);
        btnAccount = findViewById(R.id.btn_account);

        tvName = findViewById(R.id.tv_name);

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // set tabs name
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // inflate custom view for tab
            @SuppressLint("InflateParams") View customTab = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
            TextView tabText = customTab.findViewById(R.id.tab_text);
            ImageView tabIcon = customTab.findViewById(R.id.tab_icon);

            switch (position) {
                case 0:
                    tabText.setText("Transaction");
                    tabIcon.setImageResource(R.drawable.ic_transaction);
                    tab.setContentDescription("Transaction Tab");
                    break;
                    //                case 1:
                    //                    tabText.setText("Calendar");
                    //                    tabIcon.setImageResource(R.drawable.ic_calendar);
                    //                    tab.setContentDescription("Calendar Tab");
                    //                    break;
                case 1: // 2
                    tabText.setText("Statistic");
                    tabIcon.setImageResource(R.drawable.ic_statistic);
                    tab.setContentDescription("Statistic Tab");
                    break;
                case 2: // 3
                    tabText.setText("Wallet");
                    tabIcon.setImageResource(R.drawable.ic_wallet);
                    tab.setContentDescription("Wallet Tab");
                    break;
            }
            tab.setCustomView(customTab);
        }).attach();

        // Set the initial selected tab as active
        setActiveTab(Objects.requireNonNull(tabLayout.getTabAt(0)));
    }

    private void setActiveTab(TabLayout.Tab tab) {
        View customView = tab.getCustomView();
        if (customView != null) {
            TextView tabText = customView.findViewById(R.id.tab_text);
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);

            tabText.setTypeface(tabText.getTypeface(), Typeface.BOLD);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.black));
            tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void setInactiveTab(TabLayout.Tab tab) {
        View customView = tab.getCustomView();
        if (customView != null) {
            TextView tabText = customView.findViewById(R.id.tab_text);
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);

            tabText.setTypeface(tabText.getTypeface(), Typeface.NORMAL);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.gray));
            tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        }
    }
}