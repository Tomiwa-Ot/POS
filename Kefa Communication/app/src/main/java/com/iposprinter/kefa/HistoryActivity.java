package com.iposprinter.kefa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView lv;
    private RequestQueue mQueue;
    private ArrayList<Double> amountList;
    private ArrayList<String> nameList, dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        lv = (ListView) findViewById(R.id.history_list_view);
        mQueue = Volley.newRequestQueue(this);
        fetchJsonResponse();

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
//                        try {
//                            for(int i = 0; i < response.length(); i ++){
//                                JSONObject historyInfo = response.getJSONObject(i);
//                                //amountList.add(Double.parseDouble(historyInfo.getString("amount")));
//                                nameList.add(historyInfo.getString("Id"));
//                                dateList.add(historyInfo.getString("Password"));
//                            }
//                            HistoryListViewAdapter historyListViewAdapter = new HistoryListViewAdapter(getApplicationContext(), nameList, dateList);
//                            lv.setAdapter(historyListViewAdapter);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
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
