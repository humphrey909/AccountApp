package com.neo.accountapp_3.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class MonthStatisticsViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragments;

    public MonthStatisticsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList list) {
        super(fragmentActivity);
        this.mFragments = list;
    }


    //프래그먼트 하나씩 보여주는 곳
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    //프래그먼트 갯수
    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
