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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<ScanResult> wifiList = new ArrayList();
    ArrayList wifiFormatList = new ArrayList();
    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    boolean wifiStartFlag = false;

    String[] floorSelect = {"2F", "4F", "5F"};
    String[] room2items = {"201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234"};
    String[] room4items = {"401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415", "416", "417", "418", "419", "420", "421", "422", "423", "424", "425", "426", "427", "428", "429", "430", "431", "432", "433", "434", "435", "436", "437", "438", "339"};
    String[] room5items = {"501",	"502",	"503",	"504",	"505",	"507",	"5072",	"508",	"509",	"510",	"511",	"512",	"513",	"514",	"515",	"516",	"517",	"518",	"519",	"520",	"521",	"522",	"523",	"524",	"525",	"526",	"527",	"528",	"529",	"530",	"531",	"532",	"533",	"534",	"535",	"536"};
    String[] resultArray = {};

    Integer floor = 0;
    Integer room = 0;

    //RSSI 세기를 저장하는 Array
    int[] RSSI_Array;
    //각 층별 MAC 주소 가져올 Class 생성
    Floor2List floor2 = new Floor2List();
    Floor4List floor4 = new Floor4List();
    Floor5List floor5 = new Floor5List();

    //Mac 주소를 담을 Array
    ArrayList<String> MAC_Array = new ArrayList<>(45);
    callRetrofit manager;

    Button btn;
    TextView location_txt;
    TextView UI_Log;

    boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI
        location_txt = findViewById(R.id.location);
        btn = findViewById(R.id.btn);
        UI_Log = findViewById(R.id.logTxt);


        //HTTP 통신 Manager
        manager = new callRetrofit();

        //[Floor selector] adapter

        Spinner spinner = (Spinner) findViewById(R.id.floorSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, floorSelect);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //[Room selector] adapter
        EditText spinner2 = (EditText) findViewById(R.id.roomSpinner);

        //TODO - select location action
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                if(position == 0){
                    resultArray = room2items;
                    floor = 2;
                    MAC_Array = floor2.AP2F;
                }
                if(position == 1){
                    resultArray = room4items;
                    floor = 4;
                    MAC_Array = floor4.AP4F;
                }

                if(position == 2){
                    resultArray = room5items;
                    floor = 5;
                    MAC_Array = floor5.AP5F;
                }

                /*ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                        MainActivity.this, android.R.layout.simple_spinner_item, resultArray);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(adapter2);

                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                        room = Integer.valueOf(resultArray[position]);
                        location_txt.setText("You are at "+ Integer.toString(floor) +"F "+ Integer.toString(room));
                    }
                    @Override
                    public void onNothingSelected(AdapterView adapterView){
                        location_txt.setText("Select Room!");
                    }
                });*/
            }

            @Override
            public void onNothingSelected(AdapterView adapterView){
                location_txt.setText("Select Floor First!");
            }
        });

        //여기서부터 WIFISCAN

        Log.d("permission","권한 요청");
        UI_Log.append("권한 요청\n");
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
                //층에 따라 다른 어레이 크기 할당
                room = Integer.parseInt(String.valueOf(spinner2.getText()));
                switch (floor){
                    case 2:
                        RSSI_Array = new int[34];
                        Arrays.fill(RSSI_Array,0);
                        RSSI_Array[33] = room;
                        break;
                    case 4:
                        RSSI_Array = new int[45];
                        Arrays.fill(RSSI_Array,0);
                        RSSI_Array[44] = room;

                        break;
                    case 5:
                        RSSI_Array = new int[50];
                        Arrays.fill(RSSI_Array,0);
                        RSSI_Array[49] = room;
                        break;
                }

                if(!isScanning) {
                    Toast.makeText(MainActivity.this, "Scan On", Toast.LENGTH_SHORT).show();
                    isScanning = true;
                    try {
                        if(wifiStartFlag == true){
                            //TODO [와이파이 스캔이 실행 중인 경우]
                            Log.d("","\n"+"[A_WifiScan > 실시간 와이파이 스캐닝이 이미 동작 중입니다 ...]");
                            UI_Log.append("[A_WifiScan > 실시간 와이파이 스캐닝이 이미 동작 중입니다 ...]\n");
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
                    Toast.makeText(MainActivity.this, "Scan Off", Toast.LENGTH_SHORT).show();
                    isScanning = false;
                }
            }
        });
    }

    //TODO ===== [와이파이 스캔 시작 실시] =====
    public void WifiScanStart(){
        Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작]");
        UI_Log.append("[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작]\n");
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

                UI_Log.append("[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작 할 수없는 상태]\n");
                UI_Log.append("[로직 : 와이파이 스캔 기능이 정지 상태입니다. 와이파이 설정에서 비활성 후 다시 활성 필요]\n");

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
                UI_Log.append("[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 진행 중인 상태]\n");
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
                        ssid_format = wifiList.get(i).SSID;
                    //TODO SSID 값 바꿔주기! - 완료
                    //[SSID 값 확인] - Gachon Free WiFi 만 받도록!
                        if(ssid_format.equals("GC_free_WiFi")){

                            mac_format = wifiList.get(i).BSSID.trim();
                            mac_format = mac_format.substring(9);
                            //TODO [MAC 값 확인] - 해당 인덱스에 RSSI 값 기록
                            if(MAC_Array.contains(mac_format)){
                                level_format = Integer.valueOf(wifiList.get(i).level);
                                Log.d("Test", Integer.toString(level_format));
                                RSSI_Array[MAC_Array.indexOf(mac_format)] = level_format;

                            //여기서부터는 로그 출력용 Json 데이터 저장
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
                    Log.d("","\n"+"RSSI 값 매핑 : "+Arrays.toString(RSSI_Array));
                    //TODO UI에 기록
                    UI_Log.append("[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 성공]\n");
                    UI_Log.append("스캔 개수 : "+ wifiFormatList.size() +""+"\n");
                    UI_Log.append("스캔 데이터 : "+ wifiFormatList.toString() +""+"\n");
                    UI_Log.append("MAC 주소    : "+ MAC_Array.toString()+"\n");
                    UI_Log.append("RSSI 값 매핑 : "+Arrays.toString(RSSI_Array)+"\n");

                    //TODO 이걸 이제 AP에 넘겨야지
                    switch(floor){
                        case 2:
                            manager.callFloor2(RSSI_Array);
                            break;
                        case 4:
                            manager.callFloor4(RSSI_Array);
                            break;
                        case 5:
                            manager.callFloor5(RSSI_Array);
                            break;
                    }

                    //TODO [실시간 와이파이 스캐닝 종료]

                } else {
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 실패]");
                    UI_Log.append("[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 실패]\n");
                    //TODO [실시간 와이파이 스캐닝 종료]
                }

                Log.d("---","---");
                UI_Log.append("---");

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
        UI_Log.append("[A_WifiScan > WifiScanStop() 메소드 : 실시간 와이파이 스캐닝 종료]\n");
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


