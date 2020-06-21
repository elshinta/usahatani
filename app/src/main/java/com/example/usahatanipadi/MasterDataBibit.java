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

public class MasterDataBibit extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterBibitAdapter customAdapter;
    ArrayList<String> listdatabibit = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    DatabaseHelper db;
    public static String URL_SAVE_NAME = "https://usahatani.000webhostapp.com/usahatani/master_data.php";
    public static String URL_EDIT_NAME = "https://usahatani.000webhostapp.com/usahatani/edit_master_data.php";
    public static String URL_DELETE_NAME = "https://usahatani.000webhostapp.com/usahatani/hapus_master_data.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_bibit);

        viewData();

        Toolbar toolbar = findViewById(R.id.toolbar_data_bibit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Bibit");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_data_bibit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(MasterDataBibit.this);
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    public void viewData() {
        listdatabibit.clear();
        list_id.clear();
        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res_data_bibit = db.getDataBibit(nama);
        if (res_data_bibit.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while (res_data_bibit.moveToNext()) {
            listdatabibit.add(res_data_bibit.getString(1));
            list_id.add(res_data_bibit.getString(0));
        }

        simpleList = (ListView) findViewById(R.id.lv_data_bibit);

        customAdapter = new ListViewMasterBibitAdapter(getApplicationContext(), listdatabibit, list_id);

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editDialog(MasterDataBibit.this, position);
            }
        });

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog(MasterDataBibit.this, position);
                return true;
            }
        });
    }

    public void showAddItemDialog(Context c) {
        final EditText dataBibitBaru = new EditText(c);
        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Tambah Data Bibit")
                .setMessage("bibit digunakan untuk menyuburkan tanah")
                .setView(dataBibitBaru)
                .setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bibit = String.valueOf(dataBibitBaru.getText());

                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataBibit.this);
                        progressDialog.setMessage("Menambah Data");
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String bibit = String.valueOf(dataBibitBaru.getText());
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
                                        Boolean insert_bibit = db.insert_kebutuhan_tanam("tanam_" +nama_pengguna + "_" + String.valueOf(id_kebutuhan_tanam),bibit, "Data Bibit", "1");
                                        if (insert_bibit) {
                                            Toast.makeText(getApplicationContext(), "Data bibit berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data bibit berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal memasukkan data bibit", Toast.LENGTH_SHORT).show();
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
                                String bibit = String.valueOf(dataBibitBaru.getText());
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
                                Boolean insert_bibit = db.insert_kebutuhan_tanam("tanam_" +nama_pengguna + "_" + String.valueOf(id_kebutuhan_tanam),bibit, "Data Bibit", "0");
                                if (insert_bibit) {
                                    Toast.makeText(getApplicationContext(), "Data bibit berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data bibit tidak berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                    viewData();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal memasukkan data bibit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                String bibit = String.valueOf(dataBibitBaru.getText());
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
                                params.put("nama_kebutuhan_tanam", bibit);
                                params.put("kategori", "Data Bibit");
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataBibit.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void editDialog(Context c, int position) {
        final EditText editDataBibit = new EditText(c);
        final String id_data_bibit = customAdapter.getItemIds(position).toString();
        editDataBibit.setText((String) customAdapter.getItem(position));

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Ubah Data Bibit")
                .setMessage("yakin ingin mengubah data?")
                .setView(editDataBibit)
                .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String editBibit = String.valueOf(editDataBibit.getText());
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataBibit.this);
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
                                        Boolean update_bibit = db.updateDataPupuk(id_data_bibit, editBibit);
                                        if (update_bibit) {
                                            Toast.makeText(getApplicationContext(), "Data bibit berhasil diubah", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data bibit berhasil diubah ke server", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal mengubah data bibit", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Untuk mengubah data bibit harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                params.put("nama_kebutuhan_tanam", editBibit);
                                params.put("id_kebutuhan_tanam", id_data_bibit);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataBibit.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void deleteDialog(Context c, int position) {
        final String id_data_bibit = customAdapter.getItemIds(position).toString();

        final String nama_kebutuhan_tanam_tmp = (String) customAdapter.getItem(position);

        db = new DatabaseHelper(this);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Hapus Data Bibit")
                .setMessage("yakin ingin menghapus data?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(MasterDataBibit.this);
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
                                        Boolean delete_bibit = db.deleteDataPupuk(id_data_bibit);
                                        if (delete_bibit) {
                                            Toast.makeText(getApplicationContext(), "Data bibit berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data bibit", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data bibit harus terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                params.put("id_kebutuhan_tanam", id_data_bibit);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(MasterDataBibit.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }
}
