package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    UserSessionManager session;
    public static String URL_CHECK_USER = "https://usahatani.000webhostapp.com/usahatani/user.php";
    public static String URL_SAWAH = "https://usahatani.000webhostapp.com/usahatani/restore_sawah.php";
    public static String URL_PERIODE = "https://usahatani.000webhostapp.com/usahatani/restore_periode.php";
    public static String URL_KEBUTUHAN = "https://usahatani.000webhostapp.com/usahatani/restore_kebutuhan.php";
    public static String URL_HASIL = "https://usahatani.000webhostapp.com/usahatani/restore_hasil.php";
    public static String URL_PENGELUARAN = "https://usahatani.000webhostapp.com/usahatani/restore_pengeluaran.php";
    public static String URL_PENERIMAAN = "https://usahatani.000webhostapp.com/usahatani/restore_penerimaan.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText et_nama_pengguna = findViewById(R.id.nama_pengguna);
        final EditText et_kata_sandi = findViewById(R.id.kata_sandi);
        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());

        if (session.isUserLoggedIn()) {
            Intent d = new Intent(getApplicationContext(), MenuUtama.class);
            d.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(d);
            finish();
        }

        Button btn = (Button) findViewById(R.id.btn_masuk);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nama_pengguna = et_nama_pengguna.getText().toString();
                final String kata_sandi = et_kata_sandi.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(login.this);
                progressDialog.setMessage("Memproses Data");
                progressDialog.show();

                if (nama_pengguna.equals("") || kata_sandi.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nama pengguna dan kata sandi harus diisi", Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                } else {
                    Boolean cek_pengguna = db.cek_pengguna(nama_pengguna, kata_sandi);
                    if (cek_pengguna) {
                        session.createUserLoginSession(nama_pengguna, "tes");
                        Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        // Starting MainActivity
                        Intent i = new Intent(getApplicationContext(), MenuUtama.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        finish();
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(login.this);

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHECK_USER,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                        //menaruh data JSON kkedalam variabel JSON Object
                                            JSONObject jsonPost = new JSONObject(response);

                        //memasukkan data ke dalam variable
                                            String id_pengguna = jsonPost.getString("id_pengguna");
                                            String nama_usahatani = jsonPost.getString("nama_usahatani");
                                            String nama_pemilik = jsonPost.getString("nama_pemilik");
                                            String nomor_telepon = jsonPost.getString("nomor_telepon");
                                            String deskripsi_usahatani = jsonPost.getString("deskripsi_usahatani");
                                            String nama_pengguna = jsonPost.getString("nama_pengguna");
                                            String kata_sandi = jsonPost.getString("kata_sandi");

                                            Boolean insert = db.insert_restore(id_pengguna,nama_usahatani,nama_pemilik,nomor_telepon,deskripsi_usahatani,nama_pengguna,kata_sandi);
                                            if(insert){
                                                Boolean cek_pengguna = db.cek_pengguna(nama_pengguna, kata_sandi);
                                                if (cek_pengguna) {

                                                    restoreData(URL_SAWAH,nama_pengguna);
                                                    restoreData(URL_PERIODE,nama_pengguna);
                                                    restoreData(URL_KEBUTUHAN,nama_pengguna);
                                                    restoreData(URL_HASIL,nama_pengguna);
                                                    restoreData(URL_PENGELUARAN,nama_pengguna);
                                                    restoreData(URL_PENERIMAAN,nama_pengguna);

                                                    session.createUserLoginSession(nama_pengguna, "tes");
                                                    Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    // Starting MainActivity
                                                    Intent i = new Intent(getApplicationContext(), MenuUtama.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                    // Add new Flag to start new Activity
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(i);

                                                    finish();
                                                }
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "Data yang dimasukkan tidak sesuai", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }

                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "Nama pengguna dan kata sandi tidak cocok", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Hidupkan koneksi internet untuk memeriksa online database", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();

                                params.put("nama_pengguna", nama_pengguna);
                                params.put("kata_sandi", kata_sandi);
                                return params;
                            }
                        };
                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    }
                    }
                }
        });

        TextView buat_akun = (TextView) findViewById(R.id.buat_akun);
        buat_akun.setPaintFlags(buat_akun.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            buat_akun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(login.this, register_data_pengguna.class);
                    startActivity(i);
                }
            });
        }
        else{
            buat_akun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(login.this,"Untuk membuat akun baru Anda harus terhubung dengan koneksi internet.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void restoreData(final String url, final String id){
        final DatabaseHelper db;
        db = new DatabaseHelper(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);


                        if(url == URL_SAWAH){
                            try{
                            //if url sawah
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_pengguna = obj.getString("id_pengguna");
                                String id_lahan_sawah = obj.getString("id_lahan_sawah");
                                String luas = obj.getString("luas");
                                String alamat = obj.getString("alamat");
                                String kategori = obj.getString("kategori");
                                String satuan = obj.getString("satuan");

                                db.insert_sawah_restore(id_lahan_sawah,id_pengguna,luas,alamat,kategori,satuan,1);
                            }
                            Toast.makeText(getApplicationContext(), "Data sawah berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                         catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(url == URL_PERIODE){
                        try{
                            //if url periode
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_periode = obj.getString("id_periode");
                                String id_lahan_sawah = obj.getString("id_lahan_sawah");
                                String bulan_periode_awal = obj.getString("bulan_periode_awal");
                                String bulan_periode_akhir = obj.getString("bulan_periode_akhir");
                                String tahun_periode_awal = obj.getString("tahun_periode_awal");
                                String tahun_periode_akhir = obj.getString("tahun_periode_akhir");

                                db.insert_periode_restore(id_periode,id_lahan_sawah,bulan_periode_awal,tahun_periode_awal,bulan_periode_akhir,tahun_periode_akhir,1);
                            }
                            Toast.makeText(getApplicationContext(), "Data periode berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(url == URL_KEBUTUHAN){
                        try{
                            //if url kebutuhan
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_kebutuhan_tanam = obj.getString("id_kebutuhan_tanam");
                                String nama_kebutuhan_tanam = obj.getString("nama_kebutuhan_tanam");
                                String kategori = obj.getString("kategori");

                                db.insert_kebutuhan_tanam_restore(id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori,"1");
                            }
                            Toast.makeText(getApplicationContext(), "Data kebutuhan tanam berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(url == URL_HASIL){
                        try{
                            //if url hasil
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_hasil_panen = obj.getString("id_hasil_panen");
                                String nama_hasil_panen = obj.getString("nama_hasil_panen");

                                db.insert_hasil_panen_restore(id_hasil_panen,nama_hasil_panen,1);
                            }
                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(url == URL_PENGELUARAN){
                        try{
                            //if url pengeluaran
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_pengeluaran_biaya = obj.getString("id_pengeluaran_biaya");
                                String id_lahan_sawah = obj.getString("id_lahan_sawah");
                                String id_kebutuhan_tanam = obj.getString("id_kebutuhan_tanam");
                                String jumlah = obj.getString("jumlah");
                                String total_harga = obj.getString("total_harga");
                                String nama_pemasok = obj.getString("nama_pemasok");
                                String tanggal_pengeluaran_biaya = obj.getString("tanggal_pengeluaran_biaya");
                                String catatan = obj.getString("catatan");
                                String id_periode = obj.getString("id_periode");
                                String satuan = obj.getString("satuan");


                                if(Pattern.matches("tanam_*",id_kebutuhan_tanam)){
                                    String[] split_id_kebutuhan = id_kebutuhan_tanam.split("_");
                                    id_kebutuhan_tanam = split_id_kebutuhan[2];
                                }

                                db.insert_pengeluaran_biaya_restore(id_pengeluaran_biaya,id_lahan_sawah,id_kebutuhan_tanam, jumlah, total_harga, nama_pemasok, tanggal_pengeluaran_biaya, catatan,id_periode,satuan,"1");
                            }
                            Toast.makeText(getApplicationContext(), "Data pengeluaran berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(url == URL_PENERIMAAN){
                        try{
                            //if url penerimaan
                            //looping through all the elements in json array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting json object from the json array
                                JSONObject obj = jsonArray.getJSONObject(i);

                                //getting the name from the json object and putting it inside string array
                                String id_penerimaan_dana = obj.getString("id_penerimaan_dana");
                                String id_lahan_sawah = obj.getString("id_lahan_sawah");
                                String id_hasil_panen = obj.getString("id_hasil_panen");
                                String jumlah = obj.getString("jumlah");
                                String total_harga = obj.getString("total_harga");
                                String nama_pelanggan = obj.getString("nama_pelanggan");
                                String tanggal_penerimaan_dana = obj.getString("tanggal_penerimaan_dana");
                                String catatan = obj.getString("catatan");
                                String id_periode = obj.getString("id_periode");
                                String satuan = obj.getString("satuan");
                                String luas_panen = obj.getString("luas_panen");
                                String satuan_luas_panen = obj.getString("satuan_luas_panen");

                                db.insert_penerimaan_dana_restore(id_penerimaan_dana,id_lahan_sawah,id_hasil_panen, jumlah, total_harga, nama_pelanggan, tanggal_penerimaan_dana, catatan,id_periode,satuan,"1", luas_panen,satuan_luas_panen);
                            }
                            Toast.makeText(getApplicationContext(), "Data penerimaan berhasil direstore", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Peringatan jika ingin restore data harus online
                Toast.makeText(getApplicationContext(), "Untuk restore data harus online!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getInstance(login.this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah Anda ingin keluar dari aplikasi ini?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }
}
