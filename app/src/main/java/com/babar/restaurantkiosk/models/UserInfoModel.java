package com.babar.restaurantkiosk.models;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class UserInfoModel {
    private String password;
    private int duration;
    private String uploadFolder;
    private String deviceId;
    private int touchWaitingTime;
    private int updateFlag;

    public UserInfoModel(String password, int duration, String uploadFolder, String deviceId, int touchWaitingTime, int updateFlag) {
        this.password = password;
        this.duration = duration;
        this.uploadFolder = uploadFolder;
        this.deviceId = deviceId;
        this.touchWaitingTime = touchWaitingTime;
        this.updateFlag = updateFlag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getTouchWaitingTime() {
        return touchWaitingTime;
    }

    public void setTouchWaitingTime(int touchWaitingTime) {
        this.touchWaitingTime = touchWaitingTime;
    }

    public int getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(int updateFlag) {
        this.updateFlag = updateFlag;
    }
}
