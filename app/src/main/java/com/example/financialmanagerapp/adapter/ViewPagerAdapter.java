package com.example.financialmanagerapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagerapp.activity.fragment.main.CalendarTabFragment;
import com.example.financialmanagerapp.activity.fragment.main.StatisticTabFragment;
import com.example.financialmanagerapp.activity.fragment.main.TransactionTabFragment;
import com.example.financialmanagerapp.activity.fragment.main.WalletTabFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
//            case 1:
//                return new CalendarTabFragment();
            case 1:
                return new StatisticTabFragment();
            case 2:
                return new WalletTabFragment();
            default:
                return new TransactionTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;// Number of tabs
    }
}
