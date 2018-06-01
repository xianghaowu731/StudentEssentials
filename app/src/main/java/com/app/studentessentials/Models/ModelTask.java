package com.app.studentessentials.Models;

public class ModelTask {
    public String mId;
    public String mName;

    public String mDesc;
    public String mDate;
    public String mTime;
    public Boolean hasReminder;

    public Boolean isCompleted;

    public ModelTask(String name, String desc, String mdate, String mtime, Boolean hasReminder){
        this.mName = name;
        this.mDesc = desc;
        this.mDate = mdate;
        this.mTime = mtime;
        this.hasReminder = hasReminder;
        this.isCompleted = false;
    }

}
