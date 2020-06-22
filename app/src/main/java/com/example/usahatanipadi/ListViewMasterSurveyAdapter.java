package com.example.usahatanipadi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewMasterSurveyAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> list_nama_surveyor = new ArrayList<String>();
    ArrayList<String> list_kategori_pertanyaan = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    LayoutInflater inflater;
    DatabaseHelper db;

    public ListViewMasterSurveyAdapter(Context applicationContext, ArrayList<String> list_nama_surveyor, ArrayList<String> list_kategori_pertanyaan, ArrayList<String> list_id) {
        this.context = context;
        this.list_nama_surveyor = list_nama_surveyor;
        this.list_kategori_pertanyaan = list_kategori_pertanyaan;
        this.list_id = list_id;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list_nama_surveyor.size();
    }

    @Override
    public Object getItem(int i) {
        return list_nama_surveyor.get(i);
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
        view = inflater.inflate(R.layout.fragment_survey, null);
        TextView nama_surveyor = (TextView)view.findViewById(R.id.tv_nama_surveyor);
        TextView kategori_survey = (TextView)view.findViewById(R.id.tv_kategori_pertanyaan);

        nama_surveyor.setText(list_nama_surveyor.get(i));
        kategori_survey.setText(list_kategori_pertanyaan.get(i));
        return view;
    }
    public Object getItemNamaSurveyor(int i){
        return list_nama_surveyor.get(i);
    }
    public Object getItemKategoriPertanyaan(int i){
        return list_kategori_pertanyaan.get(i);
    }
}
