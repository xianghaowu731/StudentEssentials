package com.app.studentessentials.JavaClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

import com.app.studentessentials.Gsons.TipItem;
import com.app.studentessentials.R;

/**
 * Created by castle on 11/6/2017.
 */

public class FoldingCellListAdapter extends ArrayAdapter<TipItem> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;

    private OnSubImageClickListener mOnSubImageClickListener;
    public void setOnSubImageClickListener (OnSubImageClickListener listener) {
        mOnSubImageClickListener = listener;
    }


    public FoldingCellListAdapter(Context context, List<TipItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        TipItem item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_tip_item, parent, false);
            // binding view parts to view holder
            viewHolder.title_tip_no = (TextView) cell.findViewById(R.id.title_tip_no);
            //viewHolder.iv_tip = (ImageView) cell.findViewById(R.id.iv_tip);
            viewHolder.title_tip_name = (TextView) cell.findViewById(R.id.title_tip_name);
            viewHolder.tip_right_no = (TextView) cell.findViewById(R.id.tip_right_no);
            viewHolder.txt_hint_tips = (TextView) cell.findViewById(R.id.txt_hint);
            viewHolder.content_tip_title = (TextView) cell.findViewById(R.id.content_tip_title);
            //viewHolder.content_tip_description = (ImageView) cell.findViewById(R.id.content_tip_description);
            viewHolder.img_link = (ImageView) cell.findViewById(R.id.img_link);
            viewHolder.subimg_link = (ImageView) cell.findViewById(R.id.subimg_link);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        // bind data from selected element to view through view holder
        viewHolder.title_tip_no.setText(item.getNo());
        //viewHolder.iv_tip.setImageResource(item.getTip_icon());
        viewHolder.title_tip_name.setText(item.getName());
        viewHolder.tip_right_no.setText(item.getNo());
        viewHolder.txt_hint_tips.setText(item.getTxt_tips_hint());
        viewHolder.content_tip_title.setText(item.getName());
        viewHolder.img_link.setImageResource(item.getImage_link());

        if(item.suburl_link.length() > 0){
            viewHolder.subimg_link.setImageResource(item.subimg_link);
            //viewHolder.subimg_link.setVisibility(View.VISIBLE);
        } else{
            viewHolder.subimg_link.setImageResource(item.subimg_link);
            //viewHolder.subimg_link.setVisibility(View.GONE);
        }
        //viewHolder.content_tip_description.setImageResource(item.getDescription());

        // set custom btn handler for list item from that item
        if (item.getRequestBtnClickListener() != null) {
            viewHolder.img_link.setOnClickListener(item.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.img_link.setOnClickListener(defaultRequestBtnClickListener);
        }

        final int pos = position;
        viewHolder.subimg_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnSubImageClickListener != null) {
                    mOnSubImageClickListener.onSubImageClick(pos);
                }
            }
        });

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    public interface OnSubImageClickListener {
        public int onSubImageClick(int position);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView title_tip_no;
        TextView txt_hint_tips;
        TextView title_tip_name;
        TextView tip_right_no;
        TextView content_tip_title;
        ImageView img_link;
        ImageView subimg_link;
    }
}