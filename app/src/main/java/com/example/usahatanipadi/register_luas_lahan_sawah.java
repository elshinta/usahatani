package com.example.usahatanipadi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.usahatanipadi.model.ModelResponse;
import com.example.usahatanipadi.retrofit.ApiClient;
import com.example.usahatanipadi.retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class register_luas_lahan_sawah extends AppCompatActivity {
    UserSessionManager session;
//    public static String URL_SAVE_NAME = "https://ilkomunila.com/usahatani/register_luas_lahan_sawah.php";

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_luas_lahan_sawah);

        Toolbar toolbar = findViewById(R.id.toolbar_luas_lahan_sawah);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buat Akun");

        db = new DatabaseHelper(this);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        Toast.makeText(getApplicationContext(), nama, Toast.LENGTH_SHORT).show();

        final EditText et_luas_lahan = findViewById(R.id.luas_lahan);
        final EditText et_alamat = findViewById(R.id.alamat_lahan);
        et_luas_lahan.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);


        Button btn = findViewById(R.id.btn_luas_lahan);
        btn.setOnClickListener(new View.OnClickListener() {
            public String id_pengguna;

            @Override
            public void onClick(View v) {

                final Spinner pilih_kepemilikan = findViewById(R.id.pilih_kepemilikan);
                final Spinner sp_satuan = findViewById(R.id.satuan_lahan);
                String luas = et_luas_lahan.getText().toString();
                String alamat = et_alamat.getText().toString();

                Cursor res = db.getData(nama);
                if (res.getCount() == 0) {
                    Toast.makeText(register_luas_lahan_sawah.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                while (res.moveToNext()) {
                    this.id_pengguna = res.getString(0);
                }

                if (!luas.equals("") || !alamat.equals("")) {


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

                    final ProgressDialog progressDialog = new ProgressDialog(register_luas_lahan_sawah.this);
                    progressDialog.setMessage("Menyimpan Data");
                    progressDialog.show();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<ModelResponse> call = apiInterface.registerLuasLahanSawah("sawah_" + nama_pengguna + "_" + id_sawah,
                            nama_pengguna, luas, alamat, pilih_kepemilikan.getSelectedItem().toString(),
                            sp_satuan.getSelectedItem().toString(), "", "");
                    call.enqueue(new Callback<ModelResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<ModelResponse> call, @NotNull retrofit2.Response<ModelResponse> response) {
                            if (response.body() != null) {
                                if (!response.body().getError()){
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

                                    Boolean insert_sawah = db.insert_sawah("sawah_" +nama_pengguna + "_" +
                                                    id_sawah, id_pengguna, luas, alamat,
                                            pilih_kepemilikan.getSelectedItem().toString(),sp_satuan.getSelectedItem().toString(),
                                            "","",1);

                                    if (insert_sawah) {
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil diisi", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Data sawah berhasil dimasukkan ke server", Toast.LENGTH_SHORT).show();
                                        session.createUserLoginSession(nama, "tes");
                                        finish();
                                        startActivity(new Intent(register_luas_lahan_sawah.this, register_data_tersimpan.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Periksa kembali data Anda!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Periksa kembali data Anda2!" , Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ModelResponse> call, @NotNull Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(register_luas_lahan_sawah.this,"Untuk menambah data luas lahan sawah harus terkoneksi dengan internet." + t.getMessage().toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Luas lahan dan alamat harus diisi", Toast.LENGTH_SHORT).show();
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
                    }
                }).create().show();
    }
}


