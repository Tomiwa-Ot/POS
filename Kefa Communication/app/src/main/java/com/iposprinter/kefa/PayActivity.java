package com.iposprinter.kefa;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PayActivity extends AppCompatActivity {

    private EditText fullname, edtEmail, amount;
    private Pinview pin;

    private ResponseListener listener;

    private String email;
    private static final String PIN_VERIFY = "https://tomiwa.com.ng/btcproj-pos/verify_pin";
    private static final String INITIATE_TRANX = "https://tomiwa.com.ng/btcproj-pos/initialize_payment";

    public interface ResponseListener{
        void gotResponse();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setTitle("Pay");
        email = getIntent().getStringExtra("email");
        fullname = (EditText) findViewById(R.id.edt_fullname);
        edtEmail = (EditText) findViewById(R.id.edt_mail);
        amount = (EditText) findViewById(R.id.amount);
        pin = (Pinview) findViewById(R.id.pin);
        listener = new ResponseListener() {
            @Override
            public void gotResponse() {
                String url = "https://tomiwa.com.ng/btcpos-proj/initialize_payment";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        };

    }



    public void onClick(View view){
        if(formValidate()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Fill all fields");
            builder.setCancelable(true);
            builder.setPositiveButton(
                    "Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // dialog.cancel();
                        }
                    }
            );
            builder.setNegativeButton(
                "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                    }
                }
            );
            builder.show();
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, PIN_VERIFY,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try{
//                                JSONObject obj = new JSONObject(response);
//                                if(obj.getString("status").equals("valid")){
//                                    listener.gotResponse();
//                                }else{
//                                    Toast.makeText(PayActivity.this, "Invalid PIN", Toast.LENGTH_LONG).show();
//                                }
//                            }catch (JSONException jsonException){
//
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }
//            ){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("email", email);
//                    params.put("pin", pin.getValue());
//                    return params;
//                }
//            };
//            requestQueue.add(stringRequest);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Fill all fields");
            builder.setCancelable(true);
            builder.setPositiveButton(
                "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
            );
            builder.show();
        }

        String url = "https://tomiwa.com.ng/btcproj-pos/initialize_payment/?amount=" + amount.getText() + "&email=" + edtEmail.getText();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public boolean formValidate(){
        return fullname != null && edtEmail != null && amount != null &&  pin != null;
    }

}