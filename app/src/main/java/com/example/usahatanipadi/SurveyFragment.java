package com.example.usahatanipadi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyFragment extends Fragment {
    UserSessionManager session;
    public DatabaseHelper db;
    String id_pengguna;

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
            Toast.makeText(this.getActivity(), "Maaf, belum ada survey baru yang tersedia untuk periode ini", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            this.id_pengguna = res.getString(0);
        }

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
                Toast.makeText(this.getActivity(), "Erorr!", Toast.LENGTH_SHORT).show();
                return;
            }
            while (res_survey.moveToNext()) {
                nama_surveyor.add(res_survey.getString(1));
                ListViewSurvey.put(i, res_survey.getString(0));
                i++;
                listnamasurveyor.add(res_survey.getString(1));
                listkategorisurvey.add(res_survey.getString(2));
                list_id.add(res_survey.getString(0));
            }
        }
    }

    public void editData(String id_survey){
        Intent intent = new Intent(this.getActivity(), JawabanActivity.class);
        intent.putExtra("id_survey", id_survey);
        startActivity(intent);
    }
}
