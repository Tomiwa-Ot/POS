package com.iposprinter.kefa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        TextView name = (TextView) findViewById(R.id.txt_name);
        String fullname = getIntent().getStringExtra("fullname");
        // String email = getIntent().getStringExtra("email");
        name.setText(fullname);
        LinearLayout testPrinter = (LinearLayout) findViewById(R.id.test_printer);
        LinearLayout logoutLL = (LinearLayout) findViewById(R.id.logout_ll);
        LinearLayout historyLL = (LinearLayout) findViewById(R.id.history_ll);
        LinearLayout buyPage = (LinearLayout) findViewById(R.id.buy_screen);
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
    }


}