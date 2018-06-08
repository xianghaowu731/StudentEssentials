package com.app.studentessentials.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.PlanModel;
import com.app.studentessentials.R;

import java.util.List;

public class EventDailyAdapter extends RecyclerView.Adapter<EventDailyAdapter.ViewHolder>  {
    private List<PlanModel> mDataSet;

    private OnItemClickListener mOnItemClickListener;
    private OnItemEditListener onItemEditListener;
    private OnItemDeleteListener onItemDeleteListener;

    private final Context mContext;

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemEditListener (OnItemEditListener listener) {
        onItemEditListener = listener;
    }

    public void setOnItemDeleteListener (OnItemDeleteListener listener) {
        onItemDeleteListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txt_event_name,txt_time, txt_descrip ;
        public final LinearLayout lnr_color,lnr_delete, lnr_edit;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            txt_event_name = (TextView) v.findViewById(R.id.txt_event_name);
            txt_time = (TextView) v.findViewById(R.id.txt_time);
            txt_descrip = (TextView) v.findViewById(R.id.txt_descrip);
            lnr_color = (LinearLayout) v.findViewById(R.id.lnr_color);
            lnr_delete = (LinearLayout) v.findViewById(R.id.lnr_delete);
            lnr_edit = (LinearLayout) v.findViewById(R.id.lnr_edit);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public EventDailyAdapter(List<PlanModel> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.daily_planner_list_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        PlanModel one = mDataSet.get(position);
        viewHolder.txt_event_name.setText(one.event_name);
        viewHolder.txt_descrip.setText(one.event_desc);
        viewHolder.txt_time.setText(one.event_time);

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

        viewHolder.lnr_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemDeleteListener.onItemDelete(position);
            }
        });

        viewHolder.lnr_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemEditListener.onItemEdit(position);
            }
        });

        if(one.other.length() > 1){
            viewHolder.lnr_color.setBackgroundColor(mContext.getResources().getColor(Integer.parseInt(one.other)));
        } else{
            viewHolder.lnr_color.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
        }

        /*if(one.event_type.equals(GlobalVariables.EVENT_TYPE_DAILY)){
            viewHolder.lnr_color.setBackgroundColor(Color.parseColor("#b7e8fe"));
        } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_WEEKLY)){
            viewHolder.lnr_color.setBackgroundColor(Color.parseColor("#caffd1"));
        } else if(one.event_type.equals(GlobalVariables.EVENT_TYPE_MONTHLY)){
            viewHolder.lnr_color.setBackgroundColor(Color.parseColor("#ffd59f"));
        } else{
            viewHolder.lnr_color.setBackgroundColor(Color.parseColor("#f4e0f9"));
        }*/

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setDataList(List<PlanModel> dataSet){
        mDataSet = dataSet;
    }

    public interface OnItemClickListener {
        public int onItemClick(int position);
    }

    public interface OnItemEditListener {
        public int onItemEdit(int position);
    }

    public interface OnItemDeleteListener {
        public int onItemDelete(int position);
    }
}
