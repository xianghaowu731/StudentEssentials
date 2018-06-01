package com.app.studentessentials.JavaClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.studentessentials.Fragments.FragmentMonthlyPlanner;
import com.app.studentessentials.Fragments.FragmentShowDailyPlanner;
import com.app.studentessentials.Fragments.FragmentWeeklyPlanner;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentShowDailyPlanner tab1 = new FragmentShowDailyPlanner();
                return tab1;
            case 1:
                FragmentWeeklyPlanner tab2 = new FragmentWeeklyPlanner();
                return tab2;
            case 2:
                FragmentMonthlyPlanner tab3 = new FragmentMonthlyPlanner();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}