package com.app.studentessentials.JavaClasses;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.studentessentials.HomeActivity;
import com.app.studentessentials.MyApp;
import com.app.studentessentials.R;

import java.util.Calendar;
import java.util.Date;

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;
    private static int noti_index = 1;

    public AlarmService()
    {
        super("AlarmService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {

        Calendar now = Calendar.getInstance();
        String currentHour = convStr(now.get(Calendar.HOUR_OF_DAY));
        String currentMinutes = convStr(now.get(Calendar.MINUTE));
        String currentTime = currentHour + ":" + currentMinutes;
        //System.out.println("-------------mmmmmmmmmmmmmmm-------------"+currentTime);

        String cur_day = convStr(now.get(Calendar.DAY_OF_MONTH));
        String cur_month = convStr(now.get(Calendar.MONTH)+1);
        String cur_year = convStr(now.get(Calendar.YEAR));
        String cur_date = cur_year + "-" + cur_month + "-" + cur_day;
        String event = "";
        if(MyApp.getInstance().bNoti_planner)
            event = getEventName(currentTime);
        if(MyApp.getInstance().bNoti_todo)
            event = getTaskEventName(currentTime, cur_date);
        if(MyApp.getInstance().bNoti_utility)
            event = getUtilityName(currentTime);
//        if (event.equalsIgnoreCase("not found !!"))
//        {
//
//        }
//        else
//        {
//            sendNotification("Event Name " + event);
//        }

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

    private void sendNotification(String event) {
    String eventName ="", eventMessage="";
        try {
            String[] eventInfo = event.split(",");
             eventName = eventInfo[0];
             eventMessage = eventInfo[1];
        }catch (ArrayIndexOutOfBoundsException e){}

        Log.d("AlarmService", "Preparing to send notification...: " + eventName);
        /*alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent alarmClass = new Intent(this, HomeActivity.class);
        alarmClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        alarmClass.putExtra("eventName", eventName);
        alarmClass.putExtra("eventMessage", eventMessage);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, alarmClass, PendingIntent.FLAG_ONE_SHOT);//0

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle(eventName).setLargeIcon(icon).setSmallIcon(R.drawable.app_logo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(eventName))
                .setContentText(eventMessage);

        //Vibration
        alamNotificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        alamNotificationBuilder.setLights(Color.RED, 3000, 3000);

        //Ton
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(noti_index, alamNotificationBuilder.build());*/
        //------------------------------------------------------------------
        /*final Notification notification = new Notification.Builder(this)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        noti_index,
                        new Intent(this, HomeActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setContentTitle(eventName)
                .setAutoCancel(true)
                .setContentText(eventMessage)
                // Starting on Android 5, only the alpha channel of the image matters.
                // https://stackoverflow.com/a/35278871/895245
                // `android.R.drawable` resources all seem suitable.
                .setSmallIcon(R.drawable.app_logo)
                // Color of the background on which the alpha image wil drawn white.
                //.setColor(Color.GRAY)
                .build();
        final NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(noti_index, notification);*/

        /*Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventMessage", eventMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSmallIcon(R.drawable.app_logo)
                .setContentTitle(eventName)
                .setContentText(eventMessage)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            Uri noti_snd = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), noti_snd);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(noti_index , notificationBuilder.build());*/
        // There are hardcoding only for show it's just strings
        String name = "studentessentials_channel";
        String id = "studentessentials_channel_1"; // The user-visible name of the channel.
        String description = "studentessentials_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        final NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[] { 500, 500, 1000, 1000, 1000 });
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(eventName)  // required
                    .setSmallIcon(R.drawable.app_icon) // required
                    .setContentText(eventMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(eventName)
                    .setVibrate(new long[] { 500, 500, 1000, 1000, 1000 });
        } else {

            builder = new NotificationCompat.Builder(this);

            intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(eventName)                           // required
                    .setSmallIcon(R.drawable.app_icon) // required
                    .setContentText(eventMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(eventName)
                    .setVibrate(new long[] { 500, 500, 1000, 1000, 1000 })
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(noti_index, notification);
        //-----------------------------------------------------------------
        noti_index++ ; if(noti_index == Integer.MAX_VALUE) noti_index = 1;
        Log.d("AlarmService", "Notification sent.");
    }

    // Getting single contact
    String getEventName(String time) {
        String eventName = "", date ="" , type ="" , day = "", reminder="", gaps = "", newtime = "";
        String selectQuery = "SELECT * FROM planner_event WHERE event_time"+" = '" + time + "'";

        try{
            SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    eventName = cursor.getString(2) + "," + cursor.getString(4);
                    date = cursor.getString(5);
                    reminder = cursor.getString(7);
                    type = cursor.getString(8);
                    day = cursor.getString(9);


                    if (reminder.equals("1")){
                        checkEvent(eventName, date, type, day);
                    }
                    // Adding evemtItemsClass to list
                } while (cursor.moveToNext());
            } else {
                eventName = "not found !!";
            }

            db.close();
        }catch (Exception ex){

        }

        return eventName;
    }

    public void checkEvent(String name, String date, String type, String day){
        if(type.equals(GlobalVariables.EVENT_TYPE_DAILY)){
            sendNotification("Event Name : " + name);
        }else if(type.equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
            Calendar sCalendar = Calendar.getInstance();
            //System.out.println(sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())+"  --------------------------------------------------"+ day);
            int week = sCalendar.get(Calendar.DAY_OF_WEEK);
            String weekday_str = getWeekDay(week);
            if(weekday_str.equals(day)){
                sendNotification("Event Name : " + name);
            }
        }else if(type.equals(GlobalVariables.EVENT_TYPE_MONTHLY)){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            //String sDate = c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH)+1);// + "-" + c.get(Calendar.YEAR);
            //System.out.println(sDate+"  --------------------------------------------------"+ date);
            if(getDayFromDate(date) == c.get(Calendar.DAY_OF_MONTH)){
                sendNotification("Event Name : " + name);
            }
        } else if(type.equals(GlobalVariables.EVENT_TYPE_YEARLY)){
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());

            if(getDayFromDate(date) == c1.get(Calendar.DAY_OF_MONTH) && getMonthFromDate(date) == c1.get(Calendar.MONTH)+1){
                sendNotification("Event Name : " + name);
            }
        } else{
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            String sDate = c1.get(Calendar.YEAR) + "-" + convStr(c1.get(Calendar.MONTH)+1) + "-" + convStr(c1.get(Calendar.DAY_OF_MONTH));

            if(date.equals(sDate)){
                sendNotification("Event Name : " + name);
            }
        }
    }

    String getTaskEventName(String time, String date){
        String taskName = "", reminder="";
        String selectQuery = "SELECT * FROM task_event WHERE task_date = '" + date + "' AND task_time = '" + time + "';";

        try{
            SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    taskName = cursor.getString(1) + "," + cursor.getString(2);
                    reminder = cursor.getString(5);

                    if (reminder.equals("1")){
                        checkTaskEvent(taskName);
                    }
                    // Adding evemtItemsClass to list
                } while (cursor.moveToNext());
            } else {
                taskName = "not found !!";
            }
        }catch (Exception ex){

        }

        return taskName;
    }

    public void checkTaskEvent(String name){
        sendNotification("Task Name : " + name);
    }

    public int getDayFromDate(String dd){
        String[] separated = dd.split("-");
        int ret = Integer.parseInt(separated[2]);
        return ret;
    }

    private int getMonthFromDate(String dd){
        String[] separated = dd.split("-");
        int ret = Integer.parseInt(separated[1]);
        return ret;
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

    // Getting single contact
    String getUtilityName(String time) {
        String eventName = "", date ="";
        String selectQuery = "SELECT * FROM utility_event WHERE task_time"+" = '" + time + "'";

        try{
            SQLiteDatabase db = Functions.databaseInit(getApplicationContext());
            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    eventName = cursor.getString(1) + "," + cursor.getString(2);
                    date = cursor.getString(3);
                    checkUtility(eventName, date);
                    // Adding evemtItemsClass to list
                } while (cursor.moveToNext());
            } else {
                eventName = "not found !!";
            }
        }catch (Exception ex){

        }

        return eventName;
    }

    public void checkUtility(String name, String date){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        if(getDayFromDate(date) == c.get(Calendar.DAY_OF_MONTH)){
            sendNotification("Utility Name : " + name);
        }
    }

}