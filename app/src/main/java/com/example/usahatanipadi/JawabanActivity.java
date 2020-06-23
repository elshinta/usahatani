package com.example.usahatanipadi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JawabanActivity extends AppCompatActivity {
    UserSessionManager session;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jawaban);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_jawaban_survey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pertanyaan Survey");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        final String id_survey = intent.getStringExtra("id_survey");
//        Toast.makeText(this,id_survey, Toast.LENGTH_SHORT).show();

        // inisiasi layout
        LinearLayout layout=(LinearLayout) findViewById(R.id.layout_survey);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        params2.setMargins(0, R.dimen.margin_textview, 0, 0);

        db = new DatabaseHelper(this);
        Cursor res = db.getDataPertanyaan(id_survey);
        if (res.getCount() == 0) {
            Toast.makeText(this, "Maaf, belum ada pertanyaan yang ditambahkan", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            int i = 0;
            TextView[] tv_pertanyaan_survey = new TextView[res.getCount()];
            TextView[] et_jawaban_survey = new TextView[res.getCount()];

            tv_pertanyaan_survey[i] = new TextView(this);
            et_jawaban_survey[i] = new EditText(this);
            tv_pertanyaan_survey[i].setText(res.getString(2));
            tv_pertanyaan_survey[i].setTextColor(Color.rgb(0,0,0));

            et_jawaban_survey[i].setHint("Masukkan Jawaban Anda");
            et_jawaban_survey[i].setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            et_jawaban_survey[i].setId(i);

            layout.addView(tv_pertanyaan_survey[i]);
            layout.addView(et_jawaban_survey[i]);
            i++;

        }



    }
}
