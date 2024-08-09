package com.example.financialmanagerapp.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.StructureViewPagerAdapter;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.utils.TimerFormatter;
import com.example.financialmanagerapp.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class StructureActivity extends BaseActivity {

    private ImageButton btnBack, btnForward, btnReturn;
    private TextView tvDate;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SharedViewModel sharedViewModel;
    protected StructureViewPagerAdapter structureViewPagerAdapter;
    protected int year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structure);
        setControl();
        initData();
        setEvents();
    }

    private void setEvents() {
        // handle back
        btnReturn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // handle decrease month year
        btnBack.setOnClickListener(v -> {
            int oldYear = year;
            handleDecreaseMonthYear();
            sharedViewModel.setMonth(month);
            if (oldYear != year)
                sharedViewModel.setYear(year);
        });

        // handle increase month year
        btnForward.setOnClickListener(v -> {
            int oldYear = year;
            handleIncreaseMonthYear();
            sharedViewModel.setMonth(month);
            if (oldYear != year)
                sharedViewModel.setYear(year);
        });

        // Set up the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call super.onBackPressed() to handle default back press behavior
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void handleIncreaseMonthYear() {
        if (month > 10) {
            month = 0;
            year++;
        } else {
            month++;
        }
        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));
    }

    @SuppressLint("DefaultLocale")
    private void handleDecreaseMonthYear() {
        if (month < 1) {
            month = 11;
            year--;
        } else {
            month--;
        }
        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void initData() {

        // set adapter tab
        structureViewPagerAdapter = new StructureViewPagerAdapter(this);
        viewPager.setAdapter(structureViewPagerAdapter);
        Calendar calendar = Calendar.getInstance();

        year = getIntent().getIntExtra("year", calendar.get(Calendar.YEAR));
        month = getIntent().getIntExtra("month", calendar.get(Calendar.MONTH));
        sharedViewModel.setYear(year);
        sharedViewModel.setMonth(month);
        // set tabs name
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // inflate custom view for tab
            @SuppressLint("InflateParams")
            View customTab = LayoutInflater.from(this).inflate(R.layout.transaction_tab_view, null);
            TextView tabText = customTab.findViewById(R.id.transaction_tab_text);
            tabText.setTypeface(tabText.getTypeface(), Typeface.NORMAL);
            tabText.setTextColor(ContextCompat.getColor(this, R.color.black));
            switch (position) {
                case 0:
                    tabText.setText("Income");
                    tab.setId(Utils.INCOME_TAB_ID);
                    break;
                case 1:
                    tabText.setText("Expense");
                    tab.setId(Utils.EXPENSE_TAB_ID);
                    break;
            }
            tab.setCustomView(customTab);
        }).attach();

        // Move to expense tab
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tabLayout.selectTab(tab);

        tvDate.setText(String.format("%s %d", TimerFormatter.getFullMonthOfYearText(month), year));
    }

    private void setControl() {
        btnBack = findViewById(R.id.btn_back);
        btnForward = findViewById(R.id.btn_forward);
        btnReturn = findViewById(R.id.btn_return);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tvDate = findViewById(R.id.tv_date);
        // Initialize the ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }
}