package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MasterDataObatHama extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterObatHamaAdapter customAdapter;
    ArrayList<String> listdataobathama = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    DatabaseHelper db;
    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/master_data.php";
    public static String URL_EDIT_NAME = "https://ilkomunila.com/usahatani/edit_master_data.php";
    public static String URL_DELETE_NAME = "https://ilkomunila.com/usahatani/hapus_master_data.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_obat_hama);

        viewData();

        Toolbar toolbar = findViewById(R.id.toolbar_data_obat_hama);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Obat Hama");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_data_obat_hama);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(MasterDataObatHama.this);
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    public void viewData(){
        listdataobathama.clear();
        list_id.clear();
        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res_data_obat_hama = db.getDataObatHama(nama);
        if (res_data_obat_hama.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while(res_data_obat_hama.moveToNext()){
            listdataobathama.add(res_data_obat_hama.getString(1));
            list_id.add(res_data_obat_hama.getString(0));
        }

        simpleList = (ListView) findViewById(R.id.lv_data_obat_hama);

        customAdapter = new ListViewMasterObatHamaAdapter(getApplicationContext(), listdataobathama, list_id);

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editDialog (MasterDataObatHama.this, position);
            }
        });

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog (MasterDataObatHama.this, position);
                return true;
            }
        });
    }

    public void showAddItemDialog (Context c){
        final EditText dataObatHamaBaru = new EditText(c);
        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Tambah Data Obat Hama")
                .setMessage("obat hama digunakan untuk menyuburkan tanah")
                .setView(dataObatHamaBaru)
                .setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String obat_hama = String.valueOf(dataObatHamaBaru.getText());

                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataObatHama.this);
                        progressDialog.setMessage("Menambah Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String obat_hama = String.valueOf(dataObatHamaBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_kebutuhan_tanam;

                                Cursor res_kebutuhan_tanam = db.getIDTanam();
                                if (res_kebutuhan_tanam.getCount() == 0) {
                                    id_kebutuhan_tanam = 1;
                                } else  {
                                    id_kebutuhan_tanam = res_kebutuhan_tanam.getCount()+1;
                                }

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean insert_obat_hama = db.insert_kebutuhan_tanam("tanam_" +nama_pengguna + "_" + String.valueOf(id_kebutuhan_tanam),obat_hama, "Data Obat Hama", "1");
                                        if (insert_obat_hama){
                                            Toast.makeText(getApplicationContext(), "Data obat hama berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data obat hama berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal memasukkan data obat hama", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String obat_hama = String.valueOf(dataObatHamaBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_kebutuhan_tanam;

                                Cursor res_kebutuhan_tanam = db.getIDTanam();
                                if (res_kebutuhan_tanam.getCount() == 0) {
                                    id_kebutuhan_tanam = 1;
                                } else  {
                                    id_kebutuhan_tanam = res_kebutuhan_tanam.getCount()+1;
                                }

                                progressDialog.dismiss();

                                //if there is a success
                                //storing the name to sqlite with status synced
                                Boolean insert_obat_hama = db.insert_kebutuhan_tanam("tanam_" +nama_pengguna + "_" + String.valueOf(id_kebutuhan_tanam),obat_hama, "Data Obat Hama", "0");
                                if (insert_obat_hama) {
                                    Toast.makeText(getApplicationContext(), "Data obat hama berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data obat hama tidak berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                    viewData();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal memasukkan data obat hama", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                String obat_hama = String.valueOf(dataObatHamaBaru.getText());
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_kebutuhan_tanam;

                                Cursor res_kebutuhan_tanam = db.getIDTanam();
                                if (res_kebutuhan_tanam.getCount() == 0) {
                                    id_kebutuhan_tanam = 1;
                                } else  {
                                    id_kebutuhan_tanam = res_kebutuhan_tanam.getCount()+1;
                                }

                                Map<String, String> params = new HashMap<>();
                                params.put("id_kebutuhan_tanam", "tanam_" +nama_pengguna + "_" + String.valueOf(id_kebutuhan_tanam));
                                params.put("nama_kebutuhan_tanam",obat_hama);
                                params.put("kategori","Data Obat Hama");
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataObatHama.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void editDialog (Context c, int position){
        final EditText editDataObatHama = new EditText(c);
        final String id_data_obat_hama = customAdapter.getItemIds(position).toString();
        editDataObatHama.setText((String)customAdapter.getItem(position));

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Ubah Data Obat Hama")
                .setMessage("yakin ingin mengubah data?")
                .setView(editDataObatHama)
                .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String editObatHama = String.valueOf(editDataObatHama.getText());
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataObatHama.this);
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
                                        Boolean update_obat = db.updateDataPupuk(id_data_obat_hama, editObatHama);
                                        if (update_obat) {
                                            Toast.makeText(getApplicationContext(), "Data obat hama berhasil diubah", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data obat hama berhasil diubah ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal mengubah data obat hama", Toast.LENGTH_SHORT).show();
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

                                //Peringatan jika ingin menambah data harus online
                                Toast.makeText(getApplicationContext(), "Untuk mengubah data obat hama harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                params.put("nama_kebutuhan_tanam", editObatHama);
                                params.put("id_kebutuhan_tanam", id_data_obat_hama);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataObatHama.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void deleteDialog (Context c, int position){
        final String id_data_obat_hama = customAdapter.getItemIds(position).toString();

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Hapus Data Obat Hama")
                .setMessage("yakin ingin menghapus data?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataObatHama.this);
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
                                        Boolean delete_obat_hama = db.deleteDataPupuk(id_data_obat_hama);
                                        if (delete_obat_hama) {
                                            Toast.makeText(getApplicationContext(), "Data obat hama berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data obat hama", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data obat hama harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();

                                params.put("id_kebutuhan_tanam",id_data_obat_hama);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataObatHama.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }
}

