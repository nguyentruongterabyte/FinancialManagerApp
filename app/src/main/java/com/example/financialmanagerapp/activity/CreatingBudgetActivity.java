package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.fragment.search.SearchByCategoryDialog;
import com.example.financialmanagerapp.activity.fragment.transaction.EnteringAmountFragment;
import com.example.financialmanagerapp.adapter.ColorAdapter;
import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.BudgetDetail;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.DTO.BudgetDTO;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.mapper.BudgetMapper;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatingBudgetActivity extends BaseActivity {
    private enum Type {
        EDIT, CREATE
    }

    private static final String[] periods = Utils.periods;
    private Spinner spinnerPeriod, spinnerColor;
    private ImageButton btnBack;
    private TextView btnSave, tvAmount, tvCategory, tvTitle;
    private EditText edtName;
    private LinearLayout tvAmountContainer, tvCategoryContainer;
    protected FinancialManagerAPI apiService;
    protected double amount;
    protected List<Integer> categoryIds;
    protected String period;
    protected String hexColor;
    private SharedViewModel sharedViewModel;
    private Type currentType = Type.CREATE;
    protected int budgetId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_budget);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {

        btnSave.setOnClickListener(v ->
                handleSaveBudget()
        );

        // handle spinner colors on item selected listener
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int colorResId = (int) parent.getItemAtPosition(position);
                int colorInt = ContextCompat.getColor(CreatingBudgetActivity.this, colorResId);
                hexColor = String.format("#%06X", (0xFFFFFF & colorInt));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // handle spinner period change
        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                period = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // handle text view category container clicked
        tvCategoryContainer.setOnClickListener(v -> {
            if (Utils.categories.size() == 0)
                return;
            SearchByCategoryDialog searchByCategoryDialog = new SearchByCategoryDialog(Utils.EXPENSE_CATEGORY);
            searchByCategoryDialog.show(getSupportFragmentManager(), searchByCategoryDialog.getTag());
            sharedViewModel.setCategoryIds(categoryIds);
        });

        // handle text view amount container clicked
        tvAmountContainer.setOnClickListener(v -> {
            sharedViewModel.setAmount(amount);
            EnteringAmountFragment enteringAmountFragment = new EnteringAmountFragment(Utils.ENTERING_AMOUNT);
            enteringAmountFragment.show(getSupportFragmentManager(), enteringAmountFragment.getTag());
        });

        // handle edit text name change
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no - op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no - op
            }
        });

        // Handle button back clicked
        btnBack.setOnClickListener(v -> {
            // Call super.onBackPressed() to handle default back press behavior
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
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

        // Observe live data
        sharedViewModel.getAmount().observe(this, a ->
        {
            tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), a));
            amount = a;
            validateInputs();
        });

        sharedViewModel.getCategoryIds().observe(this, _categoryIds -> {
            categoryIds = _categoryIds;
            if (categoryIds.size() > 0) {
                String categoriesQuantity = categoryIds.size() > 1
                        ? categoryIds.size() + " categories"
                        : "1 category";
                tvCategory.setText(categoriesQuantity);
                validateInputs();
            } else {
                tvCategory.setText(R.string.select_category);
            }
        });
    }

    private void handleSaveBudget() {
        String name = edtName.getText().toString();
        List<BudgetDetail> budgetDetails = handleMapBudgetDetails();
        Budget budget = new Budget.Builder()
                .name(name)
                .amount(amount)
                .color(hexColor)
                .period(period)
                .budgetDetails(budgetDetails)
                .build();

        BudgetDTO budgetDTO = BudgetMapper.toBudgetDTO(budget);
        Call<ResponseObject<Budget>> call = null;
        switch (currentType) {
            case CREATE:
                call = apiService.createBudget(budgetDTO);
                break;
            case EDIT:
                call = apiService.updateBudget(budgetDTO, Utils.currentUser.getId(), budgetId);
                break;
        }

        call.enqueue(new Callback<ResponseObject<Budget>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Budget>> call, @NonNull Response<ResponseObject<Budget>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) { // created
                        budget.setId(response.body().getResult().getId());
                        Utils.currentUser.getBudgets().add(response.body().getResult());
                        finish();
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    } else if (response.body().getStatus() == 200) { // edited
                        for (int i = 0; i < Utils.currentUser.getBudgets().size(); i++) {
                            if (budgetId == Utils.currentUser.getBudgets().get(i).getId()) {
                                Utils.currentUser.getBudgets().remove(i);
                                break;
                            }
                            Utils.currentUser.getBudgets().add(response.body().getResult());
                        }
                        finish();
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    } else {
                        Toast.makeText(CreatingBudgetActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatingBudgetActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Budget>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });

    }


    private List<BudgetDetail> handleMapBudgetDetails() {
        List<BudgetDetail> budgetDetails = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            BudgetDetail budgetDetail = new BudgetDetail(categoryId);
            budgetDetails.add(budgetDetail);
        }

        return budgetDetails;
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        // get the view model
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Set spinner color
        ColorAdapter colorAdapter = new ColorAdapter(this, Utils.colors);
        spinnerColor.setAdapter(colorAdapter);

        // Set spinner period
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(periodAdapter);

        // get categories if empty
        if (Utils.categories.size() == 0) {
            getCategories();
        }
        categoryIds = new ArrayList<>();
        Budget budgetIntent = (Budget) getIntent().getSerializableExtra("budget");

        if (budgetIntent != null) {
            currentType = Type.EDIT;
            budgetId = budgetIntent.getId();
        }


        switch (currentType) {
            case CREATE:

                amount = 0;
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));

                int colorResId = Utils.colors[0];
                int colorInt = ContextCompat.getColor(this, colorResId);
                hexColor = String.format("#%06X", (0xFFFFFF & colorInt));

                period = periods[0];

                break;
            case EDIT:
                tvTitle.setText("Edit Budget");
                assert budgetIntent != null;
                amount = budgetIntent.get_amount();
                edtName.setText(budgetIntent.get_name());
                tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
                sharedViewModel.setAmount(amount);
                // set selection color
                hexColor = budgetIntent.get_color();
                int colorIndex;
                for (colorIndex = 0; colorIndex < Utils.colors.length; colorIndex++) {
                    int resId = Utils.colors[colorIndex];
                    int colorInteger = ContextCompat.getColor(this, resId);
                    String currentColorHex = String.format("#%06X", (0xFFFFFF & colorInteger));
                    if (hexColor.trim().equals(currentColorHex.trim()))
                        break;
                }
                spinnerColor.setSelection(colorIndex);

                // set selection period
                period = budgetIntent.get_period();
                int periodIndex;
                for (periodIndex = 0; periodIndex < periods.length; periodIndex++) {
                    if (period.equals(periods[periodIndex]))
                        break;
                }
                spinnerPeriod.setSelection(periodIndex);

                // set categoryIds
                for (BudgetDetail budgetDetail : budgetIntent.getBudget_details()) {
                    categoryIds.add(budgetDetail.get_category_id());
                }
                sharedViewModel.setCategoryIds(categoryIds);
                break;
        }


    }

    private void getCategories() {
        Call<ResponseObject<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ResponseObject<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Response<ResponseObject<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Utils.categories = response.body().getResult();
                    } else {
                        Toast.makeText(CreatingBudgetActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatingBudgetActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void validateInputs() {
        String name = edtName.getText().toString();
        int categoriesQuantity = categoryIds.size();
        setEnableSaveButton(
                !name.trim().equals("")
                        && categoriesQuantity > 0
                        && amount > 0
        );
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

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);

        edtName = findViewById(R.id.edt_name);

        tvAmount = findViewById(R.id.tv_amount);
        tvCategory = findViewById(R.id.tv_category);

        tvAmountContainer = findViewById(R.id.tv_amount_container);
        tvCategoryContainer = findViewById(R.id.tv_category_container);

        tvTitle = findViewById(R.id.tv_title);

        spinnerPeriod = findViewById(R.id.spinner_period);
        spinnerColor = findViewById(R.id.spinner_color);


    }
}