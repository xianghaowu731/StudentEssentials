package com.app.studentessentials.JavaClasses;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.studentessentials.R;
import com.app.studentessentials.ShowRecipes;


public class FavRecipeAdapter extends BaseAdapter{

    private final Activity context;
    private final Integer[] imgid;

    public FavRecipeAdapter(Activity context, Integer[] imgid) {
        this.context=context;
        this.imgid=imgid;
    }

    @Override
    public int getCount() {
        return imgid.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.recipe_list_row_data, null,true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img_recipe);
        imageView.setImageResource(imgid[position]);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ShowRecipes.class));
            }
        });

        return rowView;

    };
}