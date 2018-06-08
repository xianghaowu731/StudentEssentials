package com.app.studentessentials.JavaClasses;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.app.studentessentials.R;

public class VListViewAdapter extends RecyclerView.Adapter<VListViewAdapter.VerticalViewHolder>{
    private Context context;
    private List<VListViewModel> verticalList;

    public VListViewAdapter(Context context, List<VListViewModel> verticalList) {
        this.context = context;
        this.verticalList = verticalList;
    }

    @Override
    public VerticalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_vertical_list_view_row_data, parent, false);
        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VerticalViewHolder holder, int position) {
        VListViewModel vModel = verticalList.get(position);
        holder.vCardView.setTag(position);
        holder.txtBillDate.setText(vModel.billPayDate);
        holder.txtBillListName.setText(vModel.billListName);
        holder.txtBillLocation.setText(vModel.billPayLocation);
        holder.txtBillPay.setText(vModel.billPay);

    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }


    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtBillDate,txtBillListName,txtBillLocation,txtBillPay;
        View vCardView;
        public VerticalViewHolder(View itemView) {
            super(itemView);
            vCardView=(CardView)itemView.findViewById(R.id.vertical_card_view);
            txtBillDate = (TextView)itemView.findViewById(R.id.txt_bill_date);
            txtBillListName = (TextView)itemView.findViewById(R.id.txt_bill_name);
            txtBillLocation = (TextView)itemView.findViewById(R.id.txt_bill_location);
            txtBillPay = (TextView)itemView.findViewById(R.id.txt_bill_pay);
            vCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   int position=(int)v.getTag();
                   Toast.makeText(v.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}