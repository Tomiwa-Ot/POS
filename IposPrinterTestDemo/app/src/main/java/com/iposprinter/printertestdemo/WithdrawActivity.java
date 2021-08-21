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

    private EditText fullname, email, amount;
    private Pinview pin;


    private static final String INITIATE_TRANX = "https://tomiwa.com.ng/btcproj-pos/initialize_payment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        setTitle("Withdraw");

        fullname = (EditText) findViewById(R.id.edt_fullname);
        email = (EditText) findViewById(R.id.edt_mail);
        amount = (EditText) findViewById(R.id.amount);
        pin = (Pinview) findViewById(R.id.pin);
        Uri uri = getIntent().getData();
        if(uri != null){
            Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }



    public void onClick(View view){
        String url = "https://tomiwa.com.ng/btcpos-proj/initialize_payment/?amount=" + amount.getText() + "&email=" + email.getText();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


}