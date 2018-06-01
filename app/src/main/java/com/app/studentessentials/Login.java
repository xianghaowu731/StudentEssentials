package com.app.studentessentials;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.JavaClasses.EncryptionDecryption;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;
import static com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN;

public class Login extends AppCompatActivity {

    EditText edt_email, edt_password;
    Button btn_login;
    ImageView img_facebook_login, img_google_login, img_twitter_login;
    TextView txt_register, txt_finger_print, txt_alter_password, txt_forgot_password;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CallbackManager callbackManager;
    CheckBox chkbox_remember;
    private GoogleApiClient mGoogleApiClient;
    TwitterLoginButton loginButton ;
    private TwitterAuthClient client;
    private static final String TAG = "TwitterLogin";

    private static final String TWITTER_KEY = "LnT0tbOuyrGNFmF9X6m8vGHLi";//"A23Vc16rQehRo1obtC2Mrkccm";
    private static final String TWITTER_SECRET = "FLrnCdmAjOP1e0collnoHmdm1VDIBuNBRQIKeHTpcUCkvRBYPC";//"iSn48AW90JDlmkpUpNNOmpiSFeDBes5Wa0EVPL0PUE0jGgwy86";
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        FirebaseApp.initializeApp(Login.this);
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Crashlytics(), new Twitter(authConfig), new CrashlyticsNdk());
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);

        initilizeViews();
        addAutoStartup();

        /*---------------------- initilize view -------------------------------*/
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fomeValidation()){
                    loginOnFirebase();
                }
                else {
                    Toast.makeText(Login.this, "Please enter correct email..!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(sharedPreferences.getString("remember_login", "") == null)
        {
            editor.putString("remember_login", "0");
            editor.commit();
        }else if(sharedPreferences.getString("remember_login", "").equals("1")){
            edt_email.setText( sharedPreferences.getString("email", ""));
            edt_password.setText(sharedPreferences.getString("u_password", ""));
            chkbox_remember.setChecked(true);
        }

        /*------------------ check box Remember password  -----------------------------------------------------------------*/
        chkbox_remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkbox_remember.isChecked())
                {
                    editor.putString("remember_login", "1");
                    editor.commit();
                }
                else{
                    editor.putString("remember_login", "0");
                    editor.commit();
                }
            }
        });
        /*------------------------END---------------------------*/

        /*------------------------- for register --------------------------------*/
        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Registeration.class));
            }
        });

        /*------------------------- for register --------------------------------*/
        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });

       /*------------------------------ email validation ------------------------------------*/
        edt_email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (edt_email.getText().toString().trim().matches(emailPattern) && s.length() > 0){}
                else {
                    edt_email.setError("invalid email");
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        /*--------------------------------- alter password ----------------------------*/
        txt_alter_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterNativePasswordDialog();
            }
        });

        /*--------------------------------- fingureprint password ----------------------------*/
        txt_finger_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sharedPreferences.getString("username", "") != null){
                    if(!sharedPreferences.getString("username", "").equals(""))
                        startActivity(new Intent(Login.this, FingurePrintLogin.class));
                    else
                        Toast.makeText(Login.this, "Please login atleast one time..!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Login.this, "Please login atleast one time..!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        /*--------------------------------- facebook_login ----------------------------*/
        img_facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAppInstalled(Login.this, "com.facebook.katana") &&
                        !isAppInstalled(Login.this, "com.facebook.lite") &&
                        !isAppInstalled(Login.this, "com.facebook.orca") &&
                        !isAppInstalled(Login.this, "com.facebook.mlite")){
                    Toast.makeText(getApplicationContext(), "facebook app not installing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (AccessToken.getCurrentAccessToken() != null) {
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                            .Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {

                            LoginManager.getInstance().logOut();
                            //LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile, email"));
                            Toast.makeText(getApplicationContext(), "Please try to Sign up with Facebook again", Toast.LENGTH_SHORT).show();
                        }
                    }).executeAsync();
                } else{
                    LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "user_friends"));
                }

            }
        });

        /*----------------------------------- Google login ---------------------------------------*/
        img_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        /*----------------------------------- Twitter login ---------------------------------------*/
        img_twitter_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLogin();
            }
        });

        /*------------------------  Start of Sign in using facebook functionality  -----------------------------------------*/
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();

                        // Facebook Email address
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.v("LoginActivity Response ", response.toString());

                                        try {

                                            //Log.d("Person Name",object.getString("name"));
                                            //Log.d("personGivenName",object.getString("id"));
                                            registerUsingFbGmailTwitter(object.getString("id"),object.getString("email"),object.getString("name"), "Facebook_Login");
                                            //LoginManager.getInstance().logOut();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,gender");
                        request.setParameters(parameters);
                        request.executeAsync();


                        System.out.println("User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken());
                    }
                    @Override
                    public void onCancel() {
                        System.out.println("2 ------------------++++++++++++++++++++++++++++++++++++++-------------------");
                        Toast.makeText(Login.this, "Not Able to Login..!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("3 ------------------++++++++++++++++++++++++++++++++++++++-------------------");
                    }
                });
        /*------------------------END---------------------------*/

        try { EncryptionDecryption.generateKey(); } catch (Exception e) { e.printStackTrace(); }

    }

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /*--------------------------------------- initilize view -----------------------------------------------*/
    public void initilizeViews(){
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        chkbox_remember = (CheckBox) findViewById(R.id.chkbox_remember);

        btn_login = (Button) findViewById(R.id.btn_login);

        img_facebook_login = (ImageView) findViewById(R.id.img_facebook_login);
        img_google_login = (ImageView) findViewById(R.id.img_google_login);
        img_twitter_login = (ImageView) findViewById(R.id.img_twitter_login);

        txt_register = (TextView) findViewById(R.id.txt_register);
        txt_finger_print = (TextView) findViewById(R.id.txt_finger_print);
        txt_alter_password = (TextView) findViewById(R.id.txt_alter_password);
        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();


        if(extras != null && extras.get("id") == null) {

        } else if(extras != null && extras.get("id").toString().equals("1")) {
            editor.putString("email", GlobalVariables._EMAIL);
            editor.putString("username", DecodeString(GlobalVariables._USERNAME));
            editor.putString("u_password", GlobalVariables._PASSWORD);
            editor.putString("remember_login", GlobalVariables._REMEMBER);
            editor.commit();
        }
    }
    /*-------------------------------------------------------------------------------------------*/

    /*---------------------------------------- initilize view -----------------------------------------------*/
    public boolean fomeValidation()
    {
        boolean flag = true;
        if(edt_email.getText().toString().equals("") || edt_password.getText().toString().equals("")){
            flag = false;
            Toast.makeText(this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (edt_email.getText().toString().trim().matches(emailPattern)) { }
            else { flag = false; }
        }
        return flag ;
    }
    /*-----------------------------------------------------------------------------------------------*/

    /*---------------------------------------- Login using Firebase  -----------------------------------------------*/
    public void loginOnFirebase()
    {
       final String user_mail = edt_email.getText().toString().trim();
       final String user_pass = edt_password.getText().toString();
       if(!fomeValidation())
       {
           Toast.makeText(this, "Please check your email address..!", Toast.LENGTH_SHORT).show();
       }
       else if(user_pass.equals("")){
            edt_password.setError("can't be blank");
        }
        else{
            final ProgressDialog pd = new ProgressDialog(Login.this);
            pd.setMessage("Loading...");
            pd.show();
            String url = firebase_base_url+"Registered_user.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                Firebase reference = new Firebase(firebase_base_url+"Registered_user");
                @Override
                public void onResponse(String s) {
                    System.out.println("---"+ s);
                    if(s.equals("null")){
                        editor.putString("login" , "0");
                        editor.commit();
                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        try {
                            JSONObject obj = new JSONObject(s);

                            Iterator i = obj.keys();
                            String key0 = "";
                            Boolean flag = false;
                            while(i.hasNext()){
                                key0 = i.next().toString();

                                JSONObject emp=(new JSONObject(s)).getJSONObject(key0);
                                String usedmail=emp.getString("email");
                                if(usedmail.equals(user_mail)){
                                    if(emp.getString("password").equals(EncryptionDecryption.encrypt(user_pass)))
                                    {
                                        editor.putString("user_key", key0);
                                        editor.putString("username" , EncryptionDecryption.decrypt(emp.getString("username")));
                                        editor.putString("login" , "1");
                                        editor.putString("email",user_mail);
                                        if(sharedPreferences.getString("remember_login", "").equals("1")){

                                            editor.putString("u_password", user_pass);
                                        }
                                        editor.putString("previous_electricity_meter_reading" , null);
                                        editor.putString("current_electricity_meter_reading" , null);
                                        editor.putString("previous_gas_meter_reading" , null);
                                        editor.putString("current_gas_meter_reading" , null);
                                        editor.commit();

                                        if(FirebaseInstanceId.getInstance().getToken() != null)
                                            reference.child(key0).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                        startActivity(new Intent(Login.this, HomeActivity.class));
                                    }
                                    else {
                                        editor.putString("login" , "0");
                                        editor.commit();
                                        Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                    flag = true;
                                    break;
                                }
                            }

                            if(!flag){
                                editor.putString("login" , "0");
                                editor.commit();
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(Login.this);
            rQueue.add(request);
        }
    }
    /*------------------------------------------------------------------------------------------------*/

    /*--------------------------------------- Login using twitter  -----------------------------------------------*/
    public void twitterLogin(){

        PackageManager pkManager = getPackageManager();
        try {
            PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
            String getPkgInfo = pkgInfo.toString();

            if (getPkgInfo.indexOf("com.twitter.android") != -1)   {
                client = new TwitterAuthClient();
                client.authorize(Login.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        //Do something with result, which provides a TwitterSession for making API calls
                        Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                        user.enqueue(new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                String name = userResult.data.name;
                                String email = userResult.data.email;

                                // _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)
                                String photoUrlNormalSize   = userResult.data.profileImageUrl;
                                String photoUrlBiggerSize   = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                                String photoUrlMiniSize     = userResult.data.profileImageUrl.replace("_normal", "_mini");
                                String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");

                                registerUsingFbGmailTwitter( Long.toString(userResult.data.id) , userResult.data.email,  userResult.data.name , "Twitter_Login");
                            }

                            @Override
                            public void failure(TwitterException exc) {
                                Log.d("TwitterKit", "Verify Credentials Failure", exc);
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(Login.this, "failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            Toast.makeText(this, "Twitter App is not installed.", Toast.LENGTH_SHORT).show();
        }



    }
    /*----------------------------------------------------------------------------------------*/

    /*--------------------------------------- Login using gmail  -----------------------------------------------*/

    public void googleLogin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN);
    }
    /*-------------------------------------------------------------------------------------------*/

    public void alterNativePasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_alternative_password);
        View dview = dialog.getWindow().getDecorView();
        dview.setBackgroundResource(android.R.color.transparent);

        final EditText edt_Aemail = (EditText) dialog.findViewById(R.id.edt_Aemail);
        final EditText edt_Apassword = (EditText) dialog.findViewById(R.id.edt_Apassword);

        TextView txt_OK = (TextView) dialog.findViewById(R.id.txt_OK);
        txt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_email.getText().toString().trim().matches(emailPattern)) {
                    Toast.makeText(Login.this, "Please check your mail..!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else if(edt_Apassword.getText().toString().trim().equals("")) {
                    edt_Apassword.setError("Please fill key..!");
                }
                else{
                    dialog.dismiss();
                    final String user_mail = edt_Aemail.getText().toString().trim();
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();
                    String url = firebase_base_url + "Registered_user.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        Firebase reference = new Firebase(firebase_base_url + "Registered_user");

                        @Override
                        public void onResponse(String s) {
                            System.out.println("---" + s);
                            if (s.equals("null")) {
                                editor.putString("login", "0");
                                editor.commit();
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    Iterator i = obj.keys();
                                    String key0 = "";
                                    Boolean flag = false;
                                    while(i.hasNext()){
                                        key0 = i.next().toString();

                                        JSONObject emp=(new JSONObject(s)).getJSONObject(key0);
                                        String usedmail=emp.getString("email");
                                        if(usedmail.equals(user_mail)){
                                            if (emp.getString("access_key").equals(EncryptionDecryption.encrypt(edt_Apassword.getText().toString()))) {

                                                editor.putString("user_key", key0);
                                                editor.putString("username" , emp.getString("username"));
                                                editor.putString("login", "1");
                                                editor.putString("email",user_mail);
                                                editor.putString("previous_electricity_meter_reading" , null);
                                                editor.putString("current_electricity_meter_reading" , null);
                                                editor.putString("previous_gas_meter_reading" , null);
                                                editor.putString("current_gas_meter_reading" , null);
                                                editor.commit();
                                                editor.commit();

                                                if(FirebaseInstanceId.getInstance().getToken() != null)
                                                    reference.child(key0).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                                startActivity(new Intent(Login.this, HomeActivity.class));
                                            } else {
                                                editor.putString("login", "0");
                                                editor.commit();
                                                Toast.makeText(Login.this, "Incorrect Alternative Password..!", Toast.LENGTH_LONG).show();
                                            }
                                            flag = true;
                                            break;
                                        }
                                    }

                                    if (!flag) {
                                        editor.putString("login", "0");
                                        editor.commit();
                                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }
            }
        });
        TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
        // Pass the activity result to the login button.
        try {
            client.onActivityResult(requestCode, resultCode, data);
        }catch (NullPointerException e){}

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void handleSignInResult(GoogleSignInResult result)
    {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (acct != null) {
//                String personName = acct.getDisplayName();
//                String personGivenName = acct.getGivenName();
//                String personFamilyName = acct.getFamilyName();
//                String personEmail = acct.getEmail();
//                String personId = acct.getId();
                registerUsingFbGmailTwitter(acct.getId(), acct.getEmail(), acct.getDisplayName(), "Gmail_login");

            }
        } else {
            Toast.makeText(this, "Login Failed..!", Toast.LENGTH_LONG).show();
        }
    }

    public void registerUsingFbGmailTwitter(String uID, final String email,final String name ,final String login_type){
            final String var_id  = uID;
            final ProgressDialog pd = new ProgressDialog(Login.this);
            pd.setMessage("Loading...");
            pd.show();

            String url = firebase_base_url+"Registered_user.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    Firebase reference = new Firebase(firebase_base_url+"Registered_user");

                    if(s.equals("null")) {
                        String key = var_id;//reference.push().getKey();
                        reference.child(key).child("email").setValue(email);
                        reference.child(key).child("username").setValue(EncryptionDecryption.encrypt(name));
                        reference.child(key).child("password").setValue("");
                        reference.child(key).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                        reference.child(key).child("access_key").setValue("");
                        reference.child(key).child("login_type").setValue(login_type);

                        editor.putString("user_key", key);
                        editor.putString("email", email);
                        editor.putString("username" , name);
                        editor.putString("login" , "1");
                        editor.putString("previous_electricity_meter_reading" , null);
                        editor.putString("current_electricity_meter_reading" , null);
                        editor.putString("previous_gas_meter_reading" , null);
                        editor.putString("current_gas_meter_reading" , null);
                        editor.commit();

                        startActivity(new Intent(Login.this, HomeActivity.class));
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //try {
                            //JSONObject obj = new JSONObject(s);
                            //Iterator i = obj.keys();
                            String key0 = var_id;
                            /*Boolean flag = false;
                            while(i.hasNext()){
                                key0 = i.next().toString();

                                JSONObject emp=(new JSONObject(s)).getJSONObject(key0);
                                String usedmail=emp.getString("email");
                                if(usedmail.equals(var_email)){

                                    flag = true;
                                    break;
                                }

                            }*/

                            reference.child(key0).child("email").setValue(email);
                            reference.child(key0).child("username").setValue(name);
                            reference.child(key0).child("password").setValue("");
                            reference.child(key0).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                            reference.child(key0).child("access_key").setValue("");
                            reference.child(key0).child("login_type").setValue(login_type);

                            editor.putString("user_key", key0);
                            editor.putString("email", email);
                            editor.putString("username" , name);
                            editor.putString("login" , "1");
                            editor.putString("previous_electricity_meter_reading" , null);
                            editor.putString("current_electricity_meter_reading" , null);
                            editor.putString("previous_gas_meter_reading" , null);
                            editor.putString("current_gas_meter_reading" , null);
                            editor.commit();

                            startActivity(new Intent(Login.this, HomeActivity.class));
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_LONG).show();

                        /*} catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }

                    pd.dismiss();
                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(Login.this);
            rQueue.add(request);
    }

    private void addAutoStartup() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
