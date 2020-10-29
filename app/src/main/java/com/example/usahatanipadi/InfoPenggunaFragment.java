package com.example.usahatanipadi;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class InfoPenggunaFragment extends Fragment {
    UserSessionManager session;
    public DatabaseHelper db;
    private String pw = null;
    String id_pengguna;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tentang_pengguna, container, false);

        db = new DatabaseHelper(getActivity());

        final EditText et_nama_usahatani = (EditText) view.findViewById(R.id.edit_nama_kelompok_tani);
        final EditText et_nama_pemilik = (EditText) view.findViewById(R.id.edit_nama_pemilik);
        final EditText et_nomor_telepon = (EditText) view.findViewById(R.id.edit_nomor_telepon);
        final EditText et_deskripsi_usahatani = (EditText) view.findViewById(R.id.edit_deskripsi_usahatani);
        final EditText et_nama_pengguna = (EditText) view.findViewById(R.id.edit_nama_pengguna);
        final EditText et_kata_sandi_lama = (EditText) view.findViewById(R.id.edit_kata_sandi_lama);
        final EditText et_kata_sandi_baru = (EditText) view.findViewById(R.id.edit_kata_sandi_baru);
        final EditText et_kata_sandi_ulang = (EditText) view.findViewById(R.id.edit_kata_sandi_ulang);

        final TextView tv_kata_sandi_lama = (TextView) view.findViewById(R.id.sandi_lama);
        final TextView tv_kata_sandi_baru = (TextView) view.findViewById(R.id.sandi_baru);
        final TextView tv_kata_sandi_ulang = (TextView) view.findViewById(R.id.sandi_ulang);
        Button btn_edit_info_pengguna = (Button) view.findViewById(R.id.btn_edit_info_pengguna);

        et_kata_sandi_lama.setVisibility(View.GONE);
        et_kata_sandi_baru.setVisibility(View.GONE);
        et_kata_sandi_ulang.setVisibility(View.GONE);
        tv_kata_sandi_lama.setVisibility(View.GONE);
        tv_kata_sandi_baru.setVisibility(View.GONE);
        tv_kata_sandi_ulang.setVisibility(View.GONE);

        TextView ubah_sandi = (TextView) view.findViewById(R.id.ubah_sandi);
        ubah_sandi.setPaintFlags(ubah_sandi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ubah_sandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_kata_sandi_lama.setVisibility(View.VISIBLE);
                et_kata_sandi_baru.setVisibility(View.VISIBLE);
                et_kata_sandi_ulang.setVisibility(View.VISIBLE);
                tv_kata_sandi_lama.setVisibility(View.VISIBLE);
                tv_kata_sandi_baru.setVisibility(View.VISIBLE);
                tv_kata_sandi_ulang.setVisibility(View.VISIBLE);
            }
        });

        session = new UserSessionManager(this.getActivity());
        HashMap<String, String> user = session.getUserDetails();
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        et_nama_pengguna.setInputType(InputType.TYPE_NULL);
        et_nama_pengguna.setEnabled(false);
        et_nama_pengguna.setTextColor(Color.GRAY);
//        et_nama_pengguna.setBackgroundColor(Color.GRAY);

        Cursor res = db.getData(nama);

        try {
            if (res.getCount() == 0) {
                Toast.makeText(this.getActivity(), "Erorr!", Toast.LENGTH_SHORT).show();
            }
            while (res.moveToNext()) {
                et_nama_usahatani.setText(res.getString(1));
                et_nama_pemilik.setText(res.getString(2));
                et_nomor_telepon.setText(res.getString(3));
                et_deskripsi_usahatani.setText(res.getString(4));
                et_nama_pengguna.setText(res.getString(5));
                id_pengguna = res.getString(0);
                pw = res.getString(6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_edit_info_pengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_kata_sandi_lama.getText().toString().equals(pw)) {
                    if (et_kata_sandi_baru.getText().toString().equals(et_kata_sandi_ulang.getText().toString())) {

                        Boolean sukses = db.updateDataPengguna(id_pengguna, et_nama_pemilik.getText().toString(), et_nomor_telepon.getText().toString(), et_deskripsi_usahatani.getText().toString(), et_nama_pengguna.getText().toString(), et_kata_sandi_baru.getText().toString());
                        if (sukses) {
                            Toast.makeText(getActivity(), "Berhasil mengubah data!", Toast.LENGTH_SHORT).show();
                            FragmentTransaction fr = getFragmentManager().beginTransaction();
                            fr.replace(R.id.fragment_container, new MenuUtamaFragment());
                            fr.commit();
                        } else {
                            Toast.makeText(getActivity(), "Data pengguna yang Anda masukkan gagal disimpan!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Konfirmasi kata sandi baru tidak sama!", Toast.LENGTH_SHORT).show();
                    }
                } else if (et_kata_sandi_lama.getText().toString().equals("")) {
                    Boolean sukses = db.updateDataPengguna(id_pengguna, et_nama_pemilik.getText().toString(), et_nomor_telepon.getText().toString(), et_deskripsi_usahatani.getText().toString(), et_nama_pengguna.getText().toString(), pw);
                    if (sukses) {
                        Toast.makeText(getActivity(), "Berhasil mengubah data!", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fr = getFragmentManager().beginTransaction();
                        fr.replace(R.id.fragment_container, new MenuUtamaFragment());
                        fr.commit();
                    } else {
                        Toast.makeText(getActivity(), "Data password yang Anda masukkan gagal disimpan!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Kata sandi tidak cocok!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

}
