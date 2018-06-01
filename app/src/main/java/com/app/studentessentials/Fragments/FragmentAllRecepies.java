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

import com.app.studentessentials.Adapters.ReceipeAllAdapter;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.RecipeModel;
import com.app.studentessentials.R;
import com.app.studentessentials.ShowRecipes;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllRecepies extends Fragment {

    View view;
    RecyclerView recycler_recipe;
    ReceipeAllAdapter receipeAllAdapter;
    public List<RecipeModel> alldata;
    ProgressDialog pd;
    Spinner spin_recipe_frag;
    List<String> categories;
    EditText et_search;
    SharedPreferences sharedPreferences;


    public FragmentAllRecepies() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_all_recepies, container, false);
        // Inflate the layout for this fragment

        sharedPreferences = getContext().getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        initLayout(view);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();
        loadAllRecipes();
        return view;
    }

    private void initLayout(View view){
        alldata = new ArrayList<>();
        recycler_recipe=(RecyclerView) view.findViewById(R.id.recycler_recipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_recipe.setLayoutManager(layoutManager);
        receipeAllAdapter = new ReceipeAllAdapter(alldata, getActivity().getApplicationContext());
        receipeAllAdapter.setOnItemClickListener(new ReceipeAllAdapter.OnItemClickListener() {
            @Override
            public int onItemClick(int position) {
                RecipeModel obj = alldata.get(position);
                Intent intent = new Intent(getContext(), ShowRecipes.class);
                intent.putExtra("recipe", obj);
                startActivity(intent);
                return 0;
            }
        });
        recycler_recipe.setAdapter(receipeAllAdapter);

        spin_recipe_frag = (Spinner) view.findViewById(R.id.spin_recipe_frag);
        categories = new ArrayList<String>();
        categories.add("All");
        categories.add("Snack");
        categories.add("Breakfast");
        categories.add("Lunch");
        categories.add("Dinner");
        categories.add("Drink");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spin_recipe_frag.setAdapter(dataAdapter);

        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String filterStr = String.valueOf(charSequence) + ";" + categories.get(spin_recipe_frag.getSelectedItemPosition());
                receipeAllAdapter.getFilter().filter(filterStr);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spin_recipe_frag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String filterStr = et_search.getText().toString() + ";" + categories.get(i);
                receipeAllAdapter.getFilter().filter(filterStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    private void loadAllRecipes(){
        String url = firebase_base_url+"Recipes/foods.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
                //System.out.println("------------00000000000000000000000 ----- 00000000000000000000000000  "+s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    public void doOnSuccess(String s){
        final  String var_email = sharedPreferences.getString("email", "");
        try {
            alldata = new ArrayList<>();
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

                if(!one.usermail.equals(var_email)){
                    one.index = alldata.size();
                    alldata.add(one);
                }

            }
            pd.dismiss();

            receipeAllAdapter.setDataList(alldata);
            receipeAllAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            pd.dismiss();
        }
    }

}
