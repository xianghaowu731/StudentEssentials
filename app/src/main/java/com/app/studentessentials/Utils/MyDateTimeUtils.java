package com.app.studentessentials.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.app.studentessentials.JavaClasses.AlarmReciever;
import com.app.studentessentials.R;

import static com.app.studentessentials.Utils.NotificationPublisher.NOTIFICATION;
import static com.app.studentessentials.Utils.NotificationPublisher.NOTIFICATION_ID;

public class MyDateTimeUtils {
    // Three simpledateformat for each specific use
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /* This return a formatted date if the parameter passed in is empty.
     It is used in case the user select the "set time" option without first "set date".
     The default date is today.
     We can have a equivalent fillTimeIfEmpty but I decided not to implement that
     Because time range is wider so it's hard to choose 1 point of time as default.*/
    public String fillDateIfEmpty(String date) {
        if (date.equals("")) {
            Calendar calendar = Calendar.getInstance();
            return dateFormatter.format(calendar.getTime());
        }
        else return date;
    }

    /*This returns a date string with passed-in integer year, months, dayofmonth.*/
    public String dateToString(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        return dateFormatter.format(calendar.getTime());
    }

    /*This returns a time string with passed-in integer year, months, dayofmonth.*/
    public String timeToString(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0,0,0,hourOfDay,minute);
        return timeFormatter.format(calendar.getTime());
    }

    public String getReminderedDateTime(String event_date, String event_reminder) {
        Date date = null;
        try {
            date = dateTimeFormatter.parse(event_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (event_reminder.equals("None reminder")) {
            return event_date;
        } else {
            String reminderedString = "";

            if (event_reminder.equals("1 minute in advance")) {
                calendar.add(Calendar.MINUTE, -1);
                reminderedString = dateTimeFormatter.format(calendar.getTime());

            } else if (event_reminder.equals("5 minutes in advance")) {
                calendar.add(Calendar.MINUTE, -5);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("10 minutes in advance")) {
                calendar.add(Calendar.MINUTE, -10);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("20 minutes in advance")) {
                calendar.add(Calendar.MINUTE, -20);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("30 minutes in advance")) {
                calendar.add(Calendar.MINUTE, -30);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("1 hour in advance")) {
                calendar.add(Calendar.HOUR, -1);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("2 hours in advance")) {
                calendar.add(Calendar.HOUR, -2);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("1 day in advance")) {
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            } else if (event_reminder.equals("1 week in advance")) {
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                reminderedString = dateTimeFormatter.format(calendar.getTime());
            }

            return reminderedString;
        }
    }

    /* this check if a date user chooses is not in the past by comparing to current date*/
    public boolean checkInvalidDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date dateSet = calendar.getTime();
        return (today.compareTo(dateSet) > 0);
    }

    /* This check if a time user chooses is not in the past by comparing to current time */
    public boolean checkInvalidTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
        return (hourOfDay < nowHour || (hourOfDay == nowHour && minute <= nowMinute));
    }

    /* this schedule notification to the time that user set*/
    public void ScheduleNotification(Notification notification,
                                     Context context, String notificationID, String dateTime, Boolean isRepetition, String repetition) {
        Intent notificationIntent = new Intent(context, AlarmReciever.class);
        notificationIntent.putExtra(NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // parse string parameter to milliseconds for later alarm set
        Date futureInMillis = null;
        try {
            futureInMillis = dateTimeFormatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isRepetition) {
            if (repetition.equals("Daily"))
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
            if (repetition.equals("Weekly"))
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis.getTime(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            if (repetition.equals("Monthly"))
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis.getTime(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);
            if (repetition.equals("Yearly"))
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis.getTime(), AlarmManager.INTERVAL_DAY * 365, pendingIntent);
        } else
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis.getTime(), pendingIntent);
    }

    /* this cancel a future notification if item is deleted or editted */
    public void cancelScheduledNotification(Notification notification,
                                            Context context, String notificationID) {
        Intent notificationIntent = new Intent(context, AlarmReciever.class);
        notificationIntent.putExtra(NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /* This is a helper function to build a notification object */
    public Notification getNotification(String content, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_ID);
        builder.setContentTitle("Task to be done");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher));
        builder.setShowWhen(true);
        return builder.build();
    }
}
