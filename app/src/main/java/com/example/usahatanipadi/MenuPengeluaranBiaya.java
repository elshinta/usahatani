package com.example.usahatanipadi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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

public class MenuPengeluaranBiaya extends AppCompatActivity { //deklarasi variabel dan method
    EditText et_tgl_pengeluaran_biaya;
    UserSessionManager session;
    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/pengeluaran_biaya.php";

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
        setContentView(R.layout.activity_menu_pengeluaran_biaya);

        Toolbar toolbar = (Toolbar)findViewById(R.id.menu_pengeluaran_biaya);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pengeluaran Biaya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //untuk kembali ke halaman sebelumnya

        et_tgl_pengeluaran_biaya = (EditText)findViewById(R.id.tgl_pengeluaran_biaya);
        et_tgl_pengeluaran_biaya.setFocusable(false);
        et_tgl_pengeluaran_biaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                chooseDate();
            }
        });

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this);
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        String id_pengguna;
        final List<String> sawah = new ArrayList<String>();
        final List<String> kebutuhan_tanam = new ArrayList<String>();
        final HashMap<Integer, String> spinnerSawah = new HashMap<Integer, String>();
        final HashMap<Integer, String> spinnerKebutuhanTanam = new HashMap<Integer, String>();

        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");
        TextView textView = (TextView)findViewById(R.id.nama_sawah);
        TextView textView_periode = (TextView)findViewById(R.id.periode_sawah);

        Cursor res_sawah = db.getIdSawah(sawah_terpilih);
        if (res_sawah.getCount() == 0) {
            Toast.makeText(this, "Erorr!", Toast.LENGTH_SHORT).show();
        }
        else {
            res_sawah.moveToFirst(); //posisi pointer kursor pada record pertama
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

        Cursor res = db.getData(nama);

        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
            return;
        }
        while (res.moveToNext()) {
            int j = 0;
            id_pengguna = res.getString(0);

            Cursor res_kebutuhan_tanam = db.getDataKebutuhanTanam(nama);
            if (res_kebutuhan_tanam.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Erorr!", Toast.LENGTH_SHORT);
                return;
            }
            while (res_kebutuhan_tanam.moveToNext()) {
                kebutuhan_tanam.add(res_kebutuhan_tanam.getString(1));
                spinnerKebutuhanTanam.put(j, res_kebutuhan_tanam.getString(0));
                j++;
            }
        }

        final Spinner pilih_kebutuhan_tanam = (Spinner) findViewById(R.id.pilih_kebutuhan_tanam);
        ArrayAdapter arrayAdapter_pilih_kebutuhan_tanam = new ArrayAdapter(this, R.layout.spinner_text, kebutuhan_tanam);
        arrayAdapter_pilih_kebutuhan_tanam.setDropDownViewResource(R.layout.spinner_dropdown);
        pilih_kebutuhan_tanam.setAdapter(arrayAdapter_pilih_kebutuhan_tanam);

        final EditText et_jumlah_barang_jasa = (EditText) findViewById(R.id.jumlah_barang_jasa);
        final EditText et_total_barang_jasa = (EditText) findViewById(R.id.total_barang_jasa);
        final EditText et_harga = (EditText) findViewById(R.id.harga);
        final EditText et_nama_pemasok = (EditText) findViewById(R.id.nama_pemasok);
        final EditText et_catatan_pengeluaran = (EditText) findViewById(R.id.catatan_pengeluaran);
        final Spinner pilih_satuan = (Spinner)findViewById(R.id.satuan_jumlah_jenis_barang);
        final TextView nama_pemasok = (TextView)findViewById(R.id.tv_nama_pemasok);
        et_jumlah_barang_jasa.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);


        //Memberi tulisan Rp dan nominal untuk Harga per Kg pembelian barang/jasa (Pengeluaran)
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
                else{
                    konversi_satuan2 = Float.parseFloat(total)*1;
                }

                float total_harga;
                total_harga = Float.parseFloat(et_jumlah_barang_jasa.getText().toString()) * konversi_satuan2;


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

                et_total_barang_jasa.setText("Rp " + String.valueOf(number));
            }
        });

        et_total_barang_jasa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                et_total_barang_jasa.removeTextChangedListener(this);

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
                    et_total_barang_jasa.setText("Rp " + formattedString);
                    et_total_barang_jasa.setSelection(et_total_barang_jasa.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                et_total_barang_jasa.addTextChangedListener(this);
            }
        });

        Button btn = (Button)findViewById(R.id.btn_simpan_pengeluaran_biaya);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = "";
                String id_kebutuhan_tanam = spinnerKebutuhanTanam.get(pilih_kebutuhan_tanam.getSelectedItemPosition());
                String replace_harga;

                replace_harga = et_total_barang_jasa.getText().toString();
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

                if (et_tgl_pengeluaran_biaya.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Tanggal transaksi harus diisi", Toast.LENGTH_SHORT).show();
                } else if (et_jumlah_barang_jasa.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Jumlah harus diisi", Toast.LENGTH_SHORT).show();
                } else if (et_total_barang_jasa.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Total Harga harus diisi", Toast.LENGTH_SHORT).show();
                } else {

                    session = new UserSessionManager(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                    final ProgressDialog progressDialog = new ProgressDialog(MenuPengeluaranBiaya.this);
                    progressDialog.setMessage("Menyimpan Data");
                    progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String id_kebutuhan_tanam = spinnerKebutuhanTanam.get(pilih_kebutuhan_tanam.getSelectedItemPosition());
                                String replace_harga;

                                replace_harga = et_total_barang_jasa.getText().toString();
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

                                        session = new UserSessionManager(getApplicationContext());
                                        HashMap<String, String> user = session.getUserDetails();
                                        String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                        int id_pengeluaran_biaya;

                                        Cursor res_pengeluaran_biaya = db.getIDPengeluaran();
                                        if (res_pengeluaran_biaya.getCount() == 0) {
                                            id_pengeluaran_biaya = 1;
                                        } else  {
                                            id_pengeluaran_biaya = res_pengeluaran_biaya.getCount()+1;
                                        }

                                        //if there is a success
                                        //storing the name to sqlite with status synced
                                        Boolean insert_pengeluaran = db.insert_pengeluaran_biaya("pengeluaran_" +nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya),sawah_terpilih, id_kebutuhan_tanam, et_jumlah_barang_jasa.getText().toString(), replace_harga, et_nama_pemasok.getText().toString(), et_tgl_pengeluaran_biaya.getText().toString(), et_catatan_pengeluaran.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"1");
                                        if (insert_pengeluaran) {
                                            Toast.makeText(getApplicationContext(), "Data pengeluaran biaya berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data pengeluaran biaya  masuk dalam server", Toast.LENGTH_SHORT).show();
                                            finish();
                                            Intent intent = new Intent(MenuPengeluaranBiaya.this, MenuPengeluaranBiayaTersimpan.class);
                                            intent.putExtra("sawah_terpilih", sawah_terpilih);
                                            intent.putExtra("periode_terpilih", periode_terpilih);
                                            intent.putExtra("id_pengeluaran","pengeluaran_" + nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya));
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal memasukkan data penerimaan dana", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        //if there is some error
                                        //saving the name to sqlite with status unsynced

                                        session = new UserSessionManager(getApplicationContext());
                                        HashMap<String, String> user = session.getUserDetails();
                                        String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                        int id_pengeluaran_biaya;

                                        Cursor res_pengeluaran_biaya = db.getIDPengeluaran();
                                        if (res_pengeluaran_biaya.getCount() == 0) {
                                            id_pengeluaran_biaya = 1;
                                        } else  {
                                            id_pengeluaran_biaya = res_pengeluaran_biaya.getCount()+1;
                                        }

                                        Boolean insert_pengeluaran = db.insert_pengeluaran_biaya("pengeluaran_" +nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya),sawah_terpilih, id_kebutuhan_tanam, et_jumlah_barang_jasa.getText().toString(), replace_harga, et_nama_pemasok.getText().toString(), et_tgl_pengeluaran_biaya.getText().toString(), et_catatan_pengeluaran.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"0");
                                        if (insert_pengeluaran) {
                                            Toast.makeText(getApplicationContext(), "Data pengeluaran biaya berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Data pengeluaran biaya  tidak masuk dalam server", Toast.LENGTH_SHORT).show();
                                            finish();
                                            Intent intent = new Intent(MenuPengeluaranBiaya.this, MenuPengeluaranBiayaTersimpan.class);
                                            intent.putExtra("sawah_terpilih", sawah_terpilih);
                                            intent.putExtra("periode_terpilih", periode_terpilih);
                                            intent.putExtra("id_pengeluaran", "pengeluaran_" + nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya));
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal memasukkan data pengeluaran biaya", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String id_kebutuhan_tanam = spinnerKebutuhanTanam.get(pilih_kebutuhan_tanam.getSelectedItemPosition());
                                String replace_harga;

                                replace_harga = et_total_barang_jasa.getText().toString();
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

                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_pengeluaran_biaya;

                                Cursor res_pengeluaran_biaya = db.getIDPengeluaran();
                                if (res_pengeluaran_biaya.getCount() == 0) {
                                    id_pengeluaran_biaya = 1;
                                } else  {
                                    id_pengeluaran_biaya = res_pengeluaran_biaya.getCount()+1;
                                }

                                Boolean insert_pengeluaran = db.insert_pengeluaran_biaya("pengeluaran_" +nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya),sawah_terpilih, id_kebutuhan_tanam, et_jumlah_barang_jasa.getText().toString(), replace_harga, et_nama_pemasok.getText().toString(), et_tgl_pengeluaran_biaya.getText().toString(), et_catatan_pengeluaran.getText().toString(), periode_terpilih, pilih_satuan.getSelectedItem().toString(),"0");
                                if (insert_pengeluaran) {
                                    Toast.makeText(getApplicationContext(), "Data pengeluaran biaya berhasil dimasukkan", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Data tersimpan offline", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(MenuPengeluaranBiaya.this, MenuPengeluaranBiayaTersimpan.class);
                                    intent.putExtra("sawah_terpilih", sawah_terpilih);
                                    intent.putExtra("periode_terpilih", periode_terpilih);
                                    intent.putExtra("id_pengeluaran", "pengeluaran_" + nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal memasukkan data pengeluaran biaya", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                session = new UserSessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                                int id_pengeluaran_biaya;

                                Cursor res_pengeluaran_biaya = db.getIDPengeluaran();
                                if (res_pengeluaran_biaya.getCount() == 0) {
                                    id_pengeluaran_biaya = 1;
                                } else  {
                                    id_pengeluaran_biaya = res_pengeluaran_biaya.getCount()+1;
                                }

                                String id_kebutuhan_tanam = spinnerKebutuhanTanam.get(pilih_kebutuhan_tanam.getSelectedItemPosition());
                                String replace_harga;

                                replace_harga = et_total_barang_jasa.getText().toString();
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
                                params.put("id_pengeluaran_biaya", "pengeluaran_" + nama_pengguna + "_" + String.valueOf(id_pengeluaran_biaya));
                                params.put("id_lahan_sawah",sawah_terpilih);
                                params.put("id_kebutuhan_tanam",id_kebutuhan_tanam);
                                params.put("jumlah",et_jumlah_barang_jasa.getText().toString());
                                params.put("total_harga",replace_harga);
                                params.put("nama_pemasok",et_nama_pemasok.getText().toString());
                                params.put("tanggal_pengeluaran_biaya",et_tgl_pengeluaran_biaya.getText().toString());
                                params.put("catatan",et_catatan_pengeluaran.getText().toString());
                                params.put("id_periode",periode_terpilih);
                                params.put("satuan",pilih_satuan.getSelectedItem().toString());

                                return params;
                            }
                        };

                        VolleySingleton.getInstance(MenuPengeluaranBiaya.this).addToRequestQueue(stringRequest);
                }
            }
        });

        pilih_kebutuhan_tanam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==8 || position==9 || position==10 || position==11 || position==12 || position==13) {
                    et_nama_pemasok.setVisibility(View.GONE);
                    nama_pemasok.setVisibility(View.GONE);
                }
                else {
                    et_nama_pemasok.setVisibility(View.VISIBLE);
                    nama_pemasok.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

                        et_tgl_pengeluaran_biaya.setText(dateString);
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
