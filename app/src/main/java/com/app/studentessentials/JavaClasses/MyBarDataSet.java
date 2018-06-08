package com.app.studentessentials.JavaClasses;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class MyBarDataSet extends BarDataSet {


    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForXIndex(index).getVal() < (Integer.parseInt(GlobalVariables._BUDGET)- 1000)) // less than 95 green
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() < Integer.parseInt(GlobalVariables._BUDGET)) // less than 100 orange
            return mColors.get(1);
        else // greater or equal than 100 red
            return mColors.get(2);
    }

}