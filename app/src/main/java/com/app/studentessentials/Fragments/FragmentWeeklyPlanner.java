package com.app.studentessentials.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.AddPlanner;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.PlanModel;
import com.app.studentessentials.R;

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

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWeeklyPlanner extends Fragment {

    ProgressDialog pd;
    //ArrayList<ModelDailyPlanner> als=new ArrayList<ModelDailyPlanner>();
    //AdapterWeeklyPlanner adapter;
    SharedPreferences sharedPreferences;
    private WeekView mWeekView;
    ImageView iv_daily_back, iv_daily_more;
    TextView txt_day, txt_month;
    private Date seldate;


    List<WeekViewEvent> eventslist;
    List<PlanModel> planList = new ArrayList<>();

    public FragmentWeeklyPlanner() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_weekly_planner, container, false);

        sharedPreferences = getActivity().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        eventslist = new ArrayList<>();
        initLayout(view);

        seldate = new Date();
        Calendar cc = Calendar.getInstance();
        cc.setTime(seldate);
        //first day of week
        cc.set(Calendar.DAY_OF_WEEK, 1);
        seldate = cc.getTime();
        setWeekStartEnd(seldate);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        loadWeeklyEvents();

        return view;
    }

    private void initLayout(View view){
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) view.findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                PlanModel one = planList.get((int)event.getId());
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
            }
        });

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                // Populate the week view with some events.
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>(eventslist);
                return events;
            }
        });

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });

        iv_daily_back = (ImageView) view.findViewById(R.id.iv_daily_back);
        iv_daily_more = (ImageView) view.findViewById(R.id.iv_daily_more);

        iv_daily_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(seldate);
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                mWeekView.goToToday();
                seldate = calendar.getTime();
                setWeekStartEnd(seldate);
                loadWeeklyEvents();
                mWeekView.goToDate(calendar);
            }
        });

        iv_daily_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(seldate);
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                mWeekView.goToToday();
                seldate = calendar.getTime();
                setWeekStartEnd(seldate);
                loadWeeklyEvents();
                mWeekView.goToDate(calendar);
            }
        });

        txt_day = (TextView)view.findViewById(R.id.txt_day);
        txt_month = (TextView)view.findViewById(R.id.txt_month);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mWeekView.goToToday();
            //mWeekView.refreshDrawableState();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Calendar selcal = Calendar.getInstance();
                    selcal.setTime(seldate);
                    mWeekView.goToDate(selcal);
                }
            }, 1000);

        }
    }

    public void loadWeeklyEvents(){
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

    public void doOnSuccess(String s){
        planList = new ArrayList<>();
        eventslist = new ArrayList<>();
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

                if(one.event_type.equals(GlobalVariables.EVENT_TYPE_DAILY)){
                    one.index = planList.size();
                    planList.add(one);
                    addDailyEventWeeklyList(one);
                } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
                    one.index = planList.size();
                    planList.add(one);
                    addWeeklyEventList(one);
                } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_MONTHLY)){
                    one.index = planList.size();
                    planList.add(one);
                    addEventList(one);
                } else{
                    one.index = planList.size();
                    planList.add(one);
                    addEventList(one);
                }
            }

            //mWeekView.refreshDrawableState();
            //mWeekView.goToToday();
            Calendar selcal = Calendar.getInstance();
            selcal.setTime(seldate);
            mWeekView.goToDate(selcal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pd.dismiss();
    }

    private void addEventList(PlanModel plan){
        Calendar c1 = Calendar.getInstance();
        String[] time_str = plan.event_time.split(":");
        String[] day_str = plan.event_date.split("-");

        c1.setTime(seldate);
        c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str[0]));
        c1.set(Calendar.MINUTE, Integer.parseInt(time_str[1]));
        c1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day_str[2]));
        c1.set(Calendar.MONTH, Integer.parseInt(day_str[1]));

        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int hour1 = c1.get(Calendar.HOUR_OF_DAY);
        int min1 = c1.get(Calendar.MINUTE);

        if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_10MIN)){
            c1.add(Calendar.MINUTE, 10);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_30MIN)){
            c1.add(Calendar.MINUTE, 30);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1HOUR)){
            c1.add(Calendar.HOUR, 1);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2HOURS)){
            c1.add(Calendar.HOUR, 2);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1DAY)){
            c1.add(Calendar.DAY_OF_MONTH, 1);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2DAYS)){
            c1.add(Calendar.DAY_OF_MONTH, 2);
        } else{
            c1.add(Calendar.MINUTE, 5);
        }
        int year2 = c1.get(Calendar.YEAR);
        int month2 = c1.get(Calendar.MONTH);
        int day2 = c1.get(Calendar.DAY_OF_MONTH);
        int hour2 = c1.get(Calendar.HOUR_OF_DAY);
        int min2 = c1.get(Calendar.MINUTE);
        int ecolor = android.R.color.holo_blue_light;
        if(plan.other.length() > 1){
            ecolor = Integer.parseInt(plan.other);
        }
        WeekViewEvent one_event = new WeekViewEvent(plan.index,plan.event_name, year1, month1, day1, hour1, min1, year2, month2, day2, hour2, min2);
        one_event.setColor(getResources().getColor(ecolor));
        one_event.setLocation(plan.location);
        eventslist.add(one_event);
    }

    private void addWeeklyEventList(PlanModel plan){
        Calendar c1 = Calendar.getInstance();
        String[] time_str = plan.event_time.split(":");

        c1.setTime(seldate);
        c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str[0]));
        c1.set(Calendar.MINUTE, Integer.parseInt(time_str[1]));

        int wday = getDayOfWeekDay(plan.event_day);
        //first day of week
        c1.set(Calendar.DAY_OF_WEEK, wday);

        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int hour1 = c1.get(Calendar.HOUR_OF_DAY);
        int min1 = c1.get(Calendar.MINUTE);

        if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_10MIN)){
            c1.add(Calendar.MINUTE, 10);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_30MIN)){
            c1.add(Calendar.MINUTE, 30);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1HOUR)){
            c1.add(Calendar.HOUR, 1);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2HOURS)){
            c1.add(Calendar.HOUR, 2);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1DAY)){
            c1.add(Calendar.DAY_OF_MONTH, 1);
        } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2DAYS)){
            c1.add(Calendar.DAY_OF_MONTH, 2);
        } else{
            c1.add(Calendar.MINUTE, 5);
        }
        int year2 = c1.get(Calendar.YEAR);
        int month2 = c1.get(Calendar.MONTH)+1;
        int day2 = c1.get(Calendar.DAY_OF_MONTH);
        int hour2 = c1.get(Calendar.HOUR_OF_DAY);
        int min2 = c1.get(Calendar.MINUTE);

        int ecolor = android.R.color.holo_blue_light;
        if(plan.other.length() > 1){
            ecolor = Integer.parseInt(plan.other);
        }
        WeekViewEvent one_event = new WeekViewEvent(plan.index,plan.event_name, year1, month1, day1, hour1, min1, year2, month2, day2, hour2, min2);
        one_event.setColor(getResources().getColor(ecolor));
        one_event.setLocation(plan.location);
        eventslist.add(one_event);
    }

    private void addDailyEventWeeklyList(PlanModel plan){
        String[] time_str = plan.event_time.split(":");
        Calendar c1 = Calendar.getInstance();


        //first day of week
        for(int j = 1; j < 8; j++){
            c1.setTime(seldate);
            c1.set(Calendar.DAY_OF_WEEK, j);
            c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str[0]));
            c1.set(Calendar.MINUTE, Integer.parseInt(time_str[1]));

            int year1 = c1.get(Calendar.YEAR);
            int month1 = c1.get(Calendar.MONTH)+1;
            int day1 = c1.get(Calendar.DAY_OF_MONTH);
            int hour1 = c1.get(Calendar.HOUR_OF_DAY);
            int min1 = c1.get(Calendar.MINUTE);

            if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_10MIN)){
                c1.add(Calendar.MINUTE, 10);
            } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_30MIN)){
                c1.add(Calendar.MINUTE, 30);
            } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1HOUR)){
                c1.add(Calendar.HOUR, 1);
            } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2HOURS)){
                c1.add(Calendar.HOUR, 2);
            } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_1DAY)){
                c1.add(Calendar.DAY_OF_MONTH, 1);
            } else if(plan.alarm_gap.equals(GlobalVariables.EVENT_NOTI_2DAYS)){
                c1.add(Calendar.DAY_OF_MONTH, 2);
            } else{
                c1.add(Calendar.MINUTE, 5);
            }
            int year2 = c1.get(Calendar.YEAR);
            int month2 = c1.get(Calendar.MONTH)+1;
            int day2 = c1.get(Calendar.DAY_OF_MONTH);
            int hour2 = c1.get(Calendar.HOUR_OF_DAY);
            int min2 = c1.get(Calendar.MINUTE);
            int ecolor = android.R.color.holo_blue_light;
            if(plan.other.length() > 1){
                ecolor = Integer.parseInt(plan.other);
            }
            WeekViewEvent one_event = new WeekViewEvent(plan.index,plan.event_name, year1, month1, day1, hour1, min1, year2, month2, day2, hour2, min2);
            one_event.setColor(getResources().getColor(ecolor));
            one_event.setLocation(plan.location);
            eventslist.add(one_event);
        }

    }

    private static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        return monthName;
    }


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    private void setWeekStartEnd(Date mDate){
        Calendar c1 = Calendar.getInstance();

        c1.setTime(mDate);
        //first day of week
        c1.set(Calendar.DAY_OF_WEEK, 1);

        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);

        //last day of week
        c1.set(Calendar.DAY_OF_WEEK, 7);

        int year7 = c1.get(Calendar.YEAR);
        int month7 = c1.get(Calendar.MONTH)+1;
        int day7 = c1.get(Calendar.DAY_OF_MONTH);

        try{
            String month_name1 = getMonth(year1 + "-" + month1 + "-" + day1);
            String month_name7 = getMonth(year7 + "-" + month7 + "-" + day7);
            if(year1 == year7){
                txt_day.setText(month_name1 + " " + day1 + " - " + month_name7 + " " + day7);
                txt_month.setText(String.valueOf(year1));
            } else{
                txt_day.setText(month_name1 + " " + day1 + " " + year1 + " - " + month_name7 + " " + day7 + " " + year7);
                txt_month.setText("");
            }
        } catch (ParseException ex){

        }
    }

    private int getDayOfWeekDay(String weekday){
        int ret = Calendar.SUNDAY;

        if (weekday.equals("monday")) {
            ret = Calendar.MONDAY;
        } else if (weekday.equals("tuesday")) {
            ret = Calendar.TUESDAY;
        } else if (weekday.equals("wednesday")) {
            ret = Calendar.WEDNESDAY;
        } else if (weekday.equals("thursday")) {
            ret = Calendar.THURSDAY;
        } else if (weekday.equals("friday")) {
            ret = Calendar.FRIDAY;
        } else if (weekday.equals("saturday")) {
            ret = Calendar.SATURDAY;
        } else if (weekday.equals("sunday")) {
            ret = Calendar.SUNDAY;
        }
        return ret;
    }

}
