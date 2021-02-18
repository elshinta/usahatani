package com.example.usahatanipadi;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MenuPenerimaanDanaTersimpan extends AppCompatActivity {
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_penerimaan_dana_tersimpan);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tersimpan_penerimaan_dana);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Penerimaan Dana");

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah_terpilih");
        final String periode_terpilih = intent.getStringExtra("periode_terpilih");
        final String id_penerimaan = intent.getStringExtra("id_penerimaan");

        Cursor cursor = db.getDataPenerimaan(id_penerimaan);
        if(cursor != null)
        {
            if (cursor.moveToFirst()) {
                final String arrData[];
                arrData = new String[cursor.getColumnCount()];

                arrData[0] = cursor.getString(7); // tanggal
                arrData[1] = cursor.getString(1); // id lahan sawah
                arrData[2] = cursor.getString(2); // id jenis
                arrData[3] = cursor.getString(3); // jumlah
                arrData[4] = cursor.getString(5); // total
                arrData[5] = cursor.getString(6); // nama pelanggan
                arrData[6] = cursor.getString(8); // catatan
                arrData[7] = cursor.getString(4); // satuan
                arrData[8] = cursor.getString(10); // luas panen
                arrData[9] = cursor.getString(11); // satuan luas panen

                TextView tv_tanggal_tersimpan = (TextView)findViewById(R.id.tanggal_tersimpan);
                TextView tv_lahan_tersimpan = (TextView)findViewById(R.id.lahan_tersimpan);
                TextView tv_hasil_panen_tersimpan = (TextView)findViewById(R.id.hasil_panen_tersimpan);
                TextView tv_jumlah_hasil_panen_tersimpan = (TextView)findViewById(R.id.jumlah_hasil_panen_tersimpan);
                TextView tv_harga_tersimpan = (TextView)findViewById(R.id.harga_tersimpan);
                TextView tv_total_harga_hasil_panen_tersimpan = (TextView)findViewById(R.id.total_harga_hasil_panen_tersimpan);
                TextView tv_luas_panen = (TextView)findViewById(R.id.luas_panen_tersimpan);
                TextView tv_nama_pelanggan_penerimaan_tersimpan = (TextView)findViewById(R.id.nama_pelanggan_penerimaan_tersimpan);
                TextView tv_catatan_penerimaan_tersimpan = (TextView)findViewById(R.id.catatan_penerimaan_tersimpan);

                Cursor res_lahan = db.getIdSawah(arrData[1]);
                if(res_lahan.getCount() == 0){
                    Toast.makeText(this,"Erorr",Toast.LENGTH_SHORT).show();
                }
                if(res_lahan !=null){
                    if(res_lahan.moveToFirst()){
                        String lahan = res_lahan.getString(4);
                        tv_lahan_tersimpan.setText(lahan);
                    }
                }

                Cursor res_hasil_panen = db.getIdHasilPanen(arrData[2]);
                if(res_hasil_panen.getCount() == 0){
                    Toast.makeText(this,"Erorr",Toast.LENGTH_SHORT).show();
                }
                if(res_hasil_panen!=null){
                    if(res_hasil_panen.moveToFirst()){
                        String hasil_panen = res_hasil_panen.getString(1);
                        tv_hasil_panen_tersimpan.setText(hasil_panen);
                    }
                }

                DecimalFormat formatter = new DecimalFormat("#,###");
                String format_tot_hasil_panen = formatter.format(Integer.parseInt(arrData[4]));

                float konversi_satuan=0;
                if(arrData[7].equals("ton")) {
                    konversi_satuan = Float.parseFloat(arrData[3])*1000;
                }
                else if(arrData[7].equals("kuintal")){
                    konversi_satuan = Float.parseFloat(arrData[3])*100;
                }
                else if(arrData[7].equals("kg")){
                    konversi_satuan = Float.parseFloat(arrData[3])*1;
                }

                float harga;
                harga = Float.parseFloat(arrData[4]) / konversi_satuan;

                String format_harga = formatter.format(harga);
                DecimalFormat df = new DecimalFormat("#.##");
                String format_konversi = df.format(konversi_satuan);

                String format_jml = arrData[3].replaceAll("\\.", ",");

                tv_tanggal_tersimpan.setText(arrData[0]);
                tv_jumlah_hasil_panen_tersimpan.setText(format_jml + " " + arrData[7] + " (" + format_konversi + " " + "kg)");
                tv_harga_tersimpan.setText("Rp. " + format_harga);
                tv_total_harga_hasil_panen_tersimpan.setText("Rp. " + format_tot_hasil_panen);
                tv_luas_panen.setText(arrData[8] + " " + arrData[9]);
                tv_nama_pelanggan_penerimaan_tersimpan.setText(arrData[5]);
                tv_catatan_penerimaan_tersimpan.setText(arrData[6]);
            }

            cursor.close();
        }
        db.close();

        Button btn = (Button)findViewById(R.id.btn_kembali_menu_utama);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPenerimaanDanaTersimpan.this, MenuUtama.class));
            }
        });

        Button btn_laporan = (Button)findViewById(R.id.btn_lihat_laporan_penerimaan);
        btn_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPenerimaanDanaTersimpan.this, LaporanHistoriTransaksi.class);
                intent.putExtra("sawah", sawah_terpilih);
                intent.putExtra("periode", periode_terpilih);
                intent.putExtra("spinner", "1");
                startActivity(intent);
            }
        });
    }
}
