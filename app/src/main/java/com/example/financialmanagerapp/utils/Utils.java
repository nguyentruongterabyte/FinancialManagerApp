package com.example.financialmanagerapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.TransactionDate;
import com.example.financialmanagerapp.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    // Colors array
    public static int[] colors = {
            R.color.color_1, R.color.color_2, R.color.color_3,
            R.color.color_4, R.color.color_5, R.color.color_6,
            R.color.color_8, R.color.color_9, R.color.color_10,
            R.color.color_11, R.color.color_12, R.color.color_13,
            R.color.color_14, R.color.color_15, R.color.color_16,
            R.color.color_17, R.color.color_18, R.color.color_19,
            R.color.color_20, R.color.color_21, R.color.color_22,
            R.color.color_23, R.color.color_24
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

    public static void formattingImageBackground(Context context, ImageView imageView, String hexColor) {
        int color = Color.parseColor(hexColor);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        // Convert dp to px
        drawable.setCornerRadius(12 * context.getResources().getDisplayMetrics().density);
        drawable.setColor(color);

        imageView.setBackground(drawable);
    }

    public static Map<String, List<Transaction>> groupTransactionsByCategoryName(List<Transaction> transactions) {
        Map<String, List<Transaction>> transactionsByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            // Transfer transactions
            if (transaction.get_transaction_type_id() == Utils.TRANSFER_TRANSACTION_ID) {
                if (!transactionsByCategory.containsKey("Transfer")) {
                    transactionsByCategory.put("Transfer", new ArrayList<>());
                }

                Objects.requireNonNull(transactionsByCategory.get("Transfer")).add(transaction);
            } else { // other transactions
                String transactionName = transaction.getCategory().get_name();
                if (!transactionsByCategory.containsKey(transactionName)) {
                    transactionsByCategory.put(transactionName, new ArrayList<>());
                }

                Objects.requireNonNull(transactionsByCategory.get(transactionName)).add(transaction);
            }
        }

        return transactionsByCategory;
    }

    public static void addFeeTransactions(List<Transaction> transactions) {
        List<Transaction> feeTransactions = new ArrayList<>();
        for (Transaction transaction : transactions)
            if (transaction.get_fee() > 0) {
                Category category = new Category.Builder()
                        .id(Utils.OTHER_CATEGORY_EXPENSE_TRANSACTION_ID)
                        .name("Others")
                        .icon(13)
                        .color("#603C34")
                        .transactionTypeId(Utils.EXPENSE_TRANSACTION_ID)
                        .build();

                Transaction feeTransaction = new Transaction.Builder()
                        .category(category)
                        .categoryId(category.getId())
                        .date(transaction.get_date())
                        .wallet(transaction.getFrom_wallet())
                        .walletId(transaction.get_from_wallet_id())
                        .toWallet(transaction.getTo_wallet())
                        .toWalletId(transaction.get_to_wallet_id())
                        .description("Fee")
                        .amount(transaction.get_fee())
                        .transactionTypeId(Utils.EXPENSE_TRANSACTION_ID)
                        .updatedAt(transaction.getUpdated_at())
                        .isFeeTransaction(true)
                        .parent(transaction)
                        .build();
                feeTransactions.add(feeTransaction);
            }
        transactions.addAll(feeTransactions);
    }


    public static List<TransactionDate> groupTransactionsByDate(Context context, List<Transaction> transactions) {
        // Step 1: Create a map to group transaction by date
        Map<String, List<Transaction>> transactionsByDate = new HashMap<>();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        // Step 2: Iterate over transactions and group by date
        for (Transaction transaction : transactions) {

            Calendar calendar = TimerFormatter.getCalendar(transaction.get_date());
            String dateKey = dateFormatter.format(calendar.getTime());

            if (!transactionsByDate.containsKey(dateKey)) {
                transactionsByDate.put(dateKey, new ArrayList<>());
            }

            Objects.requireNonNull(transactionsByDate.get(dateKey)).add(transaction);

        }

        // step 3: convert the map to a list of TransactionDate objects
        List<TransactionDate> groupedTransactionDates = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : transactionsByDate.entrySet()) {
            String dateKey = entry.getKey();
            List<Transaction> groupedTransactions = entry.getValue();

            Calendar calendar = Calendar.getInstance();

            try {
                calendar.setTime(Objects.requireNonNull(dateFormatter.parse(dateKey)));
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            TransactionDate transactionDate = new TransactionDate.Builder()
                    .dayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
                    .dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                    .monthOfYear(calendar.get(Calendar.MONTH))
                    .year(calendar.get(Calendar.YEAR))
                    .transactions(groupedTransactions)
                    .build();
            groupedTransactionDates.add(transactionDate);
        }

        // step 4: Sort the list from latest to oldest date
        groupedTransactionDates.sort((o1, o2) -> {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(o1.getYear(), o1.getMonthOfYear(), o1.getDayOfMonth());

            Calendar cal2 = Calendar.getInstance();
            cal2.set(o2.getYear(), o2.getMonthOfYear(), o2.getDayOfMonth());

            // Compare in reverse order for latest to oldest
            return cal2.compareTo(cal1);

        });

        return groupedTransactionDates;
    }
}
