package com.example.wifitesterv2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Floor5API {
    @Headers({"Content-Type: application/json"})
    @POST("/setData/floor5")
    Call<APIRes> userJoin(@Body Floor5JsonModel data);
}
