package com.example.financialmanagerapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.SliderAdapter;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.SliderData;
import com.example.financialmanagerapp.model.response.ResponseObject;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.SharedPreferencesUtils;
import com.example.financialmanagerapp.utils.Utils;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetStartedActivity extends BaseActivity {
    protected String url1 = "https://firebasestorage.googleapis.com/v0/b/financialmanager-91d15.appspot.com/o/slider%2FExpenses_category.png?alt=media&token=d97f396c-eba4-40c6-94c9-e4538f4a871f";
    protected String url2 = "https://firebasestorage.googleapis.com/v0/b/financialmanager-91d15.appspot.com/o/slider%2FSaving_goal.png?alt=media&token=90a1afd9-5d2d-4d5b-88a6-82e86923d0fb";
    protected String url3 = "https://firebasestorage.googleapis.com/v0/b/financialmanager-91d15.appspot.com/o/slider%2FBudget_control.png?alt=media&token=9e533da4-4657-4965-86e4-b0e45e904ffc";
    protected String url4 = "https://firebasestorage.googleapis.com/v0/b/financialmanager-91d15.appspot.com/o/slider%2FFinacial_monitoring.png?alt=media&token=3d5cfb8e-b0dd-4867-bf6b-a83f83e745e6";

    protected SliderView sliderView;
    protected AppCompatButton btnGetStarted, btnLogin;
    private FinancialManagerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        setControl();
//        navigate();
        initData();
        setEvents();


    }

    private void navigate() {
        if (SharedPreferencesUtils.getAccessToken(this) != null || SharedPreferencesUtils.getRefreshToken(this) != null) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }


    private void setEvents() {
        btnGetStarted.setOnClickListener(v -> {
            Intent choosingNameActivity = new Intent(this, ChoosingNameActivity.class);
            startActivity(choosingNameActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnLogin.setOnClickListener(v -> {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void initData() {
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        sliderDataArrayList.add(new SliderData(url1, "Expenses category", "Category make it easier to track and monitor your spending"));
        sliderDataArrayList.add(new SliderData(url2, "Saving goal", "Set your first savings goal and track the savings progress"));
        sliderDataArrayList.add(new SliderData(url3, "Budget control", "Add a budget to control your spending"));
        sliderDataArrayList.add(new SliderData(url4, "Financial monitoring", "Keep your incomes and expenses on track"));
        SliderAdapter adapter = new SliderAdapter(sliderDataArrayList);

        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        apiService = retrofit.create(FinancialManagerAPI.class);

        // get all currencies and save them into global variable
        if (Utils.currencies.size() == 0)
            getAllCurrencies();
    }

    public void getAllCurrencies() {

        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, this);
        retrofit.create(FinancialManagerAPI.class);

        Call<ResponseObject<List<Currency>>> call = apiService.getCurrencies();
        call.enqueue(new Callback<ResponseObject<List<Currency>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Response<ResponseObject<List<Currency>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Utils.currencies = response.body().getResult();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<Currency>>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }

    private void setControl() {
        sliderView = findViewById(R.id.slider);
        btnGetStarted = findViewById(R.id.get_started_button);
        btnLogin = findViewById(R.id.login_button);
    }
}