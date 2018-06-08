package com.app.studentessentials;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.JavaClasses.PagerRecipeAdapter;
import com.app.studentessentials.JavaClasses.PagerUtilityAdapter;
import com.app.studentessentials.Models.RecipeModel;

public class Receipe extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private TabLayout tabLayout;
    ImageView btn_back;
    FloatingActionButton receipe_fab;


    private String tab_names[] = {"Recipes","My Recipes","Favourites"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipe);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        receipe_fab = (FloatingActionButton) findViewById(R.id.receipe_fab);
        receipe_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Receipe.this , AddRecipe.class));
            }
        });
        receipe_fab.setVisibility(View.GONE);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabIcons();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerRecipeAdapter adapter = new PagerRecipeAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 1){
                    receipe_fab.setVisibility(View.VISIBLE);
                } else{
                    receipe_fab.setVisibility(View.GONE);
                }
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
        if(tab.getPosition() == 1 ){
            receipe_fab.setVisibility(View.VISIBLE);
        } else{
            receipe_fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupTabIcons() {
        for(int i = 0 ; i < tab_names.length; i++ ){
            tabLayout.addTab(tabLayout.newTab().setText(tab_names[i]));

            View tabOne = (View) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabTitle = tabOne.findViewById(R.id.tab_title);
            tabTitle.setText(tab_names[i]);
            ImageView image = tabOne.findViewById(R.id.tab_image);
            if(i == 0){
                image.setImageResource(R.drawable.ic_recipe_all);
            } else if(i == 1){
                image.setImageResource(R.drawable.ic_recipe_my);
            } else if(i == 2){
                image.setImageResource(R.drawable.ic_recipe_fav);
            } else{
                image.setImageResource(R.drawable.ic_recipe_all);
            }
            tabLayout.getTabAt(i).setCustomView(tabOne);
        }
    }
}
