package com.app.studentessentials.JavaClasses;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.app.studentessentials.R;

public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.HorizontalViewHolder>{
    private Context context;
    private List<HListViewModel> hList;

    public HorizontalListAdapter(Context context, List<HListViewModel> hList) {
        this.context = context;
        this.hList = hList;

    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_horizontal_list_view_row_data, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder viewHolder, int position) {
        HListViewModel hModel =hList.get(position);
        viewHolder.hCardView.setTag(position);
        viewHolder.month_name.setText(hModel.month_name);
        viewHolder.total_spent.setText(hModel.total_spent);

    }

    @Override
    public int getItemCount() {
        return hList.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView month_name,total_spent;
        public View hCardView;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            hCardView = (CardView) itemView.findViewById(R.id.horizontal_card_view);
            month_name=(TextView)itemView.findViewById(R.id.txt_month_name);
            total_spent=(TextView)itemView.findViewById(R.id.txt_total_spent);
            hCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            //display toast with position of cardview in recyclerview list upon click
            Toast.makeText(v.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();

        }
    }
}
