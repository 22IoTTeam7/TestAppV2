package com.example.wifitesterv2;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * HTTP 통신 사용법입니다.
 * callRetrofit manager = new callRetrofit();
 * manager.callFloor2(Test_arr); 각 층별로 메서드 이름이 다릅니다.
 * manager.callFloor4(Test_arr); 각 층별로 int 배열 포멧에 맞게 넣어주시면 알아서 들어갑니다.
 * manager.callFloor5(Test_arr);
 * */

public class callRetrofit {

    /**
     * 2층 데이터 input method
     * */
    public void callFloor2(int[] AP) {

        Floor2JsonModel floor2JsonModel = new Floor2JsonModel(AP);

        Call<APIRes> call = HTTPManager.getApiService().postJsonF2(floor2JsonModel);

        call.enqueue(new Callback<APIRes>() {
            @Override
            public void onResponse(Call<APIRes> call, Response<APIRes> response) {
                if(!response.isSuccessful())
                {
                    Log.e("연결불가 : ",  "error code" + response.code());
                }

                APIRes apiRes = response.body();
                Log.d("연결성공 : ", response.body().toString());
            }

            @Override
            public void onFailure(Call<APIRes> call, Throwable t){
                Log.e("연결 실패", t.getMessage());
            }
        });
    }

    /**
     * 4층 데이터 input method
     * */
    public void callFloor4(int[] AP) {
        Floor4JsonModel floor4JsonModel = new Floor4JsonModel(AP);

        Call<APIRes> call = HTTPManager.getApiService().postJsonF4(floor4JsonModel);

        call.enqueue(new Callback<APIRes>() {
            @Override
            public void onResponse(Call<APIRes> call, Response<APIRes> response) {
                if(!response.isSuccessful())
                {
                    Log.e("연결불가 : ",  "error code" + response.code());
                }

                APIRes apiRes = response.body();
                Log.d("연결성공 : ", response.body().toString());
            }

            @Override
            public void onFailure(Call<APIRes> call, Throwable t) {
                Log.e("연결 실패", t.getMessage());
            }
        });
    }

    /**
     * 5층 데이터 input method
     * */
    public void callFloor5(int[] AP) {
        Floor5JsonModel floor5JsonModel = new Floor5JsonModel(AP);

        Call<APIRes> call = HTTPManager.getApiService().postJsonF5(floor5JsonModel);

        call.enqueue(new Callback<APIRes>() {
            @Override
            public void onResponse(Call<APIRes> call, Response<APIRes> response) {
                if(!response.isSuccessful())
                {
                    Log.e("연결불가 : ",  "error code" + response.code());
                }

                APIRes apiRes = response.body();
                Log.d("연결성공 : ", response.body().toString());
            }

            @Override
            public void onFailure(Call<APIRes> call, Throwable t) {
                Log.e("연결 실패", t.getMessage());
            }
        });
    }
}
