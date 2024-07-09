package com.example.financialmanagerapp.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.request.RegisterRequest;
import com.example.financialmanagerapp.model.response.AuthResponse;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnteringInitialAmountActivity extends BaseActivity {

    protected static final int ALLOWED_NUMBER_OF_DECIMALS = 12;
    protected FinancialManagerAPI apiService;
    protected AppCompatButton
            btnNumberOne,
            btnNumberTwo,
            btnNumberThree,
            btnNumberFour,
            btnNumberFive,
            btnNumberSix,
            btnNumberSeven,
            btnNumberEight,
            btnNumberNine,
            btnDot,
            btnNumberZero
    ;

    private double initialAmount = 0.00;

    protected User user;
    protected ImageButton btnFinish, btnDelete;
    protected TextView tvCurrencySymbol, tvInitialAmount, btnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_initial_amount);
        setControl();
        initData();
        setEvents();
    }

    private String getTextFromTextView() {
        return tvInitialAmount.getText().toString();
    }
    private void setEvents() {
        // handle delete button clicked
        btnDelete.setOnClickListener(v -> {
            String initialAmountStr = getTextFromTextView();
            if (!initialAmountStr.isEmpty()) {

                // remove last character
                initialAmountStr = initialAmountStr.substring(0, initialAmountStr.length() - 1);
                if (initialAmountStr.isEmpty()) {
                    initialAmountStr = "0";
                }

                // if last of string is commas, remove it
                if (initialAmountStr.endsWith(",")) {
                    initialAmountStr = initialAmountStr.substring(0, initialAmountStr.length() - 1);
                }
                tvInitialAmount.setText(formatAmount(initialAmountStr));
            }

        });

        // handle finish button clicked
        btnFinish.setOnClickListener(v -> {
            if (user != null) {
                createAccountAndProceed();
            }
        });


        btnDot.setOnClickListener(v -> onDotButtonClick());
        btnNumberZero.setOnClickListener(v -> onNumberButtonClick(btnNumberZero));
        btnNumberOne.setOnClickListener(v -> onNumberButtonClick(btnNumberOne));
        btnNumberTwo.setOnClickListener(v -> onNumberButtonClick(btnNumberTwo));
        btnNumberThree.setOnClickListener(v -> onNumberButtonClick(btnNumberThree));
        btnNumberFour.setOnClickListener(v -> onNumberButtonClick(btnNumberFour));
        btnNumberFive.setOnClickListener(v -> onNumberButtonClick(btnNumberFive));
        btnNumberSix.setOnClickListener(v -> onNumberButtonClick(btnNumberSix));
        btnNumberSeven.setOnClickListener(v -> onNumberButtonClick(btnNumberSeven));
        btnNumberEight.setOnClickListener(v -> onNumberButtonClick(btnNumberEight));
        btnNumberNine.setOnClickListener(v -> onNumberButtonClick(btnNumberNine));

        // handle skip button clicked
        btnSkip.setOnClickListener(v -> {
            if (user != null) {
                createAccountAndProceed();
            }
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

    private void createAccountAndProceed() {
        RegisterRequest request = new RegisterRequest.Builder()
                .name(user.get_name())
                .email(user.get_email())
                .password(user.get_password())
                .passwordConfirmation(user.get_password_confirmation())
                .initialCurrencyId(user.getCurrency().getId())
                .build();
        Call<ResponseObject<AuthResponse>> call = apiService.register(request);
        call.enqueue(new Callback<ResponseObject<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<AuthResponse>> call, @NonNull Response<ResponseObject<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {

                        // clear storage
                        SharedPreferencesUtils.clearSharedPreferences(EnteringInitialAmountActivity.this);

                        // save access, refresh tokens and user id
                        SharedPreferencesUtils.saveAccessToken(EnteringInitialAmountActivity.this, response.body().getResult().getToken());
                        SharedPreferencesUtils.saveRefreshToken(EnteringInitialAmountActivity.this, response.body().getResult().get_refresh_token());
                        SharedPreferencesUtils.saveUserId(EnteringInitialAmountActivity.this, response.body().getResult().getUser().getId());
                        // re-builder user with user id
                        user = new User.Builder()
                                .id(response.body().getResult().getUser().getId())
                                .name(user.get_name())
                                .email(user.get_email())
                                .password(user.get_password())
                                .passwordConfirmation(user.get_password_confirmation())
                                .currency(user.getCurrency())
                                .build();
                        // create wallet
                        createWalletAndProceed();
                    } else {
                        Toast.makeText(EnteringInitialAmountActivity.this, response.body().getResult().getErrors().get(0), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<AuthResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void createWalletAndProceed() {

        Wallet wallet = new Wallet.Builder()
                .name("Cash")
                .color(getColorHexFromResource(R.color.color_1))
                .accountId(user.getId())
                .walletTypeCode("GEN")
                .exclude(1)
                .initialAmount(initialAmount)
                .build();

        Call<ResponseObject<Wallet>> call = apiService.createWallet(wallet);
        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {
                        Intent mainActivity = new Intent(EnteringInitialAmountActivity.this, MainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Toast.makeText(EnteringInitialAmountActivity.this, response.body().getResult().getErrors().get(0), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });


    }


    private String getColorHexFromResource(int colorResId) {
        int colorInt = ContextCompat.getColor(this, colorResId);
        return String.format("#%06X", (0xFFFFFF & colorInt));
    }
    private void onDotButtonClick() {
        String currentText = getTextFromTextView();

        // Add dot only if there is no dot in the current text
        if (!currentText.contains(".")) {
            currentText += ".";
        }

        tvInitialAmount.setText(currentText);
    }

    private void onNumberButtonClick(AppCompatButton numberButton) {

        String currentText = getTextFromTextView();
        String number = numberButton.getText().toString();

        // Check if there are already 2 decimal places
        if (currentText.contains(".")
                && currentText.split("\\.").length == 2
                && currentText.split("\\.")[1].length() >= 2
        ) return; // No more numbers added

        // Only 9 decimals are allowed
        if (!currentText.contains(".")
                &&
                currentText
                        .replace(",", "")
                        .split("\\.")[0].length() == ALLOWED_NUMBER_OF_DECIMALS)
            return;


        // Remove leading zero
        if (currentText.equals("0")) {
            currentText = "";
        }

        currentText += number;

        tvInitialAmount.setText(formatAmount(currentText));
    }

    private String formatAmount(String amount) {
        // Remove all commas from the string
        String cleanString = amount.replace(",", "");

        DecimalFormat dft = new DecimalFormat("#,###.##");
        try {
            // Parse clean string into a number
            initialAmount = Double.parseDouble(cleanString);
            // Format the number and return the format string
            return dft.format(initialAmount);
        } catch (NumberFormatException e) {
            return amount; // Returns the original string if there is a parse error
        }
    }
    private void initData() {

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Get extra `user`
        Intent choosingCurrencyActivity = getIntent();

        user = (User) choosingCurrencyActivity.getSerializableExtra("user");

        // set currency symbol to text view
        if (user != null) {
            tvCurrencySymbol.setText(user.getCurrency().get_symbol());
        }

        // set number 0 to text view initial amount
        tvInitialAmount.setText("0");
    }

    private void setControl() {
        btnSkip = findViewById(R.id.btn_skip);
        btnFinish = findViewById(R.id.finish_button);
        btnDelete = findViewById(R.id.btn_delete);

        btnNumberOne = findViewById(R.id.number_one);
        btnNumberTwo = findViewById(R.id.number_two);
        btnNumberThree = findViewById(R.id.number_three);
        btnNumberFour = findViewById(R.id.number_four);
        btnNumberFive = findViewById(R.id.number_five);
        btnNumberSix = findViewById(R.id.number_six);
        btnNumberSeven = findViewById(R.id.number_seven);
        btnNumberEight = findViewById(R.id.number_eight);
        btnNumberNine = findViewById(R.id.number_nine);
        btnNumberZero = findViewById(R.id.number_zero);
        btnDot = findViewById(R.id.dot);

        tvCurrencySymbol = findViewById(R.id.tv_currency_symbol);
        tvInitialAmount = findViewById(R.id.tv_initial_amount);

    }
}