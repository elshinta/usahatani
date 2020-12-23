package com.example.usahatanipadi.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static String BASE_URL = "http://ilkomunila.com/usahatani/";

    public static Retrofit getClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES);
        httpClient.addInterceptor(interceptor);

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
