package com.app.studentessentials.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.app.studentessentials.Adapters.ReceipeMyAdapter;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.RecipeModel;
import com.app.studentessentials.R;
import com.app.studentessentials.ShowRecipes;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class FragmentMyReceipes extends Fragment{

    View view;
    RecyclerView recycler_recipe;
    ReceipeMyAdapter receipeMyAdapter;
    List<RecipeModel> mydata;
    ProgressDialog pd;
    SharedPreferences sharedPreferences;

    public FragmentMyReceipes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_myrecipe, container, false);

        sharedPreferences = getContext().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        initLayout(view);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();
        loadMyRecipes();

        return view;
    }

    private void initLayout(View view){
        // Inflate the layout for this fragment
        mydata = new ArrayList<>();
        recycler_recipe=(RecyclerView) view.findViewById(R.id.recycler_recipe);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_recipe.setLayoutManager(layoutManager);
        receipeMyAdapter = new ReceipeMyAdapter(mydata, getActivity().getApplicationContext());
        receipeMyAdapter.setOnItemClickListener(new ReceipeMyAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                RecipeModel obj = mydata.get(position);
                Intent intent = new Intent(getContext(), ShowRecipes.class);
                intent.putExtra("recipe", obj);
                startActivity(intent);
                return 0;
            }
        });
        recycler_recipe.setAdapter(receipeMyAdapter);

    }

    private void loadMyRecipes(){
        String url = firebase_base_url+"Recipes/foods.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    public void doOnSuccess(String s){
        final  String var_email = sharedPreferences.getString("email", "");
        try {
            mydata = new ArrayList<>();
            JSONObject obj = new JSONObject(s);
            int flag = 0 , total_uses =0;
            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                RecipeModel one = new RecipeModel();
                one.db_key = key;
                JSONObject emp=(new JSONObject(s)).getJSONObject(key);
                one.title=emp.getString("name");
                one.image = emp.getString("image");
                one.category = emp.getString("category");
                one.description = emp.getString("description");
                one.ingredient = emp.getString("ingredient");
                one.instruction = emp.getString("instruction");
                one.calory = emp.getString("calory");
                one.protein = emp.getString("protein");
                one.fat = emp.getString("fat");
                one.carb = emp.getString("carb");
                one.tip = emp.getString("tip");
                one.usermail = emp.getString("user");

                if(one.usermail.equals(var_email)){
                    one.index = mydata.size();
                    mydata.add(one);
                }
            }
            pd.dismiss();

            receipeMyAdapter.setDataList(mydata);
            receipeMyAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            pd.dismiss();
        }
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
}
