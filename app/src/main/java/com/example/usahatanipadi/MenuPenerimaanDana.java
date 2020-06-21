package com.example.usahatanipadi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MenuPenerimaanDana extends AppCompatActivity {
    EditText et_penerimaan;
    UserSessionManager session;
    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/penerimaan_dana.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    String total;
    String total_konversi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_penerimaan_dana);

        Toolbar toolbar = (Toolbar) findViewById(R.id.menu_penerimaan_dana);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Penerimaan Dana");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //untuk kembali ke halaman sebelumnya

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this);
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        String id_pengguna;
        final List<String> sawah = new ArrayList<String>();
        final List<String> hasil_panen = new ArrayList<String>();
        final HashMap<Integer, String> spinnerSawah = new HashMap<Integer, String>();
        final HashMap<Integer, String> spinnerHasil = new HashMap<Integer, String>();

        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");
        TextView textView = (TextView)findViewById(R.id.nama_sawah);
        TextView textView_periode = (TextView)findViewById(R.id.periode_sawah);


        Cursor res = db.getData(nama);


        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while (res.moveToNext()) {
            int i = 0;
            int j = 0;
            id_pengguna = res.getString(0);

            Cursor res_sawah = db.getIdSawah(sawah_terpilih);
            if (res_sawah.getCount() == 0) {
                Toast.makeText(this, "Erorr!", Toast.LENGTH_SHORT).show();
            }
            else {
                res_sawah.moveToFirst();
                do{
                    textView.setText(res_sawah.getString(3) + " (" + res_sawah.getString(4) + ")");
                } while (res_sawah.moveToNext());
            }

            Cursor res_periode = db.getDataIdPeriode(periode_terpilih);
            if (res_periode.getCount() == 0) {
                Toast.makeText(this, "Erorr!", Toast.LENGTH_SHORT).show();
            }
            else {
                res_periode.moveToFirst();
                do{
                    textView_periode.setText(res_periode.getString(2) + " - " + res_periode.getString(4));
                } while (res_periode.moveToNext());
            }

            Cursor res_hasil_panen = db.getDataHasilPanen(nama);
            if (res_hasil_panen.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
                return;
            }
            while (res_hasil_panen.moveToNext()) {
                hasil_panen.add(res_hasil_panen.getString(1));
                spinnerHasil.put(j, res_hasil_panen.getString(0));
                j++;
            }
        }

        final Spinner pilih_hasil_panen = (Spinner) findViewById(R.id.pilih_hasil_panen);
        ArrayAdapter arrayAdapter_pilih_hasil_panen = new ArrayAdapter(this, R.layout.spinner_text, hasil_panen);
        arrayAdapter_pilih_hasil_panen.setDropDownViewResource(R.layout.spinner_dropdown);
        pilih_hasil_panen.setAdapter(arrayAdapter_pilih_hasil_panen);

        final EditText et_tgl_penerimaan_dana = (EditText) findViewById(R.id.tgl_penerimaan_dana);
        final EditText et_jumlah_hasil_panen = (EditText) findViewById(R.id.jumlah_hasil_panen);
        final EditText et_harga = (EditText) findViewById(R.id.harga);
        final EditText et_total_hasil_panen = (EditText) findViewById(R.id.total_hasil_panen);
        final EditText et_luas_panen = (EditText) findViewById(R.id.luas_panen);
        final Spinner pilih_satuan_panen = (Spinner) findViewById(R.id.satuan_luas_panen);
        final EditText et_nama_pelanggan = (EditText) findViewById(R.id.nama_pelanggan);
        final EditText et_catatan_penerimaan = (EditText) findViewById(R.id.catatan_penerimaan);
        final Spinner pilih_satuan = (Spinner)findViewById(R.id.satuan_jumlah_hasil_panen);
        et_jumlah_hasil_panen.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        et_luas_panen.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        //Memberi tulisan Rp dan nominal untuk Harga per Kg Hasil Panen (Penerimaan)
        et_harga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                et_harga.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    if (originalString.contains("Rp ")) {
                        originalString = originalString.replaceAll("Rp ", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    et_harga.setText("Rp " + formattedString);
                    et_harga.setSelection(et_harga.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                et_harga.addTextChangedListener(this);

                total =  et_harga.getText().toString();

                try {
                    total = total.replace(",", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    total = total.replace(".", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    total = total.replace("Rp ", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(total.equals("")){
                    total = "0";
                }
                if(total.equals("Rp")){
                    total = "0";
                }
                if(total.equals("R")){
                    total = "0";
                }

                float konversi_satuan2=1;
                if(pilih_satuan.getSelectedItem().toString().equals("ton")) {
                    konversi_satuan2 = Float.parseFloat(total)*1000;
                }
                else if(pilih_satuan.getSelectedItem().toString().equals("kuintal")){
                    konversi_satuan2 = Float.parseFloat(total)*100;
                }
                else if(pilih_satuan.getSelectedItem().toString().equals("kg")){
                    konversi_satuan2 = Float.parseFloat(total)*1;
                }

                float total_harga;
                total_harga = Float.parseFloat(et_jumlah_hasil_panen.getText().toString()) * konversi_satuan2;


                String string_total_harga = String.valueOf(total_harga);
                try {
                    string_total_harga = string_total_harga.replace(".0", ",");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    string_total_harga = string_total_harga.replace(".", ",");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try{
                     total_konversi = string_total_harga.replace(",", "");
                } catch (Exception e) {
                e.printStackTrace();
            }

                    int number = new BigDecimal(total_harga).intValue();

                et_total_hasil_panen.setText("Rp " + String.valueOf(number));
            }
        });

        //Memberi tulisan Rp dan nominal untuk Total Harga Hasil Panen (Penerimaan)
        et_total_hasil_panen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                et_total_hasil_panen.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    if (originalString.contains("Rp ")) {
                        originalString = originalString.replaceAll("Rp ", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    et_total_hasil_panen.setText("Rp " + formattedString);
                    et_total_hasil_panen.setSelection(et_total_hasil_panen.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                et_total_hasil_panen.addTextChangedListener(this);
            }
        });


        Button btn = (Button) findViewById(R.id.btn_simpan_penerimaan_dana);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String replace_harga;

                replace_harga = et_total_hasil_panen.getText().toString();
                try {
                    replace_harga = replace_harga.replace(",", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    replace_harga = replace_harga.replace(".", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    replace_harga = replace_harga.replace("Rp ", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (et_tgl_penerimaan_dana.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Tanggal transaksi harus diisi", Toast.LENGTH_SHORT).show();
                } else if (et_jumlah_hasil_panen.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Jumlah harus diisi", Toast.LENGTH_SHORT).show();
                } else if (et_total_hasil_panen.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Total Harga harus diisi", Toast.LENGTH_SHORT).show();
                } else if (et_luas_panen.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Luas Panen harus diisi", Toast.LENGTH_SHORT).show();
                } else {

                    session = new UserSessionManager(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                    final ProgressDialog progressDialog = new ProgressDialog(MenuPenerimaanDana.this);
                    progressDialog.setMessage("Menyimpan Data");
                    progressDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String id_hasil = spinnerHasil.get(pilih_hasil_panen.getSelectedItemPosition());
                            String replace_harga;

                            replace_harga = et_total_hasil_panen.getText().toString();
                            try {
                                replace_harga = replace_harga.replace(",", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                replace_harga = replace_harga.replace(".", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                replace_harga = replace_harga.replace("Rp ", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();

                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    //if there is a success
                                    //storing the name to sqlite with status synced

                                    int id_penerimaan;

                                    Cursor res_penerimaaan = db.getIDPenerimaan();
                                    if (res_penerimaaan.getCount() == 0) {
                                        id_penerimaan = 1;
                                    } else  {
                                        id_penerimaan = res_penerimaaan.getCount()+1;
                                    }

                                    Boolean insert_penerimaan = db.insert_penerimaan_dana("penerimaan_" + nama_pengguna + "_" + id_penerimaan, sawah_terpilih, id_hasil, et_jumlah_hasil_panen.getText().toString(), replace_harga, et_nama_pelanggan.getText().toString(), et_tgl_penerimaan_dana.getText().toString(), et_catatan_penerimaan.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"1", et_luas_panen.getText().toString(),pilih_satuan_panen.getSelectedItem().toString());
                                    if (insert_penerimaan) {
                                        Toast.makeText(getApplicationContext(), "Data penerimaan dana berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Data penerimaan dana berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent intent = new Intent(MenuPenerimaanDana.this, MenuPenerimaanDanaTersimpan.class);
                                        intent.putExtra("sawah_terpilih", sawah_terpilih);
                                        intent.putExtra("periode_terpilih", periode_terpilih);
                                        intent.putExtra("id_penerimaan","penerimaan_" + nama_pengguna + "_" + String.valueOf(id_penerimaan));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Gagal memasukkan data penerimaan dana", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //if there is some error
                                    //saving the name to sqlite with status unsynced

                                    int id_penerimaan;

                                    Cursor res_penerimaaan = db.getIDPenerimaan();
                                    if (res_penerimaaan.getCount() == 0) {
                                        id_penerimaan = 1;
                                    } else  {
                                        id_penerimaan = res_penerimaaan.getCount()+1;
                                    }

                                    Boolean insert_penerimaan = db.insert_penerimaan_dana("penerimaan_" + nama_pengguna + "_" + id_penerimaan, sawah_terpilih, id_hasil, et_jumlah_hasil_panen.getText().toString(), replace_harga, et_nama_pelanggan.getText().toString(), et_tgl_penerimaan_dana.getText().toString(), et_catatan_penerimaan.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"0", et_luas_panen.getText().toString(),pilih_satuan_panen.getSelectedItem().toString());
                                    if (insert_penerimaan) {
                                        Toast.makeText(getApplicationContext(), "Data penerimaan dana berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Data penerimaan dana tidak berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent intent = new Intent(MenuPenerimaanDana.this, MenuPenerimaanDanaTersimpan.class);
                                        intent.putExtra("sawah_terpilih", sawah_terpilih);
                                        intent.putExtra("periode_terpilih", periode_terpilih);
                                        intent.putExtra("id_penerimaan","penerimaan_" + nama_pengguna + "_" + String.valueOf(id_penerimaan));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Gagal memasukkan data penerimaan dana", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            int id_penerimaan;

                            Cursor res_penerimaaan = db.getIDPenerimaan();
                            if (res_penerimaaan.getCount() == 0) {
                                id_penerimaan = 1;
                            } else  {
                                res_penerimaaan.moveToNext();
                                id_penerimaan = res_penerimaaan.getCount()+1;
                            }

                            String id_hasil = spinnerHasil.get(pilih_hasil_panen.getSelectedItemPosition());
                            String replace_harga;

                            replace_harga = et_total_hasil_panen.getText().toString();
                            try {
                                replace_harga = replace_harga.replace(",", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                replace_harga = replace_harga.replace(".", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                replace_harga = replace_harga.replace("Rp ", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();

                            Boolean insert_penerimaan = db.insert_penerimaan_dana("penerimaan_" + nama_pengguna + "_" + id_penerimaan, sawah_terpilih, id_hasil, et_jumlah_hasil_panen.getText().toString(), replace_harga, et_nama_pelanggan.getText().toString(), et_tgl_penerimaan_dana.getText().toString(), et_catatan_penerimaan.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"0", et_luas_panen.getText().toString(),pilih_satuan_panen.getSelectedItem().toString());
                            if (insert_penerimaan) {
                                Toast.makeText(getApplicationContext(), "Data penerimaan dana berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Data tersimpan offline", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(MenuPenerimaanDana.this, MenuPenerimaanDanaTersimpan.class);
                                intent.putExtra("sawah_terpilih", sawah_terpilih);
                                intent.putExtra("periode_terpilih", periode_terpilih);
                                intent.putExtra("id_penerimaan","penerimaan_" + nama_pengguna + "_" + String.valueOf(id_penerimaan));
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal memasukkan data penerimaan dana", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            int id_penerimaan;

                            Cursor res_penerimaaan = db.getIDPenerimaan();
                            if (res_penerimaaan.getCount() == 0) {
                                id_penerimaan = 1;
                            } else  {
                                id_penerimaan = res_penerimaaan.getCount()+1;
                            }


                            String id_hasil = spinnerHasil.get(pilih_hasil_panen.getSelectedItemPosition());
                            String replace_harga;

                            replace_harga = et_total_hasil_panen.getText().toString();
                            try {
                                replace_harga = replace_harga.replace(",", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                replace_harga = replace_harga.replace(".", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                replace_harga = replace_harga.replace("Rp ", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Map<String, String> params = new HashMap<>();
                            params.put("id_penerimaan_dana","penerimaan_" + nama_pengguna + "_" + String.valueOf(id_penerimaan));
                            params.put("id_lahan_sawah",sawah_terpilih);
                            params.put("id_hasil_panen",id_hasil);
                            params.put("jumlah",et_jumlah_hasil_panen.getText().toString());
                            params.put("total_harga",replace_harga);
                            params.put("nama_pelanggan",et_nama_pelanggan.getText().toString());
                            params.put("tanggal_penerimaan_dana",et_tgl_penerimaan_dana.getText().toString());
                            params.put("catatan",et_catatan_penerimaan.getText().toString());
                            params.put("id_periode",periode_terpilih);
                            params.put("satuan",pilih_satuan.getSelectedItem().toString());
                            params.put("luas_panen",et_luas_panen.getText().toString());
                            params.put("satuan_luas_panen",pilih_satuan_panen.getSelectedItem().toString());
                            return params;
                        }
                    };
                    VolleySingleton.getInstance(MenuPenerimaanDana.this).addToRequestQueue(stringRequest);
                }
            }
        });

        et_penerimaan = (EditText) findViewById(R.id.tgl_penerimaan_dana);
        et_penerimaan.setFocusable(false); // disable editing of this field
        et_penerimaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                chooseDate();
            }
        });

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    private void chooseDate() {

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month, final int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        calendar.set(year, month, dayOfMonth);
                        String dateString = sdf.format(calendar.getTime());

                        et_penerimaan.setText(dateString);
                    }
                }, year, month, day);

        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }
}

