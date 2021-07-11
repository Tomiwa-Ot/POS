package com.iposprinter.printertestdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void testPrint(View view){
        Intent intent = new Intent(this, IPosPrinterTestDemo.class);
        startActivity(intent);
    }
}