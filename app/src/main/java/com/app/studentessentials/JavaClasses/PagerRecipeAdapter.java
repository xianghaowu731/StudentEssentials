package com.app.studentessentials.JavaClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.studentessentials.Fragments.FragmentAllRecepies;
import com.app.studentessentials.Fragments.FragmentFavoriteReceipes;
import com.app.studentessentials.Fragments.FragmentMyReceipes;
import com.app.studentessentials.Fragments.FragmentUtilityCalculator;
import com.app.studentessentials.Fragments.FragmentUtilityElectricity;
import com.app.studentessentials.Fragments.FragmentUtilityGas;

public class PagerRecipeAdapter extends FragmentStatePagerAdapter
{
    int mNumOfTabs;

    public PagerRecipeAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentAllRecepies tab1 = new FragmentAllRecepies();
                return tab1;
            case 1:
                FragmentMyReceipes tab2 = new FragmentMyReceipes();
                return tab2;
            case 2:
                FragmentFavoriteReceipes tab3 = new FragmentFavoriteReceipes();
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