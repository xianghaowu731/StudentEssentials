package com.app.studentessentials;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.studentessentials.JavaClasses.PagerAdapter;

public class Planner extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private TabLayout tabLayout;
    ImageView btn_back;

    private String tab_names[] = {"Daily","Weekly","Monthly"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.planner_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Planner.this , AddPlanner.class));
            }
        });

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Adding the tabs using addTab() method

        setupTabIcons();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){}
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {
        for(int i = 0 ; i < tab_names.length; i++ ){
            tabLayout.addTab(tabLayout.newTab().setText(tab_names[i]));

            View tabOne = (View) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabTitle = tabOne.findViewById(R.id.tab_title);
            tabTitle.setText(tab_names[i]);
            ImageView image = tabOne.findViewById(R.id.tab_image);
            if(i == 0){
                image.setImageResource(R.drawable.ic_daily);
            } else if(i == 1){
                image.setImageResource(R.drawable.ic_weekly);
            } else if(i == 2){
                image.setImageResource(R.drawable.ic_monthly);
            } else{
                image.setImageResource(R.drawable.ic_daily);
            }
            tabLayout.getTabAt(i).setCustomView(tabOne);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
