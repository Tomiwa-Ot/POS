package com.iposprinter.printertestdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    SharedPreferences loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        loginPersistence();
    }

    public void loginPersistence(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        boolean isLoggedIn = loginState.getBoolean("isLoggedIn", false);
        if(isLoggedIn){
            startActivity(homeIntent);
        }else{
            startActivity(loginIntent);
        }
    }
}