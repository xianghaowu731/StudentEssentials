package com.app.studentessentials;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import com.app.studentessentials.JavaClasses.GlobalVariables;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class AddComment extends AppCompatActivity {

    ImageView btn_back;
    Button btn_add_comment;
    EditText comment_et_content;
    TextView comment_tv_rating;
    MaterialRatingBar rb_comment_rate;

    String food_key;

    SharedPreferences sharedPreferences;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        food_key = getIntent().getStringExtra("key");

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

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
        btn_add_comment = (Button) findViewById(R.id.btn_add_comment);
        comment_et_content = (EditText) findViewById(R.id.comment_et_content);
        comment_tv_rating = (TextView) findViewById(R.id.comment_tv_rating);
        rb_comment_rate = (MaterialRatingBar) findViewById(R.id.rb_comment_rate);
        rb_comment_rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                comment_tv_rating.setText(String.valueOf(v));
            }
        });

        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                addComment();
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage("Sending...");
    }

    private void addComment(){
        final  String var_email = EncodeString(sharedPreferences.getString("email", ""));
        String url = firebase_base_url+"Recipes.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"Recipes/");

                reference.child("comments").child(food_key).child(var_email).child("rate").setValue(comment_tv_rating.getText().toString());
                reference.child("comments").child(food_key).child(var_email).child("comment").setValue(comment_et_content.getText().toString());
                pd.dismiss();
                onBackPressed();
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Toast.makeText(AddComment.this, "Add Comment Failed. Please try to do again.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

}
