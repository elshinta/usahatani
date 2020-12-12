package com.example.usahatanipadi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import com.example.usahatanipadi.model.ModelResponse;
import com.example.usahatanipadi.retrofit.ApiClient;
import com.example.usahatanipadi.retrofit.ApiInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.location.Address;
import android.location.Geocoder;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MasterDataSawah extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewMasterSawahAdapter customAdapter;
    ArrayList<String> listalamat = new ArrayList<>();
    ArrayList<String> listluas = new ArrayList<>();
    ArrayList<String> listkategori = new ArrayList<>();
    ArrayList<String> list_id = new ArrayList<>();
    ArrayList<String> list_satuan = new ArrayList<>();
    ArrayList<String> list_latitude = new ArrayList<>();
    ArrayList<String> list_longitude = new ArrayList<>();

    DatabaseHelper db;
    String id_pengguna;
//    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/register_luas_lahan_sawah.php";
//    public static String URL_EDIT_NAME = "https://ilkomunila.com/usahatani/edit_master_data_sawah.php";
//    public static String URL_DELETE_NAME = "https://ilkomunila.com/usahatani/hapus_master_data_sawah.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;


    EditText et_koordinat, dataAlamat;
    String latitude = "", longitude ="";

    private final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data_sawah);

        Toolbar toolbar = findViewById(R.id.toolbar_data_sawah);
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

        FloatingActionButton fab = findViewById(R.id.fab_data_sawah);
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
        list_latitude.clear();
        list_longitude.clear();

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
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            int i = 0;
            int j = 0;
            id_pengguna = res.getString(0);

            Cursor res_sawah = db.getDataSawah(id_pengguna);
            if (res_sawah.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT).show();
                return;
            }
            while (res_sawah.moveToNext()) {
                sawah.add(res_sawah.getString(2) + " (" + res_sawah.getString(6) + ")");
                ListViewSawah.put(i, res_sawah.getString(0));
                i++;
                listalamat.add(res_sawah.getString(4));
                listluas.add(res_sawah.getString(2));
                listkategori.add(res_sawah.getString(5));
                list_id.add(res_sawah.getString(0));
                list_satuan.add(res_sawah.getString(6));
                list_latitude.add(res_sawah.getString(7));
                list_longitude.add(res_sawah.getString(8));

            }
        }
        simpleList = findViewById(R.id.lv_data_sawah);

        customAdapter = new ListViewMasterSawahAdapter(getApplicationContext(), listalamat, listluas, listkategori,list_id,list_satuan, list_latitude, list_longitude);

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

        Toolbar toolbar = dataSawah.findViewById(R.id.toolbar_luas_lahan_sawah);
        toolbar.setVisibility(View.GONE);

        final TextView judulLahan = dataSawah.findViewById(R.id.judul_lahan);
        final Spinner spinnerKategori = dataSawah.findViewById(R.id.pilih_kepemilikan);
        dataAlamat = dataSawah.findViewById(R.id.alamat_lahan);
        final EditText dataLuas = dataSawah.findViewById(R.id.luas_lahan);
        final Spinner spinnerSatuan = dataSawah.findViewById(R.id.satuan_lahan);
        et_koordinat = dataSawah.findViewById(R.id.koordinat);

        et_koordinat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveForResultIntent = new Intent(MasterDataSawah.this, MapsActivity.class);
                startActivityForResult(moveForResultIntent, REQUEST_CODE);
            }
        });

        judulLahan.setText("Tambah Data Sawah");

        Button btnSimpan = dataSawah.findViewById(R.id.btn_luas_lahan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataLuas.getText().toString().equals("") || !dataAlamat.getText().toString().equals("")) {

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

                    String id = "sawah_" + nama_pengguna + "_" + id_sawah;

                    final ProgressDialog progressDialog = new ProgressDialog(MasterDataSawah.this);
                    progressDialog.setMessage("Menambah Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<ModelResponse> call = apiInterface.registerLuasLahanSawah(id, nama_pengguna, dataLuas.getText().toString(),
                            dataAlamat.getText().toString(), spinnerKategori.getSelectedItem().toString(),
                            spinnerSatuan.getSelectedItem().toString(), latitude, longitude);
                    call.enqueue(new Callback<ModelResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<ModelResponse> call, @NotNull retrofit2.Response<ModelResponse> response) {
                            if (response.body() != null) {
                                if (!response.body().getError()){
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
                                    boolean insert_sawah = db.insert_sawah("sawah_" +nama_pengguna + "_" +
                                                    id_sawah,id_pengguna, dataLuas.getText().toString(),
                                            dataAlamat.getText().toString(), spinnerKategori.getSelectedItem().toString(),
                                            spinnerSatuan.getSelectedItem().toString(), latitude, longitude,1);

                                    if (insert_sawah) {
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil diisi", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil disimpan ke server", Toast.LENGTH_SHORT).show();
                                        dataSawah.dismiss();
                                        viewData();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ModelResponse> call, @NotNull Throwable t) {
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

                            boolean insert_sawah = db.insert_sawah("sawah_" +nama_pengguna + "_" + id_sawah,
                                    id_pengguna, dataLuas.getText().toString(), dataAlamat.getText().toString(),
                                    spinnerKategori.getSelectedItem().toString(),spinnerSatuan.getSelectedItem().toString(),
                                    latitude,longitude
                                    ,0);

                            if (insert_sawah) {
                                Toast.makeText(getApplicationContext(), "Data sawah berhasil diisi", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Data sawah tidak disimpan ke server", Toast.LENGTH_SHORT).show();
                                dataSawah.dismiss();
                                viewData();
                            } else {
                                Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Luas lahan dan alamat harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dataSawah.setTitle("Tambah Data Sawah");
        dataSawah.show();
    }

    @SuppressLint("SetTextI18n")
    public void editDialog(Context c, int position){

        final String id_data_sawah = customAdapter.getItemIds(position).toString();

        final Dialog dataSawah = new Dialog(this);
        dataSawah.setContentView(R.layout.activity_register_luas_lahan_sawah);
        dataSawah.setTitle("Ubah Data Sawah");

        Toolbar toolbar = dataSawah.findViewById(R.id.toolbar_luas_lahan_sawah);
        toolbar.setVisibility(View.GONE);

        final TextView judulLahan = dataSawah.findViewById(R.id.judul_lahan);
        final Spinner spinnerKategori = dataSawah.findViewById(R.id.pilih_kepemilikan);
        dataAlamat = dataSawah.findViewById(R.id.alamat_lahan);
        final EditText dataLuas = dataSawah.findViewById(R.id.luas_lahan);
        final Spinner spinnerSatuan = dataSawah.findViewById(R.id.satuan_lahan);
        et_koordinat = dataSawah.findViewById(R.id.koordinat);

        et_koordinat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveForResultIntent = new Intent(MasterDataSawah.this, MapsActivity.class);
                startActivityForResult(moveForResultIntent, REQUEST_CODE);
            }
        });
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
        et_koordinat.setText(customAdapter.getItemlatitude(position).toString()+" "+customAdapter.getListLongitude(position).toString());

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

        Button btnSimpan = dataSawah.findViewById(R.id.btn_luas_lahan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session = new UserSessionManager(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                final ProgressDialog progressDialog = new ProgressDialog(MasterDataSawah.this);
                progressDialog.setMessage("Mengubah Data");
                progressDialog.show();

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ModelResponse> call = apiInterface.edtiLuasLahanSawah(nama_pengguna, dataLuas.getText().toString(),
                        dataAlamat.getText().toString(), spinnerKategori.getSelectedItem().toString(),
                        spinnerSatuan.getSelectedItem().toString(), latitude, longitude);

                call.enqueue(new Callback<ModelResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ModelResponse> call, @NotNull retrofit2.Response<ModelResponse> response) {
                        if (response.body() != null) {
                            if (!response.body().getError()){
                                progressDialog.dismiss();

                                //if there is a success
                                //storing the name to sqlite with status synced
                                boolean update_sawah = db.updateDataSawah(id_data_sawah,Long.parseLong(id_pengguna),
                                        dataAlamat.getText().toString(),dataLuas.getText().toString(),
                                        spinnerKategori.getSelectedItem().toString(),
                                        spinnerSatuan.getSelectedItem().toString(), latitude, longitude);
                                if (update_sawah) {
                                    Toast.makeText(getApplicationContext(), "Data sawah berhasil diubah", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data sawah berhasil diubah ke server", Toast.LENGTH_SHORT).show();
                                    viewData();
                                    dataSawah.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal mengubah data sawah", Toast.LENGTH_SHORT).show();
                                    dataSawah.dismiss();
                                }
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ModelResponse> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        dataSawah.dismiss();

                        //Peringatan jika ingin menambah data harus online
                        Toast.makeText(getApplicationContext(),
                                "Untuk mengubah data sawah harus terhubung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                    }
                });
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

                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<ModelResponse> call = apiInterface.hapusLuasLahanSawah(id_data_sawah);
                        call.enqueue(new Callback<ModelResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<ModelResponse> call, @NotNull retrofit2.Response<ModelResponse> response) {
                                if (response.body() != null) {
                                    if (!response.body().getError()){
                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        boolean delete_sawah = db.deleteDataSawah(id_data_sawah);
                                        if (delete_sawah) {
                                            Toast.makeText(getApplicationContext(), "Data sawah berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data sawah", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<ModelResponse> call, @NotNull Throwable t) {
                                progressDialog.dismiss();
                                //Peringatan jika ingin hapus data harus online
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data sawah harus " +
                                        "terhunbung dengan koneksi internet!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    private String getAddress(String latitude, String longitude){
        Geocoder geocoder = new Geocoder(MasterDataSawah.this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            Address obj = addresses.get(0);

            String add = obj.getAddressLine(0);
            add = add + ","+ obj.getAdminArea();
            add = add + ","+ obj.getCountryName();

            return add;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MasterDataSawah.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == MapsActivity.RESULT_CODE) {
                latitude = data.getStringExtra(MapsActivity.EXTRA_LATITUDE);
                longitude = data.getStringExtra(MapsActivity.EXTRA_LONGITUDE);
                et_koordinat.setText(latitude+" "+longitude);
                dataAlamat.setText(getAddress(latitude, longitude));
            }
        }
    }
}

