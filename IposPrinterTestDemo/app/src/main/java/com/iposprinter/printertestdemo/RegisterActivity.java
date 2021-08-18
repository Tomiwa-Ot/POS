package com.iposprinter.printertestdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.goodiebag.pinview.Pinview;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstname, lastname, email, passwd;
    private Pinview pin;
    SharedPreferences loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        firstname = (EditText) findViewById(R.id.edt_firstname);
        lastname = (EditText) findViewById(R.id.edt_lastname);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        pin = (Pinview) findViewById(R.id.pinview);
    }

    public void register(View view){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginState.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.commit();
        Intent intent = new Intent(this,  HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




}