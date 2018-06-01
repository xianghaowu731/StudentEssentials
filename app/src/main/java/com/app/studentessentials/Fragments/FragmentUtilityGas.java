package com.app.studentessentials.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.Adapters.UtilityDataListAdapter;
import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.UtilityModel;
import com.app.studentessentials.MyApp;
import com.app.studentessentials.R;
import com.app.studentessentials.Utility;
import com.app.studentessentials.Utils.MyDateTimeUtils;
import com.firebase.client.Firebase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;


public class FragmentUtilityGas extends Fragment {

    EditText edt_meter_reading, edt_kwh_used;
    Button btn_submit;
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String var_root, previousMonth;
    TextView txt_average, txt_total_kwh, tv_month_name;
    BarDataSet dataset;
    BarData data;
    BarChart barChart;
    String month, previous_reading;
    ArrayList<BarEntry> entries;
    ArrayList<String> labels;
    ProgressDialog pd;
    boolean month_flag = false , previous_month_flag = false;
    Spinner spin_reminder;
    MyDateTimeUtils dateTimeUtils;

    RecyclerView utility_recycle;
    UtilityDataListAdapter utilityDataListAdapter;
    List<UtilityModel> datalist = new ArrayList<>();
    ArrayList<Integer> barcolors;


    public FragmentUtilityGas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_utility_gas, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Inflate the layout for this fragment
        initializeView();

        dateTimeUtils = new MyDateTimeUtils();
        getElectricReadingOnFirebase();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_meter_reading.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please fill meter reading..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(previous_month_flag) {
                        if(Integer.parseInt(edt_meter_reading.getText().toString()) < Integer.parseInt(sharedPreferences.getString("previous_gas_meter_reading", "0"))){
                            Toast.makeText(getActivity(), "Please enter MAX reading then previous..!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            insertElectricReadingOnFirebase();
                        }
                    }else {
                        insertElectricReadingOnFirebase();
                    }

                    if(spin_reminder.getSelectedItemPosition() != 0){

                        Date today = new Date();
                        try{
                            String first_day = getFirstDay(today);
                            String last_day = getLastDay(today);
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR);
                            int mMonth = c.get(Calendar.MONTH)+1;
                            //int mDay = c.get(Calendar.DAY_OF_MONTH);
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);

                            String alarmtime = "", noti_date = "", noti_time = "";
                            String noti_id = String.format("gas-%s", month);
                            if(spin_reminder.getSelectedItemPosition() == 1){
                                alarmtime = mYear + "-" + convStr(mMonth) + "-" + first_day + " " + convStr(mHour) + ":" + convStr(mMinute) + ":00";
                                noti_date = mYear + "-" + convStr(mMonth) + "-" + first_day;
                                noti_time = convStr(mHour) + ":" + convStr(mMinute);
                            } else{
                                alarmtime = mYear + "-" + convStr(mMonth) + "-" + last_day + " " + convStr(mHour) + ":" + convStr(mMinute) + ":00";
                                noti_date = mYear + "-" + convStr(mMonth) + "-" + last_day;
                                noti_time = convStr(mHour) + ":" + convStr(mMinute);
                            }
                            String noti_content = month + " Gas Meter Reading";

                            SQLiteDatabase db = Functions.databaseInit(getActivity());
                            db.execSQL("CREATE TABLE IF NOT EXISTS utility_event(id INTEGER PRIMARY KEY AUTOINCREMENT, utility_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, userkey VARCHAR);");


                            db.execSQL("INSERT INTO utility_event (utility_name, description, task_date, task_time, userkey) " +
                                    "VALUES('" + noti_id + "','" + noti_content + "','" + noti_date + "','" + noti_time + "','" + MyApp.getInstance().myProfile.userkey +"');");

                            db.close();
                            dateTimeUtils.ScheduleNotification(dateTimeUtils.getNotification(noti_content, getActivity()),
                                    getActivity(), noti_id, alarmtime, true, "Monthly");
                        } catch (Exception ex){

                        }

                    } else{
                        String noti_id = String.format("gas-%s", month);
                        String noti_content = month + " Gas Meter Reading";

                        SQLiteDatabase db = Functions.databaseInit(getActivity());
                        db.execSQL("CREATE TABLE IF NOT EXISTS utility_event(id INTEGER PRIMARY KEY AUTOINCREMENT, utility_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, userkey VARCHAR);");

                        db.execSQL("DELETE FROM utility_event WHERE utility_name = '" + noti_id + "' AND userkey = '" + MyApp.getInstance().myProfile.userkey +"';");

                        db.close();
                        dateTimeUtils.cancelScheduledNotification(dateTimeUtils.getNotification(noti_content, getActivity()),
                                getActivity(), noti_id);
                    }
                }
            }
        });

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);

         //var_root = "April 2018";
        try {
            previousMonth = getMonth(calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
            month = getMonth(format.format(today));
            var_root = getMonth(format.format(today))+" "+year;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String pre_reminder = sharedPreferences.getString("gas_reminder", "0");
        if(pre_reminder.equals("1")){
            spin_reminder.setSelection(1);
        } else if(pre_reminder.equals("2")){
            spin_reminder.setSelection(2);
        }

        return view;
    }

    public void initializeView(){
        edt_meter_reading = (EditText) view.findViewById(R.id.edt_meter_reading);
        edt_kwh_used = (EditText) view.findViewById(R.id.edt_kwh_used);
        spin_reminder = (Spinner) view.findViewById(R.id.spin_reminder);

        List<String> categories = new ArrayList<String>();
        categories.add("None");
        categories.add("First Day");
        categories.add("Last Day");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spin_reminder.setAdapter(dataAdapter);
        spin_reminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    editor.putString("gas_reminder" , "1");
                    editor.commit();
                } else if(i ==2){
                    editor.putString("gas_reminder" , "2");
                    editor.commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txt_average = (TextView) view.findViewById(R.id.txt_average);
        txt_total_kwh= (TextView) view.findViewById(R.id.txt_total_kwh);
        tv_month_name = (TextView) view.findViewById(R.id.tv_month_name);
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        tv_month_name.setText(month_name);

        btn_submit = (Button) view.findViewById(R.id.btn_submit);

        sharedPreferences = getActivity().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        utility_recycle = (RecyclerView) view.findViewById(R.id.utility_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        utility_recycle.setLayoutManager(layoutManager);
        utilityDataListAdapter = new UtilityDataListAdapter(datalist, getActivity().getApplicationContext());
        utility_recycle.setAdapter(utilityDataListAdapter);

        previous_reading = "0";
    }

    public void insertElectricReadingOnFirebase(){
        final  String var_email = sharedPreferences.getString("user_key", "");
        pd.show();

        String url = firebase_base_url+"utility.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"utility/"+var_email);

                reference.child(var_root).child("month").setValue(month);
                reference.child(var_root).child("gas_meter_reading").setValue(edt_meter_reading.getText().toString());
                if(previous_month_flag){
                    if(previous_reading.length() == 0){
                        previous_reading = "0";
                    }
                    int used_value = Integer.parseInt(edt_meter_reading.getText().toString()) - Integer.parseInt(previous_reading);
                    reference.child(var_root).child("electricity_monthly_uses").setValue(String.valueOf(used_value));}
                else{
                    reference.child(var_root).child("gas_monthly_uses").setValue(edt_kwh_used.getText().toString());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), Utility.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        pd.dismiss();
                    }
                }, 3000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    private static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        return monthName;
    }

    public void getElectricReadingOnFirebase(){
        String url = firebase_base_url+"utility/"+sharedPreferences.getString("user_key", "")+".json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
                //System.out.println("------------00000000000000000000000 ----- 00000000000000000000000000  "+s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    public void doOnSuccess(String s){
        datalist = new ArrayList<>();
        barcolors = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);
            int flag = 0 , total_uses =0;
            Iterator i = obj.keys();
            String key = "";

            entries = new ArrayList<>();
            labels = new ArrayList<String>();
            previous_month_flag = false;
            month_flag = false;

            while(i.hasNext()){
                key = i.next().toString();

                UtilityModel one = new UtilityModel();
                one.month = key;
                JSONObject emp=(new JSONObject(s)).getJSONObject(key);
                String meter_reading = "";
                String used_str = "";
                if(emp.has("gas_meter_reading")){
                    meter_reading=emp.getString("gas_meter_reading");
                    used_str = emp.getString("gas_monthly_uses");

                    if (previousMonth.equals(emp.getString("month"))){
                        editor.putString("previous_gas_meter_reading" , meter_reading);
                        editor.commit();
                        previous_month_flag = true;
                        previous_reading = meter_reading;
                    }
                    if(month.equals(emp.getString("month"))){
                        editor.putString("current_gas_meter_reading" , meter_reading);
                        editor.putString("previous_gas_meter_reading" , used_str);
                        editor.commit();
                        month_flag = true;
                    } else{
                        one.reading_value = meter_reading;
                        one.used_value = used_str;
                        datalist.add(one);
                    }

                    if(used_str.length() == 0){
                        used_str = "0";
                    }
                    entries.add(new BarEntry(Integer.parseInt(used_str), flag));
                    labels.add(emp.getString("month"));

                    total_uses = total_uses + Integer.parseInt(used_str);
                    flag++;

                    barcolors.add(ContextCompat.getColor(getContext(), R.color.blue));
                }
            }
            if(flag > 0){
                txt_total_kwh.setText(Integer.toString(total_uses));
                txt_average.setText(Float.toString(total_uses/flag));
            }

            barChart = (BarChart) view.findViewById(R.id.chart);
            dataset = new BarDataSet(entries, "Monthly Average");
            dataset.setColors(barcolors);

            barChart.invalidate();

            data = new BarData(labels, dataset);
            barChart.setData(data);
            barChart.setDescription("");

            edt_meter_reading.setText(sharedPreferences.getString("current_gas_meter_reading", ""));
            edt_kwh_used.setText(sharedPreferences.getString("previous_gas_meter_reading", ""));

            if(previous_month_flag){
                edt_kwh_used.setFocusable(false);
                edt_kwh_used.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                edt_kwh_used.setClickable(false);
            }

            utilityDataListAdapter.setDataList(datalist);
            utilityDataListAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        pd.dismiss();
    }

    public String getFirstDay(Date d) throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int fday = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(fday);
    }

    public String getLastDay(Date d) throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int lday = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(lday);
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

    private String makeAverageReadingValue(float avg){
        float rest_val = avg - (int) avg;

        int a_val = (int)avg;
        String ret_str = "";
        while (a_val != 0){
            ret_str = convStr(a_val % 100) + "." + ret_str;
            a_val = a_val / 100;
        }
        ret_str = ret_str + ((int)(rest_val * 10) % 10);
        return  ret_str;
    }

}
