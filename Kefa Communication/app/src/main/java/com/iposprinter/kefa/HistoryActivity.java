package com.iposprinter.kefa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ResponseListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        listView = (ListView) findViewById(R.id.history_list_view);
        mQueue = Volley.newRequestQueue(this);
        //fetchJsonResponse();

        swipeRefreshLayout.setOnRefreshListener(() -> {

        });

        listener = new ResponseListener() {
            @Override
            public void gotResponse(JSONObject object) {

            }

            @Override
            public void historyResponse(JSONArray obj) {

            }

            @Override
            public void printReceipt(JSONObject object) {

            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void printReceipt(View view){
        Toast.makeText(this, "This is to print the receipt", Toast.LENGTH_LONG).show();
    }

    private void fetchJsonResponse() {
        // Pass second argument as "null" for GET requests
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "https://adamscollege.org/api/users", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(HistoryActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        listener.historyResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HistoryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        /* Add your Requests to the RequestQueue to execute */
        mQueue.add(req);
    }
}
