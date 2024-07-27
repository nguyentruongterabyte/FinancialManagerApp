package com.example.financialmanagerapp.activity.fragment.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.utils.Calculator;
import com.example.financialmanagerapp.utils.MoneyFormatter;
import com.example.financialmanagerapp.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class EnteringAmountFragment extends DialogFragment {
    protected static final int ALLOWED_NUMBER_OF_DECIMALS = 12;
    protected String type;
    protected List<String> mathOperators = new ArrayList<>();
    protected List<Double> numbers = new ArrayList<>();
    private ImageView btnBack;

    protected TextView tvAmount;
    protected ImageView btnDelete, btnFinish;
    protected SharedViewModel sharedViewModel;
    protected boolean isDotButtonClicked = false;
    protected AppCompatButton
            key_c, key_division, key_multiplication,
            num_1, num_2, num_3, key_subtraction,
            num_4, num_5, num_6, key_addition,
            num_7, num_8, num_9, key_equals,
            key_dot, num_0, key_000;

    public EnteringAmountFragment(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entering_amount, container, false);
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

        // handle delete button clicked
        btnDelete.setOnClickListener(v -> {

        });

        // handle keyboard clicked
        key_dot.setOnClickListener(v -> onDotButtonClick());

        num_0.setOnClickListener(v -> onNumberButtonClick(num_0));
        num_1.setOnClickListener(v -> onNumberButtonClick(num_1));
        num_2.setOnClickListener(v -> onNumberButtonClick(num_2));
        num_3.setOnClickListener(v -> onNumberButtonClick(num_3));
        num_4.setOnClickListener(v -> onNumberButtonClick(num_4));
        num_5.setOnClickListener(v -> onNumberButtonClick(num_5));
        num_6.setOnClickListener(v -> onNumberButtonClick(num_6));
        num_7.setOnClickListener(v -> onNumberButtonClick(num_7));
        num_8.setOnClickListener(v -> onNumberButtonClick(num_8));
        num_9.setOnClickListener(v -> onNumberButtonClick(num_9));

        // handle key 000 clicked
        key_000.setOnClickListener(v -> onKeyThreeZerosClick());

        // handle operator keys clicked
        key_division.setOnClickListener(v -> onOperatorButtonClick(key_division));
        key_multiplication.setOnClickListener(v -> onOperatorButtonClick(key_multiplication));
        key_addition.setOnClickListener(v -> onOperatorButtonClick(key_addition));
        key_subtraction.setOnClickListener(v -> onOperatorButtonClick(key_subtraction));

        // handle key delete expression clicked
        key_c.setOnClickListener(v -> onKeyCClick());

        // handle button delete clicked
        btnDelete.setOnClickListener(v -> onDeleteButtonClick());

        // handle key equals clicked
        key_equals.setOnClickListener(v -> onKeyEqualsClick());

        // handle button finish clicked
        btnFinish.setOnClickListener(v -> onFinishButtonClick());
    }

    private void onFinishButtonClick() {
        double result = handleCalculate();
        switch (type) {
            case Utils.ENTERING_AMOUNT:
                sharedViewModel.setAmount(result);
                dismiss();
                break;
            case Utils.ENTERING_FEE:
                sharedViewModel.setFee(result);
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    private void onKeyThreeZerosClick() {
        num_0.performClick();
        num_0.performClick();
        num_0.performClick();
    }

    private double handleCalculate() {
        Calculator calculator = new Calculator(numbers, mathOperators);

        // remove if unnecessary last operator
        if (numbers.size() == mathOperators.size()) {
            mathOperators.remove(mathOperators.size() - 1);
        }
        try {
            double result = calculator.calculateExpression();

            // reset numbers and math operators array
            // clear numbers array
            numbers.clear();

            // clear math operators array
            mathOperators.clear();

            // add 0 to numbers array
            numbers.add(result);

            // if exists dot button clicked, set false
            isDotButtonClicked = false;

            // set expression to text view
            String expression = generateExpression();
            tvAmount.setText(expression);
            return result;
        } catch (ArithmeticException | IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0.0;
        }

    }

    private void onKeyEqualsClick() {
        handleCalculate();
    }

    private void onDeleteButtonClick() {

        String currentExpression = tvAmount.getText().toString();

        // If express length equal 1, example 5
        // reset text view
        // and return
        if (currentExpression.length() == 1) {
            resetTextViewAmount();
            return;
        }

        // if end of current expression is dot character, remove it
        // and return
        if (currentExpression.endsWith(".")) {
            isDotButtonClicked = false;
            tvAmount.setText(currentExpression.substring(0, currentExpression.length() - 1));
            return;
        }

        int numberSize = numbers.size();
        int operatorSize = mathOperators.size();

        // if length of math operators is les than
        // length of numbers array
        // handle last number of number array
        // vice verse, handle remove last operator of math operators
        if (operatorSize < numberSize) {
            double lastNumber = numbers.get(numberSize - 1);
            DecimalFormat dft = new DecimalFormat("#.##");
            String lastNumberStr = dft.format(lastNumber);

            // if length of last number is more than 1
            // remove last digit and parse to numbers array
            if (lastNumberStr.length() > 1) {
                lastNumberStr = lastNumberStr.substring(0, lastNumberStr.length() - 1);
                numbers.set(numberSize - 1, Double.parseDouble(lastNumberStr));

                if (lastNumberStr.endsWith(".0")) {
                    String expression = generateExpression();
                    tvAmount.setText(String.format("%s.0", expression));
                    return;
                }
            } else {
                // if length of last number is equals 1
                // remove this number from numbers array
                numbers.remove(numberSize - 1);
            }


        } else {
            mathOperators.remove(operatorSize - 1);
        }

        String expression = generateExpression();
        if (isDotButtonClicked && expression.charAt(expression.length() - 2) != '.') {
            tvAmount.setText(String.format("%s.", expression));
        } else {
            tvAmount.setText(expression);
        }

    }

    private void onKeyCClick() {
        resetTextViewAmount();
    }

    private void resetTextViewAmount() {
        // clear numbers array
        numbers.clear();

        // clear math operators array
        mathOperators.clear();

        // add 0 to numbers array
        numbers.add(0.0);

        // if exists dot button clicked, set false
        isDotButtonClicked = false;

        // set expression to text view
        String expression = generateExpression();
        tvAmount.setText(expression);
    }

    private void onOperatorButtonClick(AppCompatButton operatorButton) {
        String operator = operatorButton.getText().toString();

        isDotButtonClicked = false;
        if (mathOperators.size() < numbers.size()) {
            mathOperators.add(operator);
        } else {
            mathOperators.set(mathOperators.size() - 1, operator);
        }
        String expression = generateExpression();
        tvAmount.setText(expression);
    }

    private String generateExpression() {
        StringBuilder expression = new StringBuilder();
        int numberSize = numbers.size();
        int operatorSize = mathOperators.size();


        for (int i = 0; i < numbers.size(); i++) {
            expression.append(MoneyFormatter.getText("", numbers.get(i)).trim());

            if (i == numberSize - 1) {
                if (numberSize == operatorSize) {
                    expression.append(mathOperators.get(i));
                }
            } else {
                expression.append(mathOperators.get(i));
            }

        }
        return expression.toString();
    }

    private void onNumberButtonClick(AppCompatButton numberButton) {
        double lastNumber = numbers.get(numbers.size() - 1);
        DecimalFormat dft = new DecimalFormat("#.##");
        String lastNumberStr = dft.format(lastNumber);

        // get digit from key number
        String digit = numberButton.getText().toString();
        String currentExpression = tvAmount.getText().toString();
        if (currentExpression.endsWith(".") || currentExpression.endsWith(".0")) {

            // if last number string has no ".", add it
            if (!lastNumberStr.contains("."))
                lastNumberStr += ".";

            // if expression end with ".0", example: 5.0
            // append last number string "0" and new digit
            if (currentExpression.endsWith(".0")) {
                lastNumberStr += ("0" + digit);
            } else {
                lastNumberStr += digit;
            }
            if (digit.equals("0") && isDotButtonClicked) {
                String expression = generateExpression();
                tvAmount.setText(String.format("%s.0", expression));
                return;
            }

            // set last number to new last number of numbers array
            numbers.set(numbers.size() - 1, Double.parseDouble(lastNumberStr));
        } else {
            // if math operators length is less than numbers size
            // get last number and append new digit to it
            // vice verse, append to numbers array new digit
            if (mathOperators.size() < numbers.size()) {

                // Check if there are already 2 decimal places
                if (lastNumberStr.contains(".")
                        && lastNumberStr.split("\\.").length == 2
                        && lastNumberStr.split("\\.")[1].length() >= 2
                ) return; // No more numbers added
                // Only 9 decimals are allowed
                if (lastNumberStr
                        .split("\\.")[0].length() == ALLOWED_NUMBER_OF_DECIMALS)
                    return;
                // Remove leading zero
                if (lastNumberStr.equals("0"))
                    lastNumberStr = "";

                // append digit to last number
                lastNumberStr += digit;

                // set last number to new last number of numbers array
                numbers.set(numbers.size() - 1, Double.parseDouble(lastNumberStr));
            } else {
                String number = numberButton.getText().toString();
                numbers.add(Double.parseDouble(number));
            }
        }
        String expression = generateExpression();
        tvAmount.setText(expression);

    }


    private void onDotButtonClick() {

        if (isDotButtonClicked) {
            return;
        }
        isDotButtonClicked = true;
        int numberSize = numbers.size();
        int operatorSize = mathOperators.size();

        if (operatorSize < numberSize) {
            String expression = generateExpression();
            tvAmount.setText(String.format("%s.", expression));
        } else {
            numbers.add(0.0);
            String expression = generateExpression();
            tvAmount.setText(String.format("%s.", expression));
        }


    }


    private void initData() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (type.equals(Utils.ENTERING_AMOUNT)) {
            sharedViewModel.getAmount().observe(getViewLifecycleOwner(), a ->
            {
                tvAmount.setText(MoneyFormatter.getText("", a).trim());
                numbers.add(a);
            });
        } else {
            tvAmount.setText(MoneyFormatter.getText("", 0.0).trim());
            numbers.add(0.0);
        }
    }


    private void setControl(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        tvAmount = view.findViewById(R.id.tv_amount);

        key_c = view.findViewById(R.id.key_c);
        key_division = view.findViewById(R.id.key_division);
        key_multiplication = view.findViewById(R.id.key_multiplication);
        btnDelete = view.findViewById(R.id.btn_delete);

        num_1 = view.findViewById(R.id.number_one);
        num_2 = view.findViewById(R.id.number_two);
        num_3 = view.findViewById(R.id.number_three);
        key_subtraction = view.findViewById(R.id.key_subtraction);

        num_4 = view.findViewById(R.id.number_four);
        num_5 = view.findViewById(R.id.number_five);
        num_6 = view.findViewById(R.id.number_six);
        key_addition = view.findViewById(R.id.key_addition);

        num_7 = view.findViewById(R.id.number_seven);
        num_8 = view.findViewById(R.id.number_eight);
        num_9 = view.findViewById(R.id.number_nine);
        key_equals = view.findViewById(R.id.key_equals);

        key_dot = view.findViewById(R.id.dot);
        num_0 = view.findViewById(R.id.number_zero);
        key_000 = view.findViewById(R.id.number_three_zeros);
        btnFinish = view.findViewById(R.id.finish_button);


    }
}
