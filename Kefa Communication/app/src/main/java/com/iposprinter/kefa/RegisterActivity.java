package com.iposprinter.kefa;

import static com.iposprinter.kefa.TermsConditions.Terms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullname, email, phone, passwd;
    private Pinview pin;
    private CheckBox checkBox;
    private ResponseListener listener;
    private ProgressBar progressBar;
    SharedPreferences loginState;

    private static final String REGISTER_URL = "https://tomiwa.com.ng/kefa/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        listener = new ResponseListener() {
            @Override
            public void gotResponse(JSONObject object) {
                try{
                    loginState = getSharedPreferences("Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginState.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("fullname", fullname.getText().toString());
                    editor.putString("email", email.getText().toString());
                    editor.putString("phone", phone.getText().toString());
                    editor.putString("token", object.getString("token"));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("fullname", fullname.getText().toString());
                    intent.putExtra("email", email.getText().toString());
                    intent.putExtra("phone", phone.getText().toString());
                    intent.putExtra("token", object.getString("token"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (JSONException jsonException){
                    jsonException.printStackTrace();
                }
            }

            @Override
            public void historyResponse(JSONArray obj) {

            }

            @Override
            public void printReceipt(JSONObject object) {

            }
        };

        fullname = (EditText) findViewById(R.id.edt_fullname);
        email = (EditText) findViewById(R.id.edt_email);
        phone = (EditText) findViewById(R.id.edt_phone);
        passwd = (EditText) findViewById(R.id.edt_passwd);
        pin = (Pinview) findViewById(R.id.pinview);
        checkBox = (CheckBox) findViewById(R.id.accept_chkbox);
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
    }

    public void register(View view){
        if(validate()){
            progressBar.setVisibility(View.VISIBLE);
            HttpsTrustManager.allowAllSSL();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest request =  new StringRequest(Request.Method.POST, REGISTER_URL,
                    response -> {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("status").equals("success")){
                                listener.gotResponse(obj);
                            }else if(obj.getString("status").equals("account exists")){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                            } else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        } catch(JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }, error -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fullname", fullname.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("phone", phone.getText().toString());
                    params.put("password", passwd.getText().toString());
                    params.put("pin", pin.getValue());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("User-Agent", "KEFA POS");
                    return params;
                }
            };
            requestQueue.add(request);
        }
    }

    public boolean validate(){
        boolean fullnameValidate, emailValidate, phoneValidate, pwdValidate, pinValidate, checkValidate;
        TextInputLayout t_name = (TextInputLayout) findViewById(R.id.fullname_input_layout);
        TextInputLayout t_email = (TextInputLayout) findViewById(R.id.mail_input_layout);
        TextInputLayout t_phone = (TextInputLayout) findViewById(R.id.phone_input_layout);
        TextInputLayout t_passwd = (TextInputLayout) findViewById(R.id.pwd_input_layout);

        if(!fullname.getText().toString().isEmpty() && fullname.getText().toString().matches("[A-Za-z]+\\s[A-Za-z]+")){
            fullnameValidate = true;
            t_name.setError(null);
        }else{
            fullnameValidate = false;
            t_name.setError("*Enter your lastname and firstname");
        }

        if(!email.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            emailValidate = true;
            t_email.setError(null);
        }else{
            emailValidate = false;
            t_email.setError("*Enter a valid email");
        }

        if(!phone.getText().toString().isEmpty() && Patterns.PHONE.matcher(phone.getText().toString()).matches()){
            phoneValidate = true;
            t_phone.setError(null);
        }else{
            phoneValidate = false;
            t_phone.setError("*Enter a valid phone number");
        }

        if(!passwd.getText().toString().isEmpty() && passwd.getText().toString().length() >= 6){
            pwdValidate = true;
            t_passwd.setError(null);
        }else{
            pwdValidate = false;
            t_passwd.setError("*Password must be 6 characters or more");
        }

        if(pin.getValue().length() == 6){
            pinValidate = true;
        }else{
            pinValidate = false;
            Toast.makeText(RegisterActivity.this, "Enter a 6 digit pin", Toast.LENGTH_SHORT).show();
        }

        if(checkBox.isChecked()){
            checkValidate = true;
        }else{
            checkValidate = false;
            Toast.makeText(RegisterActivity.this, "Agree to Terms and Conditions", Toast.LENGTH_SHORT).show();
        }

        return fullnameValidate && emailValidate && phoneValidate && pwdValidate && pinValidate && checkValidate;
    }

    public void showTerms(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("Terms & Conditions");
        alertDialog.setMessage(Terms);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
//        final Button okBtn = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//        LinearLayout.LayoutParams okBtnLL = (LinearLayout.LayoutParams) okBtn.getLayoutParams();
//        okBtnLL.gravity = Gravity.CENTER;
//        okBtn.setLayoutParams(okBtnLL);
    }


}