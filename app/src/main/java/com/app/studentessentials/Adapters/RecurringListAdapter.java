package com.app.studentessentials.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.studentessentials.Models.MonthlyBudgetModel;
import com.app.studentessentials.R;

import java.util.List;

public class RecurringListAdapter extends RecyclerView.Adapter<RecurringListAdapter.ViewHolder> {

    private List<MonthlyBudgetModel> mDataSet;

    private OnItemDelClickListener mOnItemDelClickListener;

    private final Context mContext;

    public void setOnItemDelClickListener (OnItemDelClickListener listener) {
        mOnItemDelClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTxt, costTxt, startTxt, endTxt;
        public final ImageView iv_del;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.tv_bill_name);
            costTxt = (TextView) v.findViewById(R.id.tv_bill_cost);
            startTxt = (TextView) v.findViewById(R.id.tv_bill_start);
            endTxt = (TextView) v.findViewById(R.id.tv_bill_end);
            iv_del = (ImageView) v.findViewById(R.id.iv_bill_del);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecurringListAdapter(List<MonthlyBudgetModel> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recurring_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MonthlyBudgetModel one = mDataSet.get(position);
        viewHolder.nameTxt.setText(one.mName);
        viewHolder.costTxt.setText(one.mAmount);
        viewHolder.startTxt.setText(one.mDate);
        viewHolder.endTxt.setText(one.enddate);

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

    public void setDataList(List<MonthlyBudgetModel> dataSet){
        mDataSet = dataSet;
    }

    public interface OnItemDelClickListener{
        public int onItemDelClick(int position);
    }
}
