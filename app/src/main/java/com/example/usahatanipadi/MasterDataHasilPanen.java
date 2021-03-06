package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Map;

public class MasterDataHasilPanen extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterHasilPanenAdapter customAdapter;
    ArrayList<String> listdatahasilpanen = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    DatabaseHelper db;
    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/hasil_panen.php";
    public static String URL_EDIT_NAME = "https://ilkomunila.com/usahatani/edit_hasil_panen.php";
    public static String URL_DELETE_NAME = "https://ilkomunila.com/usahatani/hapus_hasil_panen.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_hasil_panen);

        Toolbar toolbar = findViewById(R.id.toolbar_data_hasil_panen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Hasil Panen");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewData();

        FloatingActionButton fab = findViewById(R.id.fab_data_hasil_panen);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(MasterDataHasilPanen.this);
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    public void viewData(){
        listdatahasilpanen.clear();
        list_id.clear();
        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res_data_hasil_panen = db.getDataHasilPanen(nama);
        if (res_data_hasil_panen.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while(res_data_hasil_panen.moveToNext()){
            listdatahasilpanen.add(res_data_hasil_panen.getString(1));
            list_id.add(res_data_hasil_panen.getString(0));
        }

        simpleList = (ListView) findViewById(R.id.lv_data_hasil_panen);

        customAdapter = new ListViewMasterHasilPanenAdapter(getApplicationContext(), listdatahasilpanen, list_id);

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editDialog (MasterDataHasilPanen.this, position);
            }
        });

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog (MasterDataHasilPanen.this, position);
                return true;
            }
        });
    }

    public void showAddItemDialog (Context c){
        final EditText dataHasilPanenBaru = new EditText(c);
        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Tambah Data Hasil Panen")
                .setMessage("Data hasil panen digunakan untuk mengetahui hasil panen")
                .setView(dataHasilPanenBaru)
                .setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hasil_panen = String.valueOf(dataHasilPanenBaru.getText());

                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataHasilPanen.this);
                        progressDialog.setMessage("Menambah Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String hasil_panen = String.valueOf(dataHasilPanenBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_hasil_panen;

                                Cursor res_hasil_panen = db.getLastIDHasilPanen();
                                if (res_hasil_panen.getCount() == 0) {
                                    id_hasil_panen = 1;
                                } else  {
                                    id_hasil_panen = res_hasil_panen.getCount()+1;
                                }

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean insert_hasil_panen = db.insert_hasil_panen("hasil_" +nama_pengguna + "_" + String.valueOf(id_hasil_panen),hasil_panen,1);
                                        if (insert_hasil_panen){
                                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal memasukkan data hasil panen", Toast.LENGTH_SHORT).show();
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
                                String hasil_panen = String.valueOf(dataHasilPanenBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_hasil_panen;

                                Cursor res_hasil_panen = db.getLastIDHasilPanen();
                                if (res_hasil_panen.getCount() == 0) {
                                    id_hasil_panen = 1;
                                } else  {
                                    id_hasil_panen = res_hasil_panen.getCount()+1;
                                }

                                progressDialog.dismiss();

                                Boolean insert_hasil_panen = db.insert_hasil_panen("hasil_" + nama_pengguna + "_" + String.valueOf(id_hasil_panen), hasil_panen,0);
                                if (insert_hasil_panen){
                                    Toast.makeText(getApplicationContext(), "Data hasil panen berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data hasil panen tidak dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                    viewData();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal memasukkan data hasil panen", Toast.LENGTH_SHORT).show();
                                }

                                //Peringatan jika ingin menambah data harus online
                                //Toast.makeText(getApplicationContext(),"Untuk menambah data hasil panen harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                String hasil_panen = String.valueOf(dataHasilPanenBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_hasil_panen;

                                Cursor res_hasil_panen = db.getLastIDHasilPanen();
                                if (res_hasil_panen.getCount() == 0) {
                                    id_hasil_panen = 1;
                                } else  {
                                    id_hasil_panen = res_hasil_panen.getCount()+1;
                                }

                                Map<String, String> params = new HashMap<>();
                                params.put("id_hasil_panen","hasil_" + nama_pengguna + "_" + String.valueOf(id_hasil_panen));
                                params.put("nama_hasil_panen",hasil_panen);

                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataHasilPanen.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void editDialog (Context c, int position){
        final EditText editDataHasilPanen = new EditText(c);
        final String id_data_hasil_panen = customAdapter.getItemIds(position).toString();
        editDataHasilPanen.setText((String)customAdapter.getItem(position));

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Ubah Data Hasil Panen")
                .setMessage("yakin ingin mengubah data?")
                .setView(editDataHasilPanen)
                .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String editHasilPanen = String.valueOf(editDataHasilPanen.getText());
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataHasilPanen.this);
                        progressDialog.setMessage("Mengubah Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_NAME, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (obj.getBoolean("error")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean update_hasil_panen = db.updateDataHasilPanen(id_data_hasil_panen, editHasilPanen);
                                        if (update_hasil_panen) {
                                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil diubah", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil diubah ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal mengubah data hasil panen", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                progressDialog.dismiss();

                                //Peringatan jika ingin menambah data harus online
                                Toast.makeText(getApplicationContext(), "Untuk mengubah data hasil panen harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();

                                params.put("id_hasil_panen", id_data_hasil_panen);
                                params.put("nama_hasil_panen", editHasilPanen);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataHasilPanen.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void deleteDialog (Context c, int position){
        final String id_data_hasil_panen = customAdapter.getItemIds(position).toString();

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Hapus Data Hasil Panen")
                .setMessage("yakin ingin menghapus data?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataHasilPanen.this);
                        progressDialog.setMessage("Menghapus Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_NAME, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (obj.getBoolean("error")) {

                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();

                                    } else {

                                        progressDialog.dismiss();


                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean delete_hasil_panen = db.deleteDataHasilPanen(id_data_hasil_panen);
                                        if (delete_hasil_panen) {
                                            Toast.makeText(getApplicationContext(), "Data hasil panen berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data hasil panen", Toast.LENGTH_SHORT).show();
                                        }
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
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data hasil panen harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                params.put("id_hasil_panen", id_data_hasil_panen);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataHasilPanen.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }
}

