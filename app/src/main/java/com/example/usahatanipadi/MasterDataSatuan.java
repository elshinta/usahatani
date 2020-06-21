package com.example.usahatanipadi;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MasterDataSatuan extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterSatuanAdapter customAdapter;
    ArrayList<String> listdatasatuan = new ArrayList<String>();
    ArrayList<Integer> list_id = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_satuan);

        Toolbar toolbar = findViewById(R.id.toolbar_data_satuan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Satuan");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewData();

    }
        public void viewData(){
            String list_satuan [] = getResources().getStringArray(R.array.satuan_berat_pengeluaran);
            listdatasatuan.clear();
            list_id.clear();

            for(int i=0;i<list_satuan.length;i++){
                listdatasatuan.add(list_satuan[i]);
                list_id.add(i);
            }

            simpleList = (ListView) findViewById(R.id.lv_data_satuan);

            customAdapter = new ListViewMasterSatuanAdapter(getApplicationContext(), listdatasatuan, list_id);

            simpleList.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();

        }
    }


