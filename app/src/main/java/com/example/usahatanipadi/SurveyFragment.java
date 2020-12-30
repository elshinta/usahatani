package com.example.usahatanipadi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyFragment extends Fragment {
    UserSessionManager session;
    public DatabaseHelper db;
    String id_pengguna;
    Boolean insert = false;
    int newData = 0;

    ListView simpleList;
    ListViewMasterSurveyAdapter customAdapter;
    ArrayList<String> listnamasurveyor = new ArrayList<String>();
    ArrayList<String> listkategorisurvey = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view_master_survey_adapter, container, false);

        db = new DatabaseHelper(getActivity());

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        Cursor res = db.getData(nama);
        if (res.getCount() == 0) {
            Toast.makeText(this.getActivity(), "Error tidak diketahui", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            this.id_pengguna = res.getString(0);
        }

        getSurvey(NetworkStateChecker.URL_GET_SURVEY,id_pengguna);

        viewData();

        simpleList = (ListView) view.findViewById(R.id.lv_data_survey);

        customAdapter = new ListViewMasterSurveyAdapter(this.getActivity(), listnamasurveyor, listkategorisurvey,list_id);

        simpleList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editData(list_id.get(position));
            }
        });

        return view;
    }

    private void viewData() {
        list_id.clear();
        listnamasurveyor.clear();
        listkategorisurvey.clear();
        session = new UserSessionManager(this.getActivity());
        HashMap<String, String> user = session.getUserDetails();
        db = new DatabaseHelper(this.getActivity());
        // get nama
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        String id_pengguna;
        final List<String> nama_surveyor = new ArrayList<String>();
        final HashMap<Integer, String> ListViewSurvey = new HashMap<Integer, String>();

        Cursor res = db.getData(nama);

        if (res.getCount() == 0) {
            Toast.makeText(this.getActivity(), "Erorr!", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            int i = 0;
            id_pengguna = res.getString(0);
            Cursor res_survey = db.getDataSurvey(id_pengguna);

            if (res_survey.getCount() == 0) {
                Toast.makeText(this.getActivity(), "Data Survey belum ditambahkan", Toast.LENGTH_SHORT).show();
                return;
            }
            while (res_survey.moveToNext()) {
                ListViewSurvey.put(i, res_survey.getString(0));
                listnamasurveyor.add(res_survey.getString(5));
                listkategorisurvey.add(res_survey.getString(2));
                list_id.add(res_survey.getString(0));
                i++;
            }
        }
    }

    public void editData(String id_survey){
        Intent intent = new Intent(this.getActivity(), JawabanActivity.class);
        intent.putExtra("id_survey", id_survey);
        startActivity(intent);
    }

    private void getSurvey(String url, final String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        //getting json object from the json array
                        JSONObject obj = jsonArray.getJSONObject(i);

                        //memasukkan data ke dalam variable
                        session = new UserSessionManager(getActivity());
                        HashMap<String, String> user = session.getUserDetails();
                        String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                        Cursor res = db.getData(nama_pengguna);

                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(), "Error tidak diketahui", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        while(res.moveToNext()) {
                            String id_pengguna = res.getString(0);
                            String id_survey = obj.getString("id_survey");
                            String jenis_pertanyaan = obj.getString("jenis_pertanyaan");
                            String jumlah_pertanyaan = obj.getString("jumlah_pertanyaan");
                            String id_periode = "";
                            String nama_surveyor = obj.getString("nama_surveyor");

                            insert = db.insert_survey(id_survey,id_pengguna,jenis_pertanyaan,jumlah_pertanyaan,id_periode,nama_surveyor);
                            getPertanyaanSurvey(NetworkStateChecker.URL_GET_PERTANYAAN_SURVEY,id_survey);
                            Log.d("DEBUG",insert.toString());
                            if(insert){
                                newData++;
                            }
                        }
                    }

                    if(newData >0 ){
                        Toast.makeText(getActivity(), "Anda memiliki " + newData + " survey baru", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getActivity(), "Tidak ada survey baru", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public void getPertanyaanSurvey(String url, final String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        //getting json object from the json array
                        JSONObject obj = jsonArray.getJSONObject(i);

                        //memasukkan data ke dalam variable
                        String id_pertanyaan = obj.getString("id_pertanyaan");
                        String pertanyaan_body = obj.getString("pertanyaan_body");

                        insert = db.insert_pertanyaan(id_pertanyaan,id,pertanyaan_body);

                    }

                    if(insert) {
//                        Toast.makeText(context, "Anda memiliki survey baru", Toast.LENGTH_SHORT).show();
                    }
                    else{
//                        Toast.makeText(context, "Tidak ada survey baru", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
