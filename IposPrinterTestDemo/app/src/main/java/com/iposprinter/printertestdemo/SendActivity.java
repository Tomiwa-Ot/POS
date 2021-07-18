package com.iposprinter.printertestdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.goodiebag.pinview.Pinview;

public class SendActivity extends AppCompatActivity {

    private Pinview pin;
    private EditText name, walletAddress, amountNaira, amountBTC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        setTitle("Send");
        pin = (Pinview) findViewById(R.id.pinview);
        name = (EditText) findViewById(R.id.customerName);
        walletAddress = (EditText) findViewById(R.id.wallet_address);
        amountBTC = (EditText) findViewById(R.id.amount_btc);
        amountNaira = (EditText) findViewById(R.id.amount_naira);
    }

    public void scanQRCode(View view){

    }
}