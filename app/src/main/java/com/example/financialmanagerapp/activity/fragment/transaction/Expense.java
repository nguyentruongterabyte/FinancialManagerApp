package com.example.financialmanagerapp.activity.fragment.transaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;


public class Expense extends Fragment {

    private TextView tvDate, tvTime, tvAmount, tvCategory, tvWallet;
    private EditText edtDescription, edtMemo;
    private LinearLayout tvAmountContainer, tvCategoryContainer, tvWalletContainer;
    private SharedViewModel sharedViewModel;

    protected int year, month, day;
    protected int hour, minute;
    protected double amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControl(view);
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle text view date clicked
        tvDate.setOnClickListener(v -> displayDatePickerDialog());

        // handle text view time clicked
        tvTime.setOnClickListener(v -> displayTimePickerDialog());

        // handle text view amount container clicked
        tvAmountContainer.setOnClickListener(v -> {
            sharedViewModel.setAmount(amount);
            EnteringAmountFragment enteringAmountFragment = new EnteringAmountFragment(Utils.ENTERING_AMOUNT);
            enteringAmountFragment.show(getParentFragmentManager(), enteringAmountFragment.getTag());
        });

        // handle text view category container clicked
        tvCategoryContainer.setOnClickListener(v -> {
            SelectingCategoryFragment selectingCategoryFragment = new SelectingCategoryFragment(Utils.EXPENSE_TRANSACTION_ID);
            selectingCategoryFragment.show(getParentFragmentManager(), selectingCategoryFragment.getTag());
        });

        // handle text view wallet container clicked
        tvWalletContainer.setOnClickListener(v -> {
            SelectingWalletFragment selectingWalletFragment = new SelectingWalletFragment(Utils.TO_WALLET_TYPE);
            selectingWalletFragment.show(getParentFragmentManager(), selectingWalletFragment.getTag());
        });

        // handle edit text description changed
        edtDescription.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                runnable = () -> sharedViewModel.setDescription(s.toString());
                handler.postDelayed(runnable, 300);
            }
        });
        // handle edit text memo changed
        edtMemo.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                runnable = () -> sharedViewModel.setMemo(s.toString());
                handler.postDelayed(runnable, 300);
            }
        });

        // Observe live data
        sharedViewModel.getSelectedDate().observe(getViewLifecycleOwner(), s -> tvDate.setText(s));
        sharedViewModel.getSelectedTime().observe(getViewLifecycleOwner(), s -> tvTime.setText(s));
        sharedViewModel.getDay().observe(getViewLifecycleOwner(), d -> day = d);
        sharedViewModel.getMonth().observe(getViewLifecycleOwner(), m -> month = m);
        sharedViewModel.getYear().observe(getViewLifecycleOwner(), y -> year = y);
        sharedViewModel.getHour().observe(getViewLifecycleOwner(), h -> hour = h);
        sharedViewModel.getMinute().observe(getViewLifecycleOwner(), m -> minute = m);
        sharedViewModel.getWallet().observe(getViewLifecycleOwner(), this::setTvWallet);
        sharedViewModel.getExpenseCategory().observe(getViewLifecycleOwner(), category -> {
            tvCategory.setText(category.get_name());
            tvCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        });
        sharedViewModel.getAmount().observe(getViewLifecycleOwner(), a ->
        {
            tvAmount.setText(MoneyFormatter.getText(Utils.currentUser.getCurrency().get_symbol(), a));
            amount = a;
        });
        sharedViewModel.getDescription().observe(getViewLifecycleOwner(), description -> {
            if (!edtDescription.getText().toString().equals(description)) {
                edtDescription.setText(description);
                edtDescription.setSelection(description.length());
            }
        });
        sharedViewModel.getMemo().observe(getViewLifecycleOwner(), memo -> {
            if (!edtMemo.getText().toString().equals(memo)) {
                edtMemo.setText(memo);
                edtMemo.setSelection(memo.length());
            }
        });

    }

    private void displayTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, selectedHour, selectedMinute) -> {
                    sharedViewModel.setSelectedTime(selectedHour, selectedMinute);
                    sharedViewModel.setTime(selectedHour, selectedMinute);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void displayDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    sharedViewModel.setSelectedDate(selectedYear, selectedMonth, selectedDay);
                    sharedViewModel.setDate(selectedYear, selectedMonth, selectedDay);
                }, year, month, day
        );

        datePickerDialog.show();
    }


    private void initData() {
        // get the view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        amount = 0;

        // set the first wallet of user to text view
        if (Utils.currentUser.getWallets().size() != 0) {
            setTvWallet(Utils.currentUser.getWallets().get(0));
            sharedViewModel.setWallet(Utils.currentUser.getWallets().get(0));
        }
    }

    private void setTvWallet(Wallet wallet) {
        String walletName = wallet.get_name();
        String symbol = Utils.currentUser.getCurrency().get_symbol();
        double walletBalance = wallet.get_amount();
        String renderText = walletName + "•" + MoneyFormatter.getText(symbol, walletBalance);
        tvWallet.setText(renderText);
    }

    private void setControl(View view) {
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        tvAmount = view.findViewById(R.id.tv_amount);
        tvAmountContainer = view.findViewById(R.id.tv_amount_container);
        tvCategory = view.findViewById(R.id.tv_category);
        tvCategoryContainer = view.findViewById(R.id.tv_category_container);
        tvWallet = view.findViewById(R.id.tv_wallet);
        tvWalletContainer = view.findViewById(R.id.tv_wallet_container);

        edtDescription = view.findViewById(R.id.edt_description);
        edtMemo = view.findViewById(R.id.edt_memo);

    }
}