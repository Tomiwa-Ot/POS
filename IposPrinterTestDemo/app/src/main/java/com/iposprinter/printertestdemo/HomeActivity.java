package com.iposprinter.printertestdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
    }

    public void buy(View view){
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }

    public void send(View view){
        Intent intent = new Intent(this, SendActivity.class);
        startActivity(intent);
    }

    public void receive(View view){
        Intent intent = new Intent(this, ReceiveActivity.class);
        startActivity(intent);
    }

    public void history(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void logout(View view){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginState.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void testPrint(View view){
        Intent intent = new Intent(this, IPosPrinterTestDemo.class);
        startActivity(intent);
    }
}