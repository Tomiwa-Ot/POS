package com.iposprinter.printertestdemo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences loginState;
    ClipboardManager myClipboard;
    ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
    }

    public void copy(View view){
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        String text = "replace with wallet address";
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(getApplicationContext(),"Wallet Address Copied",Toast.LENGTH_SHORT).show();
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

    public void withdraw(View view){
        Intent intent = new Intent(this, WithdrawActivity.class);
        startActivity(intent);
    }

    public void history(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logout(View view){
        loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginState.edit();
        editor.clear();
        editor.commit();
//        RequestQueue mQueue = Volley.newRequestQueue(this);
//        mQueue.getCache().clear();
        Intent intent = new Intent(this,  LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    public void testPrint(View view){
        Intent intent = new Intent(this, IPosPrinterTestDemo.class);
        startActivity(intent);
    }
}