package com.example.financialmanagerapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagerapp.activity.fragment.structure.Expense;
import com.example.financialmanagerapp.activity.fragment.structure.Income;

public class StructureViewPagerAdapter extends FragmentStateAdapter {
    public StructureViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new Expense();
        }
        return new Income();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
