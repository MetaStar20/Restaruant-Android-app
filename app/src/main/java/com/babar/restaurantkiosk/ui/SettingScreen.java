package com.babar.restaurantkiosk.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.babar.restaurantkiosk.R;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.util.RKUtil;

import androidx.appcompat.app.AppCompatActivity;


public class SettingScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView displayTime,storageSubTitle, deviceName, touchWaitingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);
        progressBar = findViewById(R.id.progressBar);
        displayTime = findViewById(R.id.displayTime);
        touchWaitingTime = findViewById(R.id.touchWaitingTime);
        storageSubTitle = findViewById(R.id.storageSubTitle);
        deviceName = findViewById(R.id.deviceName);
        String _deviceId = RKUtil.getDeviceId(this);
        deviceName.setText("Device Name : " +_deviceId);
        displayTime.setText("Display Time: "+LocalCache.getDuration()+" sec.");
        touchWaitingTime.setText("Touch Waiting Time: "+LocalCache.getTouchWaitingDuration()+" sec.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stopLockTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTime.setText("Display Time: "+LocalCache.getDuration()+" sec.");
        storageSubTitle.setText("admobi/"+LocalCache.getStorageFolder());
        touchWaitingTime.setText("Touch Waiting Time: "+LocalCache.getTouchWaitingDuration()+" sec.");
        String _deviceId = RKUtil.getDeviceId(this);
        deviceName.setText("Device Name : " +_deviceId);
    }

    public void changeStorageFolder(View v){
        startActivity(new Intent(this,ChangeStorageFolderScreen.class));
    }

    public void changeDefaultSetting(View v){
        startActivity(new Intent(this,ChangeDefaultSettingScreen.class));
    }
    public void openDashboard(View v) throws Exception{
//        if(checkPermissionForReadExtertalStorage()) {
//
//            if (RKUtil.getAppUsageStatus(this)) {
//                //startActivity(new Intent(this, DashboardScreen.class));
//                //setDefaultHome();
//                startKioskScreen();
//            } else {
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                startActivity(intent);
//            }
//        }else{
//            requestPermissionForReadExtertalStorage();
//        }
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    public void closeAdmobi(View v){
        this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("EXIT", true);
//        startActivity(intent);
//        this.finishAndRemoveTask();
    }





    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}