package com.example.usahatanipadi.retrofit;

import com.example.usahatanipadi.model.ModelResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("register_luas_lahan_sawah.php")
    Call<ModelResponse> registerLuasLahanSawah(
            @Field("id") String idLahanSawah,
            @Field("nama_pengguna") String namaPengguna,
            @Field("luas") String luasSawah,
            @Field("alamat") String alamat,
            @Field("kategori") String kategori,
            @Field("satuan") String satuan,
            @Field("lat") String latitude,
            @Field("lng") String longitude

    );

    @FormUrlEncoded
    @POST("edit_master_data_sawah.php")
    Call<ModelResponse> edtiLuasLahanSawah(
            @Field("nama_pengguna") String namaPengguna,
            @Field("luas") String luasSawah,
            @Field("alamat") String alamat,
            @Field("kategori") String kategori,
            @Field("satuan") String satuan,
            @Field("lat") String latitude,
            @Field("lng") String longitude

    );

    @FormUrlEncoded
    @POST("hapus_master_data_sawah.php")
    Call<ModelResponse> hapusLuasLahanSawah(
            @Field("id_data_sawah") String idDataSawah

    );
}
