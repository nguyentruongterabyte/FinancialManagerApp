package com.example.financialmanagerapp.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class Transaction implements Serializable {
    protected int id;
    protected Timestamp updated_at;
    @SerializedName("_amount")
    protected Double _amount;
    @SerializedName("_description")
    protected String _description;
    @SerializedName("_wallet_id")
    protected int _wallet_id;
    protected Wallet wallet;
    @SerializedName("_category_id")
    protected Integer _category_id;
    @SerializedName("_memo")
    protected String _memo;
    @SerializedName("_from_wallet_id")
    protected Integer _from_wallet_id;
    protected Wallet from_wallet;
    @SerializedName("_to_wallet_id")
    protected Integer _to_wallet_id;
    protected Wallet to_wallet;
    @SerializedName("_fee")
    protected double _fee;
    @SerializedName("_transaction_type_id")
    protected int _transaction_type_id;
    @SerializedName("_date")
    protected Timestamp _date;

    protected Category category;
    protected boolean isFeeTransaction = false;
    protected Transaction parent;


    public Transaction() {
    }

    public Transaction(Transaction transaction) {
        this.id = transaction.id;
        this.updated_at = transaction.updated_at;
        this._amount = transaction._amount;
        this._description = transaction._description;
        this._wallet_id = transaction._wallet_id;
        this.wallet = transaction.wallet;
        this.category = transaction.category;
        this._category_id = transaction._category_id;
        this._memo = transaction._memo;
        this._from_wallet_id = transaction._from_wallet_id;
        this.from_wallet = transaction.from_wallet;
        this._to_wallet_id = transaction._to_wallet_id;
        this.to_wallet = transaction.to_wallet;
        this._fee = transaction._fee;
        this._transaction_type_id = transaction._transaction_type_id;
        this._date = transaction._date;
        this.isFeeTransaction = transaction.isFeeTransaction;
        this.parent = transaction.parent;
    }

    public static void updateTransactionInList(Transaction transaction, List<Transaction> transactions) {

        for (int i = 0; i < transactions.size(); i++) {
            Transaction currentTransaction = transactions.get(i);
            if (currentTransaction.getId() == transaction.getId()) {
                transactions.set(i, transaction);
                break;
            }
        }

    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public Timestamp get_date() {
        return _date;
    }

    public void set_date(Timestamp _date) {
        this._date = _date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double get_amount() {
        return _amount;
    }

    public void set_amount(Double _amount) {
        this._amount = _amount;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public int get_wallet_id() {
        return _wallet_id;
    }

    public void set_wallet_id(int _wallet_id) {
        this._wallet_id = _wallet_id;
    }

    public Integer get_category_id() {
        return _category_id;
    }

    public void set_category_id(Integer _category_id) {
        this._category_id = _category_id;
    }

    public Category getCategory() {
        return category;
    }

    public String get_memo() {
        return _memo;
    }

    public void set_memo(String _memo) {
        this._memo = _memo;
    }

    public Integer get_from_wallet_id() {
        return _from_wallet_id;
    }

    public void set_from_wallet_id(int _from_wallet_id) {
        this._from_wallet_id = _from_wallet_id;
    }

    public int get_to_wallet_id() {
        return _to_wallet_id;
    }

    public void set_to_wallet_id(int _to_wallet_id) {
        this._to_wallet_id = _to_wallet_id;
    }

    public double get_fee() {
        return _fee;
    }

    public void set_fee(double _fee) {
        this._fee = _fee;
    }

    public int get_transaction_type_id() {
        return _transaction_type_id;
    }

    public void set_transaction_type_id(int _transaction_type_id) {
        this._transaction_type_id = _transaction_type_id;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setFrom_wallet(Wallet from_wallet) {
        this.from_wallet = from_wallet;
    }

    public void setTo_wallet(Wallet to_wallet) {
        this.to_wallet = to_wallet;
    }

    public Wallet getFrom_wallet() {
        return from_wallet;
    }

    public Wallet getTo_wallet() {
        return to_wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Transaction getParent() {
        return parent;
    }

    public boolean isFeeTransaction() {
        return isFeeTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String thisFormattedDate = dateFormat.format(this.get_date());
        String thatFormattedDate = dateFormat.format(that.get_date());

        return get_wallet_id() == that.get_wallet_id() &&
                Double.compare(that.get_fee(), get_fee()) == 0 &&
                Objects.equals(get_amount(), that.get_amount()) &&
                Objects.equals(get_description(), that.get_description()) &&
                Objects.equals(get_category_id(), that.get_category_id()) &&
                Objects.equals(get_memo(), that.get_memo()) &&
                Objects.equals(get_from_wallet_id(), that.get_from_wallet_id()) &&
                Objects.equals(get_to_wallet_id(), that.get_to_wallet_id()) &&
                thisFormattedDate.equals(thatFormattedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_amount(), get_description(), get_wallet_id(), get_category_id(), get_memo(), get_from_wallet_id(), get_to_wallet_id(), get_fee(), get_date());
    }

    public Transaction(Builder builder) {
        this.id = builder.id;
        this._amount = builder._amount;
        this.category = builder.category;
        this._date = builder._date;
        this._transaction_type_id = builder._transaction_type_id;
        this.wallet = builder.wallet;
        this._wallet_id = builder._wallet_id;
        this.to_wallet = builder.to_wallet;
        this._to_wallet_id = builder._to_wallet_id;
        this.updated_at = builder.updated_at;
        this._description = builder._description;
        this._category_id = builder._category_id;
        this.isFeeTransaction = builder.isFeeTransaction;
        this.parent = builder.parent;
    }

    public static class Builder {
        protected int id;
        private Double _amount;
        private int _transaction_type_id;
        private Timestamp _date;
        private Timestamp updated_at;

        private Category category;
        private Wallet wallet;
        private int _wallet_id;
        private Wallet to_wallet;
        private Integer _to_wallet_id;

        private String _description;
        private Integer _category_id;
        private boolean isFeeTransaction;
        private Transaction parent;

        public Builder parent(Transaction parent) {
            this.parent = parent;
            return this;
        }

        public Builder toWallet(Wallet to_wallet) {
            this.to_wallet = to_wallet;
            return this;
        }

        public Builder toWalletId(int _to_wallet_id) {
            this._to_wallet_id = _to_wallet_id;
            return this;
        }

        public Builder isFeeTransaction(boolean isFeeTransaction) {
            this.isFeeTransaction = isFeeTransaction;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder walletId(int _wallet_id) {
            this._wallet_id = _wallet_id;
            return this;
        }

        public Builder categoryId(Integer _category_id) {
            this._category_id = _category_id;
            return this;
        }

        public Builder description(String _description) {
            this._description = _description;
            return this;
        }

        public Builder amount(Double _amount) {
            this._amount = _amount;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder date(Timestamp _date) {
            this._date = _date;
            return this;
        }

        public Builder transactionTypeId(int _transaction_type_id) {
            this._transaction_type_id = _transaction_type_id;
            return this;
        }

        public Builder wallet(Wallet wallet) {
            this.wallet = wallet;
            return this;
        }

        public Builder updatedAt(Timestamp updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

}
