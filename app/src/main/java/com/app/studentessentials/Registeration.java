package com.app.studentessentials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.studentessentials.JavaClasses.EncryptionDecryption;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class Registeration extends AppCompatActivity {

    EditText edt_username, edt_mail, edt_password, edt_repassword, edt_alternative_key;
    Button btn_register;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(Registeration.this);
        setContentView(R.layout.activity_registeration);

        Firebase.setAndroidContext(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initilizeViews();

        /*---------------------- initilize view -------------------------------*/
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fomeValidation()){
                    registerOnFirebase();
                }
            }
        });


        /*------------------------------ email validation ------------------------------------*/
        edt_mail .addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (edt_mail.getText().toString().trim().matches(emailPattern) && s.length() > 0){}
                else {
                    edt_mail.setError("invalid email");
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });
    }

/*--------------------------------------- initilize view -----------------------------------------------*/
    public void initilizeViews(){
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_mail = (EditText) findViewById(R.id.edt_mail);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_repassword = (EditText) findViewById(R.id.edt_repassword);
        edt_alternative_key = (EditText) findViewById(R.id.edt_alternative_key);
        btn_register = (Button) findViewById(R.id.btn_register);
    }
/*-------------------------------------------- End -----------------------------------------------*/

/*---------------------------------------- initilize view -----------------------------------------------*/
    public boolean fomeValidation()
    {
        boolean flag = true;
        if(edt_username.getText().toString().equals("") || edt_mail.getText().toString().equals("") || edt_password.getText().toString().equals("") || edt_password.getText().toString().equals("")){
            flag = false;
            Toast.makeText(this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
        }
        else if(!edt_password.getText().toString().equals(edt_repassword.getText().toString())) {
            flag = false;
            Toast.makeText(this, "Please enter correct password..!", Toast.LENGTH_SHORT).show();
        }
        else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(edt_mail.getText().toString());
            if(!matcher.matches()){
                Toast.makeText(Registeration.this, "Please enter correct email..!", Toast.LENGTH_SHORT).show();
            }
            flag = matcher.matches();
        }
        return flag ;
    }
/*---------------------------------------------- END -------------------------------------------------*/

/*----------------------------------------  -----------------------------------------------*/
    public void registerOnFirebase()
    {
        try { EncryptionDecryption.generateKey(); } catch (Exception e) { e.printStackTrace(); }

        final String var_email  = edt_mail.getText().toString().trim();
        final String var_username = EncryptionDecryption.encrypt(edt_username.getText().toString());
        final String var_password = EncryptionDecryption.encrypt(edt_password.getText().toString());
        final String var_alternative_key = EncryptionDecryption.encrypt(edt_alternative_key.getText().toString());

        final ProgressDialog pd = new ProgressDialog(Registeration.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = firebase_base_url+"Registered_user.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"Registered_user");

                if(s.equals("null")) {
                    String key = reference.push().getKey();
                    reference.child(key).child("email").setValue(var_email);
                    reference.child(key).child("username").setValue(var_username);
                    reference.child(key).child("password").setValue(var_password);
                    reference.child(key).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                    reference.child(key).child("access_key").setValue(var_alternative_key);
                    reference.child(key).child("login_type").setValue("Firebase_login");

                    startActivity(new Intent(Registeration.this, Login.class));
                    Toast.makeText(Registeration.this, "registration successful", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        Iterator i = obj.keys();
                        String key0 = "";
                        Boolean flag = false;
                        while(i.hasNext()){
                            key0 = i.next().toString();

                            JSONObject emp=(new JSONObject(s)).getJSONObject(key0);
                            String usedmail=emp.getString("email");
                            if(usedmail.equals(var_email)){
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            String key1 = reference.push().getKey();
                            reference.child(key1).child("email").setValue(var_email);
                            reference.child(key1).child("username").setValue(var_username);
                            reference.child(key1).child("password").setValue(var_password);
                            reference.child(key1).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                            reference.child(key1).child("access_key").setValue(var_alternative_key);
                            reference.child(key1).child("login_type").setValue("Firebase_login");

                            startActivity(new Intent(Registeration.this, Login.class));
                            Toast.makeText(Registeration.this, "registration successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Registeration.this, "username already exists", Toast.LENGTH_LONG).show();
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
                System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Registeration.this);
        rQueue.add(request);
    }
/*---------------------------------------------- END -------------------------------------------------*/

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
