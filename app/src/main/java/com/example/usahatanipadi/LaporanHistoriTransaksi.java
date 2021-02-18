package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LaporanHistoriTransaksi extends AppCompatActivity {
    UserSessionManager session;
    ListView simpleList;
    ListViewHistoryTransaksiAdapter customAdapter;
    ArrayList<String> list_id = new ArrayList<String>();
    ArrayList<String> listdata = new ArrayList<String>();
    ArrayList<String> listtanggal = new ArrayList<String>();
    ArrayList<String> listjumlah = new ArrayList<String>();
    ArrayList<String> listharga = new ArrayList<String>();
    ArrayList<String> listtotal_harga = new ArrayList<String>();
    ArrayList<String> listluaspanen = new ArrayList<String>();
    ArrayList<String> listnama = new ArrayList<String>();
    ArrayList<String> listcatatan = new ArrayList<String>();
    DatabaseHelper db;
    public static String URL_DELETE_NAME = "https://ilkomunila.com/usahatani/hapus_pengeluaran_biaya.php";
    public static String URL_DELETE_NAME_PENERIMAAN = "https://ilkomunila.com/usahatani/hapus_penerimaan_dana.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_histori_transaksi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_histori_transaksi);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Histori Transaksi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //untuk kembali ke halaman sebelumnya

        db = new DatabaseHelper(this);
        Intent intent = getIntent();
        final String spinner = intent.getStringExtra("spinner");

        if (spinner.equals("1")) {
            Spinner sp_cari_berdasarkan = (Spinner) findViewById(R.id.cari_berdasarkan);
            sp_cari_berdasarkan.setSelection(1);
        }
        else {
            Spinner sp_cari_berdasarkan = (Spinner) findViewById(R.id.cari_berdasarkan);
            sp_cari_berdasarkan.setSelection(0);
        }

        Spinner sp_cari_berdasarkan = (Spinner) findViewById(R.id.cari_berdasarkan);
        if (sp_cari_berdasarkan.getSelectedItemPosition() == 0) {
                viewData();
        } else {
                viewDataPenerimaan();
        }

        sp_cari_berdasarkan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    viewData();
                } else {
                    viewDataPenerimaan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
    * Menampilkan histori transaksi berdasarkan pengeluaran biaya
    * */

    public void viewData() {
        list_id.clear();
        listdata.clear();
        listtanggal.clear();
        listjumlah.clear();
        listharga.clear();
        listtotal_harga.clear();
        listluaspanen.clear();
        listnama.clear();
        listcatatan.clear();
        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");
        TextView tv_sawah = (TextView) findViewById(R.id.alamat_sawah);

        Cursor res_sawah = db.getIdSawah(sawah_terpilih);

        while (res_sawah.moveToNext()) {
            Cursor res_periode = db.getDataIdPeriode(periode_terpilih);

            while (res_periode.moveToNext()) {
                tv_sawah.setText(res_sawah.getString(4) + " (" + res_periode.getString(3) + " " + res_periode.getString(5) + ")");
            }
        }

        Cursor res_hasil = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);

        while (res_hasil.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_hasil.getString(2));

            if (res_kebutuhan_tanam != null) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);
                    listdata.add("Pembelian " + kebutuhan_tanam);
                }
            }

            DecimalFormat formatter = new DecimalFormat("#,###");
            String format_tot_barang_jasa = formatter.format(res_hasil.getInt(5));

            float konversi_satuan=0;
            if(res_hasil.getString(4).equals("ton")) {
                konversi_satuan = Float.parseFloat(res_hasil.getString(3))*1000;
            }
            else if(res_hasil.getString(4).equals("kuintal")){
                konversi_satuan = Float.parseFloat(res_hasil.getString(3))*100;
            }
            else if(res_hasil.getString(4).equals("kg")){
                konversi_satuan = Float.parseFloat(res_hasil.getString(3))*1;
            }

            float harga;
            harga = res_hasil.getFloat(5) / konversi_satuan;
            String format_harga = formatter.format(harga);

            DecimalFormat df = new DecimalFormat("#.##");
            String format_konversi = df.format(konversi_satuan);

            String format_jml = res_hasil.getString(3).replaceAll("\\.", ",");

            list_id.add(res_hasil.getString(0));
            listtanggal.add(res_hasil.getString(7));
            listjumlah.add("Jumlah : " + format_jml + " " + res_hasil.getString(4) + " (" + format_konversi + " " + "kg)");
            listtotal_harga.add("Total Harga : Rp. " + format_tot_barang_jasa);
            listluaspanen.add("Luas Panen : ");
            listnama.add("Pemasok : " + res_hasil.getString(6));
            listcatatan.add(res_hasil.getString(8));
            listharga.add("Harga / kg : Rp. " + format_harga);
        }

        simpleList = (ListView) findViewById(R.id.lv_histori_transaksi);

        customAdapter = new ListViewHistoryTransaksiAdapter(getApplicationContext(), list_id, listdata, listtanggal, listjumlah, listharga, listtotal_harga, listluaspanen, listnama, listcatatan,"1");

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialogPengeluaran(LaporanHistoriTransaksi.this, position);
                return true;
            }
        });

    }

    /*
     * Menampilkan histori transaksi berdasarkan penerimaan dana
     * */

    public void viewDataPenerimaan() {
        list_id.clear();
        listdata.clear();
        listtanggal.clear();
        listjumlah.clear();
        listharga.clear();
        listtotal_harga.clear();
        listluaspanen.clear();
        listnama.clear();
        listcatatan.clear();
        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");
        TextView tv_sawah = (TextView) findViewById(R.id.alamat_sawah);

        Cursor res_sawah = db.getIdSawah(sawah_terpilih);

        while (res_sawah.moveToNext()) {
            Cursor res_periode = db.getDataIdPeriode(periode_terpilih);

            while (res_periode.moveToNext()) {
                tv_sawah.setText(res_sawah.getString(4) + " (" + res_periode.getString(3) + " " + res_periode.getString(5) + ")");
            }
        }

        Cursor res_hasil = db.getDataPenerimaanHistori(sawah_terpilih, periode_terpilih);

        while (res_hasil.moveToNext()) {
            Cursor res_hasil_panen = db.getIdHasilPanen(res_hasil.getString(2));

            if (res_hasil_panen != null) {
                if (res_hasil_panen.moveToFirst()) {
                    String hasil_panen = res_hasil_panen.getString(1);
                    listdata.add("Penjualan " + hasil_panen);
                }
            }

            DecimalFormat formatter = new DecimalFormat("#,###");
            String format_tot_hasil_panen = formatter.format((res_hasil.getInt(5)));

            float konversi_satuan=0;
            if(res_hasil.getString(4).equals("ton")) {
                konversi_satuan = (res_hasil.getFloat(3))*1000;
            }
            else if(res_hasil.getString(4).equals("kuintal")){
                konversi_satuan = (res_hasil.getFloat(3))*100;
            }
            else if(res_hasil.getString(4).equals("kg")){
                konversi_satuan = (res_hasil.getFloat(3))*1;
            }

            float harga;
            harga = res_hasil.getFloat(5) / konversi_satuan;
            String format_harga = formatter.format(harga);

            DecimalFormat df = new DecimalFormat("#.##");
            String format_konversi = df.format (konversi_satuan);

            String format_jml = res_hasil.getString(3).replaceAll("\\.", ",");

            list_id.add(res_hasil.getString(0));
            listtanggal.add(res_hasil.getString(7));
            listjumlah.add("Jumlah : " + format_jml + " " + res_hasil.getString(4) + " (" + format_konversi + " " + "kg)");
            listtotal_harga.add("Total Harga : Rp. " + format_tot_hasil_panen);
            listluaspanen.add("Luas Panen : " + res_hasil.getString(10) + " " + res_hasil.getString(11));
            listnama.add("Pembeli : " + res_hasil.getString(6));
            listcatatan.add(res_hasil.getString(8));
            listharga.add("Harga / kg : Rp. " + format_harga);
        }

        simpleList = (ListView) findViewById(R.id.lv_histori_transaksi);

        customAdapter = new ListViewHistoryTransaksiAdapter(getApplicationContext(), list_id, listdata, listtanggal, listjumlah, listharga, listtotal_harga, listluaspanen, listnama, listcatatan,"0");
        simpleList.setAdapter(customAdapter);

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialogPenerimaan(LaporanHistoriTransaksi.this, position);
                return true;
            }
        });
    }

    public void deleteDialogPenerimaan(Context c, int position){
        db = new DatabaseHelper(this);
        final String id_penerimaan = customAdapter.getItem(position).toString();
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Hapus Transaksi")
                .setMessage("yakin ingin menghapus penerimaan?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final ProgressDialog progressDialog = new ProgressDialog(LaporanHistoriTransaksi.this);
                        progressDialog.setMessage("Menghapus Data");
                        progressDialog.show();

                        session = new UserSessionManager(LaporanHistoriTransaksi.this);
                        HashMap<String, String> user = session.getUserDetails();
                        final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_NAME_PENERIMAAN, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        progressDialog.dismiss();

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean delete_penerimaan_dana = db.deleteDataTransaksiPenerimaan(id_penerimaan);
                                        viewDataPenerimaan();
                                        if (delete_penerimaan_dana) {
                                            viewDataPenerimaan();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data penerimaan dana", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data penerimaan harus terhubung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();

                                params.put("id_penerimaan_dana", id_penerimaan);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(LaporanHistoriTransaksi.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    public void deleteDialogPengeluaran(Context c, int position){
        db = new DatabaseHelper(this);
        final String id_pengeluaran = customAdapter.getItem(position).toString();
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Delete Transaksi")
                .setMessage("yakin ingin menghapus pengeluaran?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session = new UserSessionManager(getApplicationContext());
                        HashMap<String, String> user = session.getUserDetails();
                        final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                        final ProgressDialog progressDialog = new ProgressDialog(LaporanHistoriTransaksi.this);
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
                                        Boolean delete_pengeluaran_biaya = db.deleteDataTransaksiPengeluaran(id_pengeluaran);
                                        viewData();
                                        if (delete_pengeluaran_biaya) {
                                            viewData();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal menghapus data pengeluaran biaya", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Untuk menghapus data Pengeluaran harus terhubung dengan koneksi internet!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();

                                params.put("id_pengeluaran", id_pengeluaran);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(LaporanHistoriTransaksi.this).addToRequestQueue(stringRequest);
                    }
                })
                .setNegativeButton("Batal", null)
                .create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuUtama.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
