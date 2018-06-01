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

public class TodoTodayAdapter  extends RecyclerView.Adapter<TodoTodayAdapter.ViewHolder> {

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
        public final TextView nameTxt, despTxt, timeTxt, diffTxt;
        public final CheckBox itemCheck;
        public final ImageView iv_del;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.tv_todayitem_name);
            despTxt = (TextView) v.findViewById(R.id.tv_todayitem_desp);
            diffTxt = (TextView) v.findViewById(R.id.tv_todayitem_diff);
            timeTxt = (TextView) v.findViewById(R.id.tv_todayitem_time);
            itemCheck = (CheckBox) v.findViewById(R.id.ch_today_item);
            iv_del = (ImageView) v.findViewById(R.id.img_today_item);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public TodoTodayAdapter(List<ModelTask> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.todo_list_today_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ModelTask one = mDataSet.get(position);
        viewHolder.nameTxt.setText(one.mName);
        viewHolder.despTxt.setText(one.mDesc);
        viewHolder.timeTxt.setText(one.mTime);
        viewHolder.diffTxt.setText("");

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
