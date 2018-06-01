package com.app.studentessentials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.Adapters.RewardsAdapter;
import com.app.studentessentials.Gsons.RewardModel;

public class Rewards extends AppCompatActivity {
    CardView card_item;
    RecyclerView list_reward;
    ImageView btn_back;
    String[] listItem = {"TESCO", "SAINSBURY", "WAITROSE", "M&S", "ICELAND"};
    List<RewardModel> items = new ArrayList<>();
    RewardsAdapter rewardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        initilizeView();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void initilizeView(){
        btn_back = (ImageView) findViewById(R.id.btn_back);
        list_reward = (RecyclerView) findViewById(R.id.list_reward);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        list_reward.setLayoutManager(layoutManager);
        getListItems();
        rewardsAdapter = new RewardsAdapter(items, getApplicationContext());
        rewardsAdapter.setOnItemClickListener(new RewardsAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                Intent intent = new Intent(Rewards.this, CardScan.class);
                intent.putExtra("card_pos", position);
                startActivity(intent);
                return 0;
            }
        });

        list_reward.setAdapter(rewardsAdapter);
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    public void getListItems() {

        items = new ArrayList<>();
        items.add(new RewardModel("1","TESCO", R.drawable.reward_tesco));
        items.add(new RewardModel("2","SAINSBURY", R.drawable.reward_sainsbury));
        items.add(new RewardModel("3","WAITROSE", R.drawable.reward_waitrose));
        items.add(new RewardModel("4","M&S", R.drawable.reward_ms));
        items.add(new RewardModel("5","ICELAND", R.drawable.reward_iceland));
    }
}
