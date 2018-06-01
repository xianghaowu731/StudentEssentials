package com.app.studentessentials.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.app.studentessentials.Gsons.ModelDailyPlanner;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.CustomEvent;
import com.app.studentessentials.Models.PlanModel;
import com.app.studentessentials.R;
import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.entity.CalendarEvent;
import com.p_v.flexiblecalendar.entity.Event;
import com.p_v.flexiblecalendar.view.BaseCellView;
import com.p_v.flexiblecalendar.view.SquareCellView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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
public class FragmentMonthlyPlanner extends Fragment {

    ProgressDialog pd;
    //ArrayList<ModelDailyPlanner> als=new ArrayList<ModelDailyPlanner>();
    //AdapterMonthlyPlanner adapter;
    SharedPreferences sharedPreferences;
    private FlexibleCalendarView calendar_view;
    TextView txt_month;
    ImageView iv_daily_back, iv_daily_more;
    private Calendar selcal;
    List<PlanModel> planlist = new ArrayList<>();

    public FragmentMonthlyPlanner() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_monthly_planner, container, false);


        sharedPreferences = getActivity().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        calendar_view = (FlexibleCalendarView) view.findViewById(R.id.calendar_view);
        calendar_view.canScrollHorizontally(0);
        initCalendar();

        initEvents();

        txt_month = (TextView)view.findViewById(R.id.txt_month);
        selcal = Calendar.getInstance();
        setMonthName(selcal);

        iv_daily_back = (ImageView) view.findViewById(R.id.iv_daily_back);
        iv_daily_more = (ImageView) view.findViewById(R.id.iv_daily_more);

        iv_daily_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selcal.add(Calendar.MONTH, -1);
                calendar_view.moveToPreviousMonth();
                //setMonthName(selcal);
                //calendar_view.refresh();
            }
        });

        iv_daily_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selcal.add(Calendar.MONTH, 1);
                calendar_view.moveToNextMonth();
                //setMonthName(selcal);
                //calendar_view.refresh();
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        //pd.show();


        loadEventList();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //mWeekView.refreshDrawableState();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    calendar_view.refresh();
                }
            }, 1000);

        }
    }

    public void loadEventList(){
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
        planlist = new ArrayList<>();
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

                one.index = planlist.size();
                planlist.add(one);
            }

            calendar_view.refresh();
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

    private void setMonthName(Calendar cal){
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        int year = cal.get(Calendar.YEAR);
        txt_month.setText(month_name + " " + year);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    private void initCalendar(){
        calendar_view.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);
                }
                if (cellType == BaseCellView.OUTSIDE_MONTH) {
                    cellView.setTextColor(getResources().getColor(R.color.date_outside_month_text_color_activity));
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (SquareCellView) inflater.inflate(R.layout.calendar_week_cell_view, null);
                }
                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return String.valueOf(defaultValue.charAt(0));
            }
        });
        calendar_view.setOnMonthChangeListener(new FlexibleCalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month, int direction) {
                if(direction == 1){
                    selcal.add(Calendar.MONTH, -1);
                } else{
                    selcal.add(Calendar.MONTH, 1);
                }
                setMonthName(selcal);
                calendar_view.refresh();
            }
        });
    }

    private void initEvents(){
        calendar_view.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<? extends Event> getEventsForTheDay(int year, int month, int day) {
                int cyear = selcal.get(Calendar.YEAR);
                int cmonth = selcal.get(Calendar.MONTH);
                String date_str = convStr(month + 1) + "-" + convStr(day);

                if(year == cyear && cmonth == month){
                    List<CustomEvent> colorLst1 = new ArrayList<>();
                    for(int j = 0; j < planlist.size(); j++){
                        PlanModel one = planlist.get(j);
                        String dd = convertDate(one.event_date);
                        if(one.event_type.equals(GlobalVariables.EVENT_TYPE_DAILY)){
                            if(dd.compareTo(date_str) <= 0){
                                colorLst1.add(new CustomEvent(android.R.color.holo_green_light));
                            }
                        } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String ymd = convStr(day) + "-" + convStr(month + 1) + "-" + convStr(year);
                            try{
                                Date date = dateFormat.parse(ymd);
                                final Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                if(calendar.get(Calendar.DAY_OF_WEEK) == getDayOfWeekDay(one.event_day) && getMonthFromDate(one.event_date) <= month+1){
                                    colorLst1.add(new CustomEvent(android.R.color.holo_blue_light));
                                }
                            } catch (Exception ex){

                            }
                        } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_MONTHLY)){
                            if(getDayFromDate(one.event_date) == day && getMonthFromDate(one.event_date) <= month+1){
                                colorLst1.add(new CustomEvent(android.R.color.holo_red_light));
                            }
                        } else {
                            if(dd.equals(date_str)){
                                colorLst1.add(new CustomEvent(android.R.color.holo_purple));
                            }
                        }
                    }

                    return colorLst1;
                }

                return null;
            }
        });
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

    private String convertDate(String datestr){
        String[] temp = datestr.split("-");
        String ret_str = temp[1] + "-" + temp[2];
        return ret_str;
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

}
