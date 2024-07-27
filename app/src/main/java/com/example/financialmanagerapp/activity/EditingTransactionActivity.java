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
import com.example.financialmanagerapp.utils.TimerFormatter;
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

public class EditingTransactionActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextView tvTitle, btnSave;
    private TabLayout tabLayout;
    protected ViewPager2 viewPager;
    protected TransactionViewPagerAdapter transactionViewPagerAdapter;
    private SharedViewModel sharedViewModel;
    protected Transaction transaction = new Transaction();
    protected Transaction oldTransaction = new Transaction();
    protected Calendar calendar = Calendar.getInstance();
    protected FinancialManagerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_transaction);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {

        // Handle button save clicked
        btnSave.setOnClickListener(v ->
                handleUpdateTransaction()
        );

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

    private void handleUpdateTransaction() {
        // set date
        long millis = calendar.getTimeInMillis();
        Timestamp timestamp = new Timestamp(millis);
        transaction.set_date(timestamp);

        if (transaction.equals(oldTransaction)) {
            finish();
            return;
        }
        if (transaction.isFeeTransaction()) {
            handleFeeTransactionUpdateAndProceed();
        } else {
            handleTransactionUpdateAndProceed();
        }
    }

    private void handleTransactionUpdateAndProceed() {
        Call<ResponseObject<Transaction>> call = apiService.updateTransaction(
                transaction,
                Utils.currentUser.getId(),
                transaction.getId());

        call.enqueue(new Callback<ResponseObject<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Response<ResponseObject<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        transaction = response.body().getResult();
                        Transaction.updateTransactionInList(transaction, Utils.transactions);
                        // update wallet amount
                        double amount = 0;
                        double fromWalletAmount = 0;
                        List<Wallet> wallets = Utils.currentUser.getWallets();
                        Wallet wallet = Wallet.findById(transaction.get_wallet_id(), wallets);
                        Wallet fromWallet = null;
                        switch (oldTransaction.get_transaction_type_id()) {
                            case Utils.INCOME_TRANSACTION_ID:
                                // Income transaction amount
                                amount = wallet.get_amount() - oldTransaction.get_amount();
                                break;
                            case Utils.EXPENSE_TRANSACTION_ID:
                                // Expense transaction amount
                                amount = wallet.get_amount() + oldTransaction.get_amount();
                                break;
                            case Utils.TRANSFER_TRANSACTION_ID:
                                fromWallet = Wallet.findById(oldTransaction.getFrom_wallet().getId(), wallets);
                                // Transfer transaction amount
                                fromWalletAmount = fromWallet.get_amount() + oldTransaction.get_amount() + oldTransaction.get_fee();
                                amount = wallet.get_amount() - oldTransaction.get_amount();
                                break;
                        }

                        switch (transaction.get_transaction_type_id()) {
                            case Utils.INCOME_TRANSACTION_ID:
                                amount += transaction.get_amount();
                                break;
                            case Utils.EXPENSE_TRANSACTION_ID:
                                amount -= transaction.get_amount();
                                break;
                            case Utils.TRANSFER_TRANSACTION_ID:
                                fromWalletAmount -= (transaction.get_amount() + transaction.get_fee());
                                amount += transaction.get_amount();
                                break;
                        }

                        handleUpdateWalletAmount(wallet, fromWallet, amount, fromWalletAmount);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Transaction>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void handleUpdateWalletAmount(Wallet wallet, Wallet fromWallet, double amount, double fromWalletAmount) {
        if (fromWallet != null) {
            fromWallet.set_amount(fromWalletAmount);
            Call<ResponseObject<Wallet>> call = apiService.updateWallet(fromWallet, Utils.currentUser.getId(), fromWallet.getId());
            call.enqueue(new Callback<ResponseObject<Wallet>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            Log.d("myLog", "Update from wallet successfully!");
                            handleUpdateWalletAmount(wallet, amount);
                        } else {
                            Toast.makeText(EditingTransactionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditingTransactionActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                    Log.d("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            handleUpdateWalletAmount(wallet, amount);
        }
    }

    private void handleUpdateWalletAmount(Wallet wallet, double amount) {
        wallet.set_amount(amount);
        Call<ResponseObject<Wallet>> call = apiService.updateWallet(wallet, Utils.currentUser.getId(), wallet.getId());
        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {

                        Wallet updatedWallet = response.body().getResult();
                        boolean isUpdated = Wallet.updateWalletInList(updatedWallet, Utils.currentUser.getWallets());
                        if (isUpdated) {
                            Intent transactionDetailActivity = new Intent(EditingTransactionActivity.this, TransactionDetailActivity.class);
                            transactionDetailActivity.putExtra("transaction", transaction);
                            transactionDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(transactionDetailActivity);
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                            finish();
                        } else {
                            Toast.makeText(EditingTransactionActivity.this, "An error occur", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(EditingTransactionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditingTransactionActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                Log.d("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void handleFeeTransactionUpdateAndProceed() {

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

    private void initData() {

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction != null) {
            oldTransaction = new Transaction(transaction);
            Log.d("myLog", transaction.toString());
            TabLayout.Tab tab = tabLayout.getTabAt(0);

            switch (transaction.get_transaction_type_id()) {
                case Utils.INCOME_TRANSACTION_ID:
                    setActiveTab(Objects.requireNonNull(tabLayout.getTabAt(0)));
                    tab = tabLayout.getTabAt(0);
                    break;
                case Utils.EXPENSE_TRANSACTION_ID:
                    setActiveTab(Objects.requireNonNull(tabLayout.getTabAt(1)));
                    tab = tabLayout.getTabAt(1);
                    break;
                case Utils.TRANSFER_TRANSACTION_ID:
                    setActiveTab(Objects.requireNonNull(tabLayout.getTabAt(2)));
                    tab = tabLayout.getTabAt(2);
                    break;
            }

            tabLayout.selectTab(tab);
            Calendar calendar = TimerFormatter.getCalendar(transaction.get_date());

            sharedViewModel.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            sharedViewModel.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

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
            if (transaction.get_description() != null)
                sharedViewModel.setDescription(transaction.get_description());
            if (transaction.get_memo() != null) sharedViewModel.setMemo(transaction.get_memo());
            if (transaction.get_fee() > 0) sharedViewModel.setFee(transaction.get_fee());
            if (transaction.getCategory() != null)
                if (transaction.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                    sharedViewModel.setCategory(transaction.getCategory());
                else
                    sharedViewModel.setExpenseCategory(transaction.getCategory());
            sharedViewModel.setWallet(transaction.getWallet());
            if (transaction.get_transaction_type_id() == Utils.TRANSFER_TRANSACTION_ID)
                sharedViewModel.setFromWallet(transaction.getFrom_wallet());
            sharedViewModel.setAmount(transaction.get_amount());

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
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(this, Utils.UPDATING_TRANSACTION);
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

    }
}