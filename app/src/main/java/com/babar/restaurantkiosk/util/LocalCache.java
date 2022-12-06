package com.babar.restaurantkiosk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.babar.restaurantkiosk.App;

/***********************************
 * Created by Babar on 2/18/2020.  *
 ***********************************/
public class LocalCache {

    private final static String APP_CONFIGURATION = "configurations";
    private final static String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    public static final String deviceId = "device_id";
    private static final String STORAGE_FOLDER_KEY = "STORAGE_FOLDER_KEY";
    private static final String DURATION_KEY = "DURATION_KEY";
    private static final String TOUCH_WAITING_DURATION_KEY = "TOUCH_WAITING_DURATION_KEY";
    private static final String UPDATE_FLAG = "UPDATE_FLAG";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String DEVICE_PASSWORD_KEY = "DEVICE_PASSWORD_KEY";
    public static final String FILES_LIST = "FILES_LIST";

    private static boolean setConfiguration(String key, String value) {
        try {
            SharedPreferences.Editor editor = App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.apply();
            return true;
        } catch (Exception e) {
            DebugLog.console(e.toString() + "[LocalCache]: Exception occurred inside setConfiguration");
            return false;
        }
    }
    private static String getConfiguration(String key) {
        try {
            SharedPreferences pref = App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE);
            return pref.getString(key, null);
        } catch (Exception e) {
            DebugLog.console(e.toString() + "\r\n[LocalCache]: Exception occurred inside getConfigurations and Key is : " + key);
        }
        return null;
    }
    private static void setBooleanConfiguration(String key, boolean value) {
        try {
            SharedPreferences.Editor editor =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
            DebugLog.console(e.toString() + "[LocalCache]: Exception occurred inside setBooleanConfiguration");
        }
    }
    private static boolean getBooleanConfiguration(String key) {
        try {
            SharedPreferences pref =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE);
            return pref.getBoolean(key, false);
        } catch (Exception e) {
            DebugLog.console(e.toString() + "\r\n[LocalCache]: Exception occurred inside getBooleanConfigurations and Key is : " + key);
            return false;
        }
    }
    private static void setLongConfiguration(String key, long value) {
        try {
            SharedPreferences.Editor editor =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE).edit();
            editor.putLong(key, value);
            editor.apply();
        } catch (Exception e) {
            DebugLog.console(e.toString() + "[LocalCache]: Exception occurred inside setLongConfiguration");
        }
    }
    private static long getLongConfiguration(String key) {
        try {
            SharedPreferences pref =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE);
            return pref.getLong(key, 0);
        } catch (Exception e) {
            DebugLog.console(e.toString() + "\r\n[LocalCache]: Exception occurred inside getLongConfigurations and Key is : " + key);
        }
        return 0;
    }
    private static void setIntConfiguration(String key, int value) {
        try {
            SharedPreferences.Editor editor =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception e) {
            DebugLog.console(e.toString() + "[LocalCache]: Exception occurred inside setIntConfiguration");
        }
    }
    private static int getIntConfiguration(String key) {
        int conf;
        try {
            SharedPreferences pref =  App.getContext().getSharedPreferences(APP_CONFIGURATION, Context.MODE_PRIVATE);
            conf = pref.getInt(key, 0);
        } catch (Exception e) {
            DebugLog.console(e.toString() + "\r\n[LocalCache]: Exception occurred inside getIntConfiguration and Key is : " + key);
            conf = -1;
        }
        return conf;
    }

    public static boolean isFirstLaunchCompleted() {
        return getBooleanConfiguration(IS_FIRST_LAUNCH);
    }
    public static void setFirstLaunchCompleted(boolean firstLaunch) {
        setBooleanConfiguration(IS_FIRST_LAUNCH,firstLaunch);
    }


    public static void setDeviceId(String pin){
        setConfiguration(deviceId,pin);
    }
    public static String getDeviceId(){
        return getConfiguration(deviceId);
    }
    
    public static void setPassword(String password){
        setConfiguration(PASSWORD_KEY,password);
    }
    public static String getPassword(){
        String pass =  getConfiguration(PASSWORD_KEY);
        if(pass ==null)
            pass = RKUtil.DEFAULT_PASSWORD;
        return pass;
    }
    public static void setTouchWaitingDuration(int duration){
        setIntConfiguration(TOUCH_WAITING_DURATION_KEY,duration);
    }
    public static int getTouchWaitingDuration() {
        int duration =  getIntConfiguration(TOUCH_WAITING_DURATION_KEY);
        if(duration <1)
            duration = RKUtil.TOUCH_WAITING_DURATION_KEY;
        return duration;

    }

    public static void setDuration(int duration){
        setIntConfiguration(DURATION_KEY,duration);
    }
    public static int getDuration() {
        int duration =  getIntConfiguration(DURATION_KEY);
        if(duration <1)
            duration = RKUtil.DEFAULT_DURATION;
        return duration;

    }
    
    public static void setStorageFolder(String folder){
        setConfiguration(STORAGE_FOLDER_KEY,folder);
    }
    public static String getStorageFolder(){
        String storageFolder = getConfiguration(STORAGE_FOLDER_KEY);
        if(storageFolder==null)
            storageFolder = RKUtil.DEFAULT_STORAGE_FOLDER;
        return storageFolder;
    }

//    public static void setDevicePassword(String pass){
//        setConfiguration(DEVICE_PASSWORD_KEY,pass);
//    }
//
//    public static String getDevicePassword(){
//        String pass = getConfiguration(DEVICE_PASSWORD_KEY);
//        if(pass==null)
//            pass = RKUtil.DEFAULT_PASSWORD;
//        return pass;
//    }
//    public static void setDevicePassword(String pass){
//        setConfiguration(DEVICE_PASSWORD_KEY,pass);
//    }
//
//    public static String getDevicePassword(){
//        return getConfiguration(DEVICE_PASSWORD_KEY);
//    }


    public static void setFiles(String files){
        setConfiguration(FILES_LIST,files);
    }
    public static String getFiles(){
        return getConfiguration(FILES_LIST);
    }
}
