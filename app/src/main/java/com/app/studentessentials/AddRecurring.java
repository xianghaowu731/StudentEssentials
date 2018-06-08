package com.app.studentessentials;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.Adapters.RecurringListAdapter;
import com.app.studentessentials.Models.MonthlyBudgetModel;
import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class AddRecurring extends AppCompatActivity implements View.OnClickListener{

    ImageView img_back;
    EditText edt_name,edt_bill, edt_location;
    TextView txt_date, txt_enddate;
    LinearLayout lnr_date, lnr_enddate;
    Button btn_add;
    String var_root, month, startdate, enddate;
    int mYear, mMonth, mDay;
    SharedPreferences sharedPreferences;

    RecyclerView recurring_recycle;
    List<MonthlyBudgetModel> datalist = new ArrayList<>();
    RecurringListAdapter recurringListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recurring);

        initilizeView();
        onClicke();

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);

        // var_root = "April 2018";

        try {
            month = getMonth(format.format(today));
            var_root = getMonth(format.format(today))+" "+year;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void initilizeView(){
        img_back = (ImageView) findViewById(R.id.img_back);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_bill= (EditText) findViewById(R.id.edt_bill);
        edt_location= (EditText) findViewById(R.id.edt_location);

        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_enddate = (TextView) findViewById(R.id.txt_end);

        lnr_date = (LinearLayout) findViewById(R.id.lnr_date);
        lnr_enddate = (LinearLayout) findViewById(R.id.lnr_end);
        btn_add = (Button) findViewById(R.id.btn_add);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        recurring_recycle = (RecyclerView) findViewById(R.id.recurring_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recurring_recycle.setLayoutManager(layoutManager);
        recurringListAdapter = new RecurringListAdapter(datalist, getApplicationContext());
        recurringListAdapter.setOnItemDelClickListener(new RecurringListAdapter.OnItemDelClickListener() {
            @Override
            public int onItemDelClick(int position) {
                MonthlyBudgetModel one = datalist.get(position);
                deleteItemFromDB(one.mName);
                getFirebaseRecord();
                return position;
            }
        });

        recurring_recycle.setAdapter(recurringListAdapter);
        startdate = "";
        enddate = "";
    }
    public void onClicke(){
        img_back.setOnClickListener(this);
        lnr_date.setOnClickListener(this);
        lnr_enddate.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }
    public boolean checkValidation(){
        boolean flag = false;
        if(enddate.length() > 1 && startdate.length() > 1 && !edt_name.getText().toString().equals("") && !edt_bill.getText().toString().equals("") && !edt_location.getText().toString().equals("")){
            flag = true;
        }
        else{
            flag = false;
            Toast.makeText(this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
        }
        return  flag;
    }

    public void addTOFirebase() {

        final  String var_email = sharedPreferences.getString("user_key", "");

        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"budget_management.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"budget_management/"+var_email + "/recurring");

                reference.child(edt_name.getText().toString().trim()).child("bill").setValue(edt_bill.getText().toString().trim());
                reference.child(edt_name.getText().toString().trim()).child("location").setValue(edt_location.getText().toString().trim());
                reference.child(edt_name.getText().toString().trim()).child("date").setValue(startdate);
                reference.child(edt_name.getText().toString().trim()).child("enddate").setValue(enddate);
                reference.child(edt_name.getText().toString().trim()).child("month").setValue(month);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AddRecurring.this, BudgetManagement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        pd.dismiss();
                    }
                }, 2000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AddRecurring.this);
        rQueue.add(request);
    }

    public void deleteItemFromDB(String name){
        final String key = name;
        final  String var_email = sharedPreferences.getString("user_key", "");

        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"budget_management.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"budget_management/"+var_email+"/recurring");

                reference.child(key).removeValue();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                }, 1000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AddRecurring.this);
        rQueue.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.lnr_date:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme ,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txt_date.setText(convStr(dayOfMonth) + "-" + convStr(monthOfYear + 1) + "-" + year);
                                startdate = convStr(dayOfMonth) + "/" + convStr(monthOfYear + 1) + "/" + year;
                                /*tv_bill_start.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                if(dayOfMonth == 31){
                                    if(monthOfYear == 0)
                                        default_enddate= "28" + "-" + ((monthOfYear + 2)%12) + "-" + year;
                                    else
                                        default_enddate= dayOfMonth-1 + "-" + ((monthOfYear + 2)%12) + "-" + year;
                                } else{
                                    default_enddate = dayOfMonth + "-" + ((monthOfYear + 2)%12) + "-" + year;
                                }*/

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate((System.currentTimeMillis() - 1000) );
                datePickerDialog.show();
                break;
            case R.id.lnr_end:
                final Calendar c1 = Calendar.getInstance();
                mYear = c1.get(Calendar.YEAR);
                mMonth = c1.get(Calendar.MONTH);
                mDay = c1.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog edatePickerDlg = new DatePickerDialog(this, R.style.DialogTheme ,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txt_enddate.setText(convStr(dayOfMonth) + "-" + convStr(monthOfYear + 1) + "-" + year);
                                enddate = convStr(dayOfMonth) + "/" + convStr(monthOfYear + 1) + "/" + year;

                            }
                        }, mYear, mMonth, mDay);

                edatePickerDlg.getDatePicker().setMinDate((System.currentTimeMillis() - 1000) );
                edatePickerDlg.show();
                break;
            case R.id.btn_add:
                if(checkValidation()){
                    addTOFirebase();
                }
                break;
            default:
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getFirebaseRecord();
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private String convStr(int num){
        String retStr = "";
        if(num > 9){
            retStr = "" + num;
        } else {
            retStr = "0"+num;
        }
        return retStr;
    }

    public void getFirebaseRecord(){
        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        final  String var_email = sharedPreferences.getString("user_key", "");

        String url = firebase_base_url+"budget_management/"+var_email + "/recurring" + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnResponse(s);
                pd.dismiss();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(AddRecurring.this);
        rQueue.add(request);
    }

    public void doOnResponse(String s){
        datalist = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()){
                key = i.next().toString();

                JSONObject emp = obj.getJSONObject(key);
                String bill=emp.getString("bill");
                String tempdate = emp.getString("date");
                String temploc = emp.getString("location");
                String month = emp.getString("month");
                String tempenddate = "";
                if(emp.has("enddate")) {
                    tempenddate = emp.getString("enddate");
                }

                MonthlyBudgetModel one = new MonthlyBudgetModel(month, key, tempdate, temploc, bill, bill);
                one.enddate = tempenddate;
                datalist.add(one);
            }
            //----------------------------------------------------
            recurringListAdapter.setDataList(datalist);
            recurringListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
