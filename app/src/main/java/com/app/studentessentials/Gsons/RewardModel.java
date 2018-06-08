package com.app.studentessentials.Gsons;

public class RewardModel
{
    public String id;
    public String card_name;
    public Integer image;
    public String description;
    public String url_link;
    //private String time;

    public RewardModel(String id, String card_name, Integer image) {
        this.id = id;
        this.card_name = card_name;
        this.image = image;
    }
}
