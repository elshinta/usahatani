package com.example.usahatanipadi;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MenuPengeluaranBiayaTersimpan extends AppCompatActivity {
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pengeluaran_biaya_tersimpan);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tersimpan_pengeluaran_biaya);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pengeluaran Biaya");

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah_terpilih");
        final String periode_terpilih = intent.getStringExtra("periode_terpilih");
        final String id_pengeluaran = intent.getStringExtra("id_pengeluaran");

        Log.d("debugging",id_pengeluaran);

        Cursor cursor = db.getDataPengeluaran(id_pengeluaran);
        if(cursor != null)
        {
            if (cursor.moveToFirst()) {
                final String arrData[];
                arrData = new String[cursor.getColumnCount()];

                arrData[0] = cursor.getString(6); // tanggal
                arrData[1] = cursor.getString(1); // id lahan sawah
                arrData[2] = cursor.getString(2); // id jenis barang jasa
                arrData[3] = cursor.getString(3); // jumlah
                arrData[4] = cursor.getString(4); // total
                arrData[5] = cursor.getString(5); // nama pemasok
                arrData[6] = cursor.getString(7); // catatan
                arrData[7] = cursor.getString(8); // periode
                arrData[8] = cursor.getString(9); // satuan

                TextView tv_tgl_pengeluaran_tersimpan = (TextView)findViewById(R.id.tgl_pengeluaran_tersimpan);
                TextView tv_lahan_pengeluaran_tersimpan = (TextView)findViewById(R.id.lahan_pengeluaran_tersimpan);
                TextView tv_periode_tersimpan = (TextView)findViewById(R.id.periode_tersimpan);
                TextView tv_jenis_barang_jasa_tersimpan = (TextView)findViewById(R.id.jenis_barang_jasa_tersimpan);
                TextView tv_jumlah_barang_jasa_tersimpan = (TextView)findViewById(R.id.jumlah_barang_jasa_tersimpan);
                TextView tv_harga_tersimpan = (TextView)findViewById(R.id.harga_tersimpan);
                TextView tv_total_barang_jasa_tersimpan = (TextView)findViewById(R.id.total_barang_jasa_tersimpan);
                TextView tv_nama_pemasok_tersimpan = (TextView)findViewById(R.id.nama_pemasok_tersimpan);
                TextView tv_catatan_pengeluaran_tersimpan = (TextView)findViewById(R.id.catatan_pengeluaran_tersimpan);

                Cursor res_lahan = db.getIdSawah(arrData[1]);
                if(res_lahan.getCount() == 0){
                    Toast.makeText(this,"Erorr",Toast.LENGTH_SHORT).show();
                }
                if(res_lahan !=null){
                    if(res_lahan.moveToFirst()){
                        String lahan = res_lahan.getString(3);
                        tv_lahan_pengeluaran_tersimpan.setText(lahan);
                    }
                }


                Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(arrData[2]);
                if(res_kebutuhan_tanam.getCount() == 0){
                    Toast.makeText(this,"Erorr",Toast.LENGTH_SHORT).show();
                }
                if(res_kebutuhan_tanam!=null){
                    if(res_kebutuhan_tanam.moveToFirst()){
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);
                        tv_jenis_barang_jasa_tersimpan.setText(kebutuhan_tanam);
                    }
                }

                Cursor res_periode = db.getDataIdPeriode(arrData[7]);
                if(res_periode.getCount() == 0){
                    Toast.makeText(this,"Erorr",Toast.LENGTH_SHORT).show();
                }
                if(res_periode!=null){
                    if(res_periode.moveToFirst()){
                        String periode = res_periode.getString(2) + " - " + res_periode.getString(4);
                        tv_periode_tersimpan.setText(periode);
                    }
                }

                DecimalFormat formatter = new DecimalFormat("#,###");
                String format_tot_barang_jasa = formatter.format(Integer.parseInt(arrData[4]));

                float konversi_satuan=0;
                if(arrData[8].equals("ton")) {
                    konversi_satuan = Float.parseFloat(arrData[3])*1000;
                }
                else if(arrData[8].equals("kuintal")){
                    konversi_satuan = Float.parseFloat(arrData[3])*100;
                }
                else if(arrData[8].equals("kg")){
                    konversi_satuan = Float.parseFloat(arrData[3])*1;
                }

                float harga;
                harga = Float.parseFloat(arrData[4]) / konversi_satuan;

                String format_harga = formatter.format(harga);
                DecimalFormat df = new DecimalFormat("#.##");
                String format_konversi = df.format(konversi_satuan);

                String format_jml = arrData[3].replaceAll("\\.", ",");

                tv_tgl_pengeluaran_tersimpan.setText(arrData[0]);
                tv_jumlah_barang_jasa_tersimpan.setText(format_jml + " " + arrData[8] + " (" + format_konversi + " " + "kg)");
                tv_harga_tersimpan.setText("Rp. " + format_harga);
                tv_total_barang_jasa_tersimpan.setText("Rp. " + format_tot_barang_jasa);
                tv_nama_pemasok_tersimpan.setText(arrData[5]);
                tv_catatan_pengeluaran_tersimpan.setText(arrData[6]);

            }

            cursor.close();
        }
        db.close();


        Button btn = (Button)findViewById(R.id.btn_kembali_menu_utama);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MenuPengeluaranBiayaTersimpan.this, MenuUtama.class));
            }
        });

        Button btn_laporan = (Button)findViewById(R.id.btn_lihat_laporan_pengeluaran);
        btn_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPengeluaranBiayaTersimpan.this, LaporanHistoriTransaksi.class);
                intent.putExtra("sawah", sawah_terpilih);
                intent.putExtra("periode", periode_terpilih);
                intent.putExtra("spinner", "0");
                startActivity(intent);
            }
        });
    }
}
