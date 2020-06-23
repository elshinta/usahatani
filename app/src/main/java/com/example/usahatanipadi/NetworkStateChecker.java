package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    public static String URL_SAVE_PERIODE = "https://ilkomunila.com/usahatani/tambah_periode.php";
    public static String URL_GET_SURVEY = "https://ilkomunila.com/usahatani/get_survey.php";
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";
    UserSessionManager session;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedPengeluaranBiaya();

                Cursor cursor_penerimaan = db.getUnsyncedPenerimaanDana();

                Cursor cursor_periode = db.getUnsyncedPeriode();

                Cursor cursor_kebutuhan_tanam = db.getUnsyncedKebutuhanTanam();

                Cursor cursor_hasil_panen = db.getUnsyncedHasilPanen();

                Cursor cursor_sawah = db.getUnsyncedSawah();

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Syncing Data..");
                progressDialog.show();

                if (cursor_sawah.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced sawah to MySQL
                        saveSawah(
                                cursor_sawah.getString(0),
                                cursor_sawah.getString(2),
                                cursor_sawah.getString(3),
                                cursor_sawah.getString(4),
                                cursor_sawah.getString(5)
                        );
                    } while (cursor_sawah.moveToNext());
                }

                if (cursor_periode.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced periode to MySQL
                        savePeriode(
                                cursor_periode.getString(0),
                                cursor_periode.getString(1),
                                cursor_periode.getString(2),
                                cursor_periode.getString(3),
                                cursor_periode.getString(4),
                                cursor_periode.getString(5)
                        );
                    } while (cursor_periode.moveToNext());
                }

                if (cursor_kebutuhan_tanam.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced kebutuhan tanam to MySQL
                        saveKebutuhanTanam(
                                cursor_kebutuhan_tanam.getString(0),
                                cursor_kebutuhan_tanam.getString(1),
                                cursor_kebutuhan_tanam.getString(2)
                        );
                    } while (cursor_kebutuhan_tanam.moveToNext());
                }

                if (cursor_hasil_panen.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced hasil panen to MySQL
                        saveHasilPanen(
                                cursor_hasil_panen.getString(0),
                                cursor_hasil_panen.getString(1)
                        );
                    } while (cursor_hasil_panen.moveToNext());
                }

                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced pengeluaran biaya to MySQL
                        savePengeluaranBiaya(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(8),
                                cursor.getString(9)
                        );
                    } while (cursor.moveToNext());
                }

                if (cursor_penerimaan.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced penerimaan dana to MySQL
                        savePenerimaanDana(
                                cursor_penerimaan.getString(0),
                                cursor_penerimaan.getString(1),
                                cursor_penerimaan.getString(2),
                                cursor_penerimaan.getString(3),
                                cursor_penerimaan.getString(4),
                                cursor_penerimaan.getString(5),
                                cursor_penerimaan.getString(6),
                                cursor_penerimaan.getString(7),
                                cursor_penerimaan.getString(8),
                                cursor_penerimaan.getString(9),
                                cursor_penerimaan.getString(11),
                                cursor_penerimaan.getString(12)
                        );
                    } while (cursor_penerimaan.moveToNext());
                }

                getSurvey(URL_GET_SURVEY,"2");

                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void savePengeluaranBiaya(final String id, final String id_lahan, final String id_kebutuhan_tanam, final String jumlah, final String total_harga, final String nama_pemasok, final String tanggal_pengeluaran_biaya, final String catatan, final String id_periode, final String satuan) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MenuPengeluaranBiaya.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updatePengeluaranBiaya(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MenuPengeluaranBiaya.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id_pengeluaran_biaya",id);
                params.put("id_lahan_sawah",id_lahan);
                params.put("id_kebutuhan_tanam",id_kebutuhan_tanam);
                params.put("jumlah",jumlah);
                params.put("total_harga",total_harga);
                params.put("nama_pemasok",nama_pemasok);
                params.put("tanggal_pengeluaran_biaya",tanggal_pengeluaran_biaya);
                params.put("catatan",catatan);
                params.put("id_periode",id_periode);
                params.put("satuan",satuan);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void savePenerimaanDana(final String id, final String id_lahan, final String id_hasil_panen, final String jumlah, final String total_harga, final String nama_pelamggam, final String tanggal_penerimaan_dana, final String catatan, final String id_periode, final String satuan, final String luas_panen, final String satuan_luas_panen) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, MenuPenerimaanDana.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updatePenerimaanDana(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MenuPenerimaanDana.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                session = new UserSessionManager(context);
                HashMap<String, String> user = session.getUserDetails();
                final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                Map<String, String> params = new HashMap<>();
                params.put("id_penerimaan_dana", id);
                params.put("id_lahan_sawah",id_lahan);
                params.put("id_hasil_panen",id_hasil_panen);
                params.put("jumlah",jumlah);
                params.put("total_harga",total_harga);
                params.put("nama_pelanggan",nama_pelamggam);
                params.put("tanggal_penerimaan_dana",tanggal_penerimaan_dana);
                params.put("catatan",catatan);
                params.put("id_periode",id_periode);
                params.put("satuan",satuan);
                params.put("luas_panen",luas_panen);
                params.put("satuan_luas_panen",satuan_luas_panen);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void savePeriode(final String id, final String id_lahan, final String bulan_awal, final String bulan_akhir, final String tahun_awal, final String tahun_akhir) {

       // Toast.makeText(context, id + " " + id_lahan + " " + bulan_awal + " " + bulan_akhir + " " + tahun_awal + " " + tahun_akhir, Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_SAVE_PERIODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updatePeriode(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_lahan_sawah",id_lahan);
                params.put("bulan_periode_awal",bulan_awal);
                params.put("bulan_periode_akhir",bulan_akhir);
                params.put("tahun_periode_awal",tahun_awal);
                params.put("tahun_periode_akhir",tahun_akhir);
                params.put("id_periode",id);


                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveKebutuhanTanam(final String id, final String nama_kebutuhan_tanam, final String kategori){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MasterDataPupuk.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateKebutuhanTanam(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MasterDataPupuk.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                session = new UserSessionManager(context);
                HashMap<String, String> user = session.getUserDetails();
                final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                Map<String, String> params = new HashMap<>();

                params.put("id_kebutuhan_tanam", id);
                params.put("nama_kebutuhan_tanam", nama_kebutuhan_tanam);
                params.put("kategori", kategori);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveHasilPanen(final String id, final String nama_hasil_panen){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MasterDataHasilPanen.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateHasilPanen(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MasterDataHasilPanen.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                session = new UserSessionManager(context);
                HashMap<String, String> user = session.getUserDetails();
                final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                Map<String, String> params = new HashMap<>();

                params.put("id_hasil_panen", id);
                params.put("nama_hasil_panen",nama_hasil_panen);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveSawah(final String id, final String luas, final String alamat, final String kategori, final String satuan) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MasterDataSawah.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateSawah(id, "1");

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                session = new UserSessionManager(context);
                HashMap<String, String> user = session.getUserDetails();
                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                Map<String, String> params = new HashMap<>();

                params.put("id",id);
                params.put("nama_pengguna",nama_pengguna);
                params.put("luas",luas);
                params.put("alamat",alamat);
                params.put("kategori",kategori);
                params.put("satuan",satuan);

                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void getSurvey(String url, final String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonPost = new JSONObject(response);

                    //memasukkan data ke dalam variable
                    String id_survey = jsonPost.getString("id_survey");
                    String id_pengguna = jsonPost.getString("id_pengguna");
                    String jenis_pertanyaan = jsonPost.getString("jenis_pertanyaan");
                    String jumlah_pertanyaan = jsonPost.getString("jumlah_pertanyaan");
                    String id_periode = jsonPost.getString("id_periode");

                    Boolean insert = db.insert_survey(id_survey,id_pengguna,jenis_pertanyaan,jumlah_pertanyaan,id_periode);
                    if(insert) {
                        Toast.makeText(context, "Anda memiliki survey baru", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}