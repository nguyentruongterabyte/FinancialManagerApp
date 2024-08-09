package com.example.financialmanagerapp.activity;

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
import com.example.financialmanagerapp.model.User;
import com.example.financialmanagerapp.model.mapper.BudgetMapper;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatingBudgetActivity extends BaseActivity {
    protected static final String[] periods = {"Weekly", "Monthly", "Quarterly", "Yearly"};
    private Spinner spinnerPeriod, spinnerColor;
    private ImageButton btnBack;
    private TextView btnSave, tvAmount, tvCategory;
    private EditText edtName;
    private LinearLayout tvAmountContainer, tvCategoryContainer;
    protected FinancialManagerAPI apiService;
    protected double amount;
    protected List<Integer> categoryIds;
    protected String period;
    protected String hexColor;
    private SharedViewModel sharedViewModel;

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

        List<Timestamp> dateArray = handleCalculateDatePeriod();
        Timestamp startDate = dateArray.get(0);
        Timestamp endDate = dateArray.get(1);
        List<BudgetDetail> budgetDetails = handleMapBudgetDetails();
        Budget budget = new Budget.Builder()
                .name(name)
                .amount(amount)
                .startDate(startDate)
                .endDate(endDate)
                .color(hexColor)
                .period(period)
                .budgetDetails(budgetDetails)
                .build();

        BudgetDTO budgetDTO = BudgetMapper.toBudgetDTO(budget);
        Log.d("myLog", budgetDTO.toString());

        Call<ResponseObject<Budget>> call = apiService.createBudget(budgetDTO);
        call.enqueue(new Callback<ResponseObject<Budget>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Budget>> call, @NonNull Response<ResponseObject<Budget>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 201) {
                        handleLoadCurrentUserAndProceed();
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

    private void handleLoadCurrentUserAndProceed() {
        Call<ResponseObject<User>> call = apiService.get(Utils.currentUser.getId());
        call.enqueue(new Callback<ResponseObject<User>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<User>> call, @NonNull Response<ResponseObject<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Utils.currentUser = response.body().getResult();
                        finish();
                    } else {
                        Toast.makeText(CreatingBudgetActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatingBudgetActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<User>> call, @NonNull Throwable t) {
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

    private List<Timestamp> handleCalculateDatePeriod() {
        int startYear = 0, startMonth = 0, startDay = 0;
        int endYear = 0, endMonth = 0, endDay = 0;
        List<Timestamp> dateArray = new ArrayList<>();
        // handle calculate start and end date
        Calendar calendar = Calendar.getInstance();
        if (period.equals(periods[0])) {// Weekly

            // Set to start of the week (Sunday)
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Set to end of the week (Saturday)
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            endYear = calendar.get(Calendar.YEAR);
            endMonth = calendar.get(Calendar.MONTH);
            endDay = calendar.get(Calendar.DAY_OF_MONTH);
        } else if (period.equals(periods[1])) {// Monthly

            // Set to start of the month
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DAY_OF_MONTH);

            // set to end of the month
            endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            endYear = startYear;
            endMonth = startMonth;

        } else if (period.equals(periods[2])) {// Quarterly
            // Determine the start of the quarter
            int currentMonth = calendar.get(Calendar.MONTH);
            int quarterStartMonth = currentMonth / 3 * 3;
            calendar.set(Calendar.MONTH, quarterStartMonth);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Set to end of the quarter
            calendar.add(Calendar.MONTH, 2);
            endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            endYear = calendar.get(Calendar.YEAR);
            endMonth = calendar.get(Calendar.MONTH);
        } else if (period.equals(periods[3])) { // Yearly
            // Set to start of the year
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Set to end of the year
            calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
            endYear = calendar.get(Calendar.YEAR);
            endMonth = calendar.get(Calendar.MONTH);
            endDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        // set start date
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, startYear);
        start.set(Calendar.MONTH, startMonth);
        start.set(Calendar.DAY_OF_MONTH, startDay);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Timestamp startDate = new Timestamp(start.getTimeInMillis());
        dateArray.add(startDate);

        // set end date
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, endYear);
        end.set(Calendar.MONTH, endMonth);
        end.set(Calendar.DAY_OF_MONTH, endDay);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        Timestamp endDate = new Timestamp(end.getTimeInMillis());
        dateArray.add(endDate);

        return dateArray;
    }

    private void initData() {
        // get the view model
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        amount = 0;
        tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), amount));
        // Initialize RetrofitClient
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // Set spinner color
        ColorAdapter colorAdapter = new ColorAdapter(this, Utils.colors);
        spinnerColor.setAdapter(colorAdapter);
        int colorResId = Utils.colors[0];
        int colorInt = ContextCompat.getColor(this, colorResId);
        hexColor = String.format("#%06X", (0xFFFFFF & colorInt));

        // Set spinner period
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(periodAdapter);
        period = periods[0];

        // get categories if empty
        if (Utils.categories.size() == 0) {
            getCategories();
        }
        categoryIds = new ArrayList<>();
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

        spinnerPeriod = findViewById(R.id.spinner_period);
        spinnerColor = findViewById(R.id.spinner_color);


    }
}