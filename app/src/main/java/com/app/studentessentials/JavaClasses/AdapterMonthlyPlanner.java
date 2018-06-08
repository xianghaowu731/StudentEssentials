package com.app.studentessentials.JavaClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import com.app.studentessentials.AddPlanner;
import com.app.studentessentials.Gsons.ModelDailyPlanner;
import com.app.studentessentials.Planner;
import com.app.studentessentials.R;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class AdapterMonthlyPlanner extends BaseAdapter
{
    private Activity activity;

    ArrayList<ModelDailyPlanner> als;
    ArrayList<ModelDailyPlanner> filtr;
    int j = 0;
    // public AdapterForListviewLayout(Activity activity, ArrayList<String> collegesList , ArrayList<String> clgaddresslist , ArrayList<String> imgeslist , ArrayList<String> college_id_arry , ArrayList<String> college_code_arry) {
    public AdapterMonthlyPlanner( Activity activity, ArrayList<ModelDailyPlanner> als) {
        this.als = als;
        this.filtr = new ArrayList<ModelDailyPlanner>();
        this.filtr.addAll(als);
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return als.size();
    }

    @Override
    public Object getItem(int i) {
        return als.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i*25;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {

        TextView txt_event_name,txt_time, txt_descrip ;
        LinearLayout lnr_color,lnr_delete, lnr_edit;



        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        view=inflater.inflate(R.layout.daily_planner_list_layout   , viewGroup, false);

        //  btn_start_id=(ImageView)view.findViewById(R.id.btn_start_id);

        txt_event_name=(TextView)view.findViewById(R.id.txt_event_name);
        txt_time=(TextView)view.findViewById(R.id.txt_time);
        txt_descrip = (TextView)view.findViewById(R.id.txt_descrip);
        lnr_color = (LinearLayout) view.findViewById(R.id.lnr_color);
        lnr_delete = (LinearLayout) view.findViewById(R.id.lnr_delete);
        lnr_edit = (LinearLayout) view.findViewById(R.id.lnr_edit);

        lnr_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Do you want to delete event?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Firebase reference = new Firebase(firebase_base_url+"Planner/"+als.get(i).root_id);
                                reference.child(als.get(i).id).removeValue();

                                SQLiteDatabase db = Functions.databaseInit(activity);
                                db.delete("planner_event", "id" + " = ?",
                                        new String[]{als.get(i).root_id});
                                db.close();

                                final ProgressDialog pd = new ProgressDialog(activity);
                                pd.setMessage("Loading...");
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        Intent intent = new Intent(activity, Planner.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }
                                }, 3000);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert..!");
                alert.show();
            }
        });

        lnr_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("root_id", als.get(i).root_id.toString().trim());
                bundle.putString("id", als.get(i).id.toString().trim());
                bundle.putString("event_name", als.get(i).event.toString().trim());
                bundle.putString("event_description", als.get(i).description.toString().trim());
                bundle.putString("location", als.get(i).location.toString().trim());
                bundle.putString("event_type", als.get(i).event_type.toString().trim());
                bundle.putString("date", als.get(i).date.toString().trim());
                bundle.putString("time", als.get(i).time.toString().trim());
                bundle.putString("reminder", als.get(i).reminder_status.toString().trim());

                Intent intent = new Intent(activity, AddPlanner.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                // Create new fragment and transaction
//                Fragment mFragment = new FragmentAddPlanner();
//                mFragment.setArguments(bundle);
                //  fragmentManager.beginTransaction().replace(R.id._my_fragment, mFragment).commit();
            }

        });


        for(int j = 0 ; j < als.size() ; j++)
        {
            if(i == 0){}
            else if(i == 0){}
            else if(i == 0){}
            else if(i == 0){}
        }

        if(j==0)
        {
            lnr_color.setBackgroundColor(Color.parseColor("#f4e0f9"));
            j++;
        }else if(j == 1){
            lnr_color.setBackgroundColor(Color.parseColor("#b7e8fe"));
            j++;
        }else if(j == 2){
            lnr_color.setBackgroundColor(Color.parseColor("#caffd1"));
            j++;
        }
        else if(j == 3){
            lnr_color.setBackgroundColor(Color.parseColor("#ffd59f"));
            j=0;
        }

        txt_event_name.setText(als.get(i).event);
        txt_time.setText(als.get(i).date);
        txt_descrip.setText(als.get(i).description);


//         Picasso.with(activity).load(""+als.get(i).image.toString().trim())
//                 .placeholder(R.drawable.logo)
//                 .error(R.drawable.testt)
//                 .into(img_backgroung);


        return view;
    }


        /*
        public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        als.clear();
        if (charText.length() == 0) {
            als.addAll(filtr);
        } else {
            for (GridClass wp : filtr) {
                if (wp.getSectionFilter().toLowerCase(Locale.getDefault()).contains(charText)) {
                    als.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
         */
}

