package com.iposprinter.kefa;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences loginState;
    ClipboardManager myClipboard;
    ClipData myClip;

    private TextView wallet, name;
    private String fullname, email, address, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        name = (TextView) findViewById(R.id.txt_name);
        fullname = getIntent().getStringExtra("fullname");
        email = getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id");
        name.setText(fullname);
        wallet.setText(address);

        Uri uri = getIntent().getData();
        if(uri != null){
//            fullname = loginState.getString("fullname", null);
//            email = loginState.getString("email", null);
//            address = loginState.getString("wallet", null);
//            id = loginState.getString("id", null);
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void copy(View view){
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        myClip = ClipData.newPlainText("text", address);
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
        intent.putExtra("fullname", fullname);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    public void pay(View view){
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("email", email);
        intent.putExtra("address", address);
        intent.putExtra("id", id);
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
        editor.apply();
        Intent intent = new Intent(this,  LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    public void testPrint(View view){
        Intent intent = new Intent(this, IPosPrinterTestDemo.class);
        startActivity(intent);
    }
}