package com.iposprinter.printertestdemo;

import android.app.Service;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        firstname = (EditText) findViewById(R.id.edt_firstname);
        lastname = (EditText) findViewById(R.id.edt_lastname);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        pin = (Pinview) findViewById(R.id.pinview);
    }

    public void register(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }




}