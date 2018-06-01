package com.app.studentessentials;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Utils.MyDateTimeUtils;
import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class AddPlanner extends AppCompatActivity implements View.OnClickListener{
    TextView txt_date_picker,txt_time_picker;
    ImageView txt_eventcolor;
    String delivery_date, delivery_time;
    LinearLayout lnr_custom, lnr_flight, lnr_train, lnr_shuttle, lnr_date , lnr_birthday, lnr_memo , lnr_meeting, lnr_reservation,
            lnr_debit, lnr_film ;
    EditText edt_event , edt_location, edt_event_descr ;
    Spinner spinner, spinner_day, spinner_gap;
    Switch swtch_reminder;
    Button btn_add;
    int reminder_flag = 0;
    ImageView btn_back;
    String root_id = "", db_key = "";
    int mYear, mMonth, mDay, mHour, mMinute;

    static int alarmYear = 0;
    static int alarmMonth = 0;
    static int alarmDay = 0;
    static int alarmHour = 0;
    static int alarmMinuts = 0;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    MyDateTimeUtils dateTimeUtils;
    int eventcolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planner);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        intilizeView();
        viewOnClick();
        dateTimeUtils = new MyDateTimeUtils();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fomeValidation()){
                    addEventOnFirebase();
                }
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("Never");
        categories.add(GlobalVariables.EVENT_TYPE_DAILY);
        categories.add(GlobalVariables.EVENT_TYPE_WEEKLY);
        categories.add(GlobalVariables.EVENT_TYPE_MONTHLY);
        categories.add(GlobalVariables.EVENT_TYPE_YEARLY);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddPlanner.this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //spinner_gap.setVisibility(View.GONE);
        List<String> gap = new ArrayList<String>();
        gap.add("Select Alarm Gap");
        gap.add(GlobalVariables.EVENT_NOTI_10MIN);
        gap.add(GlobalVariables.EVENT_NOTI_30MIN);
        gap.add(GlobalVariables.EVENT_NOTI_1HOUR);
        gap.add(GlobalVariables.EVENT_NOTI_2HOURS);
        gap.add(GlobalVariables.EVENT_NOTI_1DAY);
        gap.add(GlobalVariables.EVENT_NOTI_2DAYS);
        // Creating adapter for spinner
        ArrayAdapter<String> gapAdapter = new ArrayAdapter<String>(AddPlanner.this, android.R.layout.simple_spinner_item, gap);
        // Drop down layout style - list view with radio button
        gapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_gap.setAdapter(gapAdapter);

        spinner_day.setVisibility(View.GONE);
        List<String> day = new ArrayList<String>();
        day.add("Select Event Day");
        day.add("monday");
        day.add("tuesday");
        day.add("wednesday");
        day.add("thursday");
        day.add("friday");
        day.add("saturday");
        day.add("sunday");

        // Creating adapter for spinner
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(AddPlanner.this, android.R.layout.simple_spinner_item, day);
        // Drop down layout style - list view with radio button
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_day.setAdapter(dayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner.getSelectedItem().toString().equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
                    spinner_day.setVisibility(View.VISIBLE);
                }
                else{
                    spinner_day.setVisibility(View.GONE);
                }

                /*if(i != 0){
                    spinner_gap.setVisibility(View.VISIBLE);
                } else{
                    spinner_gap.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /******************************** get previous fragment data ****************************************/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String event_name,event_description,location, event_type, date, time, reminder;
            root_id = bundle.getString("root_id", "");

            db_key = bundle.getString("id", "");

            event_name = bundle.getString("event_name", "");
            event_description = bundle.getString("event_description", "");
            location = bundle.getString("location", "");
            event_type = bundle.getString("event_type", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            reminder = bundle.getString("reminder", "");
            String event_day = bundle.getString("event_day", "");
            String alarm_gap = bundle.getString("alarm_gap", "");
            String color_str = bundle.getString("event_color", "");
            if(color_str.length() == 0){
                eventcolor = android.R.color.holo_blue_light;
            } else {
                eventcolor = Integer.parseInt(color_str);
                txt_eventcolor.setImageResource(eventcolor);
            }
            if (reminder.equals("1"))
            {
                swtch_reminder.setChecked(true);
                reminder_flag = 1;
            }

            edt_event.setText(event_name);
            edt_event_descr.setText(event_description);
            edt_location.setText(location);

            if(!db_key.equals(""))
                btn_add.setText("UPDATE");

            if(event_type.equals(GlobalVariables.EVENT_TYPE_DAILY)){
                spinner.setSelection(1);
            }
            else if(event_type.equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
                spinner.setSelection(2);
            }
            else if(event_type.equals(GlobalVariables.EVENT_TYPE_MONTHLY)){
                spinner.setSelection(3);
            }

            if(event_day.equals("monday")){
                spinner_day.setSelection(1);
            } else if(event_day.equals("tuesday")){
                spinner_day.setSelection(2);
            } else if(event_day.equals("wednesday")){
                spinner_day.setSelection(3);
            } else if(event_day.equals("thursday")){
                spinner_day.setSelection(4);
            } else if(event_day.equals("friday")){
                spinner_day.setSelection(5);
            } else if(event_day.equals("saturday")){
                spinner_day.setSelection(6);
            } else if(event_day.equals("sunday")){
                spinner_day.setSelection(7);
            }

            if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_10MIN)){
                spinner_gap.setSelection(1);
            } else if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_30MIN)){
                spinner_gap.setSelection(2);
            } else if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_1HOUR)){
                spinner_gap.setSelection(3);
            } else if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_2HOURS)){
                spinner_gap.setSelection(4);
            } else if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_1DAY)){
                spinner_gap.setSelection(5);
            } else if(alarm_gap.equals(GlobalVariables.EVENT_NOTI_2DAYS)){
                spinner_gap.setSelection(6);
            }

            txt_date_picker.setText(date);
            txt_time_picker.setText(time);

        }
      /****************************** end ***********************************/

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void intilizeView(){
        txt_eventcolor = (ImageView) findViewById(R.id.txt_eventcolor);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        lnr_custom = (LinearLayout)findViewById(R.id.lnr_custom);
        lnr_flight = (LinearLayout)findViewById(R.id.lnr_flight);
        lnr_train = (LinearLayout)findViewById(R.id.lnr_train);
        lnr_shuttle = (LinearLayout)findViewById(R.id.lnr_shuttle);
        lnr_date = (LinearLayout)findViewById(R.id.lnr_date);
        lnr_birthday = (LinearLayout)findViewById(R.id.lnr_birthday);
        lnr_memo = (LinearLayout)findViewById(R.id.lnr_memo);
        lnr_meeting = (LinearLayout)findViewById(R.id.lnr_meeting);
        lnr_reservation = (LinearLayout)findViewById(R.id.lnr_reservation);
        lnr_debit = (LinearLayout)findViewById(R.id.lnr_debit);
        lnr_film = (LinearLayout)findViewById(R.id.lnr_film);

        edt_event =(EditText) findViewById(R.id.edt_event);
        edt_event_descr =(EditText)findViewById(R.id.edt_event_descr);
        edt_location =(EditText)findViewById(R.id.edt_location);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner_day = (Spinner)findViewById(R.id.spinner_day);
        spinner_gap = (Spinner) findViewById(R.id.spinner_gap);

        txt_date_picker = findViewById(R.id.txt_date_picker);
        txt_time_picker = findViewById(R.id.txt_time_picker);

        swtch_reminder = (Switch) findViewById(R.id.swtch_reminder);

        btn_add = (Button) findViewById(R.id.btn_add);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        txt_eventcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColorDlg();
            }
        });

        eventcolor = android.R.color.holo_blue_light;
    }

    public void viewOnClick()
    {
        lnr_custom.setOnClickListener(this);
        lnr_flight.setOnClickListener(this);
        lnr_train.setOnClickListener(this);
        lnr_shuttle.setOnClickListener(this);
        lnr_date.setOnClickListener(this);
        lnr_birthday.setOnClickListener(this);
        lnr_memo.setOnClickListener(this);
        lnr_meeting.setOnClickListener(this);
        lnr_reservation.setOnClickListener(this);
        lnr_debit.setOnClickListener(this);
        lnr_film.setOnClickListener(this);

        txt_date_picker.setOnClickListener(this);
        txt_time_picker.setOnClickListener(this);
    }

    public boolean fomeValidation()
    {
        boolean flag = true;
        if(edt_event.getText().toString().equals("") || edt_event_descr.getText().toString().equals("")|| spinner.getSelectedItem().toString().equals("None")){
            flag = false;
            Toast.makeText(this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
        }
        else if(txt_date_picker.getText().toString().trim().equals("")){
            flag = false;
            Toast.makeText(this, "Please select event date..!", Toast.LENGTH_SHORT).show();
        }else if(txt_time_picker.getText().toString().trim().equals("")){
            flag = false;
            Toast.makeText(this, "Please select event time..!", Toast.LENGTH_SHORT).show();
        }
        else if(spinner.getSelectedItem().toString().equals("Select Event Type")){
            flag = false;
            Toast.makeText(this, "Please select event Type..!", Toast.LENGTH_SHORT).show();
        }
        if (swtch_reminder.isChecked()){
            reminder_flag = 1 ;
        }
        else{
            reminder_flag = 0 ;
        }

        return flag ;
    }


    @Override
    public void onClick(View view) {

        if (view == lnr_custom){
            lnr_custom.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));

            edt_event.setText("Custom");
        }
        else if (view == lnr_flight){
            lnr_flight.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Flight");
        }
        else if (view == lnr_train){
            lnr_train.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Train");
        }
        else if (view == lnr_shuttle){
            lnr_shuttle.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Shuttle");
        }
        else if (view == lnr_date){
            lnr_date.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Date");
        }
        else if (view == lnr_birthday){
            lnr_birthday.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Birthday");
        }
        else if (view == lnr_memo){
            lnr_memo.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Memo");
        }
        else if (view == lnr_meeting){
            lnr_meeting.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Meeting");
        }
        else if (view == lnr_reservation){
            lnr_reservation.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Reservation");
        }
        else if (view == lnr_debit){
            lnr_debit.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_film.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Debit");
        }
        else if (view == lnr_film){
            lnr_film.setBackgroundColor(Color.parseColor("#caffd1"));
            lnr_custom.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_flight.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_train.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_shuttle.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_date.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_birthday.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_memo.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_meeting.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_reservation.setBackgroundColor(Color.parseColor("#d9ebf7"));
            lnr_debit.setBackgroundColor(Color.parseColor("#d9ebf7"));
            edt_event.setText("Film");
        }

        else if (view == txt_date_picker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme ,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txt_date_picker.setText(year + "-" + convStr(monthOfYear + 1) + "-" + convStr(dayOfMonth));

                            alarmYear = year;
                            alarmMonth = monthOfYear + 1;
                            alarmDay = dayOfMonth;

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate((System.currentTimeMillis() - 1000) );
            datePickerDialog.show();
        }

        else if (view == txt_time_picker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,  R.style.DialogTheme ,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            txt_time_picker.setText(convStr(hourOfDay) + ":" + convStr(minute));
                            //System.out.println(hourOfDay + ":" + minute);
                            delivery_time = convStr(hourOfDay) + ":" + convStr(minute);

                            alarmHour = hourOfDay;
                            alarmMinuts = minute;

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public void addEventOnFirebase()
    {
        final  String var_email = sharedPreferences.getString("user_key", "");

        /*if(id.equals("")){
            var_root = Integer.toString(ob.nextInt(100)) +") " +txt_date_picker.getText().toString()+","+txt_time_picker.getText().toString(); }
        else {var_root = id;}*/

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"Planner.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"Planner/"+var_email);

                String var_root = db_key;
                if(db_key.length() == 0){
                    var_root = reference.push().getKey();
                }


                reference.child(var_root).child("event").setValue(edt_event.getText().toString());
                reference.child(var_root).child("location").setValue(edt_location.getText().toString());
                reference.child(var_root).child("description").setValue(edt_event_descr.getText().toString());
                reference.child(var_root).child("date").setValue(txt_date_picker.getText().toString());
                reference.child(var_root).child("time").setValue(txt_time_picker.getText().toString());
                reference.child(var_root).child("reminder_status").setValue(Integer.toString(reminder_flag));
                reference.child(var_root).child("event_type").setValue(spinner.getSelectedItem().toString());
                reference.child(var_root).child("event_day").setValue(spinner_day.getSelectedItem().toString());
                reference.child(var_root).child("alarm_gap").setValue(spinner_gap.getSelectedItem().toString());
                reference.child(var_root).child("other").setValue(String.valueOf(eventcolor));

               // createAlarm();
                String alarmtime = alarmYear + "-" + convStr(alarmMonth) + "-" + convStr(alarmDay) + " " + convStr(alarmHour) + ":" + convStr(alarmMinuts) + ":00";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try{

                    String seldate = txt_date_picker.getText().toString();
                    String seltime = txt_time_picker.getText().toString();
                    Date date = sdf.parse(alarmtime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    switch (spinner_gap.getSelectedItemPosition()){
                        case 1: //GlobalVariables.EVENT_NOTI_10MIN
                            calendar.add(Calendar.MINUTE, -10);
                            break;
                        case 2://GlobalVariables.EVENT_NOTI_30MIN
                            calendar.add(Calendar.MINUTE, -30);
                            break;
                        case 3: //GlobalVariables.EVENT_NOTI_1HOUR
                            calendar.add(Calendar.HOUR, -1);
                            break;
                        case 4://GlobalVariables.EVENT_NOTI_2HOURS
                            calendar.add(Calendar.HOUR, -2);
                            break;
                        case 5: //GlobalVariables.EVENT_NOTI_1DAY
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                            break;
                        case 6://GlobalVariables.EVENT_NOTI_2DAYS
                            calendar.add(Calendar.DAY_OF_MONTH, -2);
                            break;
                    }

                    alarmYear = calendar.get(Calendar.YEAR);
                    alarmMonth = calendar.get(Calendar.MONTH) + 1;
                    alarmDay = calendar.get(Calendar.DAY_OF_MONTH);
                    alarmHour = calendar.get(Calendar.HOUR_OF_DAY);
                    alarmMinuts = calendar.get(Calendar.MINUTE);

                    seldate = alarmYear + "-" + convStr(alarmMonth) + "-" + convStr(alarmDay);
                    seltime = convStr(alarmHour) + ":" + convStr(alarmMinuts);
                    alarmtime = alarmYear + "-" + convStr(alarmMonth) + "-" + convStr(alarmDay) + " " + convStr(alarmHour) + ":" + convStr(alarmMinuts) + ":00";

                    SQLiteDatabase db = Functions.databaseInit(AddPlanner.this);
                    db.execSQL("CREATE TABLE IF NOT EXISTS planner_event(id INTEGER PRIMARY KEY AUTOINCREMENT, db_key VARCHAR, event VARCHAR, location VARCHAR, description VARCHAR, date VARCHAR, event_time VARCHAR, reminder_status VARCHAR, event_type VARCHAR, event_day VARCHAR, alarm_gap VARCHAR, other VARCHAR);");

                    if(db_key.length() > 0 ){
                        //db.execSQL("UPDATE planner_event SET event = '" + edt_event.getText().toString() + "', location = '" + edt_location.getText().toString() + "', description = '" + edt_event_descr.getText().toString() + "', date = '" + txt_date_picker.getText().toString() + "', event_time = '" + txt_time_picker.getText().toString() + "', reminder_status = '" + Integer.toString(reminder_flag) + "', event_type = '" + spinner.getSelectedItem().toString() + "', event_day = '" + spinner_day.getSelectedItem().toString() + "', alarm_gap = '" + spinner_gap.getSelectedItem().toString() + "' WHERE id = '" + root_id + "'");
                        db.execSQL("DELETE FROM planner_event WHERE db_key = '" + var_root + "';");
                    }

                    db.execSQL("INSERT INTO planner_event (db_key, event, location, description, date, event_time , reminder_status, event_type, event_day, alarm_gap) " +
                            "VALUES('" + var_root + "','" + edt_event.getText().toString() + "','" + edt_location.getText().toString() + "','" + edt_event_descr.getText().toString() + "','" + seldate + "','" + seltime +"','" + Integer.toString(reminder_flag) +"','" + spinner.getSelectedItem().toString() + "','" + spinner_day.getSelectedItem().toString() + "','" + spinner_gap.getSelectedItem().toString() + "');");


                    if(spinner.getSelectedItemPosition() > 0){
                        dateTimeUtils.ScheduleNotification(dateTimeUtils.getNotification(edt_event.getText().toString(), AddPlanner.this),
                                AddPlanner.this, var_root, alarmtime, true, spinner.getSelectedItem().toString());
                    } else {
                        dateTimeUtils.ScheduleNotification(dateTimeUtils.getNotification(edt_event.getText().toString(), AddPlanner.this),
                                AddPlanner.this, var_root, alarmtime, false, "");
                    }
                } catch (ParseException ex){

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AddPlanner.this, Planner.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        pd.dismiss();

                        if(db_key.equals("")){
                            Toast.makeText(AddPlanner.this , "Event Added successfuly..!", Toast.LENGTH_LONG).show(); }
                        else {
                            Toast.makeText(AddPlanner.this , "Event Updated successfuly..!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, 2000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(db_key.equals("")){
                    Toast.makeText(AddPlanner.this , "Event Added failed..!", Toast.LENGTH_LONG).show(); }
                else {
                    Toast.makeText(AddPlanner.this , "Event Updated failed..!", Toast.LENGTH_LONG).show();
                }
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Planner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String convStr(int num){
        String retStr = "";
        if(num > 9){
            retStr = "" + num;
        } else {
            retStr = "0" + num;
        }
        return retStr;
    }

    private void chooseColorDlg(){
        final Button btn_lblue, btn_purple, btn_blue, btn_green, btn_orange, btn_red;
        Button btn_cancel;
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.custom_select_color_dlg);
        View dview = dialog.getWindow().getDecorView();
        dview.setBackgroundResource(android.R.color.transparent);
        btn_lblue  = (Button) dialog.findViewById(R.id.color_btn_lblue);
        btn_purple  = (Button) dialog.findViewById(R.id.color_btn_purple);
        btn_blue  = (Button) dialog.findViewById(R.id.color_btn_blue);
        btn_green  = (Button) dialog.findViewById(R.id.color_btn_green);
        btn_orange  = (Button) dialog.findViewById(R.id.color_btn_orange);
        btn_red  = (Button) dialog.findViewById(R.id.color_btn_red);
        btn_cancel  = (Button) dialog.findViewById(R.id.btn_color_cancel);

        dialog.show();

        btn_lblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_blue_light;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_purple;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_blue_dark;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_green_light;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_orange_light;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventcolor = android.R.color.holo_red_light;
                txt_eventcolor.setImageResource(eventcolor);
                // Close dialog
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

    }

//    void createAlarm() {
//        Calendar objCalendar = Calendar.getInstance();
//
//        objCalendar.set(Calendar.YEAR, alarmYear);
//        objCalendar.set(Calendar.MONTH, alarmMonth - 1);
//        objCalendar.set(Calendar.DAY_OF_MONTH, alarmDay);
//        objCalendar.set(Calendar.HOUR_OF_DAY, alarmHour);
//        objCalendar.set(Calendar.MINUTE, alarmMinuts);
//        objCalendar.set(Calendar.SECOND, 0);
//        objCalendar.set(Calendar.MILLISECOND, 0);
//
//        //making Request Code
//        int requestCode = alarmYear + alarmMonth + alarmDay + alarmHour + alarmMinuts;
//
//        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReciever.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
//       // alarmMgr.set(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(),
//                1000 * 15, pendingIntent);
//
////        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(),
////                AlarmManager.INTERVAL_DAY, pendingIntent);
////
////        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, pendingIntent);
////
////        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
////                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
////                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
//
//    }

}
