
package com.example.financialmanagerapp.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ChoosingNameActivity extends BaseActivity {

    protected TextInputEditText edtName;
    protected AppCompatButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_name);
        setControl();
        initData();
        setEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtils.saveName(this, Objects.requireNonNull(edtName.getText()).toString());
    }

    private void initData() {
        edtName.setText(SharedPreferencesUtils.getName(this));
        validateInputs();
    }

    private void setEvents() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 24) {
                    edtName.setText(s.subSequence(0, 24));
                    edtName.setSelection(24); // Move the cursor to the end
                }

                // Enable/Disable the button based on text input
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action
            }
        });

        // handle event next button clicked
        btnNext.setOnClickListener(v -> {
            User user = new User
                    .Builder()
                    .name(Objects.requireNonNull(edtName.getText()).toString())
                    .build();
            // put extra `user` and start entering email password activity
            Intent enteringEmailPasswordActivity = new Intent(this, EnteringEmailPasswordActivity.class);
            enteringEmailPasswordActivity.putExtra("user", user);
            startActivity(enteringEmailPasswordActivity);
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

    private boolean validate(String name) {
        return !name.trim().isEmpty();
    }
    private void validateInputs() {
        boolean validated = validate(
                Objects.requireNonNull(edtName.getText()).toString().trim()
        );

        if (validated) {
            btnNext.setEnabled(true);
            btnNext.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_primary_button));
        }
    }

    private void setControl() {
        btnNext = findViewById(R.id.btn_next);
        edtName = findViewById(R.id.edt_name);
    }
}