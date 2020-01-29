/*
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wifirttscan;

import static com.example.android.wifirttscan.AccessPointRangingResultsActivity.SCAN_RESULT_EXTRA;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.wifirttscan.MyAdapter.ScanResultClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays list of Access Points enabled with WifiRTT (to check distance). Requests location
 * permissions if they are not approved via secondary splash screen explaining why they are needed.
 */
public class MainActivity extends AppCompatActivity implements ScanResultClickListener {

    private static final String TAG = "MainActivity";
    private boolean mLocationPermissionApproved = false;

    List<ScanResult> mAccessPointsSupporting80211mc;

    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;

    private TextView mOutputTextView;
    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputTextView = findViewById(R.id.access_point_summary_text_view);
        mRecyclerView = findViewById(R.id.recycler_view);

        // Improve performance if you know that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAccessPointsSupporting80211mc = new ArrayList<>();

        mAdapter = new MyAdapter(mAccessPointsSupporting80211mc, this);
        mRecyclerView.setAdapter(mAdapter); //RecyclerViewにタップ判定をつける

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        mLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        registerReceiver(
                mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);
    }

    private void logToUi(final String message) {
        if (!message.isEmpty()) {
            Log.d(TAG, message);
            mOutputTextView.setText(message);
        }
    }

    @Override
    public void onScanResultItemClick(ScanResult scanResult) {  //リストのAP名がクリックされた際の処理
        /*
        本来はリストのAP名がタップされた場合の処理だが、今はAPのスキャンを行った際（ onClickFindDistancesToAccessPoints() ）に全てのRTT対応APに対してこの処理を行うようにしている
        "scanResult"がどういう構造になっているかは現状不明
         */

        Log.d(TAG, "onScanResultItemClick(): ssid: " + scanResult.SSID);

        Intent intent = new Intent(this, AccessPointRangingResultsActivity.class);
        intent.putExtra(SCAN_RESULT_EXTRA, scanResult);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //AccessPointRangingResultsActivityから戻ってきた場合
            case (1):
                if (resultCode == RESULT_OK) {
                    Log.d("Edit Text", data.getStringExtra("range"));
                    Log.d("Edit Text", data.getStringExtra("テスト"));//GithubとAndroidStudioを連携させてみたのでテスト
                    Log.d(TAG, "Ranging Succeeded.");

                    /*
                    ここからSwitch文3つで、TextViewにAP名と距離を記録
                    できることならリストに投げ込むか何かの方法でスキャンの結果が返ってきたAP名と距離を整理したい。
                    APが増えた場合等に対する冗長性の確保が課題
                    TextViewを都合のいい変数扱いしてる自覚はある
                    前回のスキャン結果は不要
                     */

                    switch(data.getStringExtra("name")){
                        case "Positioning-01":
                            TextView textview1=(TextView)findViewById(R.id.textView3);
                            textview1.setText(data.getStringExtra("name"));
                            TextView textview2=(TextView)findViewById(R.id.textView6);
                            textview2.setText(data.getStringExtra("range"));
                            TextView textview7=(TextView)findViewById(R.id.textView9);
                            textview7.setText(data.getStringExtra("RSSI"));
                            Log.d(TAG,"Range to :"+data.getStringExtra("name")+" : "+data.getStringExtra("range"));
                            break;

                        case "Positioning-02":
                            TextView textview3=(TextView)findViewById(R.id.textView2);
                            textview3.setText(data.getStringExtra("name"));
                            TextView textview4=(TextView)findViewById(R.id.textView5);
                            textview4.setText(data.getStringExtra("range"));
                            TextView textview8=(TextView)findViewById(R.id.textView8);
                            textview8.setText(data.getStringExtra("RSSI"));
                            Log.d(TAG,"Range to :"+data.getStringExtra("name")+" : "+data.getStringExtra("range"));
                            break;

                        case "Positioning-03":
                            TextView textview5=(TextView)findViewById(R.id.textView1);
                            textview5.setText(data.getStringExtra("name"));
                            TextView textview6=(TextView)findViewById(R.id.textView4);
                            textview6.setText(data.getStringExtra("range"));
                            TextView textview9=(TextView)findViewById(R.id.textView7);
                            textview9.setText(data.getStringExtra("RSSI"));
                            Log.d(TAG,"Range to :"+data.getStringExtra("name")+" : "+data.getStringExtra("range"));
                            break;

                        default:

                            break;

                    }

                    Log.d(TAG,"Range to :"+data.getStringExtra("name")+" : "+data.getStringExtra("range"));

                } else if (resultCode == RESULT_CANCELED) {
                    //キャンセルボタンを押して戻ってきたときの処理
                } else {
                    //その他
                }
                break;
            default:
                break;
        }
    }

    public void onClickFindDistancesToAccessPoints(View view) { //下部ボタンを押した際にAPをスキャンする処理
        if (mLocationPermissionApproved) {
            logToUi(getString(R.string.retrieving_access_points));
            mWifiManager.startScan();

        } else {
            // On 23+ (M+) devices, fine location permission not granted. Request permission.
            Intent startIntent = new Intent(this, LocationPermissionRequestActivity.class);
            startActivity(startIntent);
        }
    }
    public void onClickDrawPosition(View v){
        /*
        Drawボタンをクリックしたら、描画用インテントにAP名と距離を投げる
        距離はfloatで扱えるならそうしたい
        RSSIは現状使用していない値だが将来的に使う予定があるので必要な時に値を取り出せるようにしたい
        textViewという都合のいい変数から値を取り出す
         */
        Intent i = new Intent(this, SubActivity.class);

        TextView AP1_name = (TextView) findViewById(R.id.textView1);
        i.putExtra("AP1name",AP1_name.getText().toString());
        TextView AP1_range = (TextView) findViewById(R.id.textView4);
        i.putExtra("AP1range",AP1_range.getText().toString());

        TextView AP2_name = (TextView) findViewById(R.id.textView2);
        i.putExtra("AP2name",AP2_name.getText().toString());
        TextView AP2_range = (TextView) findViewById(R.id.textView5);
        i.putExtra("AP2range",AP2_range.getText().toString());

        TextView AP3_name = (TextView) findViewById(R.id.textView3);
        i.putExtra("AP3name",AP3_name.getText().toString());
        TextView AP3_range = (TextView) findViewById(R.id.textView6);
        i.putExtra("AP3range",AP3_range.getText().toString());

        startActivity(i);
    }

    public void onClickRanging(){
        if (mLocationPermissionApproved) {
            logToUi(getString(R.string.retrieving_access_points));
            mWifiManager.startScan();

        } else {
            // On 23+ (M+) devices, fine location permission not granted. Request permission.
            Intent startIntent = new Intent(this, LocationPermissionRequestActivity.class);
            startActivity(startIntent);
        }

    }

    //　https://developer.android.com/guide/topics/connectivity/wifi-rtt?hl=ja#java　にブロードキャストレシーバを実装しろと書いてある
    private class WifiScanReceiver extends BroadcastReceiver {

        private List<ScanResult> find80211mcSupportedAccessPoints(
                @NonNull List<ScanResult> originalList) {
            List<ScanResult> newList = new ArrayList<>();

            for (ScanResult scanResult : originalList) {

                if (scanResult.is80211mcResponder()) {
                    newList.add(scanResult);        //802.11mc利用可能なＡＰをリストに追加する
                    onScanResultItemClick(scanResult);  //RTT利用可能なAPに対して距離を測定 →インテント変えなくてもできるならそうしたい

                }

                if (newList.size() >= RangingRequest.getMaxPeers()) {
                    break;
                }
            }

            return newList;
        }

        // This is checked via mLocationPermissionApproved boolean
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {

            List<ScanResult> scanResults = mWifiManager.getScanResults();

            if (scanResults != null) {

                if (mLocationPermissionApproved) {
                    mAccessPointsSupporting80211mc = find80211mcSupportedAccessPoints(scanResults);

                    mAdapter.swapData(mAccessPointsSupporting80211mc);

                    logToUi(
                            scanResults.size()
                                    + " APs discovered, "
                                    + mAccessPointsSupporting80211mc.size()
                                    + " RTT capable.");

                } else {
                    // TODO (jewalker): Add Snackbar regarding permissions
                    Log.d(TAG, "Permissions not allowed.");
                }
            }
        }
    }
}
