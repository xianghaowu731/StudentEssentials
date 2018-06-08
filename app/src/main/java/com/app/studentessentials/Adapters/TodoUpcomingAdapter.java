package com.app.studentessentials.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.studentessentials.Models.ModelTask;
import com.app.studentessentials.R;

import java.util.List;

public class TodoUpcomingAdapter extends RecyclerView.Adapter<TodoUpcomingAdapter.ViewHolder> {

    private List<ModelTask> mDataSet;

    private OnItemClickListener mOnItemClickListener;
    private OnItemDelClickListener mOnItemDelClickListener;

    private final Context mContext;

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemDelClickListener (OnItemDelClickListener listener) {
        mOnItemDelClickListener = listener;
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTxt, timeTxt, diffTxt, despTxt;
        public final CheckBox itemCheck;
        public final ImageView iv_del;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.tv_upitem_name);
            despTxt = (TextView) v.findViewById(R.id.tv_upitem_desp);
            diffTxt = (TextView) v.findViewById(R.id.tv_upitem_diff);
            timeTxt = (TextView) v.findViewById(R.id.tv_upitem_time);
            itemCheck = (CheckBox) v.findViewById(R.id.ch_upcoming_item);
            iv_del = (ImageView) v.findViewById(R.id.img_upcoming_item);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public TodoUpcomingAdapter(List<ModelTask> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.todo_list_upcoming_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ModelTask one = mDataSet.get(position);
        viewHolder.nameTxt.setText(one.mName);
        viewHolder.despTxt.setText(one.mDesc);
        viewHolder.timeTxt.setText(one.mTime);
        viewHolder.diffTxt.setText(one.mDate);

        if(one.isCompleted) {
            viewHolder.itemCheck.setChecked(true);
            viewHolder.nameTxt.setPaintFlags(viewHolder.nameTxt.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);//Paint.UNDERLINE_TEXT_FLAG);
        } else {
            viewHolder.itemCheck.setChecked(false);
            viewHolder.nameTxt.setPaintFlags(viewHolder.nameTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));//(~ Paint.UNDERLINE_TEXT_FLAG));
        }

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

        viewHolder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemDelClickListener != null) {
                    mOnItemDelClickListener.onItemDelClick(position);
                }
            }
        });
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setDataList(List<ModelTask> dataSet){
        mDataSet = dataSet;
    }

    public interface OnItemClickListener {
        public int onItemClick(int position);
    }

    public interface OnItemDelClickListener{
        public int onItemDelClick(int position);
    }
}
