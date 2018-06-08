package com.app.studentessentials;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.firebase.client.Firebase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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
import java.util.Random;
import java.util.TimeZone;

import com.app.studentessentials.Adapters.BudgetManageAdapter;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.JavaClasses.MyBarDataSet;
import com.app.studentessentials.Models.MonthlyBudgetModel;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class BudgetManagement extends AppCompatActivity implements View.OnClickListener {

    LinearLayout lnr_budget;
    ImageView img_back, img_menu;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String month, var_root;
    ProgressDialog pd;
    TextView txt_current_current, txt_saved_money, txt_spend_money;
    String var_email;

    BarData data;
    BarDataSet dataset;
    BarChart barChart;
    ArrayList<BarEntry> entries;
    ArrayList<String> labels;
    ArrayList<Integer> barcolors;

    BudgetManageAdapter budgetManageAdapter;
    RecyclerView monthlyRecycle;
    List<MonthlyBudgetModel> monthlydata;
    List<MonthlyBudgetModel> recurringdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_management);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        initilizeView();
        onClicke();

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);

        try {
            month = getMonth(format.format(today));
            var_root = getMonth(format.format(today))+" "+year;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(sharedPreferences.getString("current_budget", "") != null){
            if(sharedPreferences.getString("budget_id", "").equals(var_root)){
                txt_current_current.setText(sharedPreferences.getString("current_budget", ""));
                GlobalVariables._BUDGET= sharedPreferences.getString("current_budget", "");
            }
        }

        //getRecurringRecord();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFirebaseRecord();
    }

    public void initilizeView(){
        img_back = (ImageView) findViewById(R.id.img_back);
        img_menu = (ImageView) findViewById(R.id.img_menu);
        lnr_budget = (LinearLayout) findViewById(R.id.lnr_budget);
        txt_current_current = (TextView) findViewById(R.id.txt_current_current);
        txt_saved_money = (TextView) findViewById(R.id.txt_saved_money);
        txt_spend_money = (TextView) findViewById(R.id.txt_spend_money);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        var_email = sharedPreferences.getString("user_key", "");

        monthlyRecycle = (RecyclerView) findViewById(R.id.eachRecycleView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        monthlyRecycle.setLayoutManager(layoutManager);
        monthlydata = new ArrayList<>();
        String temp_str = sharedPreferences.getString("current_budget", "0");
        budgetManageAdapter = new BudgetManageAdapter(monthlydata, Integer.parseInt(temp_str), getApplicationContext());
        budgetManageAdapter.setOnItemClickListener(new BudgetManageAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                String temp_str = sharedPreferences.getString("current_budget", "0");
                monthlydata.get(position).bExpand = !monthlydata.get(position).bExpand;
                budgetManageAdapter.setDataList(monthlydata, Integer.parseInt(temp_str));
                budgetManageAdapter.notifyDataSetChanged();
                return position;
            }
        });
        budgetManageAdapter.setOnItemAddClickListener(new BudgetManageAdapter.OnItemAddClickListener() {
            @Override
            public int onItemAddClick(int position) {
                String monthName = monthlydata.get(position).mMonth;
                addBudgetItemDlg(monthName);
                return 0;
            }
        });

        budgetManageAdapter.setOnItemDelClickListener(new BudgetManageAdapter.OnItemDelClickListener() {
            @Override
            public int onItemDelClick(int position) {
                String monthName = monthlydata.get(position).mMonth;
                deleteMonthBudget(monthName);
                return 0;
            }
        });
        monthlyRecycle.setAdapter(budgetManageAdapter);
    }

    private void deleteMonthBudget(final String month_key){
        final  String var_email = sharedPreferences.getString("user_key", "");
        Random ob = new Random();

        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"budget_management.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"budget_management/"+var_email+"/"+month_key);

                reference.child("recurring").removeValue();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadFirebaseRecord();
                        pd.dismiss();
                    }
                }, 2000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    private void addBudgetItemDlg(String month_key){
        final EditText et_addname, et_adddate, et_addlocation, et_addamount,et_enddate;
        final String sub_root = month_key;
        Button btn_add;
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.custom_add_budget_dlg);
        View dview = dialog.getWindow().getDecorView();
        dview.setBackgroundResource(android.R.color.transparent);
        et_addname  = (EditText) dialog.findViewById(R.id.edt_custom_name);
        et_adddate = (EditText) dialog.findViewById(R.id.edt_custom_date);
        et_addlocation = (EditText) dialog.findViewById(R.id.edt_custom_location);
        et_addamount = (EditText) dialog.findViewById(R.id.edt_custom_amount);

        btn_add = (Button) dialog.findViewById(R.id.btn_add);

        dialog.show();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_addname.getText().toString().equals("")  || !et_adddate.getText().toString().equals("") ||
                        !et_addlocation.getText().toString().equals("") || !et_addamount.getText().toString().equals("")){
                    addItemToDB(sub_root, et_addname.getText().toString(), et_adddate.getText().toString(), et_addlocation.getText().toString(), et_addamount.getText().toString());
                }
                else{
                    Toast.makeText(BudgetManagement.this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
                }
                // Close dialog
                dialog.dismiss();
            }
        });

    }

    public void addItemToDB(final String sub_key, final String addName, final String addDate, final String addLocation, final String addAmount){
        final  String var_email = sharedPreferences.getString("user_key", "");

        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"budget_management.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"budget_management/"+var_email+"/"+sub_key+"/recurring");

                reference.child(addName.trim()).child("bill").setValue(addAmount);
                reference.child(addName.trim()).child("location").setValue(addLocation);
                reference.child(addName.trim()).child("date").setValue(addDate);
                reference.child(addName.trim()).child("month").setValue(sub_key);
                reference.child(addName.trim()).child("enddate").setValue(addDate);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadFirebaseRecord();
                        pd.dismiss();
                    }
                }, 2000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    public void onClicke(){
        img_back.setOnClickListener(this);
        img_menu.setOnClickListener(this);
        lnr_budget.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.lnr_budget:
                updateBudgetDialog();
                break;
            case R.id.img_menu:
                Context wrapper = new ContextThemeWrapper(BudgetManagement.this, R.style.PopupMenu);
                PopupMenu popupMenu=new PopupMenu(wrapper,view);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        String sitem=item.toString();
                        //Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
                        if (sitem.equals("Add Recurring")) {

                            startActivity(new Intent(getApplicationContext(), AddRecurring.class));

                        } else if (sitem.equals("Show Monthly Budget Detail")) {
                            startActivity(new Intent(getApplicationContext(), ShowMonthlyDetail.class));
                            //Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                break;

            default:
        }

    }

    public void updateBudgetDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_update_budget);

        final EditText edt_otp = (EditText) dialog.findViewById(R.id.edt_otp);

        TextView txt_OK = (TextView) dialog.findViewById(R.id.txt_OK);
        txt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_otp.getText().toString().trim().equals("")){
                    addTOFirebase(edt_otp.getText().toString().trim());
                }
                else {
                    Toast.makeText(BudgetManagement.this, "Please add budget..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void addTOFirebase(final String budget) {

        pd.show();

        String url = firebase_base_url+"budget_management.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"budget_management/"+var_email+"/"+var_root);

                reference.child("budget").child("amount").setValue(budget);

                txt_current_current.setText(budget);

                editor.putString("current_budget" , budget);
                editor.putString("budget_id" , var_root);
                editor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(BudgetManagement.this, BudgetManagement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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

        RequestQueue rQueue = Volley.newRequestQueue(BudgetManagement.this);
        rQueue.add(request);
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

    public void loadFirebaseRecord(){
        pd.show();
        String url = firebase_base_url+"budget_management/"+var_email+"/recurring.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnRecurring(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError);
                pd.dismiss();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(BudgetManagement.this);
        rQueue.add(request);
    }

    public void doOnRecurring(String s){
        recurringdata = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()){
                key = i.next().toString();

                JSONObject sub_obj = (new JSONObject(s)).getJSONObject(key);
                String billstr=sub_obj.getString("bill");
                String datestr = sub_obj.getString("date");
                String locstr = sub_obj.getString("location");
                String endstr = sub_obj.getString("enddate");
                String monthstr = sub_obj.getString("month");

                MonthlyBudgetModel one = new MonthlyBudgetModel(monthstr, key, datestr, locstr, billstr, billstr);
                one.enddate = endstr;
                recurringdata.add(one);
            }
            //----------------------------------------------------
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getFirebaseRecord();
        pd.dismiss();
    }

    public void getFirebaseRecord(){
        String url = firebase_base_url+"budget_management/"+var_email+".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                if(!s.equals("null")){
                    doOnResponse(s);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(BudgetManagement.this);
        rQueue.add(request);
    }

    public void doOnResponse(String s){
        Boolean bCurMonth = false;
        int barindex = 0;
        monthlydata = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            entries = new ArrayList<>();
            labels = new ArrayList<String>();
            barcolors = new ArrayList<>();


            int total_uses =0;

            while (i.hasNext()){
                key = i.next().toString();

                if(!key.equals("recurring")){
                    int spend_meney = 0;

                    JSONObject sub_obj = (new JSONObject(s)).getJSONObject(key);
                    if(sub_obj.has("budget")){
                        JSONObject obj_budget = sub_obj.getJSONObject("budget");
                        String str_budget = obj_budget.getString("amount");
                        txt_current_current.setText(str_budget);
                        editor.putString("current_budget", str_budget);
                        editor.commit();
                    }
                    JSONObject obj_recurring = sub_obj.getJSONObject("recurring");
                    Iterator j = obj_recurring.keys();
                    String sub_key = "";
                    String name_str = "   Name\n";
                    String recurStr = "|  Location\n";
                    String amountStr = "|  Amount\n";
                    String dateStr = "|  Date\n";
                    for(int k = 0; k < recurringdata.size(); k++){
                        MonthlyBudgetModel recur_one = recurringdata.get(k);
                        String start_str  = convertRecurDateFormat(recur_one.mDate);
                        String end_str = convertRecurDateFormat(recur_one.enddate);
                        String key_date = convertDBkeyDate(key);
                        if(key_date.length() > 0 && start_str.compareTo(key_date) <= 0 && end_str.compareTo(key_date) >= 0){
                            spend_meney = spend_meney + Integer.parseInt(recur_one.mAmount);
                            dateStr = dateStr + "|  " + recur_one.mDate + "\n";
                            recurStr = recurStr + "|  " + recur_one.mRecurring + "\n";
                            amountStr = amountStr + "|  £" + recur_one.mAmount + "\n";
                            name_str = name_str + "   " + recur_one.mName + "\n";
                        }
                    }
                    while (j.hasNext()){
                        sub_key = j.next().toString();

                        JSONObject emp = obj_recurring.getJSONObject(sub_key);
                        String meter_reading=emp.getString("bill");
                        String tempdate = emp.getString("date");
                        String temploc = emp.getString("location");
                        spend_meney = spend_meney + Integer.parseInt(emp.getString("bill"));
                        dateStr = dateStr + "|  " + tempdate + "\n";
                        recurStr = recurStr + "|  " + temploc + "\n";
                        amountStr = amountStr + "|  £" + meter_reading + "\n";
                        name_str = name_str + "   " + sub_key + "\n";
                    }

                    if(key.equals(var_root)) bCurMonth = true;

                    MonthlyBudgetModel one = new MonthlyBudgetModel(key, name_str, dateStr, recurStr, amountStr, Integer.toString(spend_meney));
                    monthlydata.add(one);

                    total_uses += spend_meney;

                    if(key.equals(var_root)){
                        int saved_money = (Integer.parseInt(sharedPreferences.getString("current_budget", "0")) - spend_meney);
                        txt_saved_money.setText(" £ " + saved_money);
                        if(saved_money == 0){
                            txt_saved_money.setTextColor(Color.BLUE);
                        } else if(saved_money < 0){
                            txt_saved_money.setTextColor(Color.RED);
                        } else{
                            txt_saved_money.setTextColor(Color.GREEN);
                        }

                    }

                    String temp_str = sharedPreferences.getString("current_budget", "0");
                    int month_budget = Integer.parseInt(temp_str);//Integer.parseInt(str_budget);
                    int temp_color = ContextCompat.getColor(this, R.color.green);
                    if(month_budget == spend_meney){
                        temp_color = ContextCompat.getColor(this, R.color.blue);
                    } else if(month_budget < spend_meney){
                        temp_color = ContextCompat.getColor(this, R.color.red);
                    }
                    entries.add(new BarEntry(spend_meney, barindex));
                    labels.add(key);
                    barcolors.add(temp_color);
                    barindex++;
                }
            }
            //----------------------------------------------------

            txt_spend_money.setText(Integer.toString(total_uses));
            String bg_str = sharedPreferences.getString("current_budget", "0");
            budgetManageAdapter.setDataList(monthlydata, Integer.parseInt(bg_str));
            budgetManageAdapter.notifyDataSetChanged();

            barChart = (BarChart) findViewById(R.id.chart);
            //dataset = new BarDataSet(entries, "Monthly Average");

            BarDataSet set = new BarDataSet(entries, "Monthly Average");
            set.setColors(barcolors);
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);

            barChart.invalidate();

            data = new BarData(labels, dataSets);
            barChart.setData(data);
            barChart.setDescription("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(monthlydata.size()==0 || !bCurMonth){
            int spend_meney = 0;
            String name_str = "   Name\n";
            String recurStr = "|  Location\n";
            String amountStr = "|  Amount\n";
            String dateStr = "|  Date\n";
            for(int k = 0; k < recurringdata.size(); k++){
                MonthlyBudgetModel recur_one = recurringdata.get(k);
                String start_str  = convertRecurDateFormat(recur_one.mDate);
                String end_str = convertRecurDateFormat(recur_one.enddate);
                String key_date = convertDBkeyDate(var_root);

                if(key_date.length() > 0 && start_str.compareTo(key_date) <= 0 && end_str.compareTo(key_date) >= 0){
                    spend_meney = spend_meney + Integer.parseInt(recur_one.mAmount);
                    dateStr = dateStr + "|  " + recur_one.mDate + "\n";
                    recurStr = recurStr + "|  " + recur_one.mRecurring + "\n";
                    amountStr = amountStr + "|  £" + recur_one.mAmount + "\n";
                    name_str = name_str + "   " + recur_one.mName + "\n";
                }
            }
            MonthlyBudgetModel one = new MonthlyBudgetModel(var_root, name_str, dateStr, recurStr, amountStr, Integer.toString(spend_meney));
            monthlydata.add(one);

            int saved_money = (Integer.parseInt(sharedPreferences.getString("current_budget", "0")) - spend_meney);
            txt_saved_money.setText(" £ " + saved_money);
            if(saved_money == 0){
                txt_saved_money.setTextColor(Color.BLUE);
            } else if(saved_money < 0){
                txt_saved_money.setTextColor(Color.RED);
            } else{
                txt_saved_money.setTextColor(Color.GREEN);
            }

            String temp_str = sharedPreferences.getString("current_budget", "0");
            int month_budget = Integer.parseInt(temp_str);//Integer.parseInt(str_budget);

            budgetManageAdapter.setDataList(monthlydata, month_budget);
            budgetManageAdapter.notifyDataSetChanged();


            int temp_color = ContextCompat.getColor(this, R.color.green);
            if(month_budget == spend_meney){
                temp_color = ContextCompat.getColor(this, R.color.blue);
            } else if(month_budget < spend_meney){
                temp_color = ContextCompat.getColor(this, R.color.red);
            }

            entries.add(new BarEntry(spend_meney, barindex));
            labels.add(var_root);
            barcolors.add(temp_color);
            barindex++;
            barChart = (BarChart) findViewById(R.id.chart);
            //dataset = new BarDataSet(entries, "Monthly Average");

            BarDataSet set = new BarDataSet(entries, "Monthly Average");
            set.setColors(barcolors);
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);

            barChart.invalidate();

            data = new BarData(labels, dataSets);
            barChart.setData(data);
            barChart.setDescription("");
        }
        pd.dismiss();
    }

    public String convertRecurDateFormat(String sdate){
        String[] dd = sdate.split("/");
        String newStr = dd[2] + "-" + dd[1];// + "-" + dd[0];
        return  newStr;
    }

    public String convertDBkeyDate(String date){
        String monthname = "";
        try{
            Date d = new SimpleDateFormat("MMMM yyyy").parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("yyyy-MM").format(cal.getTime());
            return monthName;
        }catch (ParseException pex){

        }
        return monthname;

    }
}
