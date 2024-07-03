package com.example.financialmanagerapp.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.model.request.EmailCheckerRequest;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.EmailValidator;
import com.example.financialmanagerapp.utils.PasswordValidator;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnteringEmailPasswordActivity extends BaseActivity {

    protected User user = null;
    private FinancialManagerAPI apiService;

    protected TextInputEditText edtEmail, edtPassword, edtPasswordConfirmation;
    protected AppCompatButton btnNext;
    protected LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_email_password);
        setControl();
        initData();
        setEvents();



    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the state of EditTexts to SharedPreferences when the Activity is destroyed
        SharedPreferencesUtils.saveEmail(this, Objects.requireNonNull(edtEmail.getText()).toString());
        SharedPreferencesUtils.savePassword(this, Objects.requireNonNull(edtPassword.getText()).toString());
        SharedPreferencesUtils.savePasswordConfirmation(this, Objects.requireNonNull(edtPasswordConfirmation.getText()).toString());
    }



    private boolean validate(String email, String password, String passwordConfirmation) {
        return EmailValidator.isValidEmail(email)
                && !password.trim().isEmpty()
                && !passwordConfirmation.trim().isEmpty()
                && PasswordValidator.isValidPassword(password)
                && PasswordValidator.isMatched(password, passwordConfirmation);
    }

    private void setEvents() {

        // handle event edit text email changed
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        // handle event edit text password changed
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!PasswordValidator.isValidPassword(s.toString())) {
                    edtPassword.setError(PasswordValidator.passwordPattern);
                }
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        // handle event edit text password confirmation changed
        edtPasswordConfirmation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        // handle button next clicked event
        btnNext.setOnClickListener(v -> {
            String email = Objects.requireNonNull(edtEmail.getText()).toString();
            String password = Objects.requireNonNull(edtPassword.getText()).toString();
            String passwordConfirmation = Objects.requireNonNull(edtPasswordConfirmation.getText()).toString();
            lottieAnimationView.setVisibility(View.VISIBLE);
            checkEmailExistsAndProceed(email, password, passwordConfirmation);
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

    private void checkEmailExistsAndProceed(String email, String password, String passwordConfirmation) {
        Call<ResponseObject<List<String>>> call = apiService.emailChecker(new EmailCheckerRequest(email));
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                lottieAnimationView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        if (user != null) {
                            user = new User.Builder()
                                    .name(user.get_name())
                                    .email(email)
                                    .password(password)
                                    .passwordConfirmation(passwordConfirmation)
                                    .build();
                        }

                        // start choosing currency activity
                        Intent choosingCurrencyActivity = new Intent(EnteringEmailPasswordActivity.this, ChoosingCurrencyActivity.class);
                        choosingCurrencyActivity.putExtra("user", user);
                        startActivity(choosingCurrencyActivity);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        edtEmail.setError(response.body().getResult().get(0));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void validateInputs() {
        boolean validated = validate(
            Objects.requireNonNull(edtEmail.getText()).toString().trim(),
            Objects.requireNonNull(edtPassword.getText()).toString().trim(),
            Objects.requireNonNull(edtPasswordConfirmation.getText()).toString().trim()
        );

        if (validated) {
            btnNext.setEnabled(true);
            btnNext.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_primary_button));
        }
    }

    private void initData() {
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Get extra `user`
        Intent choosingNameActivity = getIntent();

        user = (User) choosingNameActivity.getSerializableExtra("user");

        edtEmail.setText(SharedPreferencesUtils.getEmail(this));
        edtPassword.setText(SharedPreferencesUtils.getPassword(this));
        edtPasswordConfirmation.setText(SharedPreferencesUtils.getPasswordConfirmation(this));

        validateInputs(); // Make sure that the restored fields are validated again
    }

    private void setControl() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtPasswordConfirmation = findViewById(R.id.edt_confirm_password);

        btnNext = findViewById(R.id.btn_next);
        lottieAnimationView = findViewById(R.id.animationView);
    }


}