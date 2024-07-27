package com.example.financialmanagerapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagerapp.activity.fragment.transaction.Expense;
import com.example.financialmanagerapp.activity.fragment.transaction.Income;
import com.example.financialmanagerapp.activity.fragment.transaction.Transfer;

public class TransactionViewPagerAdapter extends FragmentStateAdapter
{

    protected String action;

    public TransactionViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String action) {
        super(fragmentActivity);
        this.action = action;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new Expense(action);
            case 2:
                return new Transfer(action);
            default:
                return new Income(action);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
