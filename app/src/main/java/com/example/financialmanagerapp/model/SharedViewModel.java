package com.example.financialmanagerapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> year = new MutableLiveData<>();
    private final MutableLiveData<Integer> endYear = new MutableLiveData<>();
    private final MutableLiveData<Integer> month = new MutableLiveData<>();
    private final MutableLiveData<Integer> endMonth = new MutableLiveData<>();
    private final MutableLiveData<Integer> day = new MutableLiveData<>();
    private final MutableLiveData<Integer> endDay = new MutableLiveData<>();
    private final MutableLiveData<Integer> hour = new MutableLiveData<>();
    private final MutableLiveData<Integer> minute = new MutableLiveData<>();
    private final MutableLiveData<Double> amount = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<String> memo = new MutableLiveData<>();
    private final MutableLiveData<Double> fee = new MutableLiveData<>();
    private final MutableLiveData<Category> category = new MutableLiveData<>();// income
    private final MutableLiveData<Category> expenseCategory = new MutableLiveData<>(); // expense
    private final MutableLiveData<Wallet> wallet = new MutableLiveData<>(new Wallet());
    private final MutableLiveData<Wallet> fromWallet = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSetDateDone = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSetCategoriesDone = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSetWalletsDone = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> categoryIds = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> walletIds = new MutableLiveData<>();

    public void setWalletIds(List<Integer> _walletIds) {
        walletIds.setValue(_walletIds);
    }
    public LiveData<List<Integer>> getWalletIds() {
        return walletIds;
    }
    public void setCategoryIds(List<Integer> _categoryIds) {
        categoryIds.setValue(_categoryIds);
    }
    public LiveData<List<Integer>> getCategoryIds() {
        return categoryIds;
    }
    public LiveData<Boolean> getIsSetWalletsDone() {
        return isSetWalletsDone;
    }

    public void setIsSetWalletsDone(boolean _isDone) {
        isSetWalletsDone.setValue(_isDone);
    }
    public LiveData<Boolean> getIsSetCategoriesDone() {
        return isSetCategoriesDone;
    }

    public void setIsSetCategoriesDone(boolean _isDone) {
        isSetCategoriesDone.setValue(_isDone);
    }

    public LiveData<Boolean> getIsSetDateDone() {
        return isSetDateDone;
    }

    public void setIsSetDateDone(boolean _isDone) {
        isSetDateDone.setValue(_isDone);
    }

    public LiveData<Integer> getYear() {
        return year;
    }

    public LiveData<Integer> getEndYear() {
        return endYear;
    }

    public LiveData<Integer> getMonth() {
        return month;
    }

    public LiveData<Integer> getEndMonth() {
        return endMonth;
    }

    public LiveData<Integer> getDay() {
        return day;
    }

    public LiveData<Integer> getEndDay() {
        return endDay;
    }

    public void setYear(int _year) {
        year.setValue(_year);
    }

    public void setEndYear(int _endYear) {
        endYear.setValue(_endYear);
    }

    public void setMonth(int _month) {
        month.setValue(_month);
    }

    public void setEndMonth(int _endMonth) {
        endMonth.setValue(_endMonth);
    }

    public void setDay(int _day) {
        day.setValue(_day);
    }

    public void setEndDay(int _endDay) {
        endDay.setValue(_endDay);
    }

    public void setDate(int _year, int _month, int _day) {
        year.setValue(_year);
        month.setValue(_month);
        day.setValue(_day);
    }

    public void setTime(int _hour, int _minute) {
        hour.setValue(_hour);
        minute.setValue(_minute);
    }

    public LiveData<Integer> getHour() {
        return hour;
    }

    public LiveData<Integer> getMinute() {
        return minute;
    }


    public void setAmount(double _amount) {
        amount.setValue(_amount);
    }

    public LiveData<Double> getAmount() {
        return amount;
    }

    public void setDescription(String _description) {
        description.setValue(_description);
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public void setMemo(String _memo) {
        memo.setValue(_memo);
    }

    public LiveData<String> getMemo() {
        return memo;
    }

    public void setFee(Double _fee) {
        fee.setValue(_fee);
    }

    public LiveData<Double> getFee() {
        return fee;
    }

    // income
    public void setCategory(Category _category) {
        category.setValue(_category);
    }

    public LiveData<Category> getCategory() {
        return category;
    }

    // expense
    public void setExpenseCategory(Category _category) {
        expenseCategory.setValue(_category);
    }

    public LiveData<Category> getExpenseCategory() {
        return expenseCategory;
    }
    public void setWallet(Wallet _wallet) {
        wallet.setValue(_wallet);
    }

    public LiveData<Wallet> getWallet() {
        return wallet;
    }
    public void setFromWallet(Wallet _wallet) {
        fromWallet.setValue(_wallet);
    }

    public LiveData<Wallet> getFromWallet() {
        return fromWallet;
    }

}
