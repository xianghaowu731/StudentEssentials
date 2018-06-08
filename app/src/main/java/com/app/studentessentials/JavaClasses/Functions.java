package com.app.studentessentials.JavaClasses;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Functions
{
    public static SQLiteDatabase databaseInit(Context context)
    {
        SQLiteDatabase db;
        db=context.openOrCreateDatabase("Student_Essentials", Context.MODE_PRIVATE, null);

        return db;
    }
}
