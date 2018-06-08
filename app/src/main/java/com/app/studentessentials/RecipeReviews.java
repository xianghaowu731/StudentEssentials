package com.app.studentessentials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.Adapters.RecipeReviewAdapter;
import com.app.studentessentials.Models.ReviewModel;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecipeReviews extends AppCompatActivity {

    ImageView btn_back;
    MaterialRatingBar rb_review_rate;
    TextView tv_avg_rate;
    RecyclerView recycle_reviews;
    ArrayList<ReviewModel> datalist;
    RecipeReviewAdapter recipeReviewAdapter;
    String avg_rate;
    Button btn_rating_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_reviews);

        avg_rate = getIntent().getStringExtra("rating");
        datalist = (ArrayList<ReviewModel>) getIntent().getSerializableExtra("list");

        btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initLayout();
    }

    private void initLayout(){
        rb_review_rate = (MaterialRatingBar) findViewById(R.id.rb_review_rate);
        tv_avg_rate = (TextView) findViewById(R.id.tv_avg_rate);
        btn_rating_trans = (Button) findViewById(R.id.btn_rating_trans);

        btn_rating_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        recycle_reviews = (RecyclerView) findViewById(R.id.recycle_reviews);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycle_reviews.setLayoutManager(layoutManager);
        recipeReviewAdapter = new RecipeReviewAdapter(datalist, getApplicationContext());
        recipeReviewAdapter.setOnItemClickListener(new RecipeReviewAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                //sel_pos = position;
                return 0;
            }
        });

        recycle_reviews.setAdapter(recipeReviewAdapter);

        rb_review_rate.setRating(Float.parseFloat(avg_rate));
        tv_avg_rate.setText("Overall Rating " + avg_rate + "/" + datalist.size());

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
