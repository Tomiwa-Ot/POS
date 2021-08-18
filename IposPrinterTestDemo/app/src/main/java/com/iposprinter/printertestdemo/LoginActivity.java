package com.iposprinter.printertestdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passwd;
    TextInputLayout t_email, t_passwd;
    SharedPreferences loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        moveToHomeActivity();
    }


    public void login(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        if(loginDataValidate()){
            loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginState.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.commit();
            startActivity(intent);
        }
    }

    public void registerPage(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    public boolean loginDataValidate(){
        boolean emailValidate, pwdValidate = false;
        t_email = (TextInputLayout) findViewById(R.id.email_input_layout);
        t_passwd = (TextInputLayout) findViewById(R.id.passwd_input_layout);
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
        if(emailValidate && pwdValidate){
            return true;
        }else{
            return false;
        }
    }

    public void moveToHomeActivity(){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginState.getBoolean("isLoggedIn", false);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isLoggedIn){
            startActivity(intent);
        }
    }


}