package com.iposprinter.kefa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passwd;
    private ProgressBar progressBar;
    private ResponseListener listener;
    SharedPreferences loginState;
    private static final String LOGIN_URL = "https://tomiwa.com.ng/kefa/login";


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
                    // editor.putString("phone", object.getString("phone"));
                    editor.putString("token", object.getString("token"));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("fullname", object.getString("fullname"));
                    intent.putExtra("email", email.getText().toString());
                    // intent.putExtra("phone", object.getString("phone"));
                    intent.putExtra("token", object.getString("token"));
                    startActivity(intent);
                    LoginActivity.this.finish();
                }catch(JSONException jsonException){
                    jsonException.printStackTrace();
                }
            }

            @Override
            public void historyResponse(JSONArray obj) {

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
            HttpsTrustManager.allowAllSSL();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    response -> {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("status").equals("success")){
                                listener.gotResponse(obj);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login Failed. Invalid username/password", Toast.LENGTH_LONG).show();
                            }
                        } catch(JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }, error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Oops something went wrong", Toast.LENGTH_LONG).show();
                }
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email.getText().toString());
                    params.put("password", passwd.getText().toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("User-Agent","KEFA POS");
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
            t_email.setError("*Enter a valid email");
        }

        if(!passwd.getText().toString().isEmpty() && passwd.getText().toString().length() >= 6){
            pwdValidate = true;
            t_email.setError(null);
        }else{
            pwdValidate = false;
            t_passwd.setError("*Password cannot be empty");
        }

        return emailValidate && pwdValidate;
    }

    public void moveToHomeActivity(){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginState.getBoolean("isLoggedIn", false);
        Intent intent = new Intent(this, HomeActivity.class);
        if(isLoggedIn){
            String fullname = loginState.getString("fullname", null);
            String email = loginState.getString("email", null);
            // String phone = loginState.getString("phone", null);
            String token = loginState.getString("token", null);
            intent.putExtra("fullname", fullname);
            intent.putExtra("email", email);
            // intent.putExtra("phone", phone);
            intent.putExtra("token", token);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }


}