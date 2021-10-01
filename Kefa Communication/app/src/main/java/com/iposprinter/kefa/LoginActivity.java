package com.iposprinter.kefa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passwd;
    private ProgressBar progressBar;
    private ResponseListener listener;
    SharedPreferences loginState;
    private static final String LOGIN_URL = "https://tomiwa.com.ng/btcpos-proj/login";

    public interface ResponseListener{
        void gotResponse(JSONObject object);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        listener = new ResponseListener() {
            @Override
            public void gotResponse(JSONObject object) {
                try{
                    loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginState.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("fullname", object.getString("fullname"));
                    editor.putString("email", email.getText().toString());
                    editor.putString("wallet", object.getString("wallet"));
                    editor.putString("id", object.getString("id"));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("fullname", object.getString("fullname"));
                    intent.putExtra("email", object.getString("email"));
                    intent.putExtra("wallet", object.getString("wallet"));
                    intent.putExtra("id", object.getString("id"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch(JSONException jsonException){
                    jsonException.printStackTrace();
                }
            }
        };
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        moveToHomeActivity();
    }

    public void login(View view){
        if(loginDataValidate()){
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("status").equals("success")){
                                listener.gotResponse(obj);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login Failed! Invalid username/password", Toast.LENGTH_LONG).show();
                            }
                        } catch(JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email.getText().toString());
                    params.put("password", passwd.getText().toString());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }


    public void registerPage(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    public boolean loginDataValidate(){
        boolean emailValidate, pwdValidate;
        TextInputLayout t_email = (TextInputLayout) findViewById(R.id.email_input_layout);
        TextInputLayout t_passwd = (TextInputLayout) findViewById(R.id.passwd_input_layout);
        if(!email.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            emailValidate = true;
            t_email.setError(null);
        }else{
            emailValidate = false;
            t_email.setError("Enter a valid email");
        }
        if(!passwd.getText().toString().isEmpty()){
            pwdValidate = true;
            t_email.setError(null);
        }else{
            pwdValidate = false;
            t_passwd.setError("Password cannot be empty");
        }
        return emailValidate && pwdValidate;
    }

    public void moveToHomeActivity(){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginState.getBoolean("isLoggedIn", false);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isLoggedIn){
            String fullname = loginState.getString("fullname", null);
            String email = loginState.getString("email", null);
            String wallet = loginState.getString("wallet", null);
            String id = loginState.getString("id", null);
            intent.putExtra("fullname", fullname);
            intent.putExtra("email", email);
            intent.putExtra("wallet", wallet);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }


}