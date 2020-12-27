package com.example.usahatanipadi;

import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MenuUtama extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    UserSessionManager session;
    public DatabaseHelper db;
    public static String BENIH = "";
    public static String HAMA = "";
    public static String PENGENDALIANHAMA = "";
    public static TextView surveyNotif;
    static String COUNT_SURVEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);
        Log.d("Token ", FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices"); // don't forget change to allDevices
        // untuk mendaftarkan sinkronisasi dari NETWORKSTATECHECKER
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_menu_utama);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Halaman Utama");
        session = new UserSessionManager(getApplicationContext());

        // get nama
        HashMap<String, String> user = session.getUserDetails();
        String nama = user.get(UserSessionManager.KEY_NAMA);
        db = new DatabaseHelper(this);

        Cursor res = db.getData(nama);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        surveyNotif=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_survey));
        initializeCountDrawer();

        try {
            if (res.getCount() == 0) {
                Toast.makeText(this, "Erorr!", Toast.LENGTH_SHORT).show();
            }
            res.moveToNext();
            final String nama_usahatani = res.getString(1);
            View hView =  navigationView.getHeaderView(0);
            TextView nav_user = (TextView)hView.findViewById(R.id.drawer_nama_usahatani);
            nav_user.setText(nama_usahatani);

        } catch (Exception e) {
            e.printStackTrace();
        }

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);

        if(session.checkLogin())
            finish();

        Toast.makeText(getApplicationContext(), nama,Toast.LENGTH_SHORT).show();

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        String menuFragment = getIntent().getStringExtra("menuFragment");

        if (menuFragment != null) {
            if (menuFragment.equals("surveyFragment")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SurveyFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_survey);
                getSupportActionBar().setTitle("Survey");
            }
        } else{
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MenuUtamaFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_catat_keuangan);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_catat_keuangan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuUtamaFragment()).commit();
                getSupportActionBar().setTitle("Kelola Keuangan Taniku");
                break;
            case R.id.nav_master_data:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MasterDataFragment()).commit();
                getSupportActionBar().setTitle("Master Data");
                break;
            case R.id.nav_edukasi:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EdukasiFragment()).commit();
                getSupportActionBar().setTitle("Informasi");

                try{
                    InputStream is_benih = getAssets().open("benih_baik.txt");
                    int size = is_benih.available();
                    byte[] buffer = new byte[size];
                    is_benih.read(buffer);
                    is_benih.close();
                    BENIH = new String(buffer);

                    InputStream is_hama = getAssets().open("hama_penyakit.txt");
                    int size_hama = is_hama.available();
                    byte[] buffer2 = new byte[size_hama];
                    is_hama.read(buffer2);
                    is_hama.close();
                    HAMA = new String(buffer2);

                    InputStream is_pengendalian_hama = getAssets().open("pengendalian_hama.txt");
                    int size_pengendalian_hama = is_pengendalian_hama.available();
                    byte[] buffer3 = new byte[size_pengendalian_hama];
                    is_pengendalian_hama.read(buffer3);
                    is_pengendalian_hama.close();
                    PENGENDALIANHAMA = new String(buffer3);
                } catch (IOException ex){
                    ex.printStackTrace();
                }
                break;
            case R.id.nav_survey:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SurveyFragment()).commit();
                getSupportActionBar().setTitle("Survey");break;
            case R.id.nav_info_pengguna:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InfoPenggunaFragment()).commit();
                getSupportActionBar().setTitle("Tentang Pengguna");
                break;
            case R.id.nav_info_aplikasi:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TentangAplikasiFragment()).commit();
                getSupportActionBar().setTitle("Tentang Aplikasi");
                break;
            case R.id.nav_keluar:
                session.logoutUser();
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeCountDrawer(){
        db = new DatabaseHelper(MenuUtama.this);
        String id_pengguna;
        HashMap<String, String> user = session.getUserDetails();
        final String nama = user.get(UserSessionManager.KEY_NAMA);
        Cursor res = db.getData(nama);
        if (res.getCount() == 0) {
            Toast.makeText(MenuUtama.this, "Erorr!", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            id_pengguna = res.getString(0);

            Cursor res_count_survey = db.countDataSurvey(id_pengguna);
            if (res_count_survey.getCount() == 0) {
                Toast.makeText(MenuUtama.this, "Something error", Toast.LENGTH_SHORT).show();
                return;
            }
            while (res_count_survey.moveToNext()) {
                COUNT_SURVEY = res_count_survey.getString(0);
            }
        }

        //Gravity property aligns the text
        surveyNotif.setGravity(Gravity.CENTER_VERTICAL);
        surveyNotif.setTypeface(null, Typeface.BOLD);
        surveyNotif.setTextColor(getResources().getColor(R.color.colorAccent));
        surveyNotif.setText(COUNT_SURVEY);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
            moveTaskToBack(true);
        }
    }
}
