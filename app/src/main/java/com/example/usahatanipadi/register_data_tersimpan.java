package com.example.usahatanipadi;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class register_data_tersimpan extends AppCompatActivity {
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data_tersimpan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_data_tersimpan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Tersimpan");

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        TextView tv_nama_pengguna = (TextView) findViewById(R.id.informasi_nama_pengguna);
        tv_nama_pengguna.setText("Nama Pengguna: " + nama);

        Button btn = (Button) findViewById(R.id.btn_mulai);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register_data_tersimpan.this, MenuUtama.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(register_data_tersimpan.this, MenuUtama.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
