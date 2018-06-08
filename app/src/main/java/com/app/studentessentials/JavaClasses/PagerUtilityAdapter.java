package com.app.studentessentials.JavaClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.studentessentials.Fragments.FragmentMonthlyPlanner;
import com.app.studentessentials.Fragments.FragmentShowDailyPlanner;
import com.app.studentessentials.Fragments.FragmentUtilityCalculator;
import com.app.studentessentials.Fragments.FragmentUtilityElectricity;
import com.app.studentessentials.Fragments.FragmentUtilityGas;
import com.app.studentessentials.Fragments.FragmentWeeklyPlanner;

public class PagerUtilityAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerUtilityAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentUtilityElectricity tab1 = new FragmentUtilityElectricity();
                return tab1;
            case 1:
                FragmentUtilityGas tab2 = new FragmentUtilityGas();
                return tab2;
            case 2:
                FragmentUtilityCalculator tab3 = new FragmentUtilityCalculator();
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