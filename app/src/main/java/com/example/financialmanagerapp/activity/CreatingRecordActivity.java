package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.fragment.transaction.SharedViewModel;
import com.example.financialmanagerapp.adapter.TransactionViewPagerAdapter;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatingRecordActivity extends BaseActivity {
    private ImageButton btnBack;
    private TextView tvTitle, btnSave;
    private TabLayout tabLayout;
    protected ViewPager2 viewPager;
    protected TransactionViewPagerAdapter transactionViewPagerAdapter;
    private SharedViewModel sharedViewModel;
    protected Transaction transaction = new Transaction();
    protected Calendar calendar = Calendar.getInstance();
    protected FinancialManagerAPI apiService;
    protected int transactionTypeId = Utils.TRANSFER_TRANSACTION_ID;
    protected int tabId = Utils.INCOME_TAB_ID;
    protected int year = 2024;
    protected int month = 7;
    protected int day = 18;
    protected int hour = 13;
    protected int minute = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_record);
        setControl();
        initData();
        setEvents();
    }

    @SuppressLint("SetTextI18n")
    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        tvTitle = findViewById(R.id.tv_title);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        // Initialize the ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // set adapter tab
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(this);
        viewPager.setAdapter(transactionViewPagerAdapter);

        // set tabs name
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // inflate custom view for tab
            @SuppressLint("InflateParams")
            View customTab = LayoutInflater.from(this).inflate(R.layout.transaction_tab_view, null);
            TextView tabText = customTab.findViewById(R.id.transaction_tab_text);
            tabText.setTypeface(tabText.getTypeface(), Typeface.NORMAL);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.black));
            switch (position) {
                case 0:
                    tabText.setText("Income");
                    tab.setId(Utils.INCOME_TAB_ID);
                    break;
                case 1:
                    tabText.setText("Expense");
                    tab.setId(Utils.EXPENSE_TAB_ID);
                    break;
                case 2:
                    tabText.setText("Transfer");
                    tab.setId(Utils.TRANSFER_TAB_ID);
                    break;
            }
            tab.setCustomView(customTab);
        }).attach();


        // Set the initial selected tab as active
        setActiveTab(Objects.requireNonNull(tabLayout.getTabAt(0)));
    }

    private void initData() {

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the current time
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        sharedViewModel.setSelectedDate(year, month, day);
        sharedViewModel.setSelectedTime(hour, minute);
        sharedViewModel.setDate(year, month, day);
        sharedViewModel.setTime(hour, minute);

        // set save button disable
        btnSave.setEnabled(false);
        btnSave.setTextColor(ContextCompat.getColor(this, R.color.gray));

        // get categories
        if (Utils.categories.size() == 0) {
            Call<ResponseObject<List<Category>>> call = apiService.getCategories();

            call.enqueue(new Callback<ResponseObject<List<Category>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Response<ResponseObject<List<Category>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            Utils.categories = response.body().getResult();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        }
    }

    private void setEvents() {
        // Handle button back clicked
        btnBack.setOnClickListener(v -> {
            // Call super.onBackPressed() to handle default back press behavior
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        // set active or inactive tab when click tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sharedViewModel.setTabId(tab.getId());
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

        // handle button save click
        btnSave.setOnClickListener(v -> handleSaveRecord());

        // Observe live data
        sharedViewModel.getYear().observe(this, year -> calendar.set(Calendar.YEAR, year));
        sharedViewModel.getMonth().observe(this, month -> calendar.set(Calendar.MONTH, month));
        sharedViewModel.getDay().observe(this, day -> calendar.set(Calendar.DAY_OF_MONTH, day));
        sharedViewModel.getHour().observe(this, hour -> calendar.set(Calendar.HOUR_OF_DAY, hour));
        sharedViewModel.getMinute().observe(this, minute -> calendar.set(Calendar.MINUTE, minute));
        sharedViewModel.getDescription().observe(this, description -> transaction.set_description(description));
        sharedViewModel.getMemo().observe(this, memo -> transaction.set_memo(memo));
        sharedViewModel.getFee().observe(this, fee -> transaction.set_fee(fee));
        sharedViewModel.getCategory().observe(this, category -> {
            transaction.set_category_id(category.getId());
            validateInputs();
        });
        sharedViewModel.getExpenseCategory().observe(this, category -> {
            transaction.set_category_id(category.getId());
            validateInputs();
        });
        sharedViewModel.getWallet().observe(this, wallet -> {
            transaction.set_to_wallet_id(wallet.getId());
            transaction.set_wallet_id(wallet.getId());
            validateInputs();
        });
        sharedViewModel.getFromWallet().observe(this, fromWallet -> {
            transaction.set_from_wallet_id(fromWallet.getId());
            validateInputs();
        });
        sharedViewModel.getAmount().observe(this, amount -> {
            transaction.set_amount(amount);
            validateInputs();
        });
        sharedViewModel.getTabId().observe(this, _tabId -> {
            tabId = _tabId;
            validateInputs();
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call super.onBackPressed() to handle default back press behavior
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
    }

    private void handleSaveRecord() {
        // set transaction type id
        transaction.set_transaction_type_id(transactionTypeId);

        // set date
        long millis = calendar.getTimeInMillis();
        Timestamp timestamp = new Timestamp(millis);
        transaction.set_date(timestamp);

        Log.d("myLog", transaction.toString());
    }

    private void validateInputs() {

        switch (tabId) {
            case Utils.INCOME_TAB_ID:
                setEnableSaveButton(
                        transaction != null
                                && transaction.get_amount() != null
                                && transaction.get_amount() != 0.0
                                && transactionTypeId == Utils.INCOME_TRANSACTION_ID
                );
                break;
            case Utils.EXPENSE_TAB_ID:
                setEnableSaveButton(
                        transaction != null
                                && transaction.get_amount() != null
                                && transaction.get_amount() != 0.0
                                && transactionTypeId == Utils.EXPENSE_TRANSACTION_ID
                );
                break;
            case Utils.TRANSFER_TAB_ID:
                setEnableSaveButton(
                        transaction != null
                                && transaction.get_amount() != null
                                && transaction.get_amount() != 0.0
                                && transaction.get_from_wallet_id() != null
                                && transaction.get_from_wallet_id() != transaction.get_to_wallet_id()
                );
                break;
            default:
                break;
        }
    }

    private void setEnableSaveButton(boolean enable) {
        if (enable) {
            btnSave.setEnabled(true);
            btnSave.setTextColor(ContextCompat.getColor(this, R.color.black));
        } else {
            btnSave.setEnabled(false);
            btnSave.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

    private void setInactiveTab(TabLayout.Tab tab) {

        View customView = tab.getCustomView();
        if (customView != null) {
            // reset tabs style
            TextView tabText = customView.findViewById(R.id.transaction_tab_text);
            customView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            tabText.setTypeface(tabText.getTypeface(), Typeface.NORMAL);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setActiveTab(TabLayout.Tab tab) {
        View customView = tab.getCustomView();
        if (customView != null) {
            TextView tabText = customView.findViewById(R.id.transaction_tab_text);

            String transactionType = tabText.getText().toString();

            tabText.setTypeface(tabText.getTypeface(), Typeface.BOLD);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.white));
            switch (transactionType) {
                case "Income":
                    transactionTypeId = Utils.INCOME_TRANSACTION_ID;
                    tvTitle.setText("Income");
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                    break;
                case "Expense":
                    transactionTypeId = Utils.EXPENSE_TRANSACTION_ID;
                    tvTitle.setText("Expense");
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_6));
                    break;
                case "Transfer":
                    tvTitle.setText("Transfer");
                    transactionTypeId = Utils.TRANSFER_TRANSACTION_ID;
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_2));
                    break;
            }
        }
    }
}