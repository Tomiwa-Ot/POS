package com.iposprinter.printertestdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class QrCodeActivity extends AppCompatActivity {

    private  String walletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setTitle("Qr Code");
        walletAddress = getIntent().getStringExtra("address");
        Toast.makeText(getApplicationContext(), walletAddress, Toast.LENGTH_SHORT).show();
    }
}