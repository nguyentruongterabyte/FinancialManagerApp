package com.example.financialmanagerapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.request.LoginRequest;
import com.example.financialmanagerapp.model.response.AuthResponse;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.EmailValidator;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity {

    private FinancialManagerAPI apiService;

    protected TextInputEditText edtEmail, edtPassword;
    protected AppCompatButton btnLogin;
    protected ToggleButton btnToggle;
    protected TextView btnSignUp, btnForgotPassword;
    protected LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setControl();
        initData();
        setEvents();
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
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        // handle event button login clicked
        btnLogin.setOnClickListener(v -> {
            LoginRequest request = new LoginRequest.Builder()
                    .email(Objects.requireNonNull(edtEmail.getText()).toString())
                    .password(Objects.requireNonNull(edtPassword.getText()).toString())
                    .build();

            Call<ResponseObject<AuthResponse>> call = apiService.login(request);

            lottieAnimationView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<ResponseObject<AuthResponse>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<AuthResponse>> call, @NonNull Response<ResponseObject<AuthResponse>> response) {
                    lottieAnimationView.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            // clear storage
                            SharedPreferencesUtils.clearSharedPreferences(LoginActivity.this);

                            // save access, refresh tokens and user id
                            SharedPreferencesUtils.saveAccessToken(LoginActivity.this, response.body().getResult().getToken());
                            SharedPreferencesUtils.saveRefreshToken(LoginActivity.this, response.body().getResult().get_refresh_token());
                            SharedPreferencesUtils.saveUserId(LoginActivity.this, response.body().getResult().getUser().getId());

                            // save current user to utils
                            Utils.currentUser = response.body().getResult().getUser();

                            // start main activity
                            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainActivity);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<AuthResponse>> call, @NonNull Throwable t) {
                    lottieAnimationView.setVisibility(View.GONE);
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        });

        // handle event sign up button clicked
        btnSignUp.setOnClickListener(v -> {
            Intent choosingNameActivity = new Intent(this, ChoosingNameActivity.class);
            startActivity(choosingNameActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // handle button toggle visibility password clicked
        btnToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // show password and change toggle icon
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnToggle.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.ic_eye, 0, 0
                );


            } else {
                // hide password and change toggle icon
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnToggle.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.ic_eye_slash, 0, 0
                );
            }
            String password = Objects.requireNonNull(edtPassword.getText()).toString();
            if (!password.isEmpty()) {
                edtPassword.setSelection(password.length());
            }
        });

        // handle button forgot password clicked
        btnForgotPassword.setOnClickListener(v -> {
            Intent forgotActivity = new Intent(this, ForgotActivity.class);
            forgotActivity.putExtra("email", Objects.requireNonNull(edtEmail.getText()).toString());
            startActivity(forgotActivity);
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

    private boolean validate(String email, String password) {
        return EmailValidator.isValidEmail(email)
                && !password.trim().isEmpty();
    }

    private void validateInputs() {
        boolean validated = validate(
                Objects.requireNonNull(edtEmail.getText()).toString().trim(),
                Objects.requireNonNull(edtPassword.getText()).toString().trim()
        );

        if (validated) {
            btnLogin.setEnabled(true);
            btnLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_primary_button));
        }
    }
    private void setControl() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnToggle = findViewById(R.id.btn_toggle);

        lottieAnimationView = findViewById(R.id.animationView);
    }
    private void initData() {
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Init edit text email from forgot activity if the email sent successfully
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        edtEmail.setText(email);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        validateInputs();
    }
}