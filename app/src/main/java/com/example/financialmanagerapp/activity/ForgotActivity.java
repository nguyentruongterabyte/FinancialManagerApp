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
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.request.LoginRequest;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.EmailValidator;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotActivity extends BaseActivity {

    protected AppCompatButton btnContinue;
    protected TextInputEditText edtEmail;
    protected ImageButton btnBack;
    protected LottieAnimationView lottieAnimationView;
    private FinancialManagerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        setControl();
        initData();
        setEvents();
    }


    private boolean validate(String email) {
        return EmailValidator.isValidEmail(email);
    }

    private void validateInputs() {
        boolean validated = validate(
                Objects.requireNonNull(edtEmail.getText()).toString().trim()
        );

        if (validated) {
            btnContinue.setEnabled(true);
            btnContinue.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_primary_button));
        }
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

        // handle event button continue clicked
        btnContinue.setOnClickListener(v -> {
            lottieAnimationView.setVisibility(View.VISIBLE);
            forgotPasswordAndProceed();
        });

        // handle event button on back clicked
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void forgotPasswordAndProceed() {
        LoginRequest request = new LoginRequest.Builder()
                .email(Objects.requireNonNull(edtEmail.getText()).toString())
                .build();

        Call<ResponseObject<Void>> call = apiService.requestResetPassword(request);
        // send email request reset password
        call.enqueue(new Callback<ResponseObject<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Void>> call, @NonNull Response<ResponseObject<Void>> response) {
                lottieAnimationView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Toast.makeText(ForgotActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        // start activity login
                        Intent loginActivity = new Intent(ForgotActivity.this, LoginActivity.class);
                        loginActivity.putExtra("email", edtEmail.getText().toString());
                        startActivity(loginActivity);
                        finish();
                    } else {
                        edtEmail.setError(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Void>> call, @NonNull Throwable t) {
                lottieAnimationView.setVisibility(View.GONE);
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void initData() {
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Init edit text email from login activity
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        edtEmail.setText(email);

        validateInputs();
    }

    private void setControl() {
        btnContinue = findViewById(R.id.btn_continue);
        btnBack = findViewById(R.id.btn_back);

        edtEmail = findViewById(R.id.edt_email);

        lottieAnimationView = findViewById(R.id.animationView);
    }
}