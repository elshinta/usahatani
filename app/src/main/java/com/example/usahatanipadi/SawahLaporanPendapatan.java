package com.example.usahatanipadi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class SawahLaporanPendapatan extends AppCompatActivity {
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_periode,new TampilPeriodeLaporanPendapatanFragment()).commit();
            TextView tv_judul_periode = (TextView)findViewById(R.id.judul_periode);
            tv_judul_periode.setText("Tampil Periode");
        }
    }
    }
