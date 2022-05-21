package com.example.wifitesterv2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HTTP 통신 기반 클래스 입니다.
 * 전체 URI : https://ljhhosting.com/setData/{floor}
 * */

public class HTTPManager {
    private static final String BASE_URL = "https://ljhhosting.com/";

    public static RetrofitAPI getApiService(){return getInstance().create(RetrofitAPI.class);}

    private static Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
