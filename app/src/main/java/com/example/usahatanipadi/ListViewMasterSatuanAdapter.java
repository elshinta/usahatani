package com.example.usahatanipadi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewMasterSatuanAdapter extends BaseAdapter {
    TextView text1, text2, text3;
    String[][] data;
    Activity activity;
    Context context;
    ArrayList<String> list_satuan = new ArrayList<String>();
    ArrayList<Integer> list_id = new ArrayList<Integer>();
    LayoutInflater inflater;
    DatabaseHelper db;

    public ListViewMasterSatuanAdapter(Context applicationContext, ArrayList<String> list_satuan, ArrayList<Integer> list_id) {
        this.context = context;
        this.list_satuan = list_satuan;
        this.list_id = list_id;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list_satuan.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_listview_master_pupuk, null);
        TextView satuan = (TextView)view.findViewById(R.id.tv_data_pupuk);
        satuan.setText(list_satuan.get(i));
        return view;
    }
}
