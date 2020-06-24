package com.example.usahatanipadi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Usahatani.db", null, 1);
    }

    /**
     * DATABASE
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS pengguna(id_pengguna integer primary key autoincrement," +
                "nama_usahatani varchar(30), nama_pemilik varchar(30), nomor_telepon varchar(15)," +
                "deskripsi_usahatani text, nama_pengguna varchar(30) unique, kata_sandi varchar(15), kelompok_tani varchar(255), lama_bertani varchar(5)" +
                ",level varchar(10))");

        db.execSQL("CREATE TABLE IF NOT EXISTS sawah (id_lahan_sawah varchar(255) primary key," +
                "id_pengguna integer(11), luas float, alamat text," + "kategori varchar(20), satuan varchar(25), status integer(3), lat float(10,6), lng float(10,6))");

        db.execSQL("CREATE TABLE IF NOT EXISTS periode (id_periode varchar(255) primary key," +
                "id_lahan_sawah varchar(255), bulan_periode_awal varchar (50), bulan_periode_akhir varchar (50), tahun_periode_awal varchar (50), tahun_periode_akhir varchar (50), status integer(1), unique(id_lahan_sawah,bulan_periode_awal,tahun_periode_awal))");

        db.execSQL("CREATE TABLE IF NOT EXISTS hasil_panen (id_hasil_panen varchar(255) primary key," +
                "nama_hasil_panen varchar (50),status integer(3))");

        db.execSQL("CREATE TABLE IF NOT EXISTS kebutuhan_tanam (id_kebutuhan_tanam varchar(255) primary key," +
                "nama_kebutuhan_tanam varchar (50), kategori varchar (50), status integer(1))");

        db.execSQL("CREATE TABLE IF NOT EXISTS penerimaan_dana (id_penerimaan_dana varchar(255) primary key," +
                "id_lahan_sawah varchar(255), id_hasil_panen varchar(255), jumlah varchar(11), total_harga_penerimaan varchar(20)" +
                ", nama_pelanggan varchar(50), tanggal_penerimaan_dana varchar(50), catatan varchar(50), id_periode varchar(255), satuan varchar(25), status integer(1), luas_panen varchar (255), satuan_luas_panen varchar (25))");

        db.execSQL("CREATE TABLE IF NOT EXISTS pengeluaran_biaya (id_pengeluaran_biaya varchar(255) primary key," +
                "id_lahan_sawah varchar(255), id_kebutuhan_tanam varchar(255), jumlah varchar(11), total_harga_pengeluaran varchar(20)" +
                ", nama_pemasok varchar(50), tanggal_pengeluaran_biaya varchar(50), catatan varchar(50), id_periode varchar(255), satuan varchar(25), status integer(1))");

        db.execSQL("CREATE TABLE IF NOT EXISTS pertanyaan_survey(id_pertanyaan integer primary key autoincrement," +
                "id_survey int(5), pertanyaan_body text)");

        db.execSQL("CREATE TABLE IF NOT EXISTS survey(id_survey integer primary key autoincrement," +
                "id_pengguna int(5), jenis_pertanyaan varchar(40), jumlah_pertanyaan varchar(255), " +
                "id_periode varchar(255), nama_surveyor varchar(255))");

        db.execSQL("CREATE TABLE IF NOT EXISTS jawaban_survey(id_jawaban integer primary key autoincrement," +
                "id_pengguna int(5), id_pertanyaan int(5), jawaban_body text)");

        // tesst data survey
//        db.execSQL("INSERT INTO survey (id_survey,id_pengguna,jenis_pertanyaan,jumlah_pertanyaan,id_periode) VALUES (1,1,'pupuk','5','1')");


        db.execSQL("INSERT INTO hasil_panen (id_hasil_panen,nama_hasil_panen) VALUES ('hasil_default_1','Gabah Kering Panen')");
        db.execSQL("INSERT INTO hasil_panen (id_hasil_panen,nama_hasil_panen) VALUES ('hasil_default_2','Gabah Kering Giling')");
        db.execSQL("INSERT INTO hasil_panen (id_hasil_panen,nama_hasil_panen) VALUES ('hasil_default_3','Beras')");

        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_1','Bibit','Data Bibit')"); //data 1
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_2','Pupuk Urea','Data Pupuk')"); //data 2
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_3','Pupuk SP-36','Data Pupuk')"); //data 3
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_4','Pupuk KCl','Data Pupuk')"); //data 4
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_5','Pupuk ZA','Data Pupuk')"); //data 5
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_6','Pupuk TSP','Data Pupuk')"); //data 6
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_7','Hama Wereng','Data Obat Hama')"); //data 7
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_8','Cangkul','Data Alat')"); //data 8
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_9','TKLK','Data Tunai')"); //data 9
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_10','TKDK','Data Diperhitungkan')"); //data 10
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_11','Sewa Lahan','Data Tunai')");
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_12','Lahan Sendiri','Data Diperhitungkan')");
        db.execSQL("INSERT INTO kebutuhan_tanam (id_kebutuhan_tanam,nama_kebutuhan_tanam,kategori) VALUES ('tanam_default_13','Pajak','Data Tunai')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pengguna");
        db.execSQL("DROP TABLE IF EXISTS sawah");
        db.execSQL("DROP TABLE IF EXISTS periode");
        db.execSQL("DROP TABLE IF EXISTS hasil_panen");
        db.execSQL("DROP TABLE IF EXISTS kebutuhan_tanam");
        db.execSQL("DROP TABLE IF EXISTS penerimaan_dana");
        db.execSQL("DROP TABLE IF EXISTS pengeluaran_biaya");
        db.execSQL("DROP TABLE IF EXISTS survey");
        db.execSQL("DROP TABLE IF EXISTS jawaban_survey");
        db.execSQL("DROP TABLE IF EXISTS pertanyaan_survey");

        //Added new column to book table - book rating
//        if (oldVersion == 1){
//            db.execSQL("ALTER TABLE pengguna ADD COLUMN kelompok_tani varchar(25)");
//            db.execSQL("ALTER TABLE pengguna ADD COLUMN lama_bertani varchar(5)");
//            db.execSQL("ALTER TABLE pengguna ADD COLUMN level varchar(10)");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS pertanyaan_survey(id_pertanyaan integer primary key autoincrement," +
//                    "id_survey int(5), pertanyaan_body text)");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS survey(id_survey integer primary key autoincrement," +
//                    "id_pengguna int(5), jenis_pertanyaan varchar(40), jumlah_pertanyaan varchar(255), " +
//                    "id_periode varchar(255))");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS jawaban_survey(id_jawaban integer primary key autoincrement," +
//                    "id_pengguna int(5), id_pertanyaan int(5), jawaban_body text)");
//
//            db.execSQL(" BEGIN TRANSACTION");
//
//            db.execSQL("ALTER TABLE pengeluaran_biaya RENAME TO pengeluaran_biaya");
//            db.execSQL("CREATE TABLE IF NOT EXISTS pengeluaran_biaya (id_pengeluaran_biaya varchar(255) primary key," +
//                    "id_lahan_sawah varchar(255), id_kebutuhan_tanam varchar(255), jumlah varchar(11), total_harga_pengeluaran varchar(20)" +
//                    ", nama_pemasok varchar(50), tanggal_pengeluaran_biaya varchar(50), catatan varchar(50), id_periode varchar(255), satuan varchar(25), status integer(1))");
//
//            db.execSQL("COMMIT");
//
//            db.execSQL(" BEGIN TRANSACTION");
//
//            db.execSQL("ALTER TABLE penerimaan_dana RENAME TO penerimaan_dana");
//            db.execSQL("CREATE TABLE IF NOT EXISTS penerimaan_dana (id_penerimaan_dana varchar(255) primary key," +
//                    "id_lahan_sawah varchar(255), id_hasil_panen varchar(255), jumlah varchar(11), total_harga_penerimaan varchar(20)" +
//                    ", nama_pelanggan varchar(50), tanggal_penerimaan_dana varchar(50), catatan varchar(50), id_periode varchar(255), satuan varchar(25), status integer(1), luas_panen varchar (255), satuan_luas_panen varchar (25))");
//
//            db.execSQL("COMMIT");
//        }

    }

    /**
     * DATA PENGGUNA
     * @param nama_usahatani String
     * @param nama_pemilik String
     * @param nomor_telepon String
     * @param deskripsi_usahatani String
     * @param nama_pengguna String
     * @param kata_sandi String
     * @param kelompok_tani String
     * @param lama_bertani String
     * @param level String
     */

    public boolean insert(String nama_usahatani, String nama_pemilik, String nomor_telepon, String deskripsi_usahatani, String nama_pengguna, String kata_sandi, String kelompok_tani, String lama_bertani, String level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nama_usahatani", nama_usahatani);
        contentValues.put("nama_pemilik", nama_pemilik);
        contentValues.put("nomor_telepon", nomor_telepon);
        contentValues.put("deskripsi_usahatani", deskripsi_usahatani);
        contentValues.put("nama_pengguna", nama_pengguna);
        contentValues.put("kata_sandi", kata_sandi);
        contentValues.put("kelompok_tani", kelompok_tani);
        contentValues.put("lama_bertani", lama_bertani);
        contentValues.put("level", level);

        long ins = db.insert("pengguna", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_restore(String id,String nama_usahatani, String nama_pemilik, String nomor_telepon, String deskripsi_usahatani, String nama_pengguna, String kata_sandi,String kelompok_tani) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_pengguna", id);
        contentValues.put("nama_usahatani", nama_usahatani);
        contentValues.put("nama_pemilik", nama_pemilik);
        contentValues.put("nomor_telepon", nomor_telepon);
        contentValues.put("deskripsi_usahatani", deskripsi_usahatani);
        contentValues.put("nama_pengguna", nama_pengguna);
        contentValues.put("kata_sandi", kata_sandi);
        contentValues.put("kelompok_tani", kelompok_tani);

        long ins = db.insert("pengguna", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_survey(String id_survey,String id_pengguna,String jenis_pertanyaan, String jumlah_pertanyaan, String id_periode, String nama_surveyor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_survey", id_survey);
        contentValues.put("id_pengguna", id_pengguna);
        contentValues.put("jenis_pertanyaan", jenis_pertanyaan);
        contentValues.put("jumlah_pertanyaan", jumlah_pertanyaan);
        contentValues.put("id_periode", id_periode);
        contentValues.put("nama_surveyor", nama_surveyor);

        long ins = db.insert("survey", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_pertanyaan(String id_pertanyaan,String id_survey,String pertanyaan_body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_pertanyaan", id_pertanyaan);
        contentValues.put("id_survey", id_survey);
        contentValues.put("pertanyaan_body", pertanyaan_body);

        long ins = db.insert("pertanyaan_survey", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean cek_pengguna(String nama_pengguna, String kata_sandi) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from pengguna where nama_pengguna=? and kata_sandi=?", new String[]{nama_pengguna, kata_sandi});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public Cursor getData(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pengguna where nama_pengguna=?", new String[]{nama_pengguna});
        return res;
    }

    public boolean updateDataPengguna(String id_pengguna, String nama_usahatani, String nama_pemilik, String nomor_telepon, String deskripsi_usahatani, String nama_pengguna, String kata_sandi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nama_usahatani", nama_usahatani);
        contentValues.put("nama_pemilik", nama_pemilik);
        contentValues.put("nomor_telepon", nomor_telepon);
        contentValues.put("deskripsi_usahatani", deskripsi_usahatani);
        contentValues.put("nama_pengguna", nama_pengguna);
        contentValues.put("kata_sandi", kata_sandi);

        String whereClause = "id_pengguna=?";
        String whereArgs[] = {id_pengguna};

        long update = db.update("pengguna", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * DATA SAWAH
     * @param id_pengguna
     * @param luas
     * @param alamat
     * @param kategori
     * @param satuan
     * @return
     */

    public boolean insert_sawah(String id_lahan_sawah, String id_pengguna, String luas, String alamat, String kategori,String satuan,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_pengguna", id_pengguna);
        contentValues.put("luas", luas);
        contentValues.put("alamat", alamat);
        contentValues.put("kategori", kategori);
        contentValues.put("satuan", satuan);
        contentValues.put("status", status);

        long ins = db.insert("sawah", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_sawah_restore(String id_lahan_sawah, String id_pengguna, String luas, String alamat, String kategori,String satuan,int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_pengguna", id_pengguna);
        contentValues.put("luas", luas);
        contentValues.put("alamat", alamat);
        contentValues.put("kategori", kategori);
        contentValues.put("satuan", satuan);
        contentValues.put("status", status);

        long ins = db.insert("sawah", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public Cursor getDataSawah(String id_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from sawah where id_pengguna=? ORDER BY id_lahan_sawah asc", new String[]{id_pengguna});
        return res;
    }

    public Cursor getDataSurvey(String id_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from survey where id_pengguna= ?", new String[]{id_pengguna});
        return res;
    }

    public Cursor getDataPertanyaan(String id_survey) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pertanyaan_survey where id_survey=? ORDER BY id_survey asc", new String[]{id_survey});
        return res;
    }

    public boolean updateDataSawah(String id_lahan, long id_pengguna, String alamat, String luas, String kategori, String satuan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_pengguna", id_pengguna);
        contentValues.put("alamat", alamat);
        contentValues.put("luas", luas);
        contentValues.put("kategori", kategori);
        contentValues.put("satuan", satuan);
        String whereClause = "id_lahan_sawah=?";
        String whereArgs[] = {id_lahan};

        long update = db.update("sawah", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataSawah(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id_lahan_sawah=?";
        String whereArgs[] = {id};
        long delete = db.delete("sawah", whereClause, whereArgs);

        if(delete==-1){
            return false;
        } else {
            return true;
        }
    }

    /**
     * DATA PERIODE
     * @param id_lahan_sawah
     * @param bulan_periode_awal
     * @param tahun_periode_awal
     * @param bulan_periode_akhir
     * @param tahun_periode_akhir
     * @return
     */

    public boolean insert_periode(String id_periode, String id_lahan_sawah, String bulan_periode_awal, String tahun_periode_awal, String bulan_periode_akhir, String tahun_periode_akhir,int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("id_periode", id_periode);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("bulan_periode_awal", bulan_periode_awal);
        contentValues.put("tahun_periode_awal", tahun_periode_awal);
        contentValues.put("bulan_periode_akhir", bulan_periode_akhir);
        contentValues.put("tahun_periode_akhir", tahun_periode_akhir);
        contentValues.put("status", status);

        long ins = db.insert("periode", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_periode_restore(String id_periode, String id_lahan_sawah, String bulan_periode_awal, String tahun_periode_awal, String bulan_periode_akhir, String tahun_periode_akhir,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_periode", id_periode);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("bulan_periode_awal", bulan_periode_awal);
        contentValues.put("tahun_periode_awal", tahun_periode_awal);
        contentValues.put("bulan_periode_akhir", bulan_periode_akhir);
        contentValues.put("tahun_periode_akhir", tahun_periode_akhir);
        contentValues.put("status", status);

        long ins = db.insert("periode", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public Cursor getDataPeriode(String id_sawah) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from periode where id_lahan_sawah=?", new String[]{id_sawah});
        return res;
    }

    public Cursor getDataPeriodeTerbuka(String id_sawah) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from periode where id_lahan_sawah=? and bulan_periode_akhir=? and tahun_periode_akhir=?", new String[]{id_sawah,"",""});
        return res;
    }

    public Cursor getDataIdPeriode(String id_periode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from periode where id_periode=?", new String[]{id_periode});
        return res;
    }

    public Cursor getIDPeriode() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from periode", null);
        return res;
    }

    public boolean setPeriodeAkhir(String id_periode, String id_lahan_sawah,String bulan_periode_akhir, String tahun_periode_akhir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("bulan_periode_akhir", bulan_periode_akhir);
        contentValues.put("tahun_periode_akhir", tahun_periode_akhir);

        String whereClause = "id_periode=?";
        String whereArgs[] = {id_periode};

        long update = db.update("periode", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * DATA HASIL PANEN
     * @return
     */

    public Cursor getDataHasilPanen(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from hasil_panen where id_hasil_panen like ? or id_hasil_panen like ? ORDER BY nama_hasil_panen asc", new String[]{"hasil_default_%","hasil_" + nama_pengguna + "_%"});
        return res;
    }

    public boolean insert_hasil_panen(String id_hasil_panen,String nama_hasil_panen, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_hasil_panen", id_hasil_panen);
        contentValues.put("nama_hasil_panen", nama_hasil_panen);
        contentValues.put("status", status);

        long ins = db.insert("hasil_panen", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_hasil_panen_restore(String id_hasil_panen, String nama_hasil_panen, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("id_hasil_panen", id_hasil_panen);
        contentValues.put("nama_hasil_panen", nama_hasil_panen);
        contentValues.put("status", status);

        long ins = db.insert("hasil_panen", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    /**
     * DATA PENERIMAAN DANA
     * @param id_lahan_sawah
     * @param id_hasil_panen
     * @param jumlah
     * @param total_harga
     * @param nama_pelanggan
     * @param tanggal_penerimaan_dana
     * @param catatan
     * @param id_periode
     * @param satuan
     * @return
     */

    public boolean insert_penerimaan_dana(String id_penerimaan_dana, String id_lahan_sawah, String id_hasil_panen, String jumlah, String total_harga, String nama_pelanggan, String tanggal_penerimaan_dana, String catatan, String id_periode, String satuan, String status, String luas_panen, String satuan_luas_panen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_penerimaan_dana", id_penerimaan_dana);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_hasil_panen", id_hasil_panen);
        contentValues.put("jumlah", jumlah);
        contentValues.put("total_harga_penerimaan", total_harga);
        contentValues.put("nama_pelanggan", nama_pelanggan);
        contentValues.put("tanggal_penerimaan_dana", tanggal_penerimaan_dana);
        contentValues.put("catatan", catatan);
        contentValues.put("id_periode", id_periode);
        contentValues.put("satuan", satuan);
        contentValues.put("status",status);
        contentValues.put("luas_panen",luas_panen);
        contentValues.put("satuan_luas_panen",satuan_luas_panen);

        long ins = db.insert("penerimaan_dana", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_penerimaan_dana_restore(String id_penerimaan_dana, String id_lahan_sawah, String id_hasil_panen, String jumlah, String total_harga, String nama_pelanggan, String tanggal_penerimaan_dana, String catatan, String id_periode, String satuan, String status, String luas_panen, String satuan_luas_panen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id_penerimaan_dana", id_penerimaan_dana);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_hasil_panen", id_hasil_panen);
        contentValues.put("jumlah", jumlah);
        contentValues.put("total_harga_penerimaan", total_harga);
        contentValues.put("nama_pelanggan", nama_pelanggan);
        contentValues.put("tanggal_penerimaan_dana", tanggal_penerimaan_dana);
        contentValues.put("catatan", catatan);
        contentValues.put("id_periode", id_periode);
        contentValues.put("satuan", satuan);
        contentValues.put("status",status);
        contentValues.put("luas_panen",luas_panen);
        contentValues.put("satuan_luas_panen",satuan_luas_panen);

        long ins = db.insert("penerimaan_dana", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public Cursor getDataPenerimaan(String id_penerimaan_dana) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from penerimaan_dana where id_penerimaan_dana=?", new String[]{id_penerimaan_dana});
        return res;
    }

    public Cursor getIDPenerimaan() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from penerimaan_dana", null);
        return res;
    }

    public Cursor getDataPenerimaanHistori(String id_lahan_sawah, String id_periode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from penerimaan_dana where id_lahan_sawah=? and id_periode=? ORDER BY id_penerimaan_dana desc", new String[]{id_lahan_sawah, id_periode});
        return res;
    }

    /**
     * DATA PENGELUARAN BIAYA
     * @param id_lahan_sawah
     * @param id_kebutuhan_tanam
     * @param jumlah
     * @param total_harga
     * @param nama_pemasok
     * @param tanggal_pengeluaran_biaya
     * @param catatan
     * @param id_periode
     * @param satuan
     * @return
     */

    public boolean insert_pengeluaran_biaya(String id_pengeluaran_biaya, String id_lahan_sawah, String id_kebutuhan_tanam, String jumlah, String total_harga, String nama_pemasok, String tanggal_pengeluaran_biaya, String catatan, String id_periode, String satuan, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_pengeluaran_biaya", id_pengeluaran_biaya);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_kebutuhan_tanam", id_kebutuhan_tanam);
        contentValues.put("jumlah", jumlah);
        contentValues.put("total_harga_pengeluaran", total_harga);
        contentValues.put("nama_pemasok", nama_pemasok);
        contentValues.put("tanggal_pengeluaran_biaya", tanggal_pengeluaran_biaya);
        contentValues.put("catatan", catatan);
        contentValues.put("id_periode", id_periode);
        contentValues.put("satuan", satuan);
        contentValues.put("status",status);

        long ins = db.insert("pengeluaran_biaya", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_pengeluaran_biaya_restore(String id_pengeluaran_biaya, String id_lahan_sawah, String id_kebutuhan_tanam, String jumlah, String total_harga, String nama_pemasok, String tanggal_pengeluaran_biaya, String catatan, String id_periode, String satuan, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id_pengeluaran_biaya", id_pengeluaran_biaya);
        contentValues.put("id_lahan_sawah", id_lahan_sawah);
        contentValues.put("id_kebutuhan_tanam", id_kebutuhan_tanam);
        contentValues.put("jumlah", jumlah);
        contentValues.put("total_harga_pengeluaran", total_harga);
        contentValues.put("nama_pemasok", nama_pemasok);
        contentValues.put("tanggal_pengeluaran_biaya", tanggal_pengeluaran_biaya);
        contentValues.put("catatan", catatan);
        contentValues.put("id_periode", id_periode);
        contentValues.put("satuan", satuan);
        contentValues.put("status",status);

        long ins = db.insert("pengeluaran_biaya", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public Cursor getDataPengeluaran(String id_pengeluaran) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pengeluaran_biaya where id_pengeluaran_biaya =?", new String[]{id_pengeluaran});
        return res;
    }

    public Cursor getIDPengeluaran() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pengeluaran_biaya", null);
        return res;
    }

    public Cursor getDataPengeluaranHistori(String id_lahan_sawah, String id_periode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pengeluaran_biaya where id_lahan_sawah=? and id_periode=? ORDER BY id_pengeluaran_biaya desc", new String[]{id_lahan_sawah, id_periode});
        return res;
    }

    /**
     * DATA KEBUTUHAN TANAM
     * @param nama_kebutuhan_tanam
     * @param kategori
     * @return
     */

    public boolean insert_kebutuhan_tanam(String id_kebutuhan_tanam,String nama_kebutuhan_tanam, String kategori, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id;

        Cursor kebutuhan_tanam = db.rawQuery("SELECT * FROM kebutuhan_tanam ORDER BY id_kebutuhan_tanam DESC LIMIT 1", new String[]{});

        ContentValues contentValues = new ContentValues();

        contentValues.put("id_kebutuhan_tanam", id_kebutuhan_tanam);
        contentValues.put("nama_kebutuhan_tanam", nama_kebutuhan_tanam);
        contentValues.put("kategori", kategori);
        contentValues.put("status", status);

        long ins = db.insert("kebutuhan_tanam", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public boolean insert_kebutuhan_tanam_restore(String id_kebutuhan_tanam,String nama_kebutuhan_tanam, String kategori, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id_kebutuhan_tanam", id_kebutuhan_tanam);
        contentValues.put("nama_kebutuhan_tanam", nama_kebutuhan_tanam);
        contentValues.put("kategori", kategori);
        contentValues.put("status", status);

        long ins = db.insert("kebutuhan_tanam", null, contentValues);
        if (ins == -1)
            return false;
        else
            return true;
    }

    public Cursor getDataKebutuhanTanam(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where id_kebutuhan_tanam like ? or id_kebutuhan_tanam like ? ORDER BY id_kebutuhan_tanam asc", new String []{"tanam_default_%","tanam_" + nama_pengguna + "_%"});
        return res;
    }

    public Cursor getIdKebutuhanTanam(String id_kebutuhan_tanam) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where id_kebutuhan_tanam=?", new String[]{id_kebutuhan_tanam});
        return res;
    }

    // for get the last id from database usahatani kebutuhan_tanam
    public Cursor getIDTanam() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from kebutuhan_tanam", null);
        return res;
    }

    /*
     * mengambil data untuk Master Data
     **/

    public Cursor getDataPupuk(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        String data = "Data Pupuk";
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where kategori=? and (id_kebutuhan_tanam like ? or id_kebutuhan_tanam like ?) ORDER BY id_kebutuhan_tanam asc", new String[]{data,"tanam_default_%","tanam_" + nama_pengguna + "_%"});
        return res;
    }

    public Cursor getDataBibit(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        String data = "Data Bibit";
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where kategori=? and (id_kebutuhan_tanam like ? or id_kebutuhan_tanam like ?) ORDER BY id_kebutuhan_tanam asc", new String[]{data,"tanam_default_%","tanam_" + nama_pengguna + "_%"});
        return res;
    }

    public Cursor getDataAlat(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        String data = "Data Alat";
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where kategori=? and (id_kebutuhan_tanam like ? or id_kebutuhan_tanam like ?) ORDER BY id_kebutuhan_tanam asc", new String[]{data,"tanam_default_%","tanam_" + nama_pengguna + "_%"});
        return res;
    }

    public Cursor getDataObatHama(String nama_pengguna) {
        SQLiteDatabase db = this.getWritableDatabase();
        String data = "Data Obat Hama";
        Cursor res = db.rawQuery("select * from kebutuhan_tanam where kategori=? and (id_kebutuhan_tanam like ? or id_kebutuhan_tanam like ?) ORDER BY id_kebutuhan_tanam asc", new String[]{data,"tanam_default_%","tanam_" + nama_pengguna + "_%"});
        return res;
    }

    public Cursor getIdSawah(String id_lahan_sawah) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from sawah  where id_lahan_sawah=?", new String[]{id_lahan_sawah});
        return res;
    }

    public Cursor getIDSawah() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from sawah", null);
        return res;
    }

    public Cursor getIdHasilPanen(String id_hasil_panen) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from hasil_panen where id_hasil_panen=?", new String[]{id_hasil_panen});
        return res;
    }

    // for get the last id from database usahatani hasil panen
    public Cursor getLastIDHasilPanen() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from hasil_panen", null);
        return res;
    }

    /*
     * untuk menghapus data
     **/

    public boolean deleteDataPupuk(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id_kebutuhan_tanam=?";
        String whereArgs[] = {id};
        long delete = db.delete("kebutuhan_tanam", whereClause, whereArgs);

        if(delete==-1){
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataHasilPanen(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id_hasil_panen=?";
        String whereArgs[] = {id};
        long delete = db.delete("hasil_panen", whereClause, whereArgs);

        if(delete==-1){
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataTransaksiPenerimaan(String id_penerimaan){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id_penerimaan_dana=?";
        String whereArgs[] = {id_penerimaan};
        long delete = db.delete("penerimaan_dana", whereClause, whereArgs);

        if(delete==-1){
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataTransaksiPengeluaran(String id_pengeluaran){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id_pengeluaran_biaya=?";
        String whereArgs[] = {id_pengeluaran};
        long delete = db.delete("pengeluaran_biaya", whereClause, whereArgs);

        if(delete==-1){
            return false;
        } else {
            return true;
        }
    }

    /*
     * this method is for getting all the unsynced
     * so that we can sync it with database
     * */

    public Cursor getUnsyncedPengeluaranBiaya() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM pengeluaran_biaya  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedPenerimaanDana() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM penerimaan_dana  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedPeriode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM periode  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedKebutuhanTanam() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM kebutuhan_tanam  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedHasilPanen() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM hasil_panen  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedSawah() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM sawah  WHERE status = 0";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * untuk update data sinkronisasi ke server
    **/

    public boolean updateDataHasilPanen(String id, String nama_hasil_panen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nama_hasil_panen", nama_hasil_panen);
        String whereClause = "id_hasil_panen=?";
        String whereArgs[] = {id};

        long update = db.update("hasil_panen", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateDataPupuk(String id, String nama_kebutuhan_tanam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nama_kebutuhan_tanam", nama_kebutuhan_tanam);
        contentValues.put("id_kebutuhan_tanam", id);
        String whereClause = "id_kebutuhan_tanam=?";
        String whereArgs[] = {id};

        long update = db.update("kebutuhan_tanam", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updatePengeluaranBiaya(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_pengeluaran_biaya=?";
        String whereArgs[] = {id};

        long update = db.update("pengeluaran_biaya", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updatePenerimaanDana(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_penerimaan_dana=?";
        String whereArgs[] = {id};

        long update = db.update("penerimaan_dana", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updatePeriode(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_periode=?";
        String whereArgs[] = {id};

        long update = db.update("periode", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateKebutuhanTanam(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_kebutuhan_tanam=?";
        String whereArgs[] = {id};

        long update = db.update("kebutuhan_tanam", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateHasilPanen(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_hasil_panen=?";
        String whereArgs[] = {id};

        long update = db.update("hasil_panen", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateSawah(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        String whereClause = "id_sawah=?";
        String whereArgs[] = {id};

        long update = db.update("sawah", contentValues, whereClause, whereArgs);

        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }
}
