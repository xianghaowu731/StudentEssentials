package com.app.studentessentials.Models;

public class MonthlyBudgetModel {

    public String mMonth;
    public String mName;
    public String mDate;
    public String mRecurring;
    public String mAmount;
    public String mTotal;
    public boolean bExpand;

    public String enddate;

    public MonthlyBudgetModel(String month, String name, String mdate, String mrecurring, String mamount, String mtotal){
        this.mMonth = month;
        this.mDate = mdate;
        this.mName = name;
        this.mRecurring = mrecurring;
        this.mAmount = mamount;
        this.mTotal = mtotal;
        this.bExpand = false;
        this.enddate = "";
    }
}
