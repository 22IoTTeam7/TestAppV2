package com.example.wifitesterv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<ScanResult> wifiList = new ArrayList();
    ArrayList wifiFormatList = new ArrayList();
    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    boolean wifiStartFlag = false;

    ImageButton btn;

    TextView mac_txt;
    TextView ssid_txt;
    TextView link_txt;
    EditText location_txt;
    Integer location_store;

    int[] SSID_Array = new int[45];
    List<String> MAC_Array = new ArrayList<>();
    String[] things_to_add = {"94:64:24:a0:34:f0", "94:64:24:9e:72:f2", "94:64:24:a1:00:c2", "94:64:24:9f:03:e2", "94:64:24:9e:fa:82", "94:64:24:9e:7e:42",	"94:64:24:9e:7e:62",	"94:64:24:a0:89:f2",	"94:64:24:a0:8a:12",	"94:64:24:9d:d8:72",	"94:64:24:9d:d8:52",	"94:64:24:a1:08:b2",	"94:64:24:9e:24:30",	"94:64:24:9f:38:92",	"94:64:24:a0:3d:02",	"94:64:24:a1:08:d2",	"94:64:24:9f:ba:b2",	"94:64:24:a1:89:12",	"94:64:24:9d:f1:32",	"94:64:24:9f:c4:42",	"94:64:24:9f:c4:22",	"94:64:24:9d:f1:12",	"94:64:24:9f:ea:90",	"94:64:24:9f:fb:12",	"94:64:24:9f:ea:b0",	"94:64:24:9f:83:72",	"94:64:24:9f:83:92",	"94:64:24:9e:8a:72",	"94:64:24:9e:8a:92",	"94:64:24:9e:72:d2",	"94:64:24:9e:3f:00",	"94:64:24:9e:3e:e0",	"94:64:24:a1:6f:92",	"94:64:24:a0:1f:70",	"94:64:24:a0:1f:90",	"94:64:24:9f:03:c2",	"94:64:24:9e:05:72",	"94:64:24:9e:05:52",	"94:64:24:a1:22:60",	"94:64:24:a1:22:80",	"94:64:24:a0:fe:c0",	"94:64:24:a0:fe:e0",	"94:64:24:9e:2d:02",	"94:64:24:a0:34:d0"};

    boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Arrays.fill(SSID_Array,0);

        //TODO 이건 나중에 지우자
        MAC_Array.add("02:15:b2:00:01:00");
        //TODO MAC 주소 값 넣기
        Collections.addAll(MAC_Array, things_to_add);

        location_txt = findViewById(R.id.location);
        ssid_txt = findViewById(R.id.ssid_slot);
        mac_txt = findViewById(R.id.mac_slot);
        link_txt = findViewById(R.id.rssi_slot);

        Bitmap off_pic = BitmapFactory.decodeResource(getResources(), R.drawable.off);
        Bitmap on_pic = BitmapFactory.decodeResource(getResources(), R.drawable.on);
        Bitmap off = Bitmap.createScaledBitmap( off_pic, 150, 150, false);
        Bitmap on = Bitmap.createScaledBitmap( on_pic, 150, 150, false);

        btn = findViewById(R.id.btn);
        btn.setImageBitmap(off);

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
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                location_store = Integer.parseInt(location_txt.getText().toString());
                SSID_Array[44] = location_store;
                if(!isScanning) {
                    btn.setImageBitmap(on);
                    isScanning = true;
                    try {
                        if(wifiStartFlag == true){
                            //TODO [와이파이 스캔이 실행 중인 경우]
                            Log.d("","\n"+"[A_WifiScan > 실시간 와이파이 스캐닝이 이미 동작 중입니다 ...]");
                        }
                        else {
                            //TODO [와이파이 스캔 시작]
                            WifiScanStart();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                } else{
                    btn.setImageBitmap(off);

                    isScanning = false;
                }
            }
        });
    }

    //TODO ===== [와이파이 스캔 시작 실시] =====
    public void WifiScanStart(){
        Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작]");

        try {
            //TODO [와이파이 스캔 시작 플래그 설정]
            wifiStartFlag = true;

            //TODO [Wifi Scan 관련 객체 선언]
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            //getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter); //TODO 리시버 시작
            registerReceiver(wifiScanReceiver, intentFilter); //TODO 리시버 시작

            //TODO [와이파이 스캔 상태 확인]
            boolean success = wifiManager.startScan();

            if(!success) {
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작 할 수없는 상태]");
                Log.d("","\n"+"[로직 : 와이파이 스캔 기능이 정지 상태입니다. 와이파이 설정에서 비활성 후 다시 활성 필요]");

                try {
                    //TODO 실시간 와이파이 스캐닝 종료
                    WifiScanStop();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 진행 중인 상태]");
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
                    int level_format = 0;



                    //TODO [for 반복문을 수행하면서 데이터 확인 실시]
                    for(int i=0; i<wifiList.size(); i++){

                        //TODO [SSID 값 확인]
                        ssid_format = wifiList.get(i).SSID.trim();
//TODO SSID 값 바꿔주기!
                        if(ssid_format.equals("AndroidWifi")){

                            //TODO [MAC 값 확인]
                            mac_format = wifiList.get(i).BSSID.trim();
                            Log.d("","\n"+"지정 와이파이 확인 성공");
                            if(MAC_Array.contains(mac_format)){
                                Log.d("","\n"+"와이파이 Mac 주소 비교 성공");
                                level_format = Integer.valueOf(wifiList.get(i).level);

                                SSID_Array[MAC_Array.indexOf(mac_format)] = level_format;

                                ssid_txt.setText(ssid_format);
                                mac_txt.setText(mac_format);
                                link_txt.setText(Integer.toString(level_format));

                                //TODO [JSON 형식으로 포맷 실시]
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("SSID", ssid_format);
                                jsonObject.put("MAC",mac_format);
                                jsonObject.put("Level",level_format);

                                //TODO [배열에 데이터 저장 실시]
                                if(wifiFormatList.contains(jsonObject.toString()) == false) { //TODO 중복 저장하지 않음
                                    wifiFormatList.add(jsonObject.toString());
                                }
                            }
                        }
                    }

                    //TODO [저장된 데이터 확인 실시]
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 성공]");
                    Log.d("","\n"+"스캔 개수 : "+ wifiFormatList.size() +"");
                    Log.d("","\n"+"스캔 데이터 : "+ wifiFormatList.toString() +"");
                    Log.d("","\n"+"MAC 주소    : "+ MAC_Array.toString());
                    Log.d("","\n"+"SSID 값 매핑 : "+Arrays.toString(SSID_Array));


                    //TODO [실시간 와이파이 스캐닝 종료]

                } else {
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 실패]");
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
        Log.d("","\n"+"[A_WifiScan > WifiScanStop() 메소드 : 실시간 와이파이 스캐닝 종료]");
        try {
            //TODO [실시간 와이파이 목록 스캔 플래그값 초기화]
            wifiStartFlag = false;

            //TODO [등록한 리시버 해제 실시]
            if(wifiScanReceiver != null){
                unregisterReceiver(wifiScanReceiver);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


