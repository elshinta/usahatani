package com.example.usahatanipadi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TampilPeriodePenerimaanFragment extends Fragment {
    UserSessionManager session;
    public DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tampil_periode, container, false);


        final List<String> sawah = new ArrayList<String>();
        final HashMap<Integer, String> spinnerSawah = new HashMap<Integer, String>();
        final HashMap<Integer, String> spinnerTahun = new HashMap<Integer, String>();


        db = new DatabaseHelper(getActivity());
        session = new UserSessionManager(this.getActivity());
        HashMap<String, String> user = session.getUserDetails();
        String id_pengguna;
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res = db.getData(nama);


        if (res.getCount() == 0) {
            Toast.makeText(this.getActivity(), "Erorr!", Toast.LENGTH_SHORT).show();
            return null;
        }
        while (res.moveToNext()) {
            int i = 0;
            id_pengguna = res.getString(0);

            Cursor res_sawah = db.getDataSawah(id_pengguna);
            if (res_sawah.getCount() == 0) {
                Toast.makeText(this.getActivity(), "Erorr!", Toast.LENGTH_SHORT).show();
                return null;
            }
            while (res_sawah.moveToNext()) {
                sawah.add(res_sawah.getString(3) + " (" + res_sawah.getString(4) + ")");
                spinnerSawah.put(i, res_sawah.getString(0));
                i++;
            }
        }


        final Spinner pilih_lahan_sawah = (Spinner) view.findViewById(R.id.spinner_pilih_sawah);
        ArrayAdapter arrayAdapter_pilih_lahan_sawah = new ArrayAdapter(this.getActivity(), R.layout.spinner_text, sawah);
        arrayAdapter_pilih_lahan_sawah.setDropDownViewResource(R.layout.spinner_dropdown);
        pilih_lahan_sawah.setAdapter(arrayAdapter_pilih_lahan_sawah);

        final Spinner spinner_tahun = (Spinner) view.findViewById(R.id.spinner_tampil_pilih_tahun);

        pilih_lahan_sawah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String id_sawah = spinnerSawah.get(pilih_lahan_sawah.getSelectedItemPosition());

                final Spinner spinner_bulan = view.findViewById(R.id.spinner_pilih_bulan);
                final List<String> list_tahun = new ArrayList<String>();
                List<String> list_bulan = new ArrayList<String>();


                Cursor res_tahun = db.getDataPeriodeTerbuka(id_sawah);
                int j = 0;
                if (res_tahun.getCount() == 0) {
                    Toast.makeText(getActivity(), "Periode belum ditambahkan", Toast.LENGTH_SHORT).show();
                }
                while (res_tahun.moveToNext()) {
                    list_tahun.add(res_tahun.getString(2) + " - " + res_tahun.getString(4));
                    spinnerTahun.put(j, res_tahun.getString(0));
                    j++;
                }


                ArrayAdapter arrayAdapter_pilih_tahun = new ArrayAdapter(getActivity(), R.layout.spinner_text, list_tahun);
                arrayAdapter_pilih_tahun.setDropDownViewResource(R.layout.spinner_dropdown);
                Log.d("list", list_tahun.toString());
                spinner_tahun.setAdapter(arrayAdapter_pilih_tahun);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button btn_tampil_periode = (Button) view.findViewById(R.id.btn_tampil_periode);
        btn_tampil_periode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sawah_terpilih = spinnerSawah.get(pilih_lahan_sawah.getSelectedItemPosition());
                String tahun_terpilih = spinnerTahun.get(spinner_tahun.getSelectedItemPosition());
                try {
                    if (!tahun_terpilih.equals("")) {
                        Intent intent = new Intent(getActivity(), MenuPenerimaanDana.class);
                        intent.putExtra("sawah", sawah_terpilih);
                        intent.putExtra("periode", tahun_terpilih);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Periksa kembali pilihan Anda", Toast.LENGTH_SHORT).show();
                    Log.d("Error_button", "message error" + e);
                }
            }
        });

        return view;
    }
}
