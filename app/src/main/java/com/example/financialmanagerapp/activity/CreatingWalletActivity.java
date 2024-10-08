package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.fragment.transaction.EnteringAmountFragment;
import com.example.financialmanagerapp.adapter.ColorAdapter;
import com.example.financialmanagerapp.adapter.IconAdapter;
import com.example.financialmanagerapp.adapter.WalletTypeAdapter;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.WalletType;
import com.example.financialmanagerapp.model.mapper.WalletMapper;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatingWalletActivity extends BaseActivity {
    private Spinner spinnerType, spinnerColor, spinnerIcon;
    private ImageButton btnBack;
    private TextView btnSave, tvAmount;
    private EditText edtName;
    private LinearLayout tvAmountContainer, spinnerTypeContainer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swExclude;

    private Wallet wallet;

    protected FinancialManagerAPI apiService;
    private SharedViewModel sharedViewModel;
    private double diffAmount;

    private enum Type {
        EDIT, CREATE
    }

    private Type currentType = Type.CREATE;

    protected int walletId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_wallet);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {

        // handle button save clicked
        btnSave.setOnClickListener(v -> {
            wallet.set_account_id(Utils.currentUser.getId());
            wallet.set_name(edtName.getText().toString());

            handleSaveWallet();
        });

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable/Disable the button based on text input
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Handle button back clicked
        btnBack.setOnClickListener(v -> {
            // Call super.onBackPressed() to handle default back press behavior
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        // handle text view amount container clicked
        tvAmountContainer.setOnClickListener(v -> {
            sharedViewModel.setAmount(currentType == Type.EDIT ? wallet.get_initial_amount() : 0);
            EnteringAmountFragment enteringAmountFragment = new EnteringAmountFragment(Utils.ENTERING_AMOUNT);
            enteringAmountFragment.show(getSupportFragmentManager(), enteringAmountFragment.getTag());
        });

        //
        spinnerIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wallet.set_icon(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // handle spinner types on item selected listener
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WalletType walletType = (WalletType) parent.getItemAtPosition(position);
                Log.d("myLog", walletType.get_code());
                wallet.set_wallet_type_code(walletType.get_code());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // handle spinner colors on item selected listener
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get the color resource ID from the adapter
                int colorResId = (int) parent.getItemAtPosition(position);

                // Retrieve the actual color value using ContextCompact
                int colorInt = ContextCompat.getColor(CreatingWalletActivity.this, colorResId);

                // Convert the color int value to a hexadecimal string
                String hexColor = String.format("#%06X", (0xFFFFFF & colorInt));

                wallet.set_color(hexColor);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sharedViewModel.getAmount().observe(this, a ->
        {
            tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), a));
            wallet.set_amount(a);
            wallet.set_initial_amount(a);
        });

        swExclude.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                wallet.set_exclude(1);
            } else {
                wallet.set_exclude(0);
            }
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

    private void handleSaveWallet() {
        Call<ResponseObject<Wallet>> call = null;
        switch (currentType) {
            case CREATE:
                call = apiService.createWallet(WalletMapper.toWalletDTO(wallet));
                break;
            case EDIT:
                wallet.set_amount(wallet.get_initial_amount() - diffAmount);
                call = apiService.updateWallet(WalletMapper.toWalletDTO(wallet), Utils.currentUser.getId(), walletId);
                break;
        }


        call.enqueue(new Callback<ResponseObject<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Response<ResponseObject<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {
                        wallet = response.body().getResult();
                        Utils.currentUser.getWallets().add(wallet);
                        finish();
                    } else if (response.body().getStatus() == 200) {
                        Wallet.updateWalletInList(wallet, Utils.currentUser.getWallets());
                        finish();
                    } else {
                        Toast.makeText(CreatingWalletActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatingWalletActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Wallet>> call, @NonNull Throwable t) {

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
            btnSave.setEnabled(true);
            btnSave.setTextColor(ContextCompat.getColor(this, R.color.black));
        } else {
            btnSave.setEnabled(false);
            btnSave.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

    private void initData() {
        // get the view model
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Set spinner color
        ColorAdapter colorAdapter = new ColorAdapter(this, Utils.colors);
        spinnerColor.setAdapter(colorAdapter);

        // Set spinner icon
        IconAdapter iconAdapter = new IconAdapter(this, Utils.walletIcons);
        spinnerIcon.setAdapter(iconAdapter);


        wallet = new Wallet();
        Wallet walletIntent = (Wallet) getIntent().getSerializableExtra("wallet");
        if (walletIntent != null) {
            currentType = Type.EDIT;
            wallet = walletIntent;
            diffAmount = wallet.get_initial_amount() - wallet.get_amount();
            spinnerTypeContainer.setVisibility(View.GONE);
            walletId = walletIntent.getId();
        }

        switch (currentType) {
            case CREATE:
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), 0.0));
                getWalletTypes();
                break;
            case EDIT:
                edtName.setText(wallet.get_name());
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), wallet.get_amount()));
                sharedViewModel.setAmount(wallet.get_initial_amount());

                // set selection color
                String hexColor = wallet.get_color();
                int colorIndex;
                for (colorIndex = 0; colorIndex < Utils.colors.length; colorIndex++) {
                    int resId = Utils.colors[colorIndex];
                    int colorInteger = ContextCompat.getColor(this, resId);
                    String currentColorHex = String.format("#%06X", (0xFFFFFF & colorInteger));
                    if (hexColor.trim().equals(currentColorHex.trim()))
                        break;
                }
                spinnerColor.setSelection(colorIndex);
                // set selection icon
                spinnerIcon.setSelection(wallet.get_icon());
                validateInputs();
                break;
        }

    }


    private void getWalletTypes() {
        // Get wallet types
        Call<ResponseObject<List<WalletType>>> call = apiService.getWalletTypes();
        call.enqueue(new Callback<ResponseObject<List<WalletType>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<WalletType>>> call, @NonNull Response<ResponseObject<List<WalletType>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        List<WalletType> walletTypes = response.body().getResult();
                        WalletTypeAdapter adapter = new WalletTypeAdapter(CreatingWalletActivity.this, walletTypes);
                        spinnerType.setAdapter(adapter);
                    } else {
                        Toast.makeText(CreatingWalletActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatingWalletActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<WalletType>>> call, @NonNull Throwable t) {

            }
        });
    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        edtName = findViewById(R.id.edt_name);

        tvAmount = findViewById(R.id.tv_amount);
        tvAmountContainer = findViewById(R.id.tv_amount_container);
        spinnerTypeContainer = findViewById(R.id.spinner_type_container);

        spinnerColor = findViewById(R.id.spinner_color);
        spinnerType = findViewById(R.id.spinner_type);

        spinnerIcon = findViewById(R.id.spinner_icon);

        swExclude = findViewById(R.id.sw_exclude);
    }
}