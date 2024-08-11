package com.example.financialmanagerapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagerapp.activity.fragment.structure.Expense;
import com.example.financialmanagerapp.activity.fragment.structure.Income;

public class StructureViewPagerAdapter extends FragmentStateAdapter {
    protected int walletId;
    public StructureViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int walletId) {
        super(fragmentActivity);
        this.walletId = walletId;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new Expense(walletId);
        }
        return new Income(walletId);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
