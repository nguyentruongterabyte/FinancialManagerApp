package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.CategoryTransactionAdapter;
import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.BudgetDetail;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BudgetDetailActivity extends BaseActivity {

    private ImageButton btnBack, btnEdit, btnDelete;
    private TextView tvName, tvSpentAmount, tvLeftAmountTitle, tvLeftAmount,
            tvCategory, tvProgress, tvBudgetAmount, tvPeriod, tvDaysLeft;
    private ProgressBar progressBar;
    private ListView listView;
    private Budget budget;
    private FinancialManagerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_detail);
        setControl();
        initData();
        setEvents();
    }


    private void setEvents() {
        // handle button delete clicked
        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Transaction")
                    .setMessage("Do you really want to delete this transaction?")
                    .setPositiveButton("DELETE", (dialog, which) ->
                            handleDelete()
                    )
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // handle button edit clicked
        btnEdit.setOnClickListener(v -> {
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            Intent editingBudgetActivity = new Intent(BudgetDetailActivity.this, CreatingBudgetActivity.class);
            editingBudgetActivity.putExtra("budget", budget);
            startActivity(editingBudgetActivity);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });


        // handle button back clicked
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void handleDelete() {
        Call<ResponseObject<Void>> call = apiService.deleteBudget(
                Utils.currentUser.getId(),
                budget.getId()
        );

        call.enqueue(new Callback<ResponseObject<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Void>> call, @NonNull Response<ResponseObject<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 204) {
                        for (int i = Utils.currentUser.getBudgets().size() - 1; i >= 0; i--) {
                            if (Utils.currentUser.getBudgets().get(i).getId() == budget.getId()) {
                                Utils.currentUser.getBudgets().remove(i);
                                break;
                            }
                        }
                        finish();
                    } else {
                        Toast.makeText(BudgetDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BudgetDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Void>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {


        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);


        budget = (Budget) getIntent().getSerializableExtra("budget");
        List<Transaction> transactions = Utils.getAllTransactionsFromWallets();
        List<Transaction> expenseTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                expenseTransactions.add(transaction);
        }
        if (budget != null) {
            tvName.setText(budget.get_name()); // set budget name

            // get start date and end date
            List<Timestamp> dateArray = Utils.handleCalculateDatePeriod(budget.get_period());
            Calendar start = TimerFormatter.getCalendar(dateArray.get(0));
            Calendar end = TimerFormatter.getCalendar(dateArray.get(1));
            // get category ids from budget
            List<Category> categories = new ArrayList<>();
            List<Integer> categoryIds = new ArrayList<>();
            for (BudgetDetail budgetDetail : budget.getBudget_details()) {
                categoryIds.add(budgetDetail.getCategory().getId());
                categories.add(budgetDetail.getCategory());
            }
            // filter transaction in the budget
            List<Transaction> budgetTransactions = new ArrayList<>();
            for (Transaction transaction : expenseTransactions) {
                Calendar createdAt = TimerFormatter.getCalendar(transaction.get_date());
                if (categoryIds.contains(transaction.get_category_id())
                        && !createdAt.before(start) && !createdAt.after(end))
                    budgetTransactions.add(transaction);
            }
            // set progress color
            progressBar.setProgressTintList(
                    ColorStateList.valueOf(Color.parseColor(budget.get_color())));

            // calculate spent amount
            double spentAmount = 0;
            for (Transaction transaction : budgetTransactions) {
                spentAmount += transaction.get_amount();
            }
            tvSpentAmount.setText(MoneyFormatter.getText(
                    Utils.currentUser.getCurrency().get_symbol(),
                    spentAmount));

            // calculate left amount;
            double leftAmount = budget.get_amount() - spentAmount;
            if (leftAmount < 0) {
                tvSpentAmount.setTextColor(ContextCompat.getColor(this, R.color.color_6));
                tvLeftAmountTitle.setText("Overspent");
                tvProgress.setText("100.00%");
                progressBar.setProgress(100);
            } else {
                double percentage = spentAmount / budget.get_amount() * 100f;
                @SuppressLint("DefaultLocale")
                String formattedPercentage = String.format("%.2f", percentage);
                progressBar.setProgress(spentAmount == 0
                        ? 0
                        : (int) percentage == 0 ? 1
                        : (int) percentage);

                tvProgress.setText(formattedPercentage + "%");
            }

            tvLeftAmount.setText(MoneyFormatter.getText(
                    Utils.currentUser.getCurrency().get_symbol(),
                    Math.abs(leftAmount)
            ));

            // display text view categories
            String categoriesName = "";
            for (Category category : categories) {
                categoriesName = categoriesName.concat(category.get_name() + ", ");
            }
            if (categoriesName.length() > 0) { // remove last comma
                int length = categoriesName.length();
                categoriesName = categoriesName.substring(0, length - 2);
            }

            tvCategory.setText(categoriesName);

            // set text view budget amount
            tvBudgetAmount.setText(
                    MoneyFormatter.getText(
                            Utils.currentUser.getCurrency().get_symbol(),
                            budget.get_amount()));

            // set text view period
            String startDate =
                    TimerFormatter.convertDateString(
                            start.get(Calendar.YEAR),
                            start.get(Calendar.MONTH) + 1,
                            start.get(Calendar.DAY_OF_MONTH));
            String endDate =
                    TimerFormatter.convertDateString(
                            end.get(Calendar.YEAR),
                            end.get(Calendar.MONTH) + 1,
                            end.get(Calendar.DAY_OF_MONTH));
            tvPeriod.setText(startDate + " - " + endDate);

            // set text view days left
            Calendar currentDate = Calendar.getInstance();
            long diffInMillis = end.getTimeInMillis() - currentDate.getTimeInMillis();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            tvDaysLeft.setText(daysLeft + " days left");

            // set transactions by category for list view
            CategoryTransactionAdapter adapter = new CategoryTransactionAdapter(
                    this, budgetTransactions, -1, Utils.categoriesIcons, false);
            listView.setAdapter(adapter);
            Utils.setListViewHeightBasedOnItems(listView);
        }


    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        tvName = findViewById(R.id.tv_name);
        tvSpentAmount = findViewById(R.id.tv_spent_amount);
        tvLeftAmountTitle = findViewById(R.id.tv_left_amount_title);
        tvLeftAmount = findViewById(R.id.tv_left_amount);
        tvCategory = findViewById(R.id.tv_category);

        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress_bar);
        tvBudgetAmount = findViewById(R.id.tv_budget_amount);
        tvPeriod = findViewById(R.id.tv_period);
        tvDaysLeft = findViewById(R.id.tv_days_left);

        listView = findViewById(R.id.list_view);

    }
}