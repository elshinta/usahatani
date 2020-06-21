package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahPeriodePenerimaanFragment extends Fragment {
    UserSessionManager session;
    public DatabaseHelper db;
    public static String URL_SAVE_NAME = "https://usahatani.000webhostapp.com/usahatani/tambah_periode.php";
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah_periode, container, false);

        final List<String> sawah = new ArrayList<String>();
        final HashMap<Integer, String> spinnerSawah = new HashMap<Integer, String>();
        final Spinner spinner_tahun = view.findViewById(R.id.spinner_pilih_tahun);
        final Spinner spinner_bulan = view.findViewById(R.id.spinner_pilih_bulan);

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

        final int tahun = Calendar.getInstance().get(Calendar.YEAR);
        final String array_tahun[] = new String[5];
        for(int k=0;k<5;k++) {
            array_tahun[k] = Integer.toString(tahun+k);
        }

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this.getActivity(),R.layout.spinner_text,array_tahun);
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_tahun.setAdapter(arrayAdapter2);

        ArrayAdapter arrayAdapter_bulan = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.bulan, R.layout.spinner_text);
        arrayAdapter_bulan.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_bulan.setAdapter(arrayAdapter_bulan);

        Button btn_tambah_periode = (Button)view.findViewById(R.id.btn_tambah_periode);
        btn_tambah_periode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id_sawah = spinnerSawah.get(pilih_lahan_sawah.getSelectedItemPosition());
                final String pilih_tahun = spinner_tahun.getSelectedItem().toString();
                final String pilih_bulan = spinner_bulan.getSelectedItem().toString();

                session = new UserSessionManager(getActivity());
                HashMap<String, String> user = session.getUserDetails();
                final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Menambah Data Periode");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int id_periode;

                        Cursor res_periode = db.getIDPeriode();
                        if (res_periode.getCount() == 0) {
                            id_periode = 1;
                        } else  {
                            id_periode = res_periode.getCount()+1;
                        }

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                progressDialog.dismiss();

                                //if there is a success
                                //storing the name to sqlite with status synced
                                Boolean insert_periode = db.insert_periode("periode_" + nama_pengguna + "_" + String.valueOf(id_periode), id_sawah,pilih_bulan,pilih_tahun, "","",1);
                                if(insert_periode){
                                    Toast.makeText(getActivity(),"Periode berhasil dimasukkan",Toast.LENGTH_SHORT).show();
                                    FragmentTransaction fr  = getFragmentManager().beginTransaction();
                                    fr.replace(R.id.fragment_periode, new TampilPeriodePenerimaanFragment());
                                    fr.commit();
                                    PeriodeTransaksiPenerimaan p1 = (PeriodeTransaksiPenerimaan) getActivity();
                                    p1.setRadio(true);
                                }
                                else{
                                    Toast.makeText(getActivity(),"Gagal memasukkan periode. \nPeriode yang dimasukkan tidak boleh sama", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Periksa kembali data Anda", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int id_periode;

                        Cursor res_periode = db.getIDPeriode();
                        if (res_periode.getCount() == 0) {
                            id_periode = 1;
                        } else  {
                            id_periode = res_periode.getCount()+1;
                        }

                        progressDialog.dismiss();

                        Boolean insert_periode = db.insert_periode("periode_" + nama_pengguna + "_" + String.valueOf(id_periode), id_sawah,pilih_bulan,pilih_tahun, "","",0);
                        if(insert_periode){
                            Toast.makeText(getActivity(),"Periode berhasil dimasukkan",Toast.LENGTH_SHORT).show();
                            FragmentTransaction fr  = getFragmentManager().beginTransaction();
                            fr.replace(R.id.fragment_periode, new TampilPeriodePenerimaanFragment());
                            fr.commit();
                            PeriodeTransaksiPenerimaan p1 = (PeriodeTransaksiPenerimaan) getActivity();
                            p1.setRadio(true);
                        }
                        else{
                            Toast.makeText(getActivity(),"Gagal memasukkan periode. \nPeriode yang dimasukkan tidak boleh sama", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        int id_periode;

                        Cursor res_periode = db.getIDPeriode();
                        if (res_periode.getCount() == 0) {
                            id_periode = 1;
                        } else  {
                            id_periode = res_periode.getCount()+1;
                        }

                        Map<String, String> params = new HashMap<>();
                        params.put("id_periode","periode_" + nama_pengguna + "_" + String.valueOf(id_periode));
                        params.put("id_lahan_sawah", id_sawah);
                        params.put("bulan_periode_awal", pilih_bulan);
                        params.put("bulan_periode_akhir", "");
                        params.put("tahun_periode_awal", pilih_tahun);
                        params.put("tahun_periode_akhir", "");

                        return params;
                    }
                };
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });
        return view;
    }
}
