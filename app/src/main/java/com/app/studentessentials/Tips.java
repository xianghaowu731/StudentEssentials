package com.app.studentessentials;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

import com.app.studentessentials.Gsons.TipItem;
import com.app.studentessentials.JavaClasses.FoldingCellListAdapter;

public class Tips  extends Activity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

        final static String TAG = "TipsActivity";

        static String utility = "", cashback = "", broadband = "", student = "",txt_hint_utility="",txt_hint_cashback,txt_hint_student,txt_hint_broadband;

        ArrayList<TipItem> items = new ArrayList<>();
        ImageView btn_back;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_tips);
                initUI();

            btn_back = (ImageView) findViewById(R.id.btn_back);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }

        public void initUI() {
                ListView tipsListView = (ListView) findViewById(R.id.tipsListView);

                utility = getString(R.string.utility);
                cashback = getString(R.string.cashback);
                broadband = getString(R.string.broadband);
                student = getString(R.string.student);
                txt_hint_broadband=getString(R.string.hint_broadband);
                txt_hint_utility=getString(R.string.hint_utility);
                txt_hint_cashback=getString(R.string.hint_cashBack);
                txt_hint_student=getString(R.string.hint_student);

                // prepare elements to display
                items = getTestingList();

                // add custom btn handler to each list item
                for (int tip_index = 0; tip_index < items.size(); tip_index ++) {
                        final int index = tip_index;
                        items.get(tip_index).setRequestBtnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        //Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR EACH BUTTON", Toast.LENGTH_SHORT).show();
                                        Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(index).getUrl_link()));
                                       startActivity(browserIntent);
                                }
                        });
                }

                // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
                final FoldingCellListAdapter adapter = new FoldingCellListAdapter(getApplicationContext(), items);

                // add default btn handler for each request btn on each item if custom handler not found
                adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
                }
                });

                // set elements to adapter
                tipsListView.setAdapter(adapter);

                // set on click event listener to list view
                tipsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                // toggle clicked cell state
                                ((FoldingCell) view).toggle(false);
                                // register in adapter that state for selected cell is toggled
                                adapter.registerToggle(pos);
                                }
                });

                adapter.setOnSubImageClickListener(new FoldingCellListAdapter.OnSubImageClickListener() {
                        @Override
                        public int onSubImageClick(int position) {
                                if(items.get(position).suburl_link.length() > 0)
                                {
                                        Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).suburl_link));
                                        startActivity(browserIntent);
                                }
                                return 0;
                        }
                });
        }

/**
 * @return List of elements prepared for tests
 */
        public static ArrayList<TipItem> getTestingList() {

                ArrayList<TipItem> items = new ArrayList<>();
                items.add(new TipItem("1", student,R.drawable.tip_studentbean, "https://www.studentbeans.com/uk",txt_hint_student, R.drawable.tip_unidays, "https://www.myunidays.com/GB/en-GB/content/about"));
                items.add(new TipItem("2", utility, R.drawable.tip_uswitch, "https://www.uswitch.com/gas-electricity/",txt_hint_utility, R.drawable.tip_ucompare,"https://energy.gocompare.com/gas-electricity/"));
                items.add(new TipItem("3", broadband, R.drawable.tip_save, "https://usave.co.uk/broadband/broadband-only-deals/",txt_hint_broadband, R.drawable.tip_broadband, "https://www.broadbandchoices.co.uk/broadband"));
                items.add(new TipItem("4", cashback,R.drawable.tip_topcash, "https://www.topcashback.co.uk/",txt_hint_cashback, R.drawable.tip_quidco, "https://www.quidco.com/"));

                return items;
        }

        @Override
        public void onClick(View v) {
               switch (v.getId()) {

                default:
                        break;
                }
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer_layout);
                return true;
        }

        @Override
        protected void onNewIntent(Intent intent) {
                super.onNewIntent(intent);
                Log.d(TAG, "MyAccountActivity-onNewIntent");
        }
}
