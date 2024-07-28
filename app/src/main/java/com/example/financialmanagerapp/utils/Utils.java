package com.example.financialmanagerapp.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.User;

import java.util.ArrayList;

import java.util.List;

public class Utils {
    // Base URL
    public static final String BASE_URL = "http://192.168.1.9:6789/";

    // Currencies
    public static List<Currency> currencies = new ArrayList<>();
    public static List<Transaction> transactions = new ArrayList<>();

    // Current user
    public static User currentUser = null;

    // Icons array
    public static int[] categoriesIcons = {
            R.drawable.ic_bills, R.drawable.ic_clothing, R.drawable.ic_education,
            R.drawable.ic_entertainment, R.drawable.ic_fitness, R.drawable.ic_food,
            R.drawable.ic_gifts, R.drawable.ic_health, R.drawable.ic_furniture,
            R.drawable.ic_pet, R.drawable.ic_shopping, R.drawable.ic_transportation,
            R.drawable.ic_travel, R.drawable.ic_others, R.drawable.ic_allowance,
            R.drawable.ic_award, R.drawable.ic_bonus, R.drawable.ic_dividend,
            R.drawable.ic_investment, R.drawable.ic_salary
    };

    public static int[] walletIcons = {
            R.drawable.ic_money_bill, R.drawable.ic_cash_register, R.drawable.ic_circle_dollar_to_slot,
            R.drawable.ic_bitcoin_sign, R.drawable.ic_coins, R.drawable.ic_comments_dollar,
            R.drawable.ic_credit_card, R.drawable.ic_hand_holding_dollar, R.drawable.ic_landmark,
            R.drawable.ic_salary_2, R.drawable.ic_money_bill_trend_up, R.drawable.ic_piggy_bank,
            R.drawable.ic_receipt, R.drawable.ic_wallet_2, R.drawable.ic_bonus_2,
    };
    // Categories
    public static List<Category> categories = new ArrayList<>();

    // Transaction Type
    public static final int INCOME_TRANSACTION_ID = 1;
    public static final int EXPENSE_TRANSACTION_ID = 2;
    public static final int TRANSFER_TRANSACTION_ID = 3;
    public static final int OTHER_CATEGORY_EXPENSE_TRANSACTION_ID = 23;
    // Tab Id
    public static final int INCOME_TAB_ID = 1;
    public static final int EXPENSE_TAB_ID = 2;
    public static final int TRANSFER_TAB_ID = 3;

    // wallet type
    public static final int TO_WALLET_TYPE = 1;
    public static final int FROM_WALLET_TYPE = 2;

    // Amount type
    public static final String ENTERING_AMOUNT = "entering_amount";
    public static final String ENTERING_FEE = "entering_fee";

    public static final String CREATING_TRANSACTION = "creating_transaction";
    public static final String UPDATING_TRANSACTION = "updating_transaction";

    public static void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
