package com.example.financialmanagerapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> year = new MutableLiveData<>();
    private final MutableLiveData<Integer> month = new MutableLiveData<>();
    private final MutableLiveData<Integer> day = new MutableLiveData<>();
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

    public LiveData<Integer> getYear() {
        return year;
    }

    public LiveData<Integer> getMonth() {
        return month;
    }

    public LiveData<Integer> getDay() {
        return day;
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
