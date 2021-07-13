package com.iposprinter.printertestdemo;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstname, lastname, email, passwd;
    private EditText pin1, pin2, pin3, pin4, pin5, pin6, hiddenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        setTitle("Receive");
        firstname = (EditText) findViewById(R.id.edt_firstname);
        lastname = (EditText) findViewById(R.id.edt_lastname);
        email = (EditText) findViewById(R.id.edt_email);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        pin1 = (EditText) findViewById(R.id.digit1);
        pin2 = (EditText) findViewById(R.id.digit2);
        pin3 = (EditText) findViewById(R.id.digit3);
        pin4 = (EditText) findViewById(R.id.digit4);
        pin5 = (EditText) findViewById(R.id.digit5);
        pin6 = (EditText) findViewById(R.id.digit6);
        hiddenText = (EditText) findViewById(R.id.pin_hidden_edittext);
        setPINListeners();
    }

    public void register(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        hiddenText.addTextChangedListener((TextWatcher) this);

        pin1.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        pin2.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        pin3.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        pin4.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        pin5.setOnFocusChangeListener((View.OnFocusChangeListener) this);
        pin6.setOnFocusChangeListener((View.OnFocusChangeListener) this);

        pin1.setOnKeyListener((View.OnKeyListener) this);
        pin2.setOnKeyListener((View.OnKeyListener) this);
        pin3.setOnKeyListener((View.OnKeyListener) this);
        pin4.setOnKeyListener((View.OnKeyListener) this);
        pin5.setOnKeyListener((View.OnKeyListener) this);
        pin6.setOnKeyListener((View.OnKeyListener) this);
        hiddenText.setOnKeyListener((View.OnKeyListener) this);
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }


    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        if (hasFocus) {
            setFocus(hiddenText);
            showSoftKeyboard(hiddenText);
        }
    }

}