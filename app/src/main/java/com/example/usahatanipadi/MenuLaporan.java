package com.example.usahatanipadi;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class MenuLaporan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_laporan);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_laporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Laporan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //untuk kembali ke halaman sebelumnya

        CardView crd = (CardView)findViewById(R.id.histori_transaksi);
        crd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuLaporan.this, SawahHistoriTransaksi.class));
            }
        });

        CardView crd_pendapatan = (CardView)findViewById(R.id.laporan_pendapatan);
        crd_pendapatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuLaporan.this, SawahLaporanPendapatan.class));
            }
        });

    }
}
