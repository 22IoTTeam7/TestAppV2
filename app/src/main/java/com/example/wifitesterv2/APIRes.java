package com.example.wifitesterv2;

import com.google.gson.annotations.SerializedName;

public class APIRes {
    @SerializedName("result")
    private String result;

    public  String getResult(){
        return result;
    }
}
