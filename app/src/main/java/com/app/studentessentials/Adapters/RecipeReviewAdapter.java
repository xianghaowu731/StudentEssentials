package com.app.studentessentials.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.app.studentessentials.Models.ReviewModel;
import com.app.studentessentials.R;

public class RecipeReviewAdapter extends RecyclerView.Adapter<RecipeReviewAdapter.ViewHolder> {

    private List<ReviewModel> mDataSet;

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
        public final TextView nameTxt, ratingTxt, commentTxt;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.item_username);
            ratingTxt = (TextView) v.findViewById(R.id.item_rate);
            commentTxt = (TextView) v.findViewById(R.id.item_comment);

            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecipeReviewAdapter(List<ReviewModel> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recipe_review_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReviewModel one = mDataSet.get(position);
        viewHolder.nameTxt.setText(DecodeString(one.usermail));
        viewHolder.ratingTxt.setText("Rating : " + one.rating);
        viewHolder.commentTxt.setText(one.comment);

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setDataList(List<ReviewModel> dataSet){
        mDataSet = dataSet;
    }

    public interface OnItemClickListener {
        public int onItemClick(int position);
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
