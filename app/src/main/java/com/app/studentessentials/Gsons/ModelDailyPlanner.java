package com.app.studentessentials.Gsons;

public class ModelDailyPlanner {
    public String root_id ,id, date, description, event, event_type, location, event_day, reminder_status, time ;

    public ModelDailyPlanner(String root_id, String id, String date, String description, String event, String event_type, String location,String event_day, String reminder_status, String time) {
        this.root_id = root_id;
        this.id = id;
        this.date = date;
        this.description = description;
        this.event = event;
        this.event_type = event_type;
        this.event_day =event_day;

        this.location = location;
        this.reminder_status = reminder_status;
        this.time = time;
    }

    public String getId() {
        return id;
    }
}
