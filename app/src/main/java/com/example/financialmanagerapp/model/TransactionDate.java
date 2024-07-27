package com.example.financialmanagerapp.model;

import java.util.List;

public class TransactionDate {
    protected int dayOfMonth;
    protected int dayOfWeek;
    protected int monthOfYear;
    protected int year;

    protected List<Transaction> transactions;

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public int getYear() {
        return year;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public TransactionDate(Builder builder) {
        this.dayOfMonth = builder.dayOfMonth;
        this.dayOfWeek = builder.dayOfWeek;
        this.monthOfYear = builder.monthOfYear;
        this.year = builder.year;
        this.transactions = builder.transactions;
    }

    public static class Builder {
        private int dayOfMonth;
        private int dayOfWeek;
        private int monthOfYear;
        private int year;

        private List<Transaction> transactions;

        public Builder dayOfMonth(int dayOfMonth) {
            this.dayOfMonth = dayOfMonth;
            return this;
        }

        public Builder dayOfWeek(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public Builder monthOfYear(int monthOfYear) {
            this.monthOfYear = monthOfYear;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder transactions(List<Transaction> transactions) {
            this.transactions = transactions;
            return this;
        }

        public TransactionDate build() {return new TransactionDate(this); }
    }

}
