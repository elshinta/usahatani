package com.example.usahatanipadi;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeriodeTransaksiPengeluaran extends AppCompatActivity {
    UserSessionManager session;
    RadioButton rb_tampil_periode;
    TextView tv_tampil_periode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periode_transaksi);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_periode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Periode Awal Masa Tanam");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RadioButton radio_tampil_periode = (RadioButton)findViewById(R.id.radioTampilPeriode);
        RadioButton radio_tambah_periode = (RadioButton)findViewById(R.id.radioTambahPeriode);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_periode,new TampilPeriodePengeluaranFragment()).commit();
            TextView tv_judul_periode = (TextView)findViewById(R.id.judul_periode);
            tv_judul_periode.setText("Tampil Periode");
            radio_tampil_periode.setChecked(true);
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        TextView tv_judul_periode = (TextView)findViewById(R.id.judul_periode);

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioTampilPeriode:
                if (checked)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_periode, new TampilPeriodePengeluaranFragment()).commit();
                tv_judul_periode.setText("Tampil Periode");

                break;
            case R.id.radioTambahPeriode:
                if (checked)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_periode, new TambahPeriodePengeluaranFragment()).commit();
                tv_judul_periode.setText("Tambah Periode");

                final DatabaseHelper db;
                session = new UserSessionManager(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                db = new DatabaseHelper(this);
                // get nama
                final String nama = user.get(UserSessionManager.KEY_NAMA);
                String id_pengguna;
                final List<String> sawah = new ArrayList<String>();
                final HashMap<Integer, String> spinnerSawah = new HashMap<Integer, String>();

                Cursor res = db.getData(nama);

                if (res.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
                    return;
                }
                while (res.moveToNext()) {
                    int i = 0;
                    id_pengguna = res.getString(0);

                    Cursor res_sawah = db.getDataSawah(id_pengguna);
                    if (res_sawah.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
                        return;
                    }
                    while (res_sawah.moveToNext()) {
                        sawah.add(res_sawah.getString(3) + " (" + res_sawah.getString(4) + ")");
                        spinnerSawah.put(i, res_sawah.getString(0));
                        i++;
                        }
                }

                break;
        }
    }

    public void setRadio(Boolean isChecked){
        rb_tampil_periode = (RadioButton)findViewById(R.id.radioTampilPeriode);
        tv_tampil_periode = (TextView)findViewById(R.id.judul_periode);
        if(isChecked){
            rb_tampil_periode.setChecked(true);
            tv_tampil_periode.setText("Tampil Periode");
        }
    }
}
