package com.app.studentessentials.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.Models.RecipeModel;
import com.app.studentessentials.R;

public class ReceipeFavoriteAdapter extends RecyclerView.Adapter<ReceipeFavoriteAdapter.ViewHolder> implements Filterable {

    private List<RecipeModel> dataset;
    private List<RecipeModel> filteredList;
    private RecipeFilter recipeFilter;
    private OnItemClickListener mOnItemClickListener;

    private final Context mContext;

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iv_img;
        public final TextView tv_title, tv_protein, tv_calory, tv_fat, tv_carb;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            iv_img = (ImageView) v.findViewById(R.id.img_recipe);
            tv_title = (TextView) v.findViewById(R.id.recipe_content);
            tv_calory = (TextView) v.findViewById(R.id.tv_recipe_item_calory);
            tv_protein = (TextView) v.findViewById(R.id.tv_recipe_item_protein);
            tv_fat = (TextView) v.findViewById(R.id.tv_recipe_item_fat);
            tv_carb = (TextView) v.findViewById(R.id.tv_recipe_item_carb);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public ReceipeFavoriteAdapter(List<RecipeModel> dataSet, Context context) {
        dataset = dataSet;
        mContext = context;
        filteredList = dataSet;
        getFilter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recipe_list_row_data, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        RecipeModel one = filteredList.get(position);

        viewHolder.tv_title.setText(one.title);
        if(one.protein.length()>0)
            viewHolder.tv_protein.setText(one.protein);
        if(one.calory.length()>0)
            viewHolder.tv_calory.setText(one.calory);
        if(one.fat.length()>0)
            viewHolder.tv_fat.setText(one.fat);
        if(one.carb.length()>0)
            viewHolder.tv_carb.setText(one.carb);

        Picasso.with(mContext).load(one.image).into(viewHolder.iv_img);

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    int pos = filteredList.get(position).index;
                    mOnItemClickListener.onItemClick(pos);
                }
            }
        });

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setDataList(List<RecipeModel> dataSet){
        dataset = new ArrayList<>(dataSet);
        filteredList = new ArrayList<>(dataSet);
    }

    public interface OnItemClickListener {
        public int onItemClick(int position);
    }

    /**
     * Get custom filter
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (recipeFilter == null) {
            recipeFilter = new RecipeFilter();
        }
        return recipeFilter;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class RecipeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                List<RecipeModel> tempList = new ArrayList<RecipeModel>();

                String const_str = String.valueOf(constraint);
                String[] arrayString = const_str.split(";");
                // search content in friend list
                for (RecipeModel one : dataset) {

                    if (one.title.toLowerCase().contains(arrayString[0].toLowerCase()) || arrayString[0].length() == 0) {
                        if(arrayString[1].equals("All") || one.category.equals(arrayString[1])){
                            tempList.add(one);
                        }
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = dataset.size();
                filterResults.values = dataset;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<RecipeModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
