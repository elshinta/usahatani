package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register_data_pengguna extends AppCompatActivity {
    UserSessionManager session;
    public static String URL_SAVE_NAME = "https://usahatani.000webhostapp.com/usahatani/register_data_pengguna.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data_pengguna);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_data_pengguna);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buat Akun");

        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());


        final EditText et_nama_usahatani = findViewById(R.id.nama_kelompok_tani);
        final EditText et_nama_pemilik = findViewById(R.id.nama_pemilik);
        final EditText et_nomor_telepon = findViewById(R.id.nomor_telepon);
        final EditText et_deskripsi_usahatani =findViewById(R.id.deskripsi_usahatani);
        final EditText et_nama_pengguna = findViewById(R.id.nama_pengguna);
        final EditText et_kata_sandi = findViewById(R.id.kata_sandi);
        final EditText et_kata_sandi_ulang = findViewById(R.id.kata_sandi_ulang);

        Button btn = (Button)findViewById(R.id.btn_data_pengguna);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama_usahatani = et_nama_usahatani.getText().toString();
                String nama_pemilik = et_nama_pemilik.getText().toString();
                String nomor_telepon = et_nomor_telepon.getText().toString();
                String deskripsi_usahatani = et_deskripsi_usahatani.getText().toString();
                String nama_pengguna = et_nama_pengguna.getText().toString();
                String kata_sandi = et_kata_sandi.getText().toString();
                String kata_sandi_ulang = et_kata_sandi_ulang.getText().toString();

                if(nama_usahatani.equals("")){
                    Toast.makeText(getApplicationContext(), "Nama usahatani harus diisi", Toast.LENGTH_SHORT).show();
                } else if(nama_pemilik.equals("")){
                    Toast.makeText(getApplicationContext(), "Nama pemilik harus diisi", Toast.LENGTH_SHORT).show();
                } else if(nomor_telepon.equals("")){
                    Toast.makeText(getApplicationContext(), "Nomor telepon harus diisi", Toast.LENGTH_SHORT).show();
                } else if(nama_pengguna.equals("")){
                    Toast.makeText(getApplicationContext(), "Nama pengguna harus diisi", Toast.LENGTH_SHORT).show();
                } else if(kata_sandi.equals("")){
                    Toast.makeText(getApplicationContext(), "Kata sandi harus diisi", Toast.LENGTH_SHORT).show();
                } else{
                    if(kata_sandi_ulang.equals(kata_sandi)){

                        final ProgressDialog progressDialog = new ProgressDialog(register_data_pengguna.this);
                        progressDialog.setMessage("Memeriksa Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String nama_usahatani = et_nama_usahatani.getText().toString();
                                String nama_pemilik = et_nama_pemilik.getText().toString();
                                String nomor_telepon = et_nomor_telepon.getText().toString();
                                String deskripsi_usahatani = et_deskripsi_usahatani.getText().toString();
                                String nama_pengguna = et_nama_pengguna.getText().toString();
                                String kata_sandi = et_kata_sandi.getText().toString();

                                progressDialog.dismiss();

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean insert = db.insert(nama_usahatani,nama_pemilik,nomor_telepon,deskripsi_usahatani,nama_pengguna,kata_sandi);
                                        if(insert){
                                            session.createUserRegisterSession(nama_pengguna, "tes");
                                            Toast.makeText(getApplicationContext(), "Register berhasil!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data pengguna berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(register_data_pengguna.this, register_luas_lahan_sawah.class));
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Data gagal dimasukkan, periksa kembali.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        if(!obj.getBoolean("nama_pengguna")){
                                            Toast.makeText(getApplicationContext(), "Nama pengguna sudah ada", Toast.LENGTH_SHORT).show();
                                        }
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String nama_usahatani = et_nama_usahatani.getText().toString();
                                String nama_pemilik = et_nama_pemilik.getText().toString();
                                String nomor_telepon = et_nomor_telepon.getText().toString();
                                String deskripsi_usahatani = et_deskripsi_usahatani.getText().toString();
                                String nama_pengguna = et_nama_pengguna.getText().toString();
                                String kata_sandi = et_kata_sandi.getText().toString();

                                progressDialog.dismiss();
                                Toast.makeText(register_data_pengguna.this,"Untuk menambah data pengguna harus terkoneksi dengan internet.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                String nama_usahatani = et_nama_usahatani.getText().toString();
                                String nama_pemilik = et_nama_pemilik.getText().toString();
                                String nomor_telepon = et_nomor_telepon.getText().toString();
                                String deskripsi_usahatani = et_deskripsi_usahatani.getText().toString();
                                String nama_pengguna = et_nama_pengguna.getText().toString();
                                String kata_sandi = et_kata_sandi.getText().toString();

                                Map<String, String> params = new HashMap<>();
                                params.put("nama_usahatani",nama_usahatani);
                                params.put("nama_pemilik",nama_pemilik);
                                params.put("nomor_telepon",nomor_telepon);
                                params.put("deskripsi_usahatani",deskripsi_usahatani);
                                params.put("nama_pengguna",nama_pengguna);
                                params.put("kata_sandi",kata_sandi);

                                return params;
                            }
                        };

                        VolleySingleton.getInstance(register_data_pengguna.this).addToRequestQueue(stringRequest);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Kata sandi tidak cocok.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah Anda ingin menyelesaikan buat akun?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
//                        startActivity(new Intent(register_data_pengguna.this, login.class));
                    }
                }).create().show();
    }
}
