package com.app.studentessentials.Adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.app.studentessentials.Models.MonthlyBudgetModel;
import com.app.studentessentials.R;


public class BudgetManageAdapter extends RecyclerView.Adapter<BudgetManageAdapter.ViewHolder> {

    private List<MonthlyBudgetModel> mDataSet;
    private int curbudget = 0;

    private OnItemClickListener mOnItemClickListener;
    private OnItemAddClickListener mOnItemAddClickListener;
    private OnItemDelClickListener mOnItemDelClickListener;

    private final Context mContext;

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemAddClickListener (OnItemAddClickListener listener) {
        mOnItemAddClickListener = listener;
    }

    public void setOnItemDelClickListener (OnItemDelClickListener listener) {
        mOnItemDelClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView monthTxt, dateTxt, recurringTxt, amountTxt, totalTxt, nameTxt;
        public final LinearLayout lnr_content;
        public final ImageView img_add, img_itemdel;
        public final View mItemView;
        // We'll use this field to showcase matching the holder from the test.

        ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.tv_item_name);
            monthTxt = (TextView) v.findViewById(R.id.tv_item_month);
            lnr_content = (LinearLayout) v.findViewById(R.id.lnr_content);
            dateTxt = (TextView) v.findViewById(R.id.tv_item_date);
            recurringTxt = (TextView) v.findViewById(R.id.tv_item_recuring);
            amountTxt = (TextView) v.findViewById(R.id.tv_item_amount);
            totalTxt = (TextView) v.findViewById(R.id.tv_item_total);
            img_add = (ImageView) v.findViewById(R.id.img_add);
            img_itemdel = (ImageView) v.findViewById(R.id.img_itemdel);
            mItemView = v;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public BudgetManageAdapter(List<MonthlyBudgetModel> dataSet, int budget, Context context) {
        mDataSet = dataSet;
        mContext = context;
        curbudget = budget;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.badget_manage_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MonthlyBudgetModel one = mDataSet.get(position);
        viewHolder.monthTxt.setText(one.mMonth);
        viewHolder.dateTxt.setText(one.mDate);
        viewHolder.recurringTxt.setText(one.mRecurring);
        viewHolder.amountTxt.setText(one.mAmount);
        viewHolder.totalTxt.setText("Total Money Spent: £" + one.mTotal);
        viewHolder.nameTxt.setText(one.mName);

        if(one.bExpand){
            viewHolder.lnr_content.setVisibility(View.VISIBLE);
        } else{
            viewHolder.lnr_content.setVisibility(View.GONE);
        }

        if(Integer.parseInt(one.mTotal) == curbudget){
            viewHolder.totalTxt.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_blue));
        } else if(Integer.parseInt(one.mTotal) < curbudget){
            viewHolder.totalTxt.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_green));
        }

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

        viewHolder.img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemAddClickListener != null) {
                    mOnItemAddClickListener.onItemAddClick(position);
                }
            }
        });

        viewHolder.img_itemdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemDelClickListener != null){
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

    public void setDataList(List<MonthlyBudgetModel> dataSet, int budget){
        mDataSet = dataSet;
        curbudget = budget;
    }

    public interface OnItemClickListener {
        public int onItemClick(int position);
    }

    public interface OnItemAddClickListener {
        public int onItemAddClick(int position);
    }

    public interface OnItemDelClickListener {
        public int onItemDelClick(int position);
    }
}
