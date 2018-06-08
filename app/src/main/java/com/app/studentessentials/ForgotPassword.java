package com.app.studentessentials;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import com.app.studentessentials.Gsons.ForgotGson;
import com.app.studentessentials.Interfaces.APIInterface;
import com.app.studentessentials.JavaClasses.APIClient;
import com.app.studentessentials.JavaClasses.EncryptionDecryption;
import com.app.studentessentials.JavaClasses.GlobalVariables;
import retrofit2.Call;
import retrofit2.Callback;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class ForgotPassword extends AppCompatActivity {

    Button btn_cancel , btn_ok ;
    EditText edt_email ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        edt_email = (EditText) findViewById(R.id.edt_email);
        apiInterface = APIClient.getClient("http://pree-me.com/preemee/").create(APIInterface.class);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_email = (EditText) findViewById(R.id.edt_email);
                checkMailExistOrNot(edt_email.getText().toString().trim());
            }
        });
    }

    public boolean fomeValidation()
    {
        boolean flag = true;
        if(edt_email.getText().toString().equals("")){
            flag = false;
            Toast.makeText(this, "Please fill E-mail field..!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (edt_email.getText().toString().trim().matches(emailPattern)) { }
            else { flag = false; }
        }
        return flag ;
    }

    public void checkMailExistOrNot(final String user_mail)
    {
        if(!fomeValidation())
        {
            Toast.makeText(this, "Please check your email address..!", Toast.LENGTH_SHORT).show();
        }

        else{
            final ProgressDialog pd = new ProgressDialog(ForgotPassword.this);
            pd.setMessage("Loading...");
            pd.show();
            String url = firebase_base_url+"Registered_user.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                Firebase reference = new Firebase(firebase_base_url+"Registered_user");
                @Override
                public void onResponse(String s) {
                    System.out.println("---"+ s);
                    if(s.equals("null")){
                        Toast.makeText(ForgotPassword.this, "E-mail not Exist..!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        try {
                            JSONObject obj = new JSONObject(s);
                            String total_str = obj.toString();

                            if(total_str.indexOf(user_mail) == -1)
                            {
                                Toast.makeText(ForgotPassword.this, "E-mail not Exist..!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                sendMail(user_mail);
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

            RequestQueue rQueue = Volley.newRequestQueue(ForgotPassword.this);
            rQueue.add(request);
        }
    }

    public void sendMail(String mail){
        final ProgressDialog pd = new ProgressDialog(ForgotPassword.this);
        pd.setMessage("Loading...");
        pd.show();

        Random obj = new Random();
        GlobalVariables._OTP = Integer.toString(obj.nextInt( 999999));
        System.out.println("------"+GlobalVariables._OTP);
        Call data = apiInterface.sendMail(mail ,GlobalVariables._OTP );
        data.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                System.out.println(response.body());
                ForgotGson res = (ForgotGson) response.body();
                /*if(response.body() == null){
                    Toast.makeText(ForgotPassword.this, "Error!", Toast.LENGTH_SHORT).show();
                    openOtpDialog();
                    pd.dismiss();
                    return;
                }*/
                String login =  res.resp;
                if(login.equals("success"))
                {
                    openOtpDialog();
                }
                else{
                    Toast.makeText(ForgotPassword.this, "Resend E_mail again..!", Toast.LENGTH_SHORT).show();
                }

                pd.dismiss();
        }
            @Override
            public void onFailure(Call call, Throwable t) {
                call.cancel();
                System.out.println("============================================================end");
            }
        });
    }

    public void openOtpDialog()
    {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_forgot_otp);

            final EditText edt_otp = (EditText) dialog.findViewById(R.id.edt_otp);

            TextView txt_OK = (TextView) dialog.findViewById(R.id.txt_OK);
            txt_OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edt_otp.getText().toString().trim().equals(""))
                    {
                        edt_otp.setError("Enter OTP..!");
                    }
                    else if(edt_otp.getText().toString().trim().equals(GlobalVariables._OTP)) {
                        dialog.dismiss();
                        openReNewPasswordDialog();
                    }
                    else{
                        Toast.makeText(ForgotPassword.this, "Please Enter Correct OTP Code..!", Toast.LENGTH_SHORT).show();
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

    public void openReNewPasswordDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_change_password);

        final EditText edt_password = (EditText) dialog.findViewById(R.id.edt_password);
        final EditText edt_re_password = (EditText) dialog.findViewById(R.id.edt_re_password);

        TextView txt_OK = (TextView) dialog.findViewById(R.id.txt_OK);
        txt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_password.getText().toString().trim().equals("") || edt_re_password.getText().toString().trim().equals(""))
                {
                    Toast.makeText(ForgotPassword.this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
                }
                else if(edt_password.getText().toString().trim().equals(edt_re_password.getText().toString().trim())) {
                    dialog.dismiss();
                    changePasswordOnFirebase(edt_password.getText().toString().trim());

                }else{
                    Toast.makeText(ForgotPassword.this, "Password missmatch..!", Toast.LENGTH_SHORT).show();
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

    public void changePasswordOnFirebase(final String pass)
    {
            final ProgressDialog pd = new ProgressDialog(ForgotPassword.this);
            pd.setMessage("Loading...");
            pd.show();
            String url = firebase_base_url+"Registered_user.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                Firebase reference = new Firebase(firebase_base_url+"Registered_user");
                @Override
                public void onResponse(String s) {
                    System.out.println("---"+ s);
                    if(s.equals("null")){
                        Toast.makeText(ForgotPassword.this, "E-mail not Exist..!", Toast.LENGTH_LONG).show();
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
                                if(usedmail.equals(edt_email.getText().toString().trim())){
                                    reference.child(key0).child("password").setValue(EncryptionDecryption.encrypt(pass));
                                    startActivity(new Intent(ForgotPassword.this, Login.class));
                                    flag = true;
                                    break;
                                }
                            }

                            if(!flag)
                            {
                                Toast.makeText(ForgotPassword.this, "E-mail not Exist..!", Toast.LENGTH_LONG).show();
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

            RequestQueue rQueue = Volley.newRequestQueue(ForgotPassword.this);
            rQueue.add(request);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

}
