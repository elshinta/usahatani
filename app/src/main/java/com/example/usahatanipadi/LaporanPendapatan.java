package com.example.usahatanipadi;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LaporanPendapatan extends AppCompatActivity {
    DatabaseHelper db;
    int total_biaya = 0;
    int pendapatan_tunai = 0;
    int pendapatan_diperhitungkan = 0;
    float rc_tunai = 0f;
    float rc_total = 0f;
    int total_biaya_tunai = 0;
    int total_biaya_diperhitungkan = 0;
    UserSessionManager session;
    public static String URL_EDIT_NAME = "https://ilkomunila.com/usahatani/tutup_periode.php";
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.usahatani.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_pendapatan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pilih_periode_laporan_pendapatan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Laporan Pendapatan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //untuk kembali ke halaman sebelumnya
        db = new DatabaseHelper(this);

        TextView tv_tot_biaya_total = (TextView) findViewById(R.id.tv_tot_biaya_total);
        TextView tv_tot_pendapatan_tunai = (TextView) findViewById(R.id.tv_tot_pendapatan_tunai);
        TextView tv_tot_pendapatan_diperhitungkan = (TextView) findViewById(R.id.tv_tot_pendapatan_diperhitungkan);
        TextView tv_rc_tunai = (TextView) findViewById(R.id.tv_tot_rc_tunai);
        TextView tv_rc_total = (TextView) findViewById(R.id.tv_tot_rc_total);

        viewDataPenerimaan();
        viewDataPengeluaran();
        viewDataBiayaDiperhitungkan();

        DecimalFormat formatter = new DecimalFormat("#,###");
        String format_tot_biaya_total = formatter.format(this.getTotalBiaya());
        String format_pendapatan_tunai = formatter.format(this.getPendapatanTunai());
        String format_pendapatan_total = formatter.format(this.getPendapatanTotal());

        this.setRC_tunai(this.getRCTunai() / this.getTotalBiayaTunai());
        this.setRC_total(this.getRCTotal() / this.getTotalBiaya());

        DecimalFormat df = new DecimalFormat("#.##");

        String format_rc_tunai = df.format(this.getRCTunai());
        String format_rc_total = df.format(this.getRCTotal());

        tv_tot_biaya_total.setText("Rp. " + format_tot_biaya_total);
        tv_tot_pendapatan_tunai.setText("Rp. " + format_pendapatan_tunai);
        tv_tot_pendapatan_diperhitungkan.setText("Rp. " + format_pendapatan_total);
        tv_rc_tunai.setText("Rp. " + format_rc_tunai);
        tv_rc_total.setText("Rp. " + format_rc_total);

        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    public Integer getTotalBiaya() {
        return total_biaya;
    }

    public void setTotalBiaya(int total_biaya) {
        this.total_biaya = total_biaya;
    }

    public Integer getPendapatanTunai() {
        return pendapatan_tunai;
    }

    public void setPendapatanTunai(int pendapatan_tunai) {
        this.pendapatan_tunai = pendapatan_tunai;
    }

    public Integer getPendapatanTotal() {
        return pendapatan_diperhitungkan;
    }

    public void setPendapatanTotal(int pendapatan_diperhitungkan) {
        this.pendapatan_diperhitungkan = pendapatan_diperhitungkan;
    }

    public Float getRCTunai() {
        return rc_tunai;
    }

    public void setRC_tunai(float rc_tunai) {
        this.rc_tunai = rc_tunai;
    }

    public Float getRCTotal() {
        return rc_total;
    }

    public void setRC_total(float rc_total) {
        this.rc_total = rc_total;
    }

    public void setTotalBiayaTunai(int total_biaya_tunai){
        this.total_biaya_tunai = total_biaya_tunai;
    }

    public int getTotalBiayaTunai(){
        return total_biaya_tunai;
    }

    public void setTotalBiayaDiperhitungkan(int total_biaya_diperhitungkan){
        this.total_biaya_diperhitungkan = total_biaya_diperhitungkan;
    }

    public int getTotalBiayaDiperhitungkan(){
        return total_biaya_diperhitungkan;
    }

    //---MENU ITEM---//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.export_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    //---MENU ITEM (PILIH EXPORT/UNDUH)---//

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.export:
                try {
                    export();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tutup_periode:
                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final String bulan;
                Intent intent2 = getIntent();
                final String sawah_terpilih = intent2.getStringExtra("sawah");
                final String periode_terpilih = intent2.getStringExtra("periode");

                if(month+1==1){
                    bulan="Januari";
                }
                else if(month+1==2){
                    bulan="Februari";
                }
                else if(month+1==3){
                    bulan="Maret";
                }
                else if(month+1==4){
                    bulan="April";
                }

                else if(month+1==5){
                    bulan="Mei";
                }

                else if(month+1==6){
                    bulan="Juni";
                }

                else if(month+1==7){
                    bulan="Juli";
                }

                else if(month+1==8){
                    bulan="Agustus";
                }

                else if(month+1==9){
                    bulan="September";
                }

                else if(month+1==10){
                    bulan="Oktober";
                }

                else if(month+1==11){
                    bulan="November";
                }

                else {
                    bulan="Desember";
                }

                session = new UserSessionManager(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                final String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_NAME,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        //updating the status in sqlite
                                        db.setPeriodeAkhir(periode_terpilih,sawah_terpilih,bulan,Integer.toString(year));

                                        finish();

                                        Intent intent1 = new Intent(LaporanPendapatan.this, LaporanPendapatan.class);
                                        intent1.putExtra("sawah", sawah_terpilih);
                                        intent1.putExtra("periode", periode_terpilih);
                                        startActivity(intent1);
                                    }
                                    else{
                                        db.setPeriodeAkhir(periode_terpilih,sawah_terpilih,bulan,Integer.toString(year));

                                        finish();

                                        Intent intent1 = new Intent(LaporanPendapatan.this, LaporanPendapatan.class);
                                        intent1.putExtra("sawah", sawah_terpilih);
                                        intent1.putExtra("periode", periode_terpilih);
                                        startActivity(intent1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LaporanPendapatan.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("bulan_periode_akhir", bulan);
                        params.put("tahun_periode_akhir", Integer.toString(year));
                        params.put("id_periode", periode_terpilih);
                        return params;
                    }
                };

                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

                break;
            case R.id.halaman_awal:
                finish();
                Intent intent = new Intent(this, MenuUtama.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     *
     * LIBRARY UNTUK EXPORT PDF
     * @throws FileNotFoundException
     * @throws DocumentException
     */

    public void export() throws FileNotFoundException, DocumentException {
        Document document = new Document();
        Intent intent2 = getIntent();
        final String sawah_terpilih = intent2.getStringExtra("sawah");
        final String periode_terpilih = intent2.getStringExtra("periode");


        Paragraph paragraph = new Paragraph();
        Paragraph paragraph2 = new Paragraph();
        Paragraph luas_tabel1 = new Paragraph();
        Paragraph luas_tabel2 = new Paragraph();
        Paragraph paragraph_luas_panen = new Paragraph();

        Cursor res_sawah = db.getIdSawah(sawah_terpilih);

        while (res_sawah.moveToNext()) {
            Cursor res_periode = db.getDataIdPeriode(periode_terpilih);

            while (res_periode.moveToNext()) {
                paragraph.add(res_sawah.getString(3));
                luas_tabel1.add("Luas Lahan (" + res_sawah.getString(2) + " " + res_sawah.getString(5) + ")");

                if(res_sawah.getString(5).equals("Rante")){
                    Float luas = res_sawah.getFloat(2)/25;
                    luas_tabel2.add("Luas Lahan (" +  String.valueOf(luas)+ " Ha)");
                }
                else if(res_sawah.getString(5).equals("Bau")){
                    Float luas = res_sawah.getFloat(2)/8;
                    luas_tabel2.add("Luas Lahan (" +  String.valueOf(luas)+ " Ha)");
                }
                else{
                    Float luas = res_sawah.getFloat(2);
                    luas_tabel2.add("Luas Lahan (" +  String.valueOf(luas)+ " Ha)");
                }

                paragraph2.add(res_periode.getString(2) + " " + res_periode.getString(4) + " - " + res_periode.getString(3) + " " + res_periode.getString(5));
            }
        }

        /*
         * Tabel 1 adalah tabel yang berisi data satuan real sesuai dengan satuan yang diinputkan
         * */

        Paragraph paragraph_tabel1 = new Paragraph();
        paragraph_tabel1.add("Tabel 1. Tabel yang berisi data satuan real sesuai yang diinputkan");

        PdfPTable table = new PdfPTable(new float[]{0.8f, 3, 1.2f, 2, 2, 2});
        PdfPTable table2 = new PdfPTable(new float[]{0.8f, 3, 1.2f, 2, 2, 2});

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("No.");
        table.addCell("Uraian");
        table.addCell("Satuan");
        table.addCell("Jumlah");
        table.addCell("Harga(Rp)");
        table.addCell("Nilai(Rp)");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }

        Font bold = new Font(Font.FontFamily.HELVETICA,12, Font.BOLD);
        Phrase p = new Phrase("1", bold);
        Phrase q = new Phrase("Penerimaan Dana", bold);

        table.addCell(p);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(q);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");

        int tot_penjualan=0;
        int k=0;

        Cursor res_penerimaan = db.getDataPenerimaanHistori(sawah_terpilih, periode_terpilih);

        while (res_penerimaan.moveToNext()) {
            Cursor res_hasil_panen = db.getIdHasilPanen(res_penerimaan.getString(2));

            if (res_hasil_panen != null) {
                if (res_hasil_panen.moveToFirst()) {
                    String hasil_panen = res_hasil_panen.getString(1);

                    float konversi_satuan=1;
                    if(res_penerimaan.getString(9).equals("ton")) {
                        konversi_satuan = Float.parseFloat(res_penerimaan.getString(3))*1000;
                    }
                    else if(res_penerimaan.getString(9).equals("kuintal")){
                        konversi_satuan = Float.parseFloat(res_penerimaan.getString(3))*100;
                    }
                    else if(res_penerimaan.getString(9).equals("kg")){
                        konversi_satuan = Float.parseFloat(res_penerimaan.getString(3))*1;
                    }

                    int nilai = res_penerimaan.getInt(4
                    );
                    int harga = (int) (nilai/konversi_satuan);

                    DecimalFormat formatter = new DecimalFormat("#,###");
                    String format_harga_penjualan = formatter.format(harga);
                    String format_harga_nilai = formatter.format(nilai);

                    k++;

                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell("");
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(hasil_panen);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell("kg");
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(Float.toString(konversi_satuan));
                    table.addCell(format_harga_penjualan);
                    table.addCell(format_harga_nilai);

                    tot_penjualan += Integer.parseInt(res_penerimaan.getString(4));
                    paragraph_luas_panen.add("(Luas Panen " + res_hasil_panen.getString(1) + " : " + res_penerimaan.getString(11) + " " + res_penerimaan.getString(12) + ") ");
                }
            }
        }

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        //---TAMPILAN PDF PENGELUARAN BIAYA---//

        Phrase p2 = new Phrase("2", bold);
        Phrase q2 = new Phrase("Pengeluaran Biaya", bold);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(p2);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(q2);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");


        Phrase qq1 = new Phrase("I. Biaya Tunai", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qq1);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");

        int tot_biaya_tunai = 0;

        Cursor res_biaya_tunai = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);
        while (res_biaya_tunai.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_tunai.getString(2));

            if (res_kebutuhan_tanam != null) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (!res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") && !res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        float konversi_satuan2=1;
                        if(res_biaya_tunai.getString(9).equals("ton")) {
                            konversi_satuan2 = Float.parseFloat(res_biaya_tunai.getString(3))*1000;
                        }
                        else if(res_biaya_tunai.getString(9).equals("kuintal")){
                            konversi_satuan2 = Float.parseFloat(res_biaya_tunai.getString(3))*100;
                        }
                        else if(res_biaya_tunai.getString(9).equals("kg")){
                            konversi_satuan2 = Float.parseFloat(res_biaya_tunai.getString(3))*1;
                        }
                        else if(res_biaya_tunai.getString(9).equals("liter") || res_biaya_tunai.getString(9).equals("Rp") || res_biaya_tunai.getString(9).equals("HOK")) {
                            konversi_satuan2 = Float.parseFloat(res_biaya_tunai.getString(3))*1;
                        }

                        int nilai = res_biaya_tunai.getInt(4);
                        int harga = (int) (nilai/konversi_satuan2);

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String format_harga_biaya_tunai = formatter.format(harga);
                        String format_harga_nilai = formatter.format(nilai);


                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell("");
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(kebutuhan_tanam);
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        if(res_biaya_tunai.getString(9).equals("liter") || res_biaya_tunai.getString(9).equals("Rp") || res_biaya_tunai.getString(9).equals("Rp") || res_biaya_tunai.getString(9).equals("HOK")){
                            table.addCell(res_biaya_tunai.getString(9));
                        }
                        else{
                            table.addCell("kg");
                        }
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(Float.toString(konversi_satuan2));
                        table.addCell(format_harga_biaya_tunai);
                        table.addCell(format_harga_nilai);

                        tot_biaya_tunai += Integer.parseInt(res_biaya_tunai.getString(4));
                        this.setTotalBiayaTunai(tot_biaya_tunai);
                    }
                }
            }
        }

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qq2 = new Phrase("Total Biaya Tunai", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qq2);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Rp");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        DecimalFormat formatter_biaya_tunai = new DecimalFormat("#,###");
        String format_total_biaya_tunai = formatter_biaya_tunai.format(getTotalBiayaTunai());
        table.addCell(format_total_biaya_tunai);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qq3 = new Phrase("II. Biaya Diperhitungkan", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qq3);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");

        int tot_biaya_diperhitungkan = 0;

        Cursor res_biaya_diperhitungkan = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);
        while (res_biaya_diperhitungkan.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_diperhitungkan.getString(2));

            if (res_kebutuhan_tanam != null) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") || res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        float konversi_satuan2=1;
                        if(res_biaya_diperhitungkan.getString(9).equals("ton")) {
                            konversi_satuan2 = Float.parseFloat(res_biaya_diperhitungkan.getString(3))*1000;
                        }
                        else if(res_biaya_diperhitungkan.getString(9).equals("kuintal")){
                            konversi_satuan2 = Float.parseFloat(res_biaya_diperhitungkan.getString(3))*100;
                        }
                        else if(res_biaya_diperhitungkan.getString(9).equals("kg")){
                            konversi_satuan2 = Float.parseFloat(res_biaya_diperhitungkan.getString(3))*1;
                        }
                        else if(res_biaya_diperhitungkan.getString(9).equals("liter") || res_biaya_diperhitungkan.getString(9).equals("Rp") || res_biaya_diperhitungkan.getString(9).equals("HOK")) {
                            konversi_satuan2 = Float.parseFloat(res_biaya_diperhitungkan.getString(3))*1;
                        }

                        int nilai = res_biaya_diperhitungkan.getInt(4);
                        int harga = (int) (nilai/konversi_satuan2);

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String format_harga_biaya_tunai = formatter.format(harga);
                        String format_harga_nilai = formatter.format(nilai);


                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell("");
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(kebutuhan_tanam);
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell("");
                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(Float.toString(konversi_satuan2));
                        table.addCell(format_harga_biaya_tunai);
                        table.addCell(format_harga_nilai);

                        tot_biaya_diperhitungkan += Integer.parseInt(res_biaya_diperhitungkan.getString(4));
                        this.setTotalBiayaDiperhitungkan(tot_biaya_diperhitungkan);
                    }
                }
            }
        }

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qq4 = new Phrase("Total Biaya Diperhitungkan", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qq4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Rp");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        DecimalFormat formatter_biaya_diperhitungkan = new DecimalFormat("#,###");
        String format_total_biaya_diperhitungkan = formatter_biaya_diperhitungkan.format(getTotalBiayaDiperhitungkan());
        table.addCell(format_total_biaya_diperhitungkan);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qq5 = new Phrase("III. Total Biaya", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qq5);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Rp");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        String format_total_biaya = formatter_biaya_tunai.format(getTotalBiaya());
        table.addCell(format_total_biaya);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase p3 = new Phrase("3", bold);
        Phrase q3 = new Phrase("Pendapatan", bold);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(p3);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(q3);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qr1 = new Phrase("Pendapatan Atas Biaya Tunai", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qr1);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Rp");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        String pendapatan_tunai = formatter_biaya_tunai.format(this.getPendapatanTunai());
        table.addCell(pendapatan_tunai);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qr2 = new Phrase("Pendapatan Atas Biaya Total", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qr2);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Rp");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        String pendapatan_total = formatter_biaya_tunai.format(this.getPendapatanTotal());
        table.addCell(pendapatan_total);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase p4 = new Phrase("4", bold);
        Phrase q4 = new Phrase("R/C", bold);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(p4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(q4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        table.addCell("");

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qs1 = new Phrase("R/C Atas Biaya Tunai", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qs1);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        DecimalFormat df = new DecimalFormat("#.##");
        String format_rc_tunai = df.format(this.getRCTunai());
        table.addCell(format_rc_tunai);

        table.addCell(" ");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");

        Phrase qs2 = new Phrase("R/C Atas Biaya Total", bold);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(qs2);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("");
        table.addCell("");
        DecimalFormat df2 = new DecimalFormat("#.##");
        String format_rc_total = df.format(this.getRCTotal());
        table.addCell(format_rc_total);

        DecimalFormat formatter = new DecimalFormat("#,###");
        String format_tot_penjualann = formatter.format(tot_penjualan);

        /*
         * Tabel 2 adalah tabel yang berisi data satuan yang telah dikonversi
         * */

        Paragraph paragraph_tabel2 = new Paragraph();
        paragraph_tabel2.add("Tabel 2. Tabel yang berisi data satuan yang telah dikonversi");

        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("No.");
        table2.addCell("Uraian");
        table2.addCell("Satuan");
        table2.addCell("Jumlah");
        table2.addCell("Harga(Rp)");
        table2.addCell("Nilai(Rp)");

        table2.setHeaderRows(1);
        PdfPCell[] cells2 = table2.getRow(0).getCells();

        for (int j = 0; j < cells2.length; j++) {
            cells2[j].setBackgroundColor(BaseColor.GRAY);
        }

        p = new Phrase("1", bold);
        q = new Phrase("Penerimaan Dana", bold);

        table2.addCell(p);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(q);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        res_penerimaan = db.getDataPenerimaanHistori(sawah_terpilih, periode_terpilih);

        while (res_penerimaan.moveToNext()) {
            Cursor res_hasil_panen = db.getIdHasilPanen(res_penerimaan.getString(2));
            if (res_hasil_panen.getCount() == 0) {
            }
            if (res_hasil_panen != null) {
                if (res_hasil_panen.moveToFirst()) {
                    String hasil_panen = res_hasil_panen.getString(1);

                    int nilai = res_penerimaan.getInt(4);
                    int harga = (int) (nilai/res_penerimaan.getFloat(3));

                    String format_harga_penjualan = formatter.format(harga);
                    String format_harga_nilai = formatter.format(nilai);

                    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table2.addCell("");
                    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                    table2.addCell(hasil_panen);
                    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table2.addCell(res_penerimaan.getString(9));
                    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table2.addCell(res_penerimaan.getString(3));
                    table2.addCell(format_harga_penjualan);
                    table2.addCell(format_harga_nilai);

                    tot_penjualan += Integer.parseInt(res_penerimaan.getString(4));

                }
            }
        }

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        //---TAMPILAN PDF PENGELUARAN BIAYA---//

        p2 = new Phrase("2", bold);
        q2 = new Phrase("Pengeluaran Biaya", bold);

        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(p2);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(q2);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");


        qq1 = new Phrase("I. Biaya Tunai", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qq1);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        tot_biaya_tunai = 0;

        res_biaya_tunai = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);
        while (res_biaya_tunai.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_tunai.getString(2));

            if (res_kebutuhan_tanam != null) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (!res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") && !res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        int nilai = res_biaya_tunai.getInt(4);
                        int harga = (int) (nilai/res_biaya_tunai.getFloat(3));

                        String format_harga_biaya_tunai = formatter.format(harga);
                        String format_harga_nilai = formatter.format(nilai);

                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table2.addCell("");
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                        table2.addCell(kebutuhan_tanam);
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table2.addCell(res_biaya_tunai.getString(9));
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table2.addCell(res_biaya_tunai.getString(3));
                        table2.addCell(format_harga_biaya_tunai);
                        table2.addCell(format_harga_nilai);

                        tot_biaya_tunai += Integer.parseInt(res_biaya_tunai.getString(4));
                        this.setTotalBiayaTunai(tot_biaya_tunai);
                    }
                }
            }
        }

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qq2 = new Phrase("Total Biaya Tunai", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qq2);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("Rp");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");

        formatter_biaya_tunai = new DecimalFormat("#,###");
        format_total_biaya_tunai = formatter_biaya_tunai.format(getTotalBiayaTunai());
        table2.addCell(format_total_biaya_tunai);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qq3 = new Phrase("II. Biaya Diperhitungkan", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qq3);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        tot_biaya_diperhitungkan = 0;

        res_biaya_diperhitungkan = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);
        while (res_biaya_diperhitungkan.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_diperhitungkan.getString(2));

            if (res_kebutuhan_tanam != null) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") || res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        int nilai = res_biaya_diperhitungkan.getInt(4);
                        int harga = (int) (nilai/res_biaya_diperhitungkan.getFloat(3));

                        formatter = new DecimalFormat("#,###");
                        String format_harga_biaya_tunai = formatter.format(harga);
                        String format_harga_nilai = formatter.format(nilai);


                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table2.addCell("");
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                        table2.addCell(kebutuhan_tanam);
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        table2.addCell("");
                        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table2.addCell(res_biaya_diperhitungkan.getString(3));
                        table2.addCell(format_harga_biaya_tunai);
                        table2.addCell(format_harga_nilai);

                        tot_biaya_diperhitungkan += Integer.parseInt(res_biaya_diperhitungkan.getString(4));
                        this.setTotalBiayaDiperhitungkan(tot_biaya_diperhitungkan);
                    }
                }
            }
        }

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qq4 = new Phrase("Total Biaya Diperhitungkan", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qq4);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("Rp");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        formatter_biaya_diperhitungkan = new DecimalFormat("#,###");
        format_total_biaya_diperhitungkan = formatter_biaya_diperhitungkan.format(getTotalBiayaDiperhitungkan());
        table2.addCell(format_total_biaya_diperhitungkan);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qq5 = new Phrase("III. Total Biaya", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qq5);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("Rp");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        format_total_biaya = formatter_biaya_tunai.format(getTotalBiaya());
        table2.addCell(format_total_biaya);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        p3 = new Phrase("3", bold);
        q3 = new Phrase("Pendapatan", bold);

        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(p3);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(q3);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qr1 = new Phrase("Pendapatan Atas Biaya Tunai", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qr1);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("Rp");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        pendapatan_tunai = formatter_biaya_tunai.format(this.getPendapatanTunai());
        table2.addCell(pendapatan_tunai);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qr2 = new Phrase("Pendapatan Atas Biaya Total", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qr2);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("Rp");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        pendapatan_total = formatter_biaya_tunai.format(this.getPendapatanTotal());
        table2.addCell(pendapatan_total);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        p4 = new Phrase("4", bold);
        q4 = new Phrase("R/C", bold);

        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(p4);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(q4);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qs1 = new Phrase("R/C Atas Biaya Tunai", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qs1);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        df = new DecimalFormat("#.##");
        format_rc_tunai = df.format(this.getRCTunai());
        table2.addCell(format_rc_tunai);

        table2.addCell(" ");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");
        table2.addCell("");

        qs2 = new Phrase("R/C Atas Biaya Total", bold);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.addCell(qs2);
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell("");
        table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell("");
        table2.addCell("");
        df2 = new DecimalFormat("#.##");
        format_rc_total = df.format(this.getRCTotal());
        table2.addCell(format_rc_total);

        Paragraph paragraph_konversi = new Paragraph();
        paragraph_konversi.add("Format satuan dalam laporan ini telah dikonversi dalam bentuk kilogram/kg yaitu : \n" +
                "1 ton = 1000 kg\n" +
                "1 kuintal = 100 kg\n" +
                "\n" +
                "R/C lebih besar dari 1, maka usahatani padi yang dilakukan menguntungkan\n" +
                "R/C sama dengan 1, maka usahatani padi yang dilakukan berada pada titik impas\n" +
                "R/C lebih kecil dari 1, maka usahatani padi yang dilakukan belum menguntungkan");

        int WriteExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String nama_pengguna = user.get(UserSessionManager.KEY_NAMA);
        String namaFile = nama_pengguna + ts;

        String file = Environment.getExternalStorageDirectory().getPath() + "/" + namaFile + ".pdf";
        if (Build.VERSION.SDK_INT >= 23) {
            if (WriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LaporanPendapatan.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

            } else {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph2.setAlignment(Element.ALIGN_CENTER);
                paragraph2.setSpacingAfter(20);
                luas_tabel1.setAlignment(Element.ALIGN_CENTER);
                paragraph_tabel1.setAlignment(Element.ALIGN_LEFT);
                paragraph_tabel1.setSpacingAfter(20);

                document.add(paragraph);
                document.add(luas_tabel1);
                document.add(paragraph2);
                document.add(paragraph_tabel1);
                document.add(table2);
                paragraph_luas_panen.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraph_luas_panen);

                document.newPage();
                paragraph_tabel2.setAlignment(Element.ALIGN_LEFT);
                paragraph_tabel2.setSpacingAfter(20);
                luas_tabel2.setAlignment(Element.ALIGN_CENTER);

                document.add(paragraph);
                document.add(luas_tabel2);
                document.add(paragraph2);
                document.add(paragraph_tabel2);
                document.add(table);
                document.add(paragraph_luas_panen);
                paragraph_konversi.setAlignment(Element.ALIGN_LEFT);
                paragraph_luas_panen.setAlignment(Element.ALIGN_LEFT);
                paragraph_konversi.setSpacingBefore(20);
                document.add(paragraph_konversi);
                paragraph_konversi.setSpacingBefore(20);

                document.close();
                Toast.makeText(this, "File has been written to :" + file, Toast.LENGTH_LONG).show();
            }
        }
        else{
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            paragraph2.setSpacingAfter(20);
            luas_tabel1.setAlignment(Element.ALIGN_CENTER);
            paragraph_tabel1.setAlignment(Element.ALIGN_LEFT);
            paragraph_tabel1.setSpacingAfter(20);

            document.add(paragraph);
            document.add(luas_tabel1);
            document.add(paragraph2);
            document.add(paragraph_tabel1);
            document.add(table2);
            paragraph_luas_panen.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph_luas_panen);

            document.newPage();
            paragraph_tabel2.setAlignment(Element.ALIGN_LEFT);
            paragraph_tabel2.setSpacingAfter(20);
            luas_tabel2.setAlignment(Element.ALIGN_CENTER);

            document.add(paragraph);
            document.add(luas_tabel2);
            document.add(paragraph2);
            document.add(paragraph_tabel2);
            document.add(table);
            document.add(paragraph_luas_panen);
            paragraph_konversi.setAlignment(Element.ALIGN_LEFT);
            paragraph_luas_panen.setAlignment(Element.ALIGN_LEFT);
            paragraph_konversi.setSpacingBefore(20);
            document.add(paragraph_konversi);
            paragraph_konversi.setSpacingBefore(20);

            document.close();
            Toast.makeText(this, "File has been written to :" + file, Toast.LENGTH_LONG).show();
        }

        /**
         * READ FILE
         */

        int ReadExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ReadExternalStoragePermission!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } else {

                File open_file = new File(file);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                target.setDataAndType(Uri.fromFile(open_file), "application/pdf");
                Uri output = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", open_file);
                target.setDataAndType(output, "application/pdf");
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this,"Download terlebih dahulu PDF Reader",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Intent target = new Intent(Intent.ACTION_VIEW);
            File open_file = new File(file);
            target.setDataAndType(Uri.fromFile(open_file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,"Download terlebih dahulu PDF Reader",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                break;

            case 3:
                break;
        }
    }

    public void viewDataPenerimaan() {
        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");
        TextView tv_sawah = (TextView) findViewById(R.id.tv_nama_sawah);
        TextView tv_lahan = (TextView) findViewById(R.id.tv_luas_lahan);
        TextView tv_periode = (TextView) findViewById(R.id.tv_periode_sawah_pendapatan);
        TextView tv_luas_panen = (TextView) findViewById(R.id.tv_luas_panen);

        Cursor res_sawah = db.getIdSawah(sawah_terpilih);
        while (res_sawah.moveToNext()) {
            Cursor res_periode = db.getDataIdPeriode(periode_terpilih);
            while (res_periode.moveToNext()) {
                tv_sawah.setText(res_sawah.getString(3));
                tv_lahan.setText("Luas Lahan (" + res_sawah.getString(2) + " " + res_sawah.getString(5) + ")");
                tv_periode.setText(res_periode.getString(2) + " " + res_periode.getString(4) + " - " + res_periode.getString(3) + " " + res_periode.getString(5));
            }
        }

        TableLayout tb_penjualan = (TableLayout) findViewById(R.id.table_penjualan);
        TextView tv_tot_penjualan = (TextView) findViewById(R.id.tv_tot_penjualan);
        int tot_penjualan = 0;

        Cursor res_penerimaan = db.getDataPenerimaanHistori(sawah_terpilih, periode_terpilih);

        while (res_penerimaan.moveToNext()) {
            Cursor res_hasil_panen = db.getIdHasilPanen(res_penerimaan.getString(2));

            if (res_hasil_panen != null) {
                if (res_hasil_panen.moveToFirst()) {
                    String hasil_panen = res_hasil_panen.getString(1);

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    TextView tv_penjualan = new TextView(this);
                    TextView tv_harga_penjualan = new TextView(this);

                    DecimalFormat formatter = new DecimalFormat("#,###");
                    String format_harga_penjualan = formatter.format(res_penerimaan.getInt(4));

                    tv_penjualan.setText(hasil_panen);
                    tv_penjualan.setTextColor(Color.BLACK);
//                    tv_satuan_penjualan.setText(res_penerimaan.getString(3) + " " + res_penerimaan.getString(9));
//                    tv_satuan_penjualan.setTextColor(Color.BLACK);
                    tv_harga_penjualan.setText("Rp. " + format_harga_penjualan);
                    tv_harga_penjualan.setTextColor(Color.BLACK);
                    row.addView(tv_penjualan,0);
//                    row.addView(tv_satuan_penjualan);
                    row.addView(tv_harga_penjualan,1);
                    tb_penjualan.addView(row);
                    tot_penjualan += Integer.parseInt(res_penerimaan.getString(4));
                }
                tv_luas_panen.setText("Luas Panen (" + res_hasil_panen.getString(1) + " : " + res_penerimaan.getString(11) + " " + res_penerimaan.getString(12) + ")");
            }
        }

        this.setPendapatanTunai(tot_penjualan);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String format_tot_penjualann = formatter.format(tot_penjualan);

        this.setRC_tunai(tot_penjualan);
        this.setRC_total(tot_penjualan);

        tv_tot_penjualan.setText("Rp. " + format_tot_penjualann);
    }

    public void viewDataPengeluaran() {
        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");

        TableLayout tb_biaya_tunai = (TableLayout) findViewById(R.id.table_biaya_tunai);
        TextView tv_tot_biaya_tunai = (TextView) findViewById(R.id.tv_tot_biaya_tunai);
        int tot_biaya_tunai = 0;

        Cursor res_biaya_tunai = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);

        while (res_biaya_tunai.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_tunai.getString(2));
            if (res_kebutuhan_tanam.getCount() != 0) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (!res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") && !res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);
                        TextView tv_nomor = new TextView(this);
                        TextView tv_biaya_tunai = new TextView(this);
//                        TextView tv_satuan_biaya_tunai = new TextView(this);
                        TextView tv_harga_biaya_tunai = new TextView(this);

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String format_harga_penjualan = formatter.format(res_biaya_tunai.getInt(4));

                        tv_nomor.setText("");
                        tv_biaya_tunai.setText(kebutuhan_tanam);
                        tv_biaya_tunai.setTextColor(Color.BLACK);
                        // tv_satuan_biaya_tunai.setText(res_biaya_tunai.getString(3) + " " +res_biaya_tunai.getString(9) );
//                        tv_satuan_biaya_tunai.setText("");
//                        tv_satuan_biaya_tunai.setTextColor(Color.BLACK);
                        tv_harga_biaya_tunai.setText("Rp. " + format_harga_penjualan);
                        tv_harga_biaya_tunai.setTextColor(Color.BLACK);
                        row.addView(tv_nomor);
                        row.addView(tv_biaya_tunai);
//                        row.addView(tv_satuan_biaya_tunai);
                        row.addView(tv_harga_biaya_tunai);
                        tb_biaya_tunai.addView(row);
                        tot_biaya_tunai += Integer.parseInt(res_biaya_tunai.getString(4));

                    }
                }
            }
        }

        this.setTotalBiaya(tot_biaya_tunai);
        this.setPendapatanTunai(this.getPendapatanTunai() - tot_biaya_tunai);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String format_tot_biaya_tunai = formatter.format(tot_biaya_tunai);
        this.setTotalBiayaTunai(tot_biaya_tunai);

        tv_tot_biaya_tunai.setText("Rp. " + format_tot_biaya_tunai);
    }

    public void viewDataBiayaDiperhitungkan() {
        Intent intent = getIntent();
        final String sawah_terpilih = intent.getStringExtra("sawah");
        final String periode_terpilih = intent.getStringExtra("periode");

        TableLayout tb_biaya_diperhitungkan = (TableLayout) findViewById(R.id.table_biaya_diperhitungkan);
        TextView tv_tot_biaya_diperhitungkan = (TextView) findViewById(R.id.tv_tot_biaya_diperhitungkan);
        int tot_biaya_diperhitungkan = 0;

        Cursor res_biaya_diperhitungkan = db.getDataPengeluaranHistori(sawah_terpilih, periode_terpilih);

        while (res_biaya_diperhitungkan.moveToNext()) {
            Cursor res_kebutuhan_tanam = db.getIdKebutuhanTanam(res_biaya_diperhitungkan.getString(2));

            if (res_kebutuhan_tanam.getCount() != 0) {
                if (res_kebutuhan_tanam.moveToFirst()) {
                    if (res_kebutuhan_tanam.getString(2).equals("Data Diperhitungkan") || res_kebutuhan_tanam.getString(2).equals("Data Alat")) {
                        String kebutuhan_tanam = res_kebutuhan_tanam.getString(1);

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        TextView tv_nomor = new TextView(this);
                        TextView tv_biaya_diperhitungkan = new TextView(this);
//                        TextView tv_satuan_biaya_diperhitungkan = new TextView(this);
                        TextView tv_harga_biaya_diperhitungkan = new TextView(this);

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String format_harga_penjualan = formatter.format(res_biaya_diperhitungkan.getInt(4));

                        tv_nomor.setText("");
                        tv_biaya_diperhitungkan.setText(kebutuhan_tanam);
                        tv_biaya_diperhitungkan.setTextColor(Color.BLACK);
                        // tv_satuan_biaya_diperhitungkan.setText(res_biaya_diperhitungkan.getString(3) + " " + res_biaya_diperhitungkan.getString(9 ));
//                        tv_satuan_biaya_diperhitungkan.setText("");
//                        tv_satuan_biaya_diperhitungkan.setTextColor(Color.BLACK);
                        tv_harga_biaya_diperhitungkan.setText("Rp. " + format_harga_penjualan);
                        tv_harga_biaya_diperhitungkan.setTextColor(Color.BLACK);
                        row.addView(tv_nomor);
                        row.addView(tv_biaya_diperhitungkan);
//                        row.addView(tv_satuan_biaya_diperhitungkan);
                        row.addView(tv_harga_biaya_diperhitungkan);
                        tb_biaya_diperhitungkan.addView(row);
                        tot_biaya_diperhitungkan += Integer.parseInt(res_biaya_diperhitungkan.getString(4));

                    }
                }
            }
        }

        int tot_diperhitungkan = this.getTotalBiaya();

        tot_diperhitungkan += tot_biaya_diperhitungkan;
        this.setTotalBiaya(tot_diperhitungkan);
        this.setPendapatanTotal(this.getPendapatanTunai() - tot_biaya_diperhitungkan);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String format_tot_biaya_diperhitungkan = formatter.format(tot_biaya_diperhitungkan);

        tv_tot_biaya_diperhitungkan.setText("Rp. " + format_tot_biaya_diperhitungkan);
    }
}