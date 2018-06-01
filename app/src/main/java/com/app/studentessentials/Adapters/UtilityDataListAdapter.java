package com.app.studentessentials.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.studentessentials.Models.UtilityModel;
import com.app.studentessentials.R;

import java.util.List;


public class UtilityDataListAdapter extends RecyclerView.Adapter<UtilityDataListAdapter.ViewHolder> {
    private List<UtilityModel> mDataSet;

    private OnItemSubmitListener mOnItemClickListener;

    private final Context mContext;

    public void setOnItemClickListener (OnItemSubmitListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTxt;
        public final TextView txt_meter_reading, txt_kwh_used;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.tv_month_name);
            txt_meter_reading = (TextView) v.findViewById(R.id.txt_meter_reading);
            txt_kwh_used = (TextView) v.findViewById(R.id.txt_kwh_used);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public UtilityDataListAdapter(List<UtilityModel> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.utility_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        UtilityModel one = mDataSet.get(position);
        viewHolder.nameTxt.setText(one.month);
        viewHolder.txt_meter_reading.setText(one.reading_value);
        viewHolder.txt_kwh_used.setText(one.used_value);

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setDataList(List<UtilityModel> dataSet){
        mDataSet = dataSet;
    }

    public interface OnItemSubmitListener{
        public int onItemSubmit(int position);
    }

}
