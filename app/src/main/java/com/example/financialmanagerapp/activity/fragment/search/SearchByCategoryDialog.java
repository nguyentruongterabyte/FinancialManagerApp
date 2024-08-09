package com.example.financialmanagerapp.activity.fragment.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.CategoryAdapter;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchByCategoryDialog extends DialogFragment {
    private int categoryType;
    private List<Category> categories;
    private ImageButton btnBack;
    private ListView listView;
    private TextView btnDone;
    private CategoryAdapter adapter;
    protected SharedViewModel sharedViewModel;

    public SearchByCategoryDialog(int categoryType) {
        this.categoryType = categoryType;
        categories = new ArrayList<>();
        switch (categoryType) {
            case Utils.EXPENSE_CATEGORY:
                for (Category category : Utils.categories) {
                    if (category.get_transaction_type_id() == Utils.EXPENSE_TRANSACTION_ID)
                        categories.add(category);
                }
                break;
            case Utils.INCOME_CATEGORY:
                for (Category category : Utils.categories) {
                    if (category.get_transaction_type_id() == Utils.INCOME_TRANSACTION_ID)
                        categories.add(category);
                }
                break;
            default: // all category
                categories.addAll(Utils.categories);
                break;
        }
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

    private void setEvents() {


        // handle button back clicked
        btnBack.setOnClickListener(v -> dismiss());

        // handle button done clicked
        btnDone.setOnClickListener(v -> {
            List<Integer> categoryIds = new ArrayList<>();
            CategoryAdapter adapter = (CategoryAdapter) listView.getAdapter();
            Map<Integer, Boolean> checkBoxState = adapter.getCheckBoxStateMap();

            for (Map.Entry<Integer, Boolean> entry : checkBoxState.entrySet()) {
                int categoryId = entry.getKey();
                boolean isChecked = entry.getValue();

                if (isChecked) {
                    categoryIds.add(categoryId);
                }
            }

            // set categoryIds to live data
            sharedViewModel.setCategoryIds(categoryIds);
            sharedViewModel.setIsSetCategoriesDone(categoryIds.size() != 0);
            dismiss();
        });

        // observe live data
        sharedViewModel.getCategoryIds().observe(getViewLifecycleOwner(), categoryIds -> {
            if (adapter == null) {
                adapter = new CategoryAdapter(requireContext(), categories, Utils.categoriesIcons, true, categoryIds);
                listView.setAdapter(adapter);
            } else {
                adapter.updateCategories(categories, categoryIds);
            }
        });

    }

    private void initData() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        adapter = new CategoryAdapter(requireContext(), categories, Utils.categoriesIcons, true, null);
        listView.setAdapter(adapter);
    }

    private void setControl(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnDone = view.findViewById(R.id.btn_done);

        listView = view.findViewById(R.id.list_view);
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
}
