package com.iposprinter.printertestdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
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
import java.util.UUID;

import butterknife.OnTextChanged;

public class WithdrawActivity extends AppCompatActivity {

    private EditText fullname, edtEmail, amount;
    private Pinview pin;

    private ResponseListener listener;

    private String email;
    private static final String PIN_VERIFY = "https://tomiwa.com.ng/btcproj-pos/verify_pin";
    private static final String INITIATE_TRANX = "https://tomiwa.com.ng/btcproj-pos/initialize_payment";

    public interface ResponseListener{
        void gotResponse();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        setTitle("Withdraw");
        email = getIntent().getStringExtra("email");
        fullname = (EditText) findViewById(R.id.edt_fullname);
        edtEmail = (EditText) findViewById(R.id.edt_mail);
        amount = (EditText) findViewById(R.id.amount);
        pin = (Pinview) findViewById(R.id.pin);
        listener = new ResponseListener() {
            @Override
            public void gotResponse() {
                String url = "https://tomiwa.com.ng/btcpos-proj/initialize_payment?amount=" + amount.getText() + "&email=" + edtEmail.getText();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        };
        Uri uri = getIntent().getData();
        if(uri != null){
            Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }



    public void onClick(View view){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, PIN_VERIFY,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            JSONObject obj = new JSONObject(response);
//                            if(obj.getString("status").equals("valid")){
//                                listener.gotResponse();
//                            }else{
//                                Toast.makeText(WithdrawActivity.this, "Invalid PIN", Toast.LENGTH_LONG).show();
//                            }
//                        }catch (JSONException jsonException){
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
//                params.put("pin", pin.getValue());
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
        String url = "https://tomiwa.com.ng/btcproj-pos/initialize_payment/?amount=" + amount.getText() + "&email=" + edtEmail.getText();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


}