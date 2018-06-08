package com.app.studentessentials;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.studentessentials.JavaClasses.AlarmReciever;
import com.app.studentessentials.JavaClasses.Functions;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.app.studentessentials.Models.UserModel;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.sjl.foreground.Foreground;

import java.util.Calendar;


public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, Foreground.Listener {

    public static final int MY_PERMISSIONS_REQUEST= 123;

    private Foreground.Binding listenerBinding;

    LinearLayout lnr_planner, lnr_utility, lnr_reward, lnr_recepies;
    ImageView img_navigation, btn_drawer;
    DrawerLayout drawer;
    Menu menu;
    LinearLayout lnr_budget_manage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView tv_username;
    Handler myBgHandle = null;
    Runnable bg_runable = null;
    //String eventname;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(HomeActivity.this);
        setContentView(R.layout.activity_home);
        Firebase.setAndroidContext(this);

        //eventname = getIntent().getStringExtra("eventName");

        initializeViews();

        lnr_budget_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this , BudgetManagement.class));
            }
        });

        lnr_planner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this , Planner.class));
                SQLiteDatabase db = Functions.databaseInit(HomeActivity.this);
                db.execSQL("CREATE TABLE IF NOT EXISTS planner_event(id INTEGER PRIMARY KEY AUTOINCREMENT, db_key VARCHAR, event VARCHAR, location VARCHAR, description VARCHAR, date VARCHAR, event_time VARCHAR, reminder_status VARCHAR, event_type VARCHAR, event_day VARCHAR, alarm_gap VARCHAR, other VARCHAR);");

                createAlarm();

            }
        });
        lnr_utility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this , Utility.class));
            }
        });
        lnr_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this , Rewards.class));

            }
        });
        lnr_recepies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this , Receipe.class));

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn_drawer = (ImageView) findViewById(R.id.btn_drawer);

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        View headerLayout = navigationView.getHeaderView(0);
        tv_username = (TextView) headerLayout.findViewById(R.id.tv_nav_username);
        tv_username.setText(GlobalVariables._USERNAME);

        listenerBinding = Foreground.get(getApplication()).addListener(this);

        checkPermission(this);

    }

    public void initializeViews(){
        lnr_planner = (LinearLayout) findViewById(R.id.lnr_planner);
        lnr_utility = (LinearLayout) findViewById(R.id.lnr_utility);
        lnr_reward = (LinearLayout) findViewById(R.id.lnr_reward);
        lnr_recepies = (LinearLayout) findViewById(R.id.lnr_recepies);

        lnr_budget_manage = (LinearLayout) findViewById(R.id.lnr_budget_manage);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        GlobalVariables._USERKEY = sharedPreferences.getString("user_key", "");
        GlobalVariables._EMAIL = sharedPreferences.getString("email", "");
        GlobalVariables._USERNAME = sharedPreferences.getString("username", "");
        GlobalVariables._PASSWORD = sharedPreferences.getString("u_password", "");
        GlobalVariables._REMEMBER = sharedPreferences.getString("remember_login", "");
        UserModel one = new UserModel(GlobalVariables._USERKEY, GlobalVariables._EMAIL,GlobalVariables._USERNAME, GlobalVariables._PASSWORD, GlobalVariables._REMEMBER);
        MyApp.getInstance().myProfile = one;

        SQLiteDatabase db = Functions.databaseInit(this);
        db.execSQL("CREATE TABLE IF NOT EXISTS task_event(id INTEGER PRIMARY KEY AUTOINCREMENT, task_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, reminder_status VARCHAR, checked VARCHAR, userkey VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS utility_event(id INTEGER PRIMARY KEY AUTOINCREMENT, utility_name VARCHAR, description VARCHAR, task_date VARCHAR, task_time VARCHAR, userkey VARCHAR);");
        db.close();
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public void onBecameForeground() {
        //Log.i(Foreground.TAG, getClass().getName() + " became foreground");
        if(bg_runable != null && myBgHandle != null){
            myBgHandle.removeCallbacks(bg_runable);
            myBgHandle = null;
            bg_runable = null;
        }
    }

    @Override
    public void onBecameBackground() {
        bg_runable = new Runnable() {
            @Override
            public void run() {
                editor.putString("login" , "0");
                editor.commit();
                //editor.clear().commit();
                Intent intent = new Intent(HomeActivity.this, SplashScreen.class);
                intent.putExtra("id", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //finish();

            }
        };
        myBgHandle = new Handler();
        myBgHandle.postDelayed(bg_runable, 300000);//300000
    }

    @Override
    public void onResume(){
        super.onResume();

        String noti_str = sharedPreferences.getString("planner_noti_setting", "");
        if(noti_str.equals("1")){
            MyApp.getInstance().bNoti_planner = true;
        } else if(noti_str.equals("0")){
            MyApp.getInstance().bNoti_planner = false;
        } else{
            MyApp.getInstance().bNoti_planner = true;
            editor.putString("planner_noti_setting", "1");
            editor.commit();
        }
        noti_str = sharedPreferences.getString("utility_noti_setting", "");
        if(noti_str.equals("1")){
            MyApp.getInstance().bNoti_utility = true;
        } else if(noti_str.equals("0")){
            MyApp.getInstance().bNoti_utility = false;
        } else{
            MyApp.getInstance().bNoti_utility = true;
            editor.putString("utility_noti_setting", "1");
            editor.commit();
        }
        noti_str = sharedPreferences.getString("todo_noti_setting", "");
        if(noti_str.equals("1")){
            MyApp.getInstance().bNoti_todo = true;
        } else if(noti_str.equals("0")){
            MyApp.getInstance().bNoti_todo = false;
        } else{
            MyApp.getInstance().bNoti_todo = true;
            editor.putString("todo_noti_setting", "1");
            editor.commit();
        }

        String login_str = sharedPreferences.getString("login","");
        if(login_str.equals("0") || login_str.length()==0){
            Intent intent = new Intent(HomeActivity.this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } /*else{
            if(eventname != null && eventname.length() > 1){
                startActivity(new Intent(this , Planner.class));
            }
        }*/
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        // The Application has been closed!
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        finishAffinity();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_todo) {
            startActivity(new Intent(this , ToDo.class));

        } else if (id == R.id.nav_budget) {
            startActivity(new Intent(this , BudgetManagement.class));

        } else if (id == R.id.nav_utility) {
            startActivity(new Intent(this , Utility.class));

        }  else if (id == R.id.nav_recipes) {
            startActivity(new Intent(this , Receipe.class));

        }  else if (id == R.id.nav_reward) {
            startActivity(new Intent(this , Rewards.class));

        } else if (id == R.id.nav_planner) {
            startActivity(new Intent(this , Planner.class));

        } else if (id == R.id.nav_find) {
            Toast.makeText(this, "Under Development..!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_tips) {
            startActivity(new Intent(this , Tips.class));

        }  else if (id == R.id.nav_terms) {
            Toast.makeText(this, "Under Development..!", Toast.LENGTH_SHORT).show();

        }  else if (id == R.id.nav_contact) {
            Toast.makeText(this, "Under Development..!", Toast.LENGTH_SHORT).show();

        }  else if (id == R.id.nav_settings) {
            startActivity(new Intent(this , SettingNotification.class));

         } else if (id == R.id.nav_logout) {
            editor.putString("login" , "0");
            editor.commit();

            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
            pd.setMessage("Loading...");
            pd.show();

            //editor.clear().commit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, Login.class);
                    intent.putExtra("id", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    pd.dismiss();
                    finish();

                }
            }, 2500);
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void createAlarm() {
        Calendar objCalendar = Calendar.getInstance();

        //making Request Code
        int requestCode = 0000000000;

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        // alarmMgr.set(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(),
                1000 * 15, pendingIntent);

//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
//
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, pendingIntent);
//
//        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
//                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

    }

    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                /*if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)||
                        ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Location service permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {*/
                try {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //code for deny
                }
                break;
        }
    }

}
