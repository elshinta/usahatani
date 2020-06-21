package com.example.usahatanipadi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewMasterPupukAdapter extends BaseAdapter {
    String[][] data;
    Activity activity;
    Context context;
    ArrayList<String> list_pupuk = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    LayoutInflater inflater;
    DatabaseHelper db;

    public ListViewMasterPupukAdapter(Context applicationContext, ArrayList<String> list_pupuk, ArrayList<String> list_id) {
        this.context = context;
        this.list_pupuk = list_pupuk;
        this.list_id = list_id;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list_pupuk.size();
    }

    @Override
    public Object getItem(int i) {
        return list_pupuk.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public Object getItemIds(int i) {
        return list_id.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_listview_master_pupuk, null);
        TextView tv_pupuk = (TextView)view.findViewById(R.id.tv_data_pupuk);
        tv_pupuk.setText(list_pupuk.get(i));
        return view;
    }
}
