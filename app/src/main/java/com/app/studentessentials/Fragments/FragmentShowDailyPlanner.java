package com.app.studentessentials.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.Adapters.EventDailyAdapter;
import com.app.studentessentials.AddPlanner;
import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.PlanModel;
import com.app.studentessentials.Planner;
import com.app.studentessentials.R;
import com.app.studentessentials.Utils.MyDateTimeUtils;
import com.firebase.client.Firebase;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShowDailyPlanner extends Fragment {

    ProgressDialog pd;
    //ArrayList<ModelDailyPlanner> als=new ArrayList<ModelDailyPlanner>();
    //AdapterDailyPlanner adapter;
    RecyclerView recycle_daily;
    FragmentManager fragmentManager;
    SharedPreferences sharedPreferences;
    TextView txt_day, txt_month;
    ImageView iv_daily_back, iv_daily_more;
    List<PlanModel> datalist;
    String today_str, weekday_str;
    EventDailyAdapter eventDailyAdapter;

    MyDateTimeUtils dateTimeUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_show_daily_planner, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        sharedPreferences = getActivity().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        initLayout(view);
        dateTimeUtils = new MyDateTimeUtils();

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        today_str = year + "-" + convStr(month) + "-" + convStr(day);

        int week = calendar.get(Calendar.DAY_OF_WEEK);
        weekday_str = getWeekDay(week);

        try {
            txt_day.setText(Integer.toString(day));
            txt_month.setText(getMonth(format.format(today))+" "+year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initRecycleView(view);


        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        loadDailyEvents();

        return view;
    }

    private void initRecycleView(View view){
        recycle_daily = (RecyclerView) view.findViewById(R.id.recycle_daily);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycle_daily.setLayoutManager(layoutManager);
        datalist = new ArrayList<>();
        eventDailyAdapter = new EventDailyAdapter(datalist, getContext());
        eventDailyAdapter.setOnItemEditListener(new EventDailyAdapter.OnItemEditListener() {
            @Override
            public int onItemEdit(int position) {
                PlanModel one = datalist.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("root_id", String.valueOf(one.index));
                bundle.putString("id", one.db_key);
                bundle.putString("event_name", one.event_name);
                bundle.putString("event_description", one.event_desc);
                bundle.putString("location", one.location);
                bundle.putString("event_type", one.event_type);
                bundle.putString("date", one.event_date);
                bundle.putString("time", one.event_time);
                bundle.putString("reminder", one.reminder);
                bundle.putString("event_day", one.event_day);
                bundle.putString("alarm_gap", one.alarm_gap);
                bundle.putString("event_color", one.other);
                Intent intent = new Intent(getActivity(), AddPlanner.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                return 0;
            }
        });

        eventDailyAdapter.setOnItemDeleteListener(new EventDailyAdapter.OnItemDeleteListener() {
            @Override
            public int onItemDelete(int position) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to delete event?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PlanModel one = datalist.get(pos);
                                final  String var_email = sharedPreferences.getString("user_key", "");
                                Firebase reference = new Firebase(firebase_base_url+"Planner/" + var_email);
                                reference.child(one.db_key).removeValue();

                                SQLiteDatabase db = Functions.databaseInit(getActivity());
                                //db.delete("planner_event", "db_key = ?", new String[]{one.db_key});
                                db.execSQL("DELETE FROM planner_event WHERE db_key = '" + one.db_key + "';");
                                db.close();

                                dateTimeUtils.cancelScheduledNotification(dateTimeUtils.getNotification(one.event_name, getContext()),getContext(), one.db_key);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getContext(), Planner.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getActivity().startActivity(intent);
                                        getActivity().finish();
                                    }
                                }, 2000);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert..!");
                alert.show();
                return 0;
            }
        });

        recycle_daily.setAdapter(eventDailyAdapter);
    }

    private void loadDailyEvents(){
        final  String var_email = sharedPreferences.getString("user_key", "");
        String url = firebase_base_url+"Planner/" + var_email + ".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    private void initLayout(View view){
        txt_day = (TextView)view.findViewById(R.id.txt_day);
        txt_month = (TextView)view.findViewById(R.id.txt_month);
        iv_daily_back = (ImageView) view.findViewById(R.id.iv_daily_back);
        iv_daily_more = (ImageView) view.findViewById(R.id.iv_daily_more);

        iv_daily_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateString = txt_day.getText().toString() + " " + txt_month.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                try{
                    Date myDate = dateFormat.parse(dateString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(myDate);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    Date ndate = calendar.getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    int year = calendar.get(Calendar.YEAR);
                    int nday = calendar.get(Calendar.DAY_OF_MONTH);
                    int nmonth = calendar.get(Calendar.MONTH)+1;
                    txt_day.setText(Integer.toString(nday));
                    txt_month.setText(getMonth(format.format(ndate))+" "+year);
                    today_str = year + "-" + convStr(nmonth) + "-" + convStr(nday);
                    int week = calendar.get(Calendar.DAY_OF_WEEK);
                    weekday_str = getWeekDay(week);
                    loadDailyEvents();
                } catch (ParseException ex){

                }

            }
        });

        iv_daily_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateString = txt_day.getText().toString() + " " + txt_month.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                try{
                    Date myDate = dateFormat.parse(dateString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(myDate);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date ndate = calendar.getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    int year = calendar.get(Calendar.YEAR);
                    int nday = calendar.get(Calendar.DAY_OF_MONTH);
                    int nmonth = calendar.get(Calendar.MONTH)+1;
                    txt_day.setText(Integer.toString(nday));
                    txt_month.setText(getMonth(format.format(ndate))+" "+year);
                    today_str = year + "-" + convStr(nmonth) + "-" + convStr(nday);
                    int week = calendar.get(Calendar.DAY_OF_WEEK);
                    weekday_str = getWeekDay(week);
                    loadDailyEvents();
                } catch (ParseException ex){

                }
            }
        });
    }

    public void doOnSuccess(String s){
        if(datalist != null){
            datalist.clear();
        }
        datalist = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();
                JSONObject emp = obj.getJSONObject(key);
                PlanModel one = new PlanModel();
                one.db_key = key;
                one.event_name = emp.getString("event");
                one.event_desc = emp.getString("description");
                one.location = emp.getString("location");
                one.event_date = emp.getString("date");
                one.event_time = emp.getString("time");
                one.reminder = emp.getString("reminder_status");
                one.event_type = emp.getString("event_type");
                one.event_day = emp.getString("event_day");
                one.alarm_gap = emp.getString("alarm_gap");
                one.other = emp.getString("other");

                if(one.event_type.equals(GlobalVariables.EVENT_TYPE_DAILY) || one.event_date.equals(today_str)){
                    if(getMonthFromDate(one.event_date) <= getMonthFromDate(today_str)){
                        one.index = datalist.size();
                        datalist.add(one);
                    }

                } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_WEEKLY) && one.event_day.equals(weekday_str) && (getMonthFromDate(one.event_date) <= getMonthFromDate(today_str))){
                    one.index = datalist.size();
                    datalist.add(one);
                } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_MONTHLY) && (getDayFromDate(one.event_date) == getDayFromDate(today_str)) && (getMonthFromDate(one.event_date) <= getMonthFromDate(today_str))){
                    one.index = datalist.size();
                    datalist.add(one);
                } else if(getDayFromDate(one.event_date) == getDayFromDate(today_str)){
                    one.index = datalist.size();
                    datalist.add(one);
                }
            }

            eventDailyAdapter.setDataList(datalist);
            eventDailyAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pd.dismiss();
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

    private String getWeekDay(int weekday){
        String weekDay = "";

        if (Calendar.MONDAY == weekday) {
            weekDay = "monday";
        } else if (Calendar.TUESDAY == weekday) {
            weekDay = "tuesday";
        } else if (Calendar.WEDNESDAY == weekday) {
            weekDay = "wednesday";
        } else if (Calendar.THURSDAY == weekday) {
            weekDay = "thursday";
        } else if (Calendar.FRIDAY == weekday) {
            weekDay = "friday";
        } else if (Calendar.SATURDAY == weekday) {
            weekDay = "saturday";
        } else if (Calendar.SUNDAY == weekday) {
            weekDay = "sunday";
        }
        return weekDay;
    }

    private int getDayFromDate(String dd){
        String[] separated = dd.split("-");
        int ret = Integer.parseInt(separated[2]);
        return ret;
    }

    private int getMonthFromDate(String dd){
        String[] separated = dd.split("-");
        int ret = Integer.parseInt(separated[1]);
        return ret;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }


}
