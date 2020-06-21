package com.example.usahatanipadi;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SawahHistoriTransaksi extends AppCompatActivity {
    UserSessionManager session;
    RadioButton rb_tampil_periode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sawah_transaksi);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_periode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pilih Masa Tanam");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_periode,new TampilPeriodeHistoriTransaksiFragment()).commit();
            TextView tv_judul_periode = (TextView)findViewById(R.id.judul_periode);
            tv_judul_periode.setText("Tampil Periode");
        }
    }
    }
