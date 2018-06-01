package com.app.studentessentials;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.RecipeModel;
import com.app.studentessentials.Models.ReviewModel;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class ShowRecipes extends AppCompatActivity {
    ImageView btn_back;
    ImageView img_photo, img_favorite;
    TextView tv_title, tv_comments, tv_likes;
    LinearLayout lnr_comment, lnr_like, lnr_rating;
    MaterialRatingBar rb_rate;
    JustifiedTextView jtv_description, jtv_instruction, jtv_ingredient, jtv_tip;
    TextView tv_calory, tv_protein, tv_fat, tv_carb, tv_category;
    Button rtb_trans;

    SharedPreferences sharedPreferences;

    RecipeModel curObj;
    boolean isFavorite = false;
    ArrayList<ReviewModel> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipes);
        curObj = (RecipeModel) getIntent().getSerializableExtra("recipe");

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initLayout();
        setProcessFunction();
        loadData();
    }

    private void initLayout(){
        img_photo = (ImageView) findViewById(R.id.img_recipe_photo);
        img_favorite = (ImageView) findViewById(R.id.img_recipe_favorite);

        tv_title = (TextView) findViewById(R.id.tv_recipe_title);
        tv_comments = (TextView) findViewById(R.id.tv_comment_number);
        tv_likes = (TextView) findViewById(R.id.tv_like_number);

        lnr_comment = (LinearLayout) findViewById(R.id.lnr_recipe_comment);
        lnr_like = (LinearLayout) findViewById(R.id.lnr_recipe_like);
        lnr_rating = (LinearLayout) findViewById(R.id.lnr_rating);

        rb_rate = (MaterialRatingBar) findViewById(R.id.rb_recipe_rate);

        jtv_description = (JustifiedTextView) findViewById(R.id.jtv_description);
        jtv_instruction = (JustifiedTextView) findViewById(R.id.jtv_instruction);
        jtv_ingredient = (JustifiedTextView) findViewById(R.id.jtv_ingredient);
        jtv_tip = (JustifiedTextView) findViewById(R.id.jtv_tip);

        tv_calory = (TextView) findViewById(R.id.tv_recipe_calory);
        tv_protein = (TextView) findViewById(R.id.tv_recipe_protein);
        tv_fat = (TextView) findViewById(R.id.tv_recipe_carb);
        tv_carb = (TextView) findViewById(R.id.tv_recipe_carb);

        tv_category = (TextView) findViewById(R.id.tv_recipe_category);
        rtb_trans = (Button) findViewById(R.id.rtb_trans);
        rtb_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setProcessFunction(){
        img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavorite();
            }
        });

        lnr_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowRecipes.this, AddComment.class);
                intent.putExtra("key", curObj.db_key);
                startActivity(intent);
            }
        });

        lnr_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reviewList.size() > 0){
                    Intent intent = new Intent(ShowRecipes.this, RecipeReviews.class);
                    intent.putExtra("list", reviewList);
                    intent.putExtra("rating", String.valueOf(rb_rate.getRating()));
                    startActivity(intent);
                }

            }
        });
    }

    private void redrawFavorite(){
        if(isFavorite){
            img_favorite.setImageResource(R.drawable.ic_favorite_full);
        } else{
            img_favorite.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void setFavorite(){
        final  String var_email = EncodeString(sharedPreferences.getString("email", ""));
        String url = firebase_base_url+"Recipes.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"Recipes/"+"favorites");

                if(isFavorite){
                    reference.child(var_email).child(curObj.db_key).setValue("0");
                } else{
                    reference.child(var_email).child(curObj.db_key).setValue("1");
                }
                isFavorite = !isFavorite;
                redrawFavorite();

            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

    }

    private void loadData(){
        if(curObj == null) return;
        Picasso.with(this).load(curObj.image).into(img_photo);

        tv_title.setText(curObj.title);

        jtv_description.setText(curObj.description);
        jtv_ingredient.setText(curObj.ingredient);
        jtv_instruction.setText(curObj.instruction);
        jtv_tip.setText(curObj.tip);

        tv_category.setText(curObj.category);
        if(curObj.calory.length()>0)
            tv_calory.setText(curObj.calory);
        if(curObj.protein.length()>0)
            tv_protein.setText(curObj.protein);
        if(curObj.fat.length()>0)
            tv_fat.setText(curObj.fat);
        if(curObj.carb.length()>0)
            tv_carb.setText(curObj.carb);

        reviewList = new ArrayList<>();
        loadComments();
        loadFavorites();
    }

    private void loadComments(){
        String url = firebase_base_url+"Recipes/comments/" + curObj.db_key + ".json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doCommentSuccess(s);
                //System.out.println("------------00000000000000000000000 ----- 00000000000000000000000000  "+s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError);
                //pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

    }

    private void doCommentSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";
            float rate_val = 0.0f;
            while(i.hasNext()){
                key = i.next().toString();
                ReviewModel one = new ReviewModel();
                one.usermail = key;
                JSONObject subobj = obj.getJSONObject(key);
                String rate_str = subobj.getString("rate");
                rate_val += Float.parseFloat(rate_str);
                one.rating = rate_str;
                one.comment = subobj.getString("comment");
                reviewList.add(one);
            }

            if(reviewList.size() > 0){
                tv_comments.setText(String.valueOf(reviewList.size()));
                rb_rate.setRating((float) rate_val/reviewList.size());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFavorites(){

        String url = firebase_base_url+"Recipes/favorites.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doIDSuccess(s);
                //System.out.println("------------00000000000000000000000 ----- 00000000000000000000000000  "+s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError);
                //pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    private void doIDSuccess(String s){
        String var_email = EncodeString(sharedPreferences.getString("email", ""));
        try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";
            int fav_number = 0;
            while(i.hasNext()){
                key = i.next().toString();
                JSONObject subobj = obj.getJSONObject(key);

                Iterator j = subobj.keys();
                String subkey = "";
                while (j.hasNext()){
                    subkey = j.next().toString();
                    String fav = subobj.getString(subkey);
                    if(subkey.equals(curObj.db_key) && fav.equals("1")){
                        fav_number++;
                        if(key.equals(var_email)){
                            isFavorite = true;
                        }
                    }
                }


            }

            redrawFavorite();
            if(fav_number > 0)
                tv_likes.setText(String.valueOf(fav_number));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
}
