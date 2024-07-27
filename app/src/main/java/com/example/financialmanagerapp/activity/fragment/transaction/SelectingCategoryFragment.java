package com.example.financialmanagerapp.activity.fragment.transaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.CategoryAdapter;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectingCategoryFragment extends DialogFragment {

    protected int categoryType;
    private ImageButton btnBack;
    private ListView listView;

    protected FinancialManagerAPI apiService;
    protected List<Category> categories = new ArrayList<>();
    protected SharedViewModel sharedViewModel;

    public SelectingCategoryFragment(int categoryType) {
        this.categoryType = categoryType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selecting_category, container, false);
        setControl(view);
        initData();
        setEvents();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Make the dialog full screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // set match parent to dialog
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // set match parent to dialog
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void setEvents() {
        // handle button back clicked
        btnBack.setOnClickListener(v -> dismiss());

        // handle list item clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = categories.get(position);
            if (selectedCategory.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID) {
                sharedViewModel.setCategory(selectedCategory);
            } else {
                sharedViewModel.setExpenseCategory(selectedCategory);
            }

            // Dismiss the fragment
            dismiss();
        });
    }

    private void initData() {

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, requireContext());
        apiService = retrofit.create(FinancialManagerAPI.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // get categories if they are empty
        if (Utils.categories.size() == 0) {
            Call<ResponseObject<List<Category>>> call = apiService.getCategories();

            call.enqueue(new Callback<ResponseObject<List<Category>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Response<ResponseObject<List<Category>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            Utils.categories = response.body().getResult();
                            initCategories();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<Category>>> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "API call failed: " + t.getMessage());
                }
            });
        } else {
            initCategories();
        }
    }

    private void initCategories() {
        categories.clear();
        switch (categoryType) {

            case Utils.INCOME_TRANSACTION_ID:     // income
                for (Category category : Utils.categories)
                    if (category.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                        categories.add(category);
                break;
            case Utils.EXPENSE_TRANSACTION_ID:     // expense
                for (Category category : Utils.categories)
                    if (category.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                        categories.add(category);
                break;
            default:
                break;
        }

        CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories, Utils.categoriesIcons);
        listView.setAdapter(adapter);
    }

    private void setControl(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        listView = view.findViewById(R.id.list_view);
    }
}
