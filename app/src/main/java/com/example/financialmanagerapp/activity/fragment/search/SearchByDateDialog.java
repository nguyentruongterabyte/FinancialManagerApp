package com.example.financialmanagerapp.activity.fragment.search;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SearchByDateDialog extends BottomSheetDialogFragment {

    private LinearLayout tvStartContainer, tvEndContainer;
    private TextView tvStart, tvEnd;
    private AppCompatButton btnCancel, btnDone;
    private SharedViewModel sharedViewModel;
    protected int day, month, year, endDay, endMonth, endYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_by_date_bottom_sheet_layout, container, false);
        setControl(view);
        initData();
        setEvents();
        return view;
    }

    private void setEvents() {
        // handle button done clicked
        btnDone.setOnClickListener(v -> {

            sharedViewModel.setIsSetDateDone(true);

            sharedViewModel.setDay(day);
            sharedViewModel.setMonth(month);
            sharedViewModel.setYear(year);

            sharedViewModel.setEndDay(endDay);
            sharedViewModel.setEndMonth(endMonth);
            sharedViewModel.setEndYear(endYear);


            dismiss();
        });

        // handle cancel when button cancel clicked
        btnCancel.setOnClickListener(v -> dismiss());

        // handle text view start date clicked
        tvStartContainer.setOnClickListener(v ->
                displayStartDatePickerDialog()
        );

        tvEndContainer.setOnClickListener(v ->
                displayEndDatePickerDialog()
        );
        // Observe live data
        sharedViewModel.getDay().observe(getViewLifecycleOwner(), d -> {
            int oldDay = day;
            day = d;
            if (day != oldDay)
                setTVStart();

        });
        sharedViewModel.getMonth().observe(getViewLifecycleOwner(), m -> {
            int oldMonth = month;
            month = m;
            if (month != oldMonth)
                setTVStart();

        });
        sharedViewModel.getYear().observe(getViewLifecycleOwner(), y -> {
            int oldYear = year;
            year = y;
            if (year != oldYear)
                setTVStart();
        });

        sharedViewModel.getEndDay().observe(getViewLifecycleOwner(), d -> {
            int oldEndDay = endDay;
            endDay = d;
            if (endDay != oldEndDay)
                setTVEnd();
        });
        sharedViewModel.getEndMonth().observe(getViewLifecycleOwner(), m -> {
            int oldEndMonth = endMonth;
            endMonth = m;
            if (endMonth != oldEndMonth)
                setTVEnd();
        });
        sharedViewModel.getEndYear().observe(getViewLifecycleOwner(), y -> {
            int oldEndYear = endYear;
            endYear = y;
            if (endYear != oldEndYear)
                setTVEnd();
        });

    }

    private void displayEndDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) ->
                {
                    tvEnd.setText(
                            TimerFormatter.convertDateString(
                                    selectedYear,
                                    selectedMonth + 1,
                                    selectedDay));
                    endYear = selectedYear;
                    endMonth = selectedMonth;
                    endDay = selectedDay;
                },
                endYear, endMonth, endDay
        );

        datePickerDialog.show();
    }

    private void displayStartDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) ->
                {
                    tvStart.setText(
                            TimerFormatter.convertDateString(
                                    selectedYear,
                                    selectedMonth + 1,
                                    selectedDay));

                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void setTVEnd() {
        String dateStr = TimerFormatter.convertDateString(endYear, endMonth + 1, endDay);
        tvEnd.setText(dateStr);
    }

    private void setTVStart() {
        String dateStr = TimerFormatter.convertDateString(year, month + 1, day);
        tvStart.setText(dateStr);
    }

    private void initData() {
        // get the view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


    }

    private void setControl(View view) {
        tvStartContainer = view.findViewById(R.id.tv_start_container);
        tvEndContainer = view.findViewById(R.id.tv_end_container);

        tvStart = view.findViewById(R.id.tv_start);
        tvEnd = view.findViewById(R.id.tv_end);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnDone = view.findViewById(R.id.btn_done);
    }
}
