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

    public TransactionViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new Expense();
            case 2:
                return new Transfer();
            default:
                return new Income();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
