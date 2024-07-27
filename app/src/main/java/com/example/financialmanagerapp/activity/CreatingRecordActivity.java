package com.example.financialmanagerapp.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.TransactionViewPagerAdapter;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.Wallet;
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
    protected Calendar calendar;
    protected FinancialManagerAPI apiService;

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
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(this, Utils.CREATING_TRANSACTION);
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
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the current time
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

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
                setActiveTab(tab);
                validateInputs();
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

        // set date
        long millis = calendar.getTimeInMillis();
        Timestamp timestamp = new Timestamp(millis);
        transaction.set_date(timestamp);


        switch (transaction.get_transaction_type_id()) {
            case Utils.EXPENSE_TRANSACTION_ID:
                handleSaveExpenseTransaction();
                break;
            case Utils.INCOME_TRANSACTION_ID:
                handleSaveIncomeTransaction();
                break;
            case Utils.TRANSFER_TRANSACTION_ID:
                handleSaveTransferTransaction();
                break;
        }
    }

    private void handleSaveTransferTransaction() {
        Call<ResponseObject<Transaction>> call = apiService.createTransaction(transaction);
        call.enqueue(new Callback<ResponseObject<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Response<ResponseObject<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {

                        // add transaction to transactions utils
                        transaction = response.body().getResult();
                        Utils.transactions.add(transaction);

                        // add fee transaction to transactions utils
                        // Check if the transaction has a fee greater than 0
                        if (transaction.get_fee() > 0) {
                            // Create the fee transaction
                            Category category = new Category.Builder()
                                    .id(Utils.OTHER_CATEGORY_EXPENSE_TRANSACTION_ID)
                                    .name("Others")
                                    .icon(13)
                                    .color("#603C34")
                                    .transactionTypeId(Utils.EXPENSE_TRANSACTION_ID)
                                    .build();

                            Transaction feeTransaction = new Transaction.Builder()
                                    .id(transaction.getId())
                                    .category(category)
                                    .categoryId(category.getId())
                                    .date(transaction.get_date())
                                    .wallet(transaction.getWallet())
                                    .walletId(transaction.get_wallet_id())
                                    .toWallet(transaction.getTo_wallet())
                                    .toWalletId(transaction.get_to_wallet_id())
                                    .description("Fee")
                                    .amount(transaction.get_fee())
                                    .transactionTypeId(Utils.EXPENSE_TRANSACTION_ID)
                                    .updatedAt(transaction.getUpdated_at())
                                    .isFeeTransaction(true)
                                    .parent(transaction)
                                    .build();

                            // Add the fee transaction to the list
                            Utils.transactions.add(feeTransaction);
                        }

                        // Update to wallet amount, then update from wallet amount
                        Wallet updateToWallet = Wallet.findById(transaction.get_to_wallet_id(), Utils.currentUser.getWallets());
                        double amount = updateToWallet.get_amount();
                        amount += transaction.get_amount();
                        updateToWallet.set_amount(amount);
                        updateWallet(updateToWallet, false);


                        // Update from wallet amount
                        Wallet updateFromWallet = Wallet.findById(transaction.get_from_wallet_id(), Utils.currentUser.getWallets());
                        amount = updateFromWallet.get_amount();
                        amount -= (transaction.get_amount() + transaction.get_fee());
                        updateFromWallet.set_amount(amount);
                        updateWallet(updateFromWallet, true);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }


    private void handleSaveIncomeTransaction() {
        Call<ResponseObject<Transaction>> call = apiService.createTransaction(transaction);
        call.enqueue(new Callback<ResponseObject<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Response<ResponseObject<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {
                        // add transaction to transaction utils
                        transaction = response.body().getResult();
                        Utils.transactions.add(transaction);

                        // Update wallet amount
                        Wallet updateWallet = Wallet.findById(transaction.get_wallet_id(), Utils.currentUser.getWallets());
                        double amount = updateWallet.get_amount();
                        amount = amount + transaction.get_amount();
                        updateWallet.set_amount(amount);
                        updateWallet(updateWallet, true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void handleSaveExpenseTransaction() {
        Call<ResponseObject<Transaction>> call = apiService.createTransaction(transaction);
        call.enqueue(new Callback<ResponseObject<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Response<ResponseObject<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {
                        // add transaction to transaction utils
                        transaction = response.body().getResult();
                        Utils.transactions.add(transaction);

                        // update wallet amount
                        Wallet updateWallet = Wallet.findById(transaction.get_wallet_id(), Utils.currentUser.getWallets());
                        double amount = updateWallet.get_amount();
                        amount = amount - transaction.get_amount();
                        updateWallet.set_amount(amount);
                        updateWallet(updateWallet, true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void updateWallet(@NonNull Wallet wallet, boolean finish) {

        Call<ResponseObject<Wallet>> call = apiService.updateWallet(wallet, Utils.currentUser.getId(), wallet.getId());
        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {

                        // update wallet in wallets utils
                        Wallet updatedWallet = response.body().getResult();
                        boolean isUpdated = Wallet.updateWalletInList(updatedWallet, Utils.currentUser.getWallets());
                        if (!isUpdated) {
                            Toast.makeText(CreatingRecordActivity.this, "An error occur", Toast.LENGTH_SHORT).show();
                        }
                        if (finish) {



                            Intent mainActivity = new Intent(CreatingRecordActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                            finish();
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {

            }
        });
    }

    private void validateInputs() {

        switch (transaction.get_transaction_type_id()) {
            case Utils.INCOME_TRANSACTION_ID:
                setEnableSaveButton(
                        transaction != null
                                && transaction.get_amount() != null
                                && transaction.get_amount() != 0.0
                                && sharedViewModel.getCategory().getValue() != null
                );
                break;
            case Utils.EXPENSE_TRANSACTION_ID:
                setEnableSaveButton(
                        transaction != null
                                && transaction.get_amount() != null
                                && transaction.get_amount() != 0.0
                                && sharedViewModel.getExpenseCategory().getValue() != null
                );
                break;
            case Utils.TRANSFER_TRANSACTION_ID:
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
                    tvTitle.setText("Income");
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                    transaction.set_transaction_type_id(Utils.INCOME_TRANSACTION_ID);
                    break;
                case "Expense":
                    tvTitle.setText("Expense");
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_6));
                    transaction.set_transaction_type_id(Utils.EXPENSE_TRANSACTION_ID);
                    break;
                case "Transfer":
                    tvTitle.setText("Transfer");
                    customView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_2));
                    transaction.set_transaction_type_id(Utils.TRANSFER_TRANSACTION_ID);
                    break;
            }
        }
    }
}