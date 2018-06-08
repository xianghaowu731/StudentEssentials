package com.app.studentessentials;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.app.studentessentials.JavaClasses.GlobalVariables;

public class SettingNotification extends AppCompatActivity {

    ImageView btn_back;
    Switch sw_planner, sw_todo, sw_utility;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sw_planner = (Switch) findViewById(R.id.switch_planner);
        sw_todo = (Switch) findViewById(R.id.switch_todo);
        sw_utility = (Switch) findViewById(R.id.switch_utility);

        sw_planner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putString("planner_noti_setting", "1");
                    MyApp.getInstance().bNoti_planner = true;
                } else{
                    editor.putString("planner_noti_setting", "0");
                    MyApp.getInstance().bNoti_planner = false;
                }
                editor.commit();
            }
        });

        sw_todo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putString("todo_noti_setting", "1");
                    MyApp.getInstance().bNoti_todo = true;
                } else{
                    editor.putString("todo_noti_setting", "0");
                    MyApp.getInstance().bNoti_todo = false;
                }
                editor.commit();
            }
        });

        sw_utility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putString("utility_noti_setting", "1");
                    MyApp.getInstance().bNoti_utility = true;
                } else{
                    editor.putString("utility_noti_setting", "0");
                    MyApp.getInstance().bNoti_utility = false;
                }
                editor.commit();
            }
        });

        initValue();
    }

    public void initValue(){
        String noti_str = sharedPreferences.getString("planner_noti_setting", "0");
        if(noti_str.equals("1")){
            sw_planner.setChecked(true);
        } else{
            sw_planner.setChecked(false);
        }
        noti_str = sharedPreferences.getString("utility_noti_setting", "0");
        if(noti_str.equals("1")){
            sw_utility.setChecked(true);
        } else{
            sw_utility.setChecked(false);
        }
        noti_str = sharedPreferences.getString("todo_noti_setting", "0");
        if(noti_str.equals("1")){
            sw_todo.setChecked(true);
        } else{
            sw_todo.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
