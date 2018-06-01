package com.app.studentessentials;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.app.studentessentials.Models.UserModel;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;


public class MyApp extends MultiDexApplication {

    public static MyApp myApp = null;
    public static UserModel myProfile;
    public static boolean bNoti_planner;
    public static boolean bNoti_todo;
    public static boolean bNoti_utility;

    private static final String TWITTER_KEY = "LnT0tbOuyrGNFmF9X6m8vGHLi";//"A23Vc16rQehRo1obtC2Mrkccm";
    private static final String TWITTER_SECRET = "FLrnCdmAjOP1e0collnoHmdm1VDIBuNBRQIKeHTpcUCkvRBYPC";//"iSn48AW90JDlmkpUpNNOmpiSFeDBes5Wa0EVPL0PUE0jGgwy86";

    @Override
    public void onCreate() {
        super.onCreate();

        /*TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);*/
    }

    public static MyApp getInstance(){
        if(myApp == null)
        {
            myApp = new MyApp();
        }
        return myApp;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
