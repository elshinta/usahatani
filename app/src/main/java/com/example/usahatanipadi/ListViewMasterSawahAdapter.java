package com.example.usahatanipadi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListViewMasterSawahAdapter extends BaseAdapter {
    TextView text1, text2, text3;
    String[][] data;
    Activity activity;
    Context context;
    ArrayList<String> list_alamat = new ArrayList<String>();
    ArrayList<String> list_luas = new ArrayList<String>();
    ArrayList<String> list_kategori = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    ArrayList<String> list_satuan = new ArrayList<String>();
    ArrayList<String> list_latitude = new ArrayList<>();
    ArrayList<String> list_longitude = new ArrayList<>();
    LayoutInflater inflater;
    DatabaseHelper db;

    public ListViewMasterSawahAdapter(Context applicationContext, ArrayList<String> list_alamat, ArrayList<String> list_luas,
                                      ArrayList<String> list_kategori, ArrayList<String> list_id,ArrayList<String> list_satuan,
                                      ArrayList<String> list_latitude, ArrayList<String> list_longitude) {
        this.context = context;
        this.list_alamat = list_alamat;
        this.list_luas = list_luas;
        this.list_kategori = list_kategori;
        this.list_id = list_id;
        this.list_satuan = list_satuan;
        this.list_latitude = list_latitude;
        this.list_longitude = list_longitude;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list_alamat.size();
    }

    @Override
    public Object getItem(int i) {
        return list_alamat.get(i);
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
        view = inflater.inflate(R.layout.custom_listview_data_sawah, null);
        TextView alamat = (TextView)view.findViewById(R.id.tv_alamat);
        TextView luas = (TextView)view.findViewById(R.id.tv_luas);
        TextView kategori = (TextView)view.findViewById(R.id.tv_kategori);
        TextView tvLatitude = view.findViewById(R.id.tv_latitude);
        TextView tvLongitude = view.findViewById(R.id.tv_longitude);

        alamat.setText(list_alamat.get(i));
        DecimalFormat df = new DecimalFormat("#.##");

        if(list_satuan.get(i).equals("Rante")){
            Float luas_sawah = Float.parseFloat(list_luas.get(i))/25;
            String format_konversi = df.format(luas_sawah);

            luas.setText(list_luas.get(i) + " " + list_satuan.get(i) + " (" + format_konversi + " Ha)");
        }
        else if(list_satuan.get(i).equals("Bau")){
            Float luas_sawah = Float.parseFloat(list_luas.get(i))/8;
            String format_konversi = df.format(luas_sawah);
            luas.setText(list_luas.get(i) + " " + list_satuan.get(i) + " (" + format_konversi + " Ha)");
        }
        else{
            Float luas_sawah = Float.parseFloat(list_luas.get(i));
            String format_konversi = df.format(luas_sawah);
            luas.setText(list_luas.get(i) + " " + list_satuan.get(i) + " (" + format_konversi + " Ha)");
        }

        kategori.setText(list_kategori.get(i));
        tvLatitude.setText(list_latitude.get(i));
        tvLongitude.setText(list_longitude.get(i));
        return view;
    }
    public Object getItemLuas(int i){
        return list_luas.get(i);
    }
    public Object getItemAlamat(int i){
        return list_alamat.get(i);
    }
    public Object getItemKategori(int i){
        return list_kategori.get(i);
    }
    public Object getItemSatuan(int i){
        return list_satuan.get(i);
    }

    public Object getItemlatitude(int i) {
        return list_latitude.get(i);
    }

    public Object getListLongitude(int i) {
        return list_longitude.get(i);
    }
}
