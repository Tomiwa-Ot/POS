package com.iposprinter.printertestdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
    }

    public void login(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public boolean loginDataValidate(){
        if(!email.getText().toString().isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() &&
                !passwd.getText().toString().isEmpty()
        ){
            return true;
        }else {
            return false;
        }
    }
}