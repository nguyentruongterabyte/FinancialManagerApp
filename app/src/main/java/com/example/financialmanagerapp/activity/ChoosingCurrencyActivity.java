package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChoosingCurrencyActivity extends BaseActivity {

    private TextView tvSelectedCurrency;
    private FinancialManagerAPI apiService;
    protected ArrayAdapter<String> adapter;
    protected AppCompatButton btnNext;
    protected List<Currency> currencyList;
    protected List<String> currencyNames;
    protected User user;
    protected Currency selectedCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_currency);
        setControl();
        initData();
        setEvents();
        validateInputs();
    }

    private void setEvents() {

        // open dialog select currency
        tvSelectedCurrency.setOnClickListener(v ->
            showCurrencyDialog()
        );

        // enable/disable next button when text view change text
        tvSelectedCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        });

        // handle next button clicked
        btnNext.setOnClickListener(v -> {
            user = new User.Builder()
                    .name(user.get_name())
                    .email(user.get_email())
                    .password(user.get_password())
                    .passwordConfirmation(user.get_password_confirmation())
                    .currency(selectedCurrency)
                    .build();

            Intent enteringInitialAmountActivity = new Intent(ChoosingCurrencyActivity.this, EnteringInitialAmountActivity.class);
            enteringInitialAmountActivity.putExtra("user", user);
            startActivity(enteringInitialAmountActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

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

    private boolean validate(String currencyName) {
        return !currencyName.trim().isEmpty() && selectedCurrency != null;
    }
    private void validateInputs() {
        boolean validated = validate(
                Objects.requireNonNull(tvSelectedCurrency.getText()).toString().trim()
        );

        if (validated) {
            btnNext.setEnabled(true);
            btnNext.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(ContextCompat.getDrawable(
                    this, R.drawable.disabled_primary_button
            ));
        }
    }

    @SuppressLint("SetTextI18n")
    private void showCurrencyDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_select_currency);
        ListView listView = dialog.findViewById(R.id.listView);
        Toolbar toolbar = dialog.findViewById(R.id.toolbar);
        TextView tvSearch = dialog.findViewById(R.id.tv_search);
        EditText edtSearch = dialog.findViewById(R.id.edt_search);
        ImageView searchIcon = dialog.findViewById(R.id.search_icon);
        AtomicBoolean isSearchFieldEnabled = new AtomicBoolean(false);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // update TextView with selected Currency
            selectedCurrency = currencyList.get(position);
            String currencyName =
                    selectedCurrency.get_ISO_code()
                            + " - " + selectedCurrency.get_currency()
                            + "(" + selectedCurrency.get_symbol() + ")";
            tvSelectedCurrency.setText(currencyName);

            // Save currency name and currency id
            SharedPreferencesUtils.saveInitialCurrencyName(this, currencyName);
            SharedPreferencesUtils.saveInitialCurrencyId(this, selectedCurrency.getId());

            // Reset currency list with Utils.currencies
            clearCurrencyList();
            for (Currency currency : Utils.currencies) {
                currencyList.add(currency);
                currencyNames.add(
                        currency.get_ISO_code()
                                + " - " + currency.get_currency()
                                + "(" + currency.get_symbol() + ")");
            }
            dialog.dismiss();
        });

        searchIcon.setOnClickListener(v -> {
            // if search input is disabled, turn on it
            // else search currency
            if (!isSearchFieldEnabled.get()) {
                isSearchFieldEnabled.set(true);
                tvSearch.setVisibility(View.GONE);
                edtSearch.setVisibility(View.VISIBLE);
                edtSearch.requestFocus();
            } else {
                String searchKey = edtSearch.getText().toString().trim();
                if (!searchKey.isEmpty()) {
                    searchCurrency(searchKey, listView);
                } else {
                    initializeCurrencies();
                }
            }
        });

        // Set Toolbar as ActionBar and add return button
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {

            // if search input is enabled, turn off it
            // else close the dialog
            if (isSearchFieldEnabled.get()) {
                isSearchFieldEnabled.set(false);
                tvSearch.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.GONE);
            } else {
                clearCurrencyList();
                for (Currency currency : Utils.currencies) {
                    currencyList.add(currency);
                    currencyNames.add(
                            currency.get_ISO_code()
                                    + " - " + currency.get_currency()
                                    + "(" + currency.get_symbol() + ")");
                }
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(dialog1 -> {
            clearCurrencyList();
            for (Currency currency : Utils.currencies) {
                currencyList.add(currency);
                currencyNames.add(
                        currency.get_ISO_code() +
                        " - " + currency.get_currency()
                                + "(" + currency.get_symbol() + ")");
            }
        });
        dialog.show();

    }

    // clear currency list function
    private void clearCurrencyList() {
        currencyList.clear();
        currencyNames.clear();
    }

    private void setControl() {
        btnNext = findViewById(R.id.btn_next);
        tvSelectedCurrency = findViewById(R.id.tv_selected_currency);
    }

    private void initData() {

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);


        // If currencies have not been loaded, load them
        if (Utils.currencies.size() == 0) {
            Call<ResponseObject<List<Currency>>> call = apiService.getCurrencies();
            call.enqueue(new Callback<ResponseObject<List<Currency>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Response<ResponseObject<List<Currency>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            Utils.currencies = response.body().getResult();
                            initializeCurrencies();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            initializeCurrencies();
        }



        // get extra `user`
        Intent enteringEmailPasswordActivity = getIntent();

        user = (User) enteringEmailPasswordActivity.getSerializableExtra("user");

        // setText for text view tvSelectedCurrency from shared preferences utils
        tvSelectedCurrency.setText(SharedPreferencesUtils.getInitialCurrencyName(this));

        // get currency from saved currencyId from shared preferences utils
        int currencyId = SharedPreferencesUtils.getInitialCurrencyId(this);

        if (currencyId != -1) {
            getInitialCurrency(currencyId);
        }
    }

    private void initializeCurrencies() {
        // Initialize currency list and currency list string
        currencyList = new ArrayList<>();
        currencyNames = new ArrayList<>();
        for (Currency currency : Utils.currencies) {
            currencyList.add(currency);
            currencyNames.add(currency.get_ISO_code()
                    + " - " + currency.get_currency()
                    + "(" + currency.get_symbol() + ")");
        }
        adapter = new ArrayAdapter<>(
                ChoosingCurrencyActivity.this,
                android.R.layout.simple_list_item_1,
                currencyNames);
    }

    private void getInitialCurrency(int currencyId) {
        Call<ResponseObject<Currency>> call = apiService.getCurrency(currencyId);
        call.enqueue(new Callback<ResponseObject<Currency>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseObject<Currency>> call,
                    @NonNull Response<ResponseObject<Currency>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        selectedCurrency = response.body().getResult();
                        validateInputs();
                    }
                }
                else {
                    Toast.makeText(
                            ChoosingCurrencyActivity.this,
                            "Cannot initialize currency from currency id (" + currencyId + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Currency>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void searchCurrency(String keyword, ListView listView) {
        Call<ResponseObject<List<Currency>>> call = apiService.searchCurrency(keyword);
        call.enqueue(new Callback<ResponseObject<List<Currency>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Response<ResponseObject<List<Currency>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        List<Currency> currencies = response.body().getResult();
                        clearCurrencyList();
                        for (Currency currency : currencies) {
                            currencyList.add(currency);
                            currencyNames.add(currency.get_ISO_code() + " - " + currency.get_currency()  + "(" + currency.get_symbol() + ")");
                        }
                        adapter = new ArrayAdapter<>(ChoosingCurrencyActivity.this, android.R.layout.simple_list_item_1, currencyNames);
                        listView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }
}