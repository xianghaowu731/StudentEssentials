package com.app.studentessentials;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.Models.ModelTask;
import com.app.studentessentials.Models.UserModel;
import com.app.studentessentials.Utils.MyDateTimeUtils;

public class AddTaskActivity extends AppCompatActivity {

    ImageView left_arrow_icon;
    EditText et_name, et_desp;
    TextView tv_selDate, tv_selTime;
    Button btn_submit;
    SwitchCompat switch_reminder;

    int mYear, mMonth, mDay, mHour, mMinute;

    int alarmYear = 0;
    int alarmMonth = 0;
    int alarmDay = 0;
    int alarmHour = 0;
    int alarmMinuts = 0;

    MyDateTimeUtils dateTimeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        left_arrow_icon = (ImageView) findViewById(R.id.left_arrow_icon);
        left_arrow_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initLayout();
        dateTimeUtils = new MyDateTimeUtils();
    }

    private void initLayout(){
        et_name = (EditText) findViewById(R.id.et_taskname);
        et_desp = (EditText) findViewById(R.id.et_taskdesp);
        tv_selDate = (TextView) findViewById(R.id.tv_todo_date);
        tv_selTime = (TextView) findViewById(R.id.tv_todo_time);
        btn_submit = (Button) findViewById(R.id.btn_todo_submit);
        switch_reminder = (SwitchCompat) findViewById(R.id.switchButton);
        tv_selDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, R.style.DialogTheme ,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                tv_selDate.setText(convStr(dayOfMonth) + "-" + convStr(monthOfYear + 1) + "-" + year);

                                alarmYear = year;
                                alarmMonth = monthOfYear + 1;
                                alarmDay = dayOfMonth;

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate((System.currentTimeMillis() - 1000) );
                datePickerDialog.show();
            }
        });

        tv_selTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,  R.style.DialogTheme ,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                tv_selTime.setText(convStr(hourOfDay) + ":" + convStr(minute));
                                // System.out.println(hourOfDay + ":" + minute);
                                alarmHour = hourOfDay;
                                alarmMinuts = minute;

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcSubmit();
            }
        });
        Calendar cal = Calendar.getInstance();
        alarmYear = cal.get(Calendar.YEAR);
        alarmMonth = cal.get(Calendar.MONTH) + 1;
        alarmDay = cal.get(Calendar.DAY_OF_MONTH);
        alarmHour = cal.get(Calendar.HOUR_OF_DAY);
        alarmMinuts = cal.get(Calendar.MINUTE);
    }

    private void funcSubmit(){
        if(et_name.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Task name is incorrect!", Toast.LENGTH_SHORT).show();
            return;
        }

        String choosedate = alarmYear + "-" + convStr(alarmMonth) + "-" + convStr(alarmDay);
        String sel_time = convStr(alarmHour) + ":" + convStr(alarmMinuts);
        ModelTask oneTask = new ModelTask(et_name.getText().toString(), et_desp.getText().toString(), choosedate, sel_time, switch_reminder.isChecked());

        String reminder_flag = "0";
        if(oneTask.hasReminder){
            reminder_flag = "1";
        }
        SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
        db.execSQL("CREATE TABLE IF NOT EXISTS task_event(id INTEGER PRIMARY KEY AUTOINCREMENT, task_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, reminder_status VARCHAR, checked VARCHAR, userkey VARCHAR);");

        UserModel myModel = MyApp.getInstance().myProfile;

        db.execSQL("INSERT INTO task_event (task_name, description, task_date, task_time , reminder_status, checked, userkey) " +
                "VALUES('" + oneTask.mName + "','" + oneTask.mDesc + "','" + oneTask.mDate + "','" + oneTask.mTime + "','" + reminder_flag + "' , '0', '"+ myModel.userkey +"');");

        db.close();

        String alarmtime = alarmYear + "-" + convStr(alarmMonth) + "-" + convStr(alarmDay) + " " + convStr(alarmHour) + ":" + convStr(alarmMinuts) + ":00";
        long newRowId= System.currentTimeMillis();

        dateTimeUtils.ScheduleNotification(dateTimeUtils.getNotification(oneTask.mName, AddTaskActivity.this),
                AddTaskActivity.this, Long.toString(newRowId), alarmtime, false, "");
        finish();
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
