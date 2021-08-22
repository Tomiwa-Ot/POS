package com.iposprinter.kefa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstname, lastname, email, passwd;
    private Pinview pin;
    private ResponseListener listener;
    private ProgressBar progressBar;
    SharedPreferences loginState;

    private static final String REGISTER_URL = "https://tomiwa.com.ng/btcpos-proj/register";

    public interface ResponseListener{
        void gotResponse(JSONObject object);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        listener = new ResponseListener() {
            @Override
            public void gotResponse(JSONObject object) {
                try{
                    loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginState.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("firstname", firstname.getText().toString());
                    editor.putString("lastname", lastname.getText().toString());
                    editor.putString("email", email.getText().toString());
                    editor.putString("address", object.getString("address"));
                    editor.putString("id", object.getString("id"));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (JSONException jsonException){

                }
            }
        };
        firstname = (EditText) findViewById(R.id.edt_firstname);
        lastname = (EditText) findViewById(R.id.edt_lastname);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        pin = (Pinview) findViewById(R.id.pinview);
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
    }

    public void register(View view){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request =  new StringRequest(Request.Method.POST, REGISTER_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equals("success")){
                            listener.gotResponse(obj);
                        }else if(obj.getString("status").equals("account exists")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                        } else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
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
                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", firstname.getText().toString());
                params.put("lastname", lastname.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", passwd.getText().toString());
                params.put("pin", pin.getValue());
                return params;
            }
        };
        requestQueue.add(request);
    }




}