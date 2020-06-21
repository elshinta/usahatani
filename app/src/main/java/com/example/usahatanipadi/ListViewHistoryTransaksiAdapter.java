package com.example.usahatanipadi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewHistoryTransaksiAdapter extends BaseAdapter {
    String[][] data;
    Activity activity;
    Context context;
    ArrayList<String> list_id = new ArrayList<String>();
    ArrayList<String> list_data = new ArrayList<String>();
    ArrayList<String> list_tanggal = new ArrayList<String>();
    ArrayList<String> list_jumlah = new ArrayList<String>();
    ArrayList<String> list_harga = new ArrayList<String>();
    ArrayList<String> list_total_harga = new ArrayList<String>();
    ArrayList<String> list_luas_panen = new ArrayList<String>();
    ArrayList<String> list_nama = new ArrayList<String>();
    ArrayList<String> list_catatan = new ArrayList<String>();
    String status;
    LayoutInflater inflater;
    DatabaseHelper db;

    public ListViewHistoryTransaksiAdapter(Context applicationContext, ArrayList<String> list_id, ArrayList<String> list_data, ArrayList<String> list_tanggal, ArrayList<String> list_jumlah, ArrayList<String> list_harga, ArrayList<String> list_total_harga, ArrayList<String> list_luas_panen, ArrayList<String> list_nama, ArrayList<String> list_catatan, String status) {
        this.context = context;
        this.list_id = list_id;
        this.list_data = list_data;
        this.list_tanggal = list_tanggal;
        this.list_jumlah = list_jumlah;
        this.list_harga = list_harga;
        this.list_total_harga = list_total_harga;
        this.list_luas_panen = list_luas_panen;
        this.list_nama = list_nama;
        this.list_catatan = list_catatan;
        this.status = status;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list_data.size();
    }

    @Override
    public Object getItem(int i) {
        return list_id.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_listview_histori_transaksi, null);
        TextView data = (TextView)view.findViewById(R.id.tv_data);
        TextView tanggal = (TextView)view.findViewById(R.id.tv_tanggal);
        TextView jumlah = (TextView)view.findViewById(R.id.tv_jumlah);
        TextView harga = (TextView)view.findViewById(R.id.tv_harga);
        TextView total_harga = (TextView)view.findViewById(R.id.tv_total_harga);
        TextView luas_panen = (TextView)view.findViewById(R.id.tv_luas_panen);
        TextView nama = (TextView)view.findViewById(R.id.tv_nama);
        TextView catatan = (TextView)view.findViewById(R.id.tv_catatan);
        data.setText(list_data.get(i));
        tanggal.setText(list_tanggal.get(i));
        jumlah.setText(list_jumlah.get(i));
        harga.setText(list_harga.get(i));
        total_harga.setText(list_total_harga.get(i));
        luas_panen.setText(list_luas_panen.get(i));
        nama.setText(list_nama.get(i));
        catatan.setText(list_catatan.get(i));

        if(status.equals("1")){
            luas_panen.setVisibility(View.GONE);
        }
        return view;
    }
}
