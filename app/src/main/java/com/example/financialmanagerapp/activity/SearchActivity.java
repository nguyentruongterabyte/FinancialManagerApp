package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.fragment.search.SearchByCategoryDialog;
import com.example.financialmanagerapp.activity.fragment.search.SearchByDateDialog;
import com.example.financialmanagerapp.activity.fragment.search.SearchByWalletDialog;
import com.example.financialmanagerapp.adapter.TransactionAdapter;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends BaseActivity {
    private AppCompatButton btnSearchByDate, btnSearchByCategory, btnSearchByWallet;
    private EditText edtSearch;
    private TextView tvNoResult, tvResultQuantity,
            tvDate, tvWalletsQuantity, tvCategoriesQuantity;
    private ImageButton btnBack;
    private LinearLayout noResult, btnSearchByDate2, btnSearchByCategory2, btnSearchByWallet2;
    private ImageButton btnRemoveDate, btnRemoveCategories, btnRemoveWallets;
    private ScrollView resultContainer;
    private ListView listView;
    private SharedViewModel sharedViewModel;
    protected int day, month, year, endDay, endMonth, endYear;
    protected List<Transaction> transactions;
    protected List<Integer> categoryIds;
    protected List<Integer> walletIds;
    protected boolean isSetDateDone;
    protected boolean isSetCategoriesDone;
    protected boolean isSetWalletsDone;
    protected FinancialManagerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {

        // handle button search on keyboard clicked
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Handle the search action
                handleFilterSearch();
                Log.d("myLog", "key search: " + edtSearch.getText().toString());
                return true;
            }
            return false;
        });

        // handle list view item (transaction) clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Transaction clickedTransaction = (Transaction) parent.getItemAtPosition(position);

            // starting transaction details
            Intent transactionDetailActivity = new Intent(this, TransactionDetailActivity.class);
            transactionDetailActivity.putExtra("transaction", clickedTransaction);
            startActivity(transactionDetailActivity);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // handle when remove regex wallet symbol clicked
        btnRemoveWallets.setOnClickListener(v -> {
            resetButton(btnSearchByWallet, btnSearchByWallet2);
            isSetWalletsDone = false;
            sharedViewModel.setWalletIds(new ArrayList<>());
            handleFilterSearch();
        });

        // handle search by wallet button clicked
        btnSearchByWallet.setOnClickListener(v -> handleBtnSearchByWalletClicked());
        tvWalletsQuantity.setOnClickListener(v -> handleBtnSearchByWalletClicked());

        // handle when remove regex categories symbol clicked
        btnRemoveCategories.setOnClickListener(v -> {
            resetButton(btnSearchByCategory, btnSearchByCategory2);
            isSetCategoriesDone = false;
            sharedViewModel.setCategoryIds(new ArrayList<>());
            handleFilterSearch();
        });

        // handle search by categories button clicked
        btnSearchByCategory.setOnClickListener(v -> handleBtnSearchByCategoryClicked());
        tvCategoriesQuantity.setOnClickListener(v -> handleBtnSearchByCategoryClicked());

        // handle when remove regex date symbol clicked
        btnRemoveDate.setOnClickListener(v -> {
            resetButton(btnSearchByDate, btnSearchByDate2);
            resetDate();
            isSetDateDone = false;
            handleFilterSearch();
        });


        // handle search by date button clicked
        btnSearchByDate.setOnClickListener(v -> handleBtnSearchByDateClicked());
        tvDate.setOnClickListener(v -> handleBtnSearchByDateClicked());

        // handle button back clicked
        btnBack.setOnClickListener(v -> {
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
        sharedViewModel.getDay().observe(this, d -> {
            if (isSetDateDone && day != d) {
                day = d;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });
        sharedViewModel.getMonth().observe(this, m -> {
            if (isSetDateDone && month != m) {
                month = m;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });
        sharedViewModel.getYear().observe(this, y -> {
            if (isSetDateDone && year != y) {
                year = y;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });

        sharedViewModel.getEndDay().observe(this, d -> {
            if (isSetDateDone && endDay != d) {
                endDay = d;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });
        sharedViewModel.getEndMonth().observe(this, m -> {
            if (isSetDateDone && endMonth != m) {
                endMonth = m;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });
        sharedViewModel.getEndYear().observe(this, y -> {
            if (isSetDateDone && endYear != y) {
                endYear = y;
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });

        sharedViewModel.getIsSetDateDone().observe(this, b -> {
            isSetDateDone = b;
            if (isSetDateDone) {
                setTextBtnSearchByDate();
                handleFilterSearch();
            }
        });

        sharedViewModel.getIsSetCategoriesDone().observe(this, b -> {
            isSetCategoriesDone = b;
            handleFilterSearch();
        });
        sharedViewModel.getIsSetWalletsDone().observe(this, b -> {
            isSetWalletsDone = b;
            handleFilterSearch();
        });

        sharedViewModel.getCategoryIds().observe(this, _categoryIds -> {
            categoryIds = _categoryIds;
            if (categoryIds.size() > 0)
                setTextBtnSearchByCategories();
        });

        sharedViewModel.getWalletIds().observe(this, _walletIds -> {
            walletIds = _walletIds;
            if (walletIds.size() > 0)
                setTextBtnSearchByWallets();
        });
    }

    private void handleBtnSearchByDateClicked() {
        SearchByDateDialog searchByDateDialog = new SearchByDateDialog();
        searchByDateDialog.show(getSupportFragmentManager(), searchByDateDialog.getTag());

        sharedViewModel.setDay(day);
        sharedViewModel.setMonth(month);
        sharedViewModel.setYear(year);

        sharedViewModel.setEndDay(endDay);
        sharedViewModel.setEndMonth(endMonth);
        sharedViewModel.setEndYear(endYear);
    }

    private void handleBtnSearchByCategoryClicked() {
        if (Utils.categories.size() == 0)
            return;
        SearchByCategoryDialog searchByCategoryDialog = new SearchByCategoryDialog(Utils.ALL_CATEGORY);
        searchByCategoryDialog.show(getSupportFragmentManager(), searchByCategoryDialog.getTag());
        sharedViewModel.setCategoryIds(categoryIds);
    }

    private void handleBtnSearchByWalletClicked() {
        if (Utils.currentUser.getWallets().size() == 0)
            return;
        SearchByWalletDialog searchByWalletDialog = new SearchByWalletDialog();
        searchByWalletDialog.show(getSupportFragmentManager(), searchByWalletDialog.getTag());
        sharedViewModel.setWalletIds(walletIds);
    }

    @SuppressLint("SetTextI18n")
    private void handleFilterSearch() {
        List<Transaction> filterTransactions = transactions;

        if (!edtSearch.getText().toString().trim().equals("")) {
            filterTransactions = searchTransactionsByType(filterTransactions, Utils.SEARCH_BY_KEY);
        }

        if (isSetDateDone) {
            filterTransactions = searchTransactionsByType(filterTransactions, Utils.SEARCH_BY_DATE);
        }

        if (isSetCategoriesDone) {
            filterTransactions = searchTransactionsByType(filterTransactions, Utils.SEARCH_BY_CATEGORIES);
        }

        if (isSetWalletsDone) {
            filterTransactions = searchTransactionsByType(filterTransactions, Utils.SEARCH_BY_WALLETS);
        }

        if (filterTransactions.size() == 0) {
            noResult.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.GONE);
            tvNoResult.setText("No result found");
        } else {
            noResult.setVisibility(View.GONE);
            resultContainer.setVisibility(View.VISIBLE);
            tvResultQuantity.setText(filterTransactions.size() > 1
                    ? filterTransactions.size() + " transactions found"
                    : "1 transaction found");

            TransactionAdapter adapter = new TransactionAdapter(this, filterTransactions, Utils.categoriesIcons, -1, false);
            listView.setAdapter(adapter);
            Utils.setListViewHeightBasedOnItems(listView);
        }
    }

    private List<Transaction> searchTransactionsByType(List<Transaction> filterTransactions, int searchType) {
        List<Transaction> result = new ArrayList<>();
        switch (searchType) {
            case Utils.SEARCH_BY_KEY:
                String searchKey = edtSearch.getText().toString().trim().toLowerCase();
                for (Transaction transaction : filterTransactions) {
                    String description = transaction.get_description();

                    if (description != null && description.trim().toLowerCase().contains(searchKey)) {
                        result.add(transaction);
                    }
                }
                return result;
            case Utils.SEARCH_BY_DATE:
                for (Transaction transaction : filterTransactions) {
                    Calendar createAt = TimerFormatter.getCalendar(transaction.get_date());

                    Calendar start = Calendar.getInstance();
                    start.set(Calendar.YEAR, year);
                    start.set(Calendar.MONTH, month);
                    start.set(Calendar.DAY_OF_MONTH, day);
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.MINUTE, 0);
                    start.set(Calendar.SECOND, 0);
                    start.set(Calendar.MILLISECOND, 0);

                    Calendar end = Calendar.getInstance();
                    end.set(Calendar.YEAR, endYear);
                    end.set(Calendar.MONTH, endMonth);
                    end.set(Calendar.DAY_OF_MONTH, endDay);
                    end.set(Calendar.HOUR_OF_DAY, 23);
                    end.set(Calendar.MINUTE, 59);
                    end.set(Calendar.SECOND, 59);
                    end.set(Calendar.MILLISECOND, 999);

                    // if transaction created at time between start date and end
                    if (!createAt.before(start) && !createAt.after(end)) {
                        result.add(transaction);
                    }
                }
                return result;
            case Utils.SEARCH_BY_CATEGORIES:
                for (Transaction transaction : filterTransactions) {
                    if (categoryIds.contains(transaction.get_category_id())) {
                        result.add(transaction);
                    }
                }
                return result;
            case Utils.SEARCH_BY_WALLETS:
                for (Transaction transaction : filterTransactions) {
                    if (walletIds.contains(transaction.get_wallet_id())
                            || walletIds.contains(transaction.get_from_wallet_id())) {
                        result.add(transaction);
                    }
                }
                return result;
            default:
                return filterTransactions;
        }
    }


    private void resetDate() {
        Calendar calendar = Calendar.getInstance();
        day = 1; // start day
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        // set the calendar to the first day of given month and year
        calendar.set(year, month, day);

        // get the last day of the month
        endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        endMonth = month;
        endYear = year;
    }

    private void setTextBtnSearchByWallets() {
        String walletsQuantity = walletIds.size() > 1 ? walletIds.size() + " wallets" : "1 wallet";
        tvWalletsQuantity.setText(walletsQuantity);
        setButton(btnSearchByWallet, btnSearchByWallet2);
    }

    private void setTextBtnSearchByCategories() {
        String categoriesQuantity = categoryIds.size() > 1 ? categoryIds.size() + " categories" : "1 category";
        tvCategoriesQuantity.setText(categoriesQuantity);
        setButton(btnSearchByCategory, btnSearchByCategory2);
    }

    @SuppressLint("SetTextI18n")
    private void setTextBtnSearchByDate() {
        String period = TimerFormatter.getMonthOfYearText(month) + " " + TimerFormatter.formatNumber(day)
                + " - " + TimerFormatter.getMonthOfYearText(endMonth) + " " + TimerFormatter.formatNumber(endDay);
        tvDate.setText(period);
        setButton(btnSearchByDate, btnSearchByDate2);
    }

    private void setButton(AppCompatButton button, LinearLayout button2) {
        button.setVisibility(View.GONE);
        button2.setVisibility(View.VISIBLE);
    }


    private void resetButton(AppCompatButton button, LinearLayout button2) {
        button.setVisibility(View.VISIBLE);
        button2.setVisibility(View.GONE);
    }

    private void initData() {
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // get the view model
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        isSetDateDone = false;
        isSetCategoriesDone = false;
        isSetWalletsDone = false;

        resetDate();

        // get categories if empty
        if (Utils.categories.size() == 0) {
            getCategories();
        }

        categoryIds = new ArrayList<>();
        walletIds = new ArrayList<>();

        // get all transactions from wallets
        transactions = Utils.getAllTransactionsFromWallets();

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
                        Toast.makeText(SearchActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }


    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnSearchByDate = findViewById(R.id.btn_search_by_date);
        btnSearchByDate2 = findViewById(R.id.btn_search_by_date_2);
        btnSearchByCategory = findViewById(R.id.btn_search_by_category);
        btnSearchByCategory2 = findViewById(R.id.btn_search_by_category_2);
        btnSearchByWallet = findViewById(R.id.btn_search_by_wallet);
        btnSearchByWallet2 = findViewById(R.id.btn_search_by_wallet_2);
        btnRemoveDate = findViewById(R.id.btn_remove_date);
        btnRemoveCategories = findViewById(R.id.btn_remove_categories);
        btnRemoveWallets = findViewById(R.id.btn_remove_wallet);

        tvNoResult = findViewById(R.id.tv_no_result);
        tvDate = findViewById(R.id.tv_date);
        tvCategoriesQuantity = findViewById(R.id.tv_categories_quantity);
        tvWalletsQuantity = findViewById(R.id.tv_wallets_quantity);

        noResult = findViewById(R.id.no_result);

        tvResultQuantity = findViewById(R.id.tv_result_quantity);
        resultContainer = findViewById(R.id.result_container);
        listView = findViewById(R.id.list_view);

        edtSearch = findViewById(R.id.edt_search);

    }
}