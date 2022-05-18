package com.example.wifitesterv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ScanResult> wifiList = new ArrayList();
    ArrayList wifiFormatList = new ArrayList();
    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    boolean wifiStartFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("permission","권한 요청");
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission. ACCESS_COARSE_LOCATION},
                1000);

            try {
                if(wifiStartFlag == true){
                    //TODO [와이파이 스캔이 실행 중인 경우]
                    Log.d("---","---");
                    Log.e("//===========//","================================================");
                    Log.d("","\n"+"[A_WifiScan > 실시간 와이파이 스캐닝이 이미 동작 중입니다 ...]");
                    Log.e("//===========//","================================================");
                    Log.d("---","---");
                }
                else {
                    //TODO [와이파이 스캔 시작]
                    WifiScanStart();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

    }

    //TODO ===== [와이파이 스캔 시작 실시] =====
    public void WifiScanStart(){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            //TODO [와이파이 스캔 시작 플래그 설정]
            wifiStartFlag = true;

            //TODO [Wifi Scna 관련 객체 선언]
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            //getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter); //TODO 리시버 시작
            registerReceiver(wifiScanReceiver, intentFilter); //TODO 리시버 시작

            //TODO [와이파이 스캔 상태 확인]
            boolean success = wifiManager.startScan();
            Log.d("---","---");
            if(!success) {
                Log.e("//===========//","================================================");
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작 할 수없는 상태]");
                Log.d("","\n"+"[로직 : 와이파이 스캔 기능이 정지 상태입니다. 와이파이 설정에서 비활성 후 다시 활성 필요]");
                Log.e("//===========//","================================================");
                Log.d("---","---");
                try {
                    //TODO 실시간 와이파이 스캐닝 종료
                    WifiScanStop();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                Log.w("//===========//","================================================");
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 진행 중인 상태]");
                Log.w("//===========//","================================================");
                Log.d("---","---");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            //TODO wifiManager.startScan(); 시 발동되는 메소드 (실시간 와이파이 목록 감지)
            try {
                //TODO [스캔 성공 여부 값 반환]
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                //TODO [실시간 와이파이 목록 스캔 성공한 경우]
                if (success) {
                    //TODO [기존에 저장된 리스트 초기화 실시]
                    if(wifiList != null){
                        if (wifiList.size() > 0){
                            wifiList.clear();
                        }
                    }
                    else {
                        wifiList = new ArrayList();
                    }
                    if(wifiFormatList != null){
                        if (wifiFormatList.size() > 0){
                            wifiFormatList.clear();
                        }
                    }
                    else {
                        wifiFormatList = new ArrayList();
                    }

                    //TODO [실시간 스캔된 와이파이 리스트 결과 얻어옴]
                    wifiList = wifiManager.getScanResults();
                    String ssid_format = "";
                    String mac_format = "";
                    int level_format;

                    //TODO [for 반복문을 수행하면서 데이터 확인 실시]
                    for(int i=0; i<wifiList.size(); i++){
                        //TODO [SSID 값 확인]
                        ssid_format = String.valueOf(wifiList.get(i).SSID.trim());
                        //ssid_format = ssid_format.replaceAll(" ",""); //공백 제거 코드

                        //TODO [MAC 값 확인]
                        mac_format = String.valueOf(wifiList.get(i).BSSID.trim());
                        //mac_format = mac_format.replaceAll(":",""); //구분자 제거 코드

                        level_format = Integer.valueOf(wifiList.get(i).level);

                        //TODO [JSON 형식으로 포맷 실시]
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("SSID", ssid_format);
                        jsonObject.put("MAC",mac_format);
                        jsonObject.put("Level",level_format);

                        //TODO [배열에 데이터 저장 실시]
                        if(wifiFormatList.contains(jsonObject.toString()) == false){ //TODO 중복 저장하지 않음
                            wifiFormatList.add(jsonObject.toString());
                        }
                    }

                    //TODO [저장된 데이터 확인 실시]
                    Log.d("---","---");
                    Log.w("//===========//","================================================");
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 성공]");
                    Log.d("","\n"+"스캔 개수 : "+String.valueOf(wifiFormatList.size())+"");
                    Log.d("","\n"+"스캔 데이터 : "+String.valueOf(wifiFormatList.toString())+"");
                    Log.w("//===========//","================================================");

                    //TODO [실시간 와이파이 스캐닝 종료]

                } else {
                    Log.d("---","---");
                    Log.e("//===========//","================================================");
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 실패]");
                    Log.e("//===========//","================================================");

                    //TODO [실시간 와이파이 스캐닝 종료]
                }
                Log.d("---","---");
                WifiScanStop();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //TODO ===== [와이파이 스캔 종료 실시] =====
    public void WifiScanStop(){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_WifiScan > WifiScanStop() 메소드 : 실시간 와이파이 스캐닝 종료]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            //TODO [실시간 와이파이 목록 스캔 플래그값 초기화]
            wifiStartFlag = false;

            //TODO [등록한 리시버 해제 실시]
            if(wifiScanReceiver != null){
                unregisterReceiver(wifiScanReceiver);
                //wifiScanReceiver = null;
                //wifiManager = null;
            }
            //unregisterReceiver(wifiScanReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

