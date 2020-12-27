package com.example.usahatanipadi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JawabanActivity extends AppCompatActivity {
    UserSessionManager session;
    DatabaseHelper db;
    List<EditText> allEds = new ArrayList<EditText>();
    String id_pengguna;
    JSONArray arrayJawaban = new JSONArray();
    public static String URL_SAVE_JAWABAN = "https://ilkomunila.com/usahatani/sendJawabanSurvey.php";

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

        db = new DatabaseHelper(this);
        session = new UserSessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

        Cursor res_pengguna = db.getData(nama_pengguna);

        if (res_pengguna.getCount() == 0) {
            Toast.makeText(this, "Error no user!", Toast.LENGTH_SHORT).show();
        }
        while (res_pengguna.moveToNext()) {
            id_pengguna = res_pengguna.getString(0);
        }

//        Log.d("id_survey",id_survey);

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
        Intent intent = getIntent();
        final String id_survey = intent.getStringExtra("id_survey");
        Cursor res = db.getDataPertanyaan(id_survey);
        if (res.getCount() == 0) {
            Toast.makeText(this, "Maaf, belum ada pertanyaan yang ditambahkan", Toast.LENGTH_SHORT).show();
            return;
        }
        int i = 0;
        while (res.moveToNext()) {
            TextView[] tv_pertanyaan_survey = new TextView[res.getCount()];

            tv_pertanyaan_survey[i] = new TextView(this);
            EditText et_jawaban_survey = new EditText(this);
            allEds.add(et_jawaban_survey);

            tv_pertanyaan_survey[i].setText(res.getString(2));
            tv_pertanyaan_survey[i].setTextColor(Color.rgb(0,0,0));

            et_jawaban_survey.setHint("Masukkan Jawaban Anda");
            et_jawaban_survey.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            et_jawaban_survey.setId(i);

            layout.addView(tv_pertanyaan_survey[i]);
            layout.addView(et_jawaban_survey);
            i++;
        }

        submitJawaban();
    }

    private void submitJawaban(){
        Button btn = findViewById(R.id.btn_simpan_survey);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    int i = 0;
                    db = new DatabaseHelper(JawabanActivity.this);

                    Intent intent = getIntent();
                    final String id_survey = intent.getStringExtra("id_survey");

                    Cursor res = db.getDataPertanyaan(id_survey);
                    if (res.getCount() == 0) {
                        Toast.makeText(JawabanActivity.this, "Maaf, belum ada pertanyaan yang ditambahkan", Toast.LENGTH_SHORT).show();
                        return;
                    } while (res.moveToNext()) {
                        Date date = new Date();
                        Log.d("DEBUG", String.valueOf(i));
                        Log.d("DEBUG", allEds.get(i).getText().toString());
                        JSONObject dataJawaban = new JSONObject();
                        dataJawaban.put("id_pertanyaan",res.getString(0));
                        dataJawaban.put("jawaban_body",allEds.get(i).getText().toString());
                        dataJawaban.put("id_pengguna",id_pengguna);
                        dataJawaban.put("updated_at",formatter.format(date));
                        arrayJawaban.put(dataJawaban);
                        i++;
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(JawabanActivity.this);
                    progressDialog.setMessage("Menngirim jawaban");
                    progressDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_JAWABAN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    progressDialog.dismiss();
                                    Toast.makeText(JawabanActivity.this, "berhasil mengirim jawaban", Toast.LENGTH_SHORT).show();

                                    //if there is a success
                                    // insert jawaban survey to the phone
                                    for(int j = 0;j<arrayJawaban.length();j++){
                                        JSONObject jawabanSingle = arrayJawaban.getJSONObject(j);
                                        String id_pertanyaan = jawabanSingle.getString("id_pertanyaan");
                                        String jawaban_body = jawabanSingle.getString("jawaban_body");
                                        String updated_at = jawabanSingle.getString("updated_at");

//                                        Log.d("DEBUG",jawaban_body);
                                        db.insert_jawaban(id_pengguna,id_pertanyaan,id_survey,jawaban_body,updated_at);

                                    }

                                } else {
                                    Toast.makeText(JawabanActivity.this, "Gagal mengirim jawaban", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(JawabanActivity.this,"Terjadi kesalahan saat mengirim data.", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("json", arrayJawaban.toString());

                            return params;
                        }
                    };
                    VolleySingleton.getInstance(JawabanActivity.this).addToRequestQueue(stringRequest);

                    finish();
                    int count = Integer.parseInt(MenuUtama.COUNT_SURVEY);
                    count--;
                    MenuUtama.COUNT_SURVEY = String.valueOf(count);
                    MenuUtama.surveyNotif.setText(MenuUtama.COUNT_SURVEY);
                    Intent intent2 = new Intent(JawabanActivity.this, MenuUtama.class);
                    intent2.putExtra("menuFragment", "surveyFragment");
                    startActivity(intent2);

//                    Log.d("DEBUG", arrayJawaban.toString());
                } catch (Exception e){
                    Log.d("DEBUG",e.toString());
                }

            }
        });
    }
}
