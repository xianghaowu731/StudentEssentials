package com.app.studentessentials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.JavaClasses.HListViewModel;
import com.app.studentessentials.JavaClasses.HorizontalListAdapter;
import com.app.studentessentials.JavaClasses.VListViewAdapter;
import com.app.studentessentials.JavaClasses.VListViewModel;

public class ShowMonthlyDetail extends AppCompatActivity {

    ImageView img_back;
    RecyclerView vRecyclerView,hRecyclerView;
    VListViewAdapter adapter;
    HorizontalListAdapter radapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_monthly_detail);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //horizontally list view
        LinearLayoutManager hLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hRecyclerView = (RecyclerView)findViewById(R.id.hRecyclerView);
        hRecyclerView.setLayoutManager(hLayoutManager);
        hRecyclerView.setHasFixedSize(true);
        hRecyclerView.setNestedScrollingEnabled(false);
        radapter = new HorizontalListAdapter(ShowMonthlyDetail.this, getHListViewData());
        hRecyclerView.setAdapter(radapter);
        //vertically list view
        vRecyclerView = (RecyclerView)findViewById(R.id.vRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ShowMonthlyDetail.this);
        vRecyclerView.setLayoutManager(layoutManager);
        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setNestedScrollingEnabled(false);
        adapter = new VListViewAdapter(ShowMonthlyDetail.this, getVListViewData());
        vRecyclerView.setAdapter(adapter);


    }

    private List<HListViewModel> getHListViewData() {
        List<HListViewModel> hListView = new ArrayList<HListViewModel>();
        hListView.add(new HListViewModel("Jan", "$250"));
        hListView.add(new HListViewModel("Feb", "$550"));
        hListView.add(new HListViewModel("March", "$550"));
        hListView.add(new HListViewModel("April", "$850"));
        hListView.add(new HListViewModel("May", "$450"));
        hListView.add(new HListViewModel("June", "$750"));
        hListView.add(new HListViewModel("July", "$1050"));
        hListView.add(new HListViewModel("August", "$250"));
        return hListView;
    }

    private List<VListViewModel> getVListViewData()
    {
        List<VListViewModel> vListView=new ArrayList<VListViewModel>();
        vListView.add(new VListViewModel("1-May-2018","Groccery","xyz","$12"));
        vListView.add(new VListViewModel("10-May-2018","Electricity","abc","$10"));
        vListView.add(new VListViewModel("15-May-2018","Gas","pqr","$13"));
        vListView.add(new VListViewModel("18-May-2018","Hospitality","stu","$18"));
        vListView.add(new VListViewModel("21-May-2018","Study","mno","$21"));
        return  vListView;

    }


}