package com.iposprinter.kefa;

//import android.content.ClipData;
//import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences loginState;
//    ClipboardManager myClipboard;
//    ClipData myClip;

    private TextView name;
    private String fullname, email;
    private LinearLayout buyPage, historyLL, testPrinter, logoutLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        name = (TextView) findViewById(R.id.txt_name);
        fullname = getIntent().getStringExtra("fullname");
        email = getIntent().getStringExtra("email");
        name.setText(fullname);
        testPrinter = (LinearLayout) findViewById(R.id.test_printer);
        logoutLL = (LinearLayout) findViewById(R.id.logout_ll);
        historyLL = (LinearLayout) findViewById(R.id.history_ll);
        buyPage = (LinearLayout) findViewById(R.id.buy_screen);
        buyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BuyActivity.class);
                startActivity(intent);
            }
        });
        historyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        testPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, IPosPrinterTestDemo.class);
                startActivity(intent);
            }
        });
        logoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginState.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(HomeActivity.this,  LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

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
//        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//        myClip = ClipData.newPlainText("text", address);
//        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(getApplicationContext(),"Wallet Address Copied",Toast.LENGTH_SHORT).show();
    }
//
//    public void buy(View view){
//        Intent intent = new Intent(this, BuyActivity.class);
//        startActivity(intent);
//    }


//    public void send(View view){
//        Intent intent = new Intent(this, SendActivity.class);
//        startActivity(intent);
//    }

//    public void receive(View view){
//        Intent intent = new Intent(this, ReceiveActivity.class);
//        intent.putExtra("fullname", fullname);
//        startActivity(intent);
//    }
//
//    public void pay(View view){
//        Intent intent = new Intent(this, PayActivity.class);
//        intent.putExtra("fullname", fullname);
//        intent.putExtra("email", email);
//        startActivity(intent);
//    }

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
//
//
//
//    public void testPrint(View view){
//        Intent intent = new Intent(this, IPosPrinterTestDemo.class);
//        startActivity(intent);
//    }
}