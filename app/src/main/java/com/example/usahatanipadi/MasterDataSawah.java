package com.example.usahatanipadi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterDataSawah extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterSawahAdapter customAdapter;
    ArrayList<String> listalamat = new ArrayList<String>();
    ArrayList<String> listluas = new ArrayList<String>();
    ArrayList<String> listkategori = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    ArrayList<String> list_satuan = new ArrayList<String>();
    DatabaseHelper db;
    String id_pengguna;
    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/register_luas_lahan_sawah.php";
    public static String URL_EDIT_NAME = "https://ilkomunila.com/usahatani/edit_master_data_sawah.php";
    public static String URL_DELETE_NAME = "https://ilkomunila.com/usahatani/hapus_master_data_sawah.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_sawah);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_data_sawah);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Sawah");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DatabaseHelper(this);
        session = new UserSessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res = db.getData(nama);
        if (res.getCount() == 0) {
            Toast.makeText(this, "Erorr!", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            this.id_pengguna = res.getString(0);
        }

        viewData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_data_sawah);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(MasterDataSawah.this);
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    public void viewData(){
        listalamat.clear();
        listluas.clear();
        listkategori.clear();
        list_id.clear();
        list_satuan.clear();
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this);
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        String id_pengguna;
        final List<String> sawah = new ArrayList<String>();
        final HashMap<Integer, String> ListViewSawah = new HashMap<Integer, String>();

        Cursor res = db.getData(nama);

        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while (res.moveToNext()) {
            int i = 0;
            int j = 0;
            id_pengguna = res.getString(0);

            Cursor res_sawah = db.getDataSawah(id_pengguna);
            if (res_sawah.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
                return;
            }
            while (res_sawah.moveToNext()) {
                sawah.add(res_sawah.getString(3) + " (" + res_sawah.getString(4) + ")");
                ListViewSawah.put(i, res_sawah.getString(0));
                i++;
                listalamat.add(res_sawah.getString(3));
                listluas.add(res_sawah.getString(2));
                listkategori.add(res_sawah.getString(4));
                list_id.add(res_sawah.getString(0));
                list_satuan.add(res_sawah.getString(5));
            }
        }
        simpleList = (ListView) findViewById(R.id.lv_data_sawah);

        customAdapter = new ListViewMasterSawahAdapter(getApplicationContext(), listalamat, listluas, listkategori,list_id,list_satuan);

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editDialog(MasterDataSawah.this, position);
            }
        });
        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                deleteDialog(MasterDataSawah.this,index);
                return true;
            }
        });
    }

    private void showAddItemDialog(Context c) {

        db = new DatabaseHelper(this);
        final Dialog dataSawah = new Dialog(this);
        dataSawah.setContentView(R.layout.activity_register_luas_lahan_sawah);

        Toolbar toolbar = (Toolbar) dataSawah.findViewById(R.id.toolbar_luas_lahan_sawah);
        toolbar.setVisibility(View.GONE);

        final TextView judulLahan = (TextView)dataSawah.findViewById(R.id.judul_lahan);
        final Spinner spinnerKategori = (Spinner)dataSawah.findViewById(R.id.pilih_kepemilikan);
        final EditText dataAlamat = (EditText)dataSawah.findViewById(R.id.alamat_lahan);
        final EditText dataLuas = (EditText)dataSawah.findViewById(R.id.luas_lahan);
        final Spinner spinnerSatuan = (Spinner)dataSawah.findViewById(R.id.satuan_lahan);

        judulLahan.setText("Tambah Data Sawah");

        Button btnSimpan = (Button)dataSawah.findViewById(R.id.btn_luas_lahan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataLuas.getText().toString().equals("") || !dataAlamat.getText().toString().equals("")) {

                    final ProgressDialog progressDialog = new ProgressDialog(MasterDataSawah.this);
                    progressDialog.setMessage("Menambah Data");
                    progressDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    progressDialog.dismiss();

                                    session = new UserSessionManager(getApplicationContext());
                                    HashMap<String, String> user = session.getUserDetails();
                                    String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);
                                    int id_sawah;

                                    Cursor res_sawah = db.getIDSawah();
                                    if (res_sawah.getCount() == 0) {
                                        id_sawah = 1;
                                    } else  {
                                        id_sawah = res_sawah.getCount()+1;
                                    }

                                    //if there is a success
                                    //storing the name to sqlite with status synced
                                    Boolean insert_sawah = db.insert_sawah("sawah_" +nama_pengguna + "_" + String.valueOf(id_sawah),id_pengguna, dataLuas.getText().toString(), dataAlamat.getText().toString(), spinnerKategori.getSelectedItem().toString(),spinnerSatuan.getSelectedItem().toString(),1);

                                    if (insert_sawah) {
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil diisi", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil disimpan ke server", Toast.LENGTH_SHORT).show();
                                        dataSawah.dismiss();
                                        viewData();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                } else{
                                    Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.dismiss();

                            dataSawah.dismiss();

                            session = new UserSessionManager(getApplicationContext());
                            HashMap<String, String> user = session.getUserDetails();
                            String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);
                            int id_sawah;

                            Cursor res_sawah = db.getIDSawah();
                            if (res_sawah.getCount() == 0) {
                                id_sawah = 1;
                            } else  {
                                id_sawah = res_sawah.getCount()+1;
                            }

                            Boolean insert_sawah = db.insert_sawah("sawah_" +nama_pengguna + "_" + String.valueOf(id_sawah),id_pengguna, dataLuas.getText().toString(), dataAlamat.getText().toString(), spinnerKategori.getSelectedItem().toString(),spinnerSatuan.getSelectedItem().toString(),0);

                            if (insert_sawah) {
                                Toast.makeText(getApplicationContext(), "Data sawah berhasil diisi", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Data sawah tidak disimpan ke server", Toast.LENGTH_SHORT).show();
                                dataSawah.dismiss();
                                viewData();
                            } else {
                                Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                            }

                            //Peringatan jika ingin menambah data harus online
                           // Toast.makeText(getApplicationContext(),"Untuk menambah data sawah harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            session = new UserSessionManager(getApplicationContext());
                            HashMap<String, String> user = session.getUserDetails();
                            String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                            String nama_pengguna_unik;
                            int id_sawah;

                            Cursor res_sawah = db.getIDSawah();
                            if (res_sawah.getCount() == 0) {
                                id_sawah = 1;
                            } else  {
                                id_sawah = res_sawah.getCount()+1;
                            }

                            Map<String, String> params = new HashMap<>();
                            params.put("id","sawah_" + nama_pengguna + "_" + String.valueOf(id_sawah));
                            params.put("nama_pengguna",nama_pengguna);
                            params.put("luas",dataLuas.getText().toString());
                            params.put("alamat",dataAlamat.getText().toString());
                            params.put("kategori",spinnerKategori.getSelectedItem().toString());
                            params.put("satuan",spinnerSatuan.getSelectedItem().toString());

                            return params;
                        }
                    };
                    VolleySingleton.getInstance(MasterDataSawah.this).addToRequestQueue(stringRequest);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Luas lahan dan alamat harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dataSawah.setTitle("Tambah Data Sawah");
        dataSawah.show();
    }

    public void editDialog(Context c,int position){

        final String id_data_sawah = customAdapter.getItemIds(position).toString();

        final Dialog dataSawah = new Dialog(this);
        dataSawah.setContentView(R.layout.activity_register_luas_lahan_sawah);
        dataSawah.setTitle("Ubah Data Sawah");

        Toolbar toolbar = (Toolbar) dataSawah.findViewById(R.id.toolbar_luas_lahan_sawah);
        toolbar.setVisibility(View.GONE);

        final TextView judulLahan = (TextView)dataSawah.findViewById(R.id.judul_lahan);
        final Spinner spinnerKategori = (Spinner)dataSawah.findViewById(R.id.pilih_kepemilikan);
        final EditText dataAlamat = (EditText)dataSawah.findViewById(R.id.alamat_lahan);
        final EditText dataLuas = (EditText)dataSawah.findViewById(R.id.luas_lahan);
        final Spinner spinnerSatuan = (Spinner)dataSawah.findViewById(R.id.satuan_lahan);

        judulLahan.setText("Ubah Data Sawah");

        if(customAdapter.getItemKategori(position).equals("Milik Sendiri"))
        {
            spinnerKategori.setSelection(0);
        }
        else
        {
            spinnerKategori.setSelection(1);
        }

        dataAlamat.setText(customAdapter.getItemAlamat(position).toString());
        dataLuas.setText(customAdapter.getItemLuas(position).toString());

        if(customAdapter.getItemSatuan(position).equals("Ha")) {
            spinnerSatuan.setSelection(0);
        }
        else if (customAdapter.getItemSatuan(position).equals("Rante"))
        {
            spinnerSatuan.setSelection(1);
        }
        else{
            spinnerSatuan.setSelection(2);
        }

        Button btnSimpan = (Button)dataSawah.findViewById(R.id.btn_luas_lahan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MasterDataSawah.this);
                progressDialog.setMessage("Mengubah Data");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_NAME, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                progressDialog.dismiss();

                                //if there is a success
                                //storing the name to sqlite with status synced
                                Boolean update_sawah = db.updateDataSawah(id_data_sawah,Long.parseLong(id_pengguna),dataAlamat.getText().toString(),dataLuas.getText().toString(),spinnerKategori.getSelectedItem().toString(),spinnerSatuan.getSelectedItem().toString());
                                if (update_sawah) {
                                    Toast.makeText(getApplicationContext(), "Data sawah berhasil diubah", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data sawah berhasil diubah ke server", Toast.LENGTH_SHORT).show();
                                    //viewData();
                                    //dataSawah.dismiss();
                                    Intent intent = new Intent(MasterDataSawah.this, MapsActivity.class);
                                    intent.putExtra("edit_latLng", id_data_sawah);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal mengubah data sawah", Toast.LENGTH_SHORT).show();
                                    dataSawah.dismiss();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        dataSawah.dismiss();

                        //Peringatan jika ingin menambah data harus online
                        Toast.makeText(getApplicationContext(), "Untuk mengubah data sawah harus terhubung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("id_lahan_sawah",id_data_sawah);
                        params.put("luas",dataLuas.getText().toString());
                        params.put("alamat",dataAlamat.getText().toString());
                        params.put("kategori",spinnerKategori.getSelectedItem().toString());
                        params.put("satuan",spinnerSatuan.getSelectedItem().toString());
                        return params;
                    }
                };
                VolleySingleton.getInstance(MasterDataSawah.this).addToRequestQueue(stringRequest);
            }
        });
        dataSawah.show();
    }

    public void deleteDialog(Context c,int position){
        db = new DatabaseHelper(this);
        final String id_data_sawah = customAdapter.getItemIds(position).toString();
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Hapus Data Sawah")
                .setMessage("yakin ingin menghapus data?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataSawah.this);
                        progressDialog.setMessage("Menghapus Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_NAME, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean delete_sawah = db.deleteDataSawah(id_data_sawah);
                                        if (delete_sawah) {
                                            Toast.makeText(getApplicationContext(), "Data sawah berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data sawah", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                progressDialog.dismiss();

                                //Peringatan jika ingin hapus data harus online
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data sawah harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                params.put("id_lahan_sawah", id_data_sawah);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataSawah.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }
}

