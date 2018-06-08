package com.app.studentessentials.Models;

import com.p_v.flexiblecalendar.entity.Event;

public class CustomEvent implements Event {

    private int color;

    public CustomEvent(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }
}