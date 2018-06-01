package com.app.studentessentials;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.app.studentessentials.Adapters.TodoTodayAdapter;
import com.app.studentessentials.Adapters.TodoUpcomingAdapter;
import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.Models.ModelTask;

public class ToDo extends AppCompatActivity {
    ImageView btn_back, img_add;
    RecyclerView recyclerView, recyclerView_upcoming;
    List<ModelTask> datalist, upcominglist;
    TodoTodayAdapter todayAdapter;
    TodoUpcomingAdapter upcomingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        img_add = (ImageView) findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToDo.this , AddTaskActivity.class));
            }
        });

        initLayout();
    }

    private void loadData(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        String currentDate = mYear + "-" + convStr(mMonth) + "-" + convStr(mDay) + " " + convStr(mHour) + ":" + convStr(mMinute);
        String todate = mYear + "-" + convStr(mMonth) + "-" + convStr(mDay);
        String selectQuery = "SELECT * FROM task_event WHERE task_date" + " = '" + todate + "' AND userkey = '" + MyApp.getInstance().myProfile.userkey + "' ORDER BY task_time ASC";

        SQLiteDatabase db = Functions.databaseInit(ToDo.this);
        db.execSQL("CREATE TABLE IF NOT EXISTS task_event(id INTEGER PRIMARY KEY AUTOINCREMENT, task_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, reminder_status VARCHAR, checked VARCHAR, userkey VARCHAR);");

        Cursor cursor = db.rawQuery(selectQuery, null);
        String task_name, task_desc, task_date, task_time, task_has, task_checked;
        if (cursor.moveToFirst()) {
            datalist = new ArrayList<>();
            do {

                task_name = cursor.getString(1);
                task_desc = cursor.getString(2);
                task_date = cursor.getString(3);
                task_time = cursor.getString(4);
                task_has = cursor.getString(5);
                task_checked = cursor.getString(6);
                String dbtime = task_date + " " + task_time;

                ModelTask one = new ModelTask(task_name, task_desc, task_date, task_time, task_has.equals("1"));
                one.mId = String.valueOf(cursor.getInt(0));
                //if(currentDate.compareTo(dbtime)>=0){
                 //   one.isCompleted = true;
                //}
                if(task_checked.equals("1")){
                    one.isCompleted = true;
                }
                datalist.add(one);

            } while (cursor.moveToNext());
        }
        if(todayAdapter != null){
            todayAdapter.setDataList(datalist);
            todayAdapter.notifyDataSetChanged();
        }

        selectQuery = "SELECT * FROM task_event WHERE task_date" + " > '" + todate + "' AND userkey = '" + MyApp.getInstance().myProfile.userkey + "' ORDER BY task_date , task_time ASC";

        Cursor cursor1 = db.rawQuery(selectQuery, null);
        if (cursor1.moveToFirst()) {
            upcominglist = new ArrayList<>();
            do {

                task_name = cursor1.getString(1);
                task_desc = cursor1.getString(2);
                task_date = cursor1.getString(3);
                task_time = cursor1.getString(4);
                task_has = cursor1.getString(5);
                task_checked = cursor1.getString(6);

                ModelTask one = new ModelTask(task_name, task_desc, task_date, task_time, task_has.equals("1"));
                one.mId = String.valueOf(cursor1.getInt(0));
                if(task_checked.equals("1")){
                    one.isCompleted = true;
                }
                upcominglist.add(one);

            } while (cursor1.moveToNext());
        }
        if(upcomingAdapter != null){
            upcomingAdapter.setDataList(upcominglist);
            upcomingAdapter.notifyDataSetChanged();
        }
    }

    private void deleteTaskFromDB(String pos){
        String selectQuery = "DELETE FROM task_event WHERE id" + " = '" + pos + "'";

        SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
        db.execSQL("DELETE FROM task_event WHERE id = '" + pos + "';");
    }

    private void updateDBItem(String dbID, String value){
        SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
        db.execSQL("UPDATE task_event SET checked = '" + value + "' WHERE id = '" + dbID + "';");
    }

    private void initLayout(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        datalist = new ArrayList<>();
        todayAdapter = new TodoTodayAdapter(datalist, getApplicationContext());
        todayAdapter.setOnItemClickListener(new TodoTodayAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                ModelTask one = datalist.get(position);
                one.isCompleted = !one.isCompleted;
                if(one.isCompleted)
                    updateDBItem(one.mId, "1");
                else
                    updateDBItem(one.mId, "0");
                todayAdapter.setDataList(datalist);
                todayAdapter.notifyDataSetChanged();
                return 0;
            }
        });

        todayAdapter.setOnItemDelClickListener(new TodoTodayAdapter.OnItemDelClickListener() {
            @Override
            public int onItemDelClick(int position) {
                ModelTask one = datalist.get(position);
                deleteTaskFromDB(one.mId);
                datalist.remove(position);
                todayAdapter.setDataList(datalist);
                todayAdapter.notifyDataSetChanged();
                return 0;
            }
        });
        recyclerView.setAdapter(todayAdapter);

        recyclerView_upcoming = (RecyclerView) findViewById(R.id.recyclerView_upcoming);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView_upcoming.setLayoutManager(layoutManager1);
        upcominglist = new ArrayList<>();
        upcomingAdapter = new TodoUpcomingAdapter(upcominglist, getApplicationContext());
        upcomingAdapter.setOnItemClickListener(new TodoUpcomingAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                ModelTask one = upcominglist.get(position);
                one.isCompleted = !one.isCompleted;
                if(one.isCompleted)
                    updateDBItem(one.mId, "1");
                else
                    updateDBItem(one.mId, "0");
                upcomingAdapter.setDataList(upcominglist);
                upcomingAdapter.notifyDataSetChanged();
                return 0;
            }
        });
        upcomingAdapter.setOnItemDelClickListener(new TodoUpcomingAdapter.OnItemDelClickListener() {
            @Override
            public int onItemDelClick(int position) {
                ModelTask one = upcominglist.get(position);
                deleteTaskFromDB(one.mId);
                upcominglist.remove(position);
                upcomingAdapter.setDataList(upcominglist);
                upcomingAdapter.notifyDataSetChanged();
                return 0;
            }
        });
        recyclerView_upcoming.setAdapter(upcomingAdapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String convStr(int num){
        String retStr = "";
        if(num > 9){
            retStr = "" + num;
        } else {
            retStr = "0"+num;
        }
        return retStr;
    }
}
