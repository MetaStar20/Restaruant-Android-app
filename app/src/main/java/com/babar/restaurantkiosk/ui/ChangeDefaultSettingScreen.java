package com.babar.restaurantkiosk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.babar.restaurantkiosk.R;

import com.babar.restaurantkiosk.models.UserInfoModel;
import com.babar.restaurantkiosk.ui.dialogues.ChangeDisplayTimeDialog;
import com.babar.restaurantkiosk.ui.dialogues.ChangePasswordDialog;
import com.babar.restaurantkiosk.ui.dialogues.ChangeTouchWaitingTimeDialog;
import com.babar.restaurantkiosk.util.DebugLog;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.util.RKUtil;

public class ChangeDefaultSettingScreen extends AppCompatActivity implements ChangeDisplayTimeDialog.DisplayTimeListener, ChangePasswordDialog.PassListener, ChangeTouchWaitingTimeDialog.DisplayTimeListener {
    private TextView displayTime;
    private TextView deviceName;
    private TextView touchWaitingTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_default_setting_screen);
        displayTime = findViewById(R.id.displayTime);
        deviceName = findViewById(R.id.deviceName);
        touchWaitingTime = findViewById(R.id.touchWaitingTime);
        displayTime.setText("Display Time : "+LocalCache.getDuration()+" sec.");
        touchWaitingTime.setText("Touch Waiting Time: "+LocalCache.getTouchWaitingDuration()+" sec.");
        String _deviceId = RKUtil.getDeviceId(this);
        deviceName.setText("Device Name : " +_deviceId);
    }

    public void touchWaitingTimeChange(View v){
        ChangeTouchWaitingTimeDialog changeTouchWaitingTimeDialog = new ChangeTouchWaitingTimeDialog(this,this);
        changeTouchWaitingTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changeTouchWaitingTimeDialog.show();
    }

    public void changeDisplayTime(View v){
        ChangeDisplayTimeDialog changeDisplayTimeDialog = new ChangeDisplayTimeDialog(this,this);
        changeDisplayTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changeDisplayTimeDialog.show();
    }
    public void changePassword(View v){
        ChangePasswordDialog changeDisplayTimeDialog = new ChangePasswordDialog(this,this);
        changeDisplayTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changeDisplayTimeDialog.show();
    }

    @Override
    public void onChange(int value) {
        DebugLog.console("[ChangeDefaultSettingScreen] inside onChange() value : "+value);
        displayTime.setText("Display Time: "+value+" sec.");
        LocalCache.setDuration(value);
        String deviceId = RKUtil.getDeviceId(this);
        DebugLog.console("[SettingScreen] inside onCreate() deviceId : " + deviceId);
        UserInfoModel userInfoModel = new UserInfoModel(LocalCache.getPassword(), LocalCache.getDuration(), LocalCache.getStorageFolder(), deviceId, LocalCache.getTouchWaitingDuration(), RKUtil.UPDATE_FLAG);
        RKUtil.uploadDeviceInfo(this, userInfoModel);
    }

    @Override
    public void onTouchWaitingChange(int value) {
        DebugLog.console("[ChangeDefaultSettingScreen] inside onTouchWaitingChange() value : "+value);
        touchWaitingTime.setText("Touch Waiting Time: "+value+" sec.");
        LocalCache.setTouchWaitingDuration(value);
        String deviceId = RKUtil.getDeviceId(this);
        DebugLog.console("[SettingScreen] inside onCreate() deviceId : " + deviceId);
        UserInfoModel userInfoModel = new UserInfoModel(LocalCache.getPassword(), LocalCache.getDuration(), LocalCache.getStorageFolder(), deviceId, LocalCache.getTouchWaitingDuration(), RKUtil.UPDATE_FLAG);
        RKUtil.uploadDeviceInfo(this, userInfoModel);
    }

    @Override
    public void onPassChange(String value) {
        DebugLog.console("[ChangeDefaultSettingScreen] inside onPassChange() "+value);
        LocalCache.setPassword(value);
        String deviceId = RKUtil.getDeviceId(this);
        DebugLog.console("[SettingScreen] inside onCreate() deviceId : " + deviceId);
        UserInfoModel userInfoModel = new UserInfoModel(LocalCache.getPassword(), LocalCache.getDuration(), LocalCache.getStorageFolder(), deviceId, LocalCache.getTouchWaitingDuration(), RKUtil.UPDATE_FLAG);
        RKUtil.uploadDeviceInfo(this, userInfoModel);
    }

    public void closeDefault(View v){
        finish();
    }
    public void saveDefault(View v){
        finish();
    }
}