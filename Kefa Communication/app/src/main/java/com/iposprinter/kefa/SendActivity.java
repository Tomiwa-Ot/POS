package com.iposprinter.kefa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {

    private Pinview pin;
    private EditText name, walletAddress, amountNaira, amountBTC;
    private ProgressBar progressBar;

    private static final String VALIDATE_ADDRESS = "https://tomiwa.com.ng/btcpos-proj/validate_address";
    private static final String SEND_URL = "https://tomiwa.com.ng/btcpos-proj/send";

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
        progressBar = (ProgressBar) findViewById(R.id.send_progress);
    }

    public void scanQRCode(View view){

    }

    public void transaction(View view){
        // check if balance if enough
        verifyAddress();
    }

    public void sendBTC(){
        new StringRequest(Request.Method.POST, SEND_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equals("success")){

                        }else if(obj.getString("status").equals("invalid pin")){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SendActivity.this, "INVALID PIN", Toast.LENGTH_LONG).show();
                        }else{

                        }
                    }catch (JSONException exception){

                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", walletAddress.getText().toString());
                params.put("id", walletAddress.getText().toString());
                params.put("name", name.getText().toString());
                params.put("amount", amountBTC.getText().toString());
                params.put("address", walletAddress.getText().toString());
                return params;
            }
        };
    }

    public void verifyAddress(){
        progressBar.setVisibility(View.VISIBLE);
        new StringRequest(Request.Method.POST, VALIDATE_ADDRESS,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("status")){
                            sendBTC();
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SendActivity.this, "Invalid BTC address", Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException exception){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SendActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("address", walletAddress.getText().toString());
                return params;
            }
        };
    }
}