package com.iposprinter.kefa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter {

    LayoutInflater inflter;
    Context context;
    //ArrayList<Double> amountList;
    ArrayList<String> nameList;
    ArrayList<String> dateList;

    public HistoryListViewAdapter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> dateList){
        //this.amountList = amountList;
        this.nameList = nameList;
        this.dateList = dateList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return nameList.toArray().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.activity_history_list, null);
        //TextView amount = (TextView) convertView.findViewById(R.id.history_amount);
        TextView name = (TextView) convertView.findViewById(R.id.history_name);
        TextView date = (TextView) convertView.findViewById(R.id.history_date);
        //amount.setText(amountList.get(position).toString());
        name.setText(nameList.get(position));
        date.setText(dateList.get(position));
        return convertView;
    }
}
