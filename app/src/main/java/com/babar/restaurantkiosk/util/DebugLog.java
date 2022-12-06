package com.babar.restaurantkiosk.util;

import android.util.Log;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class DebugLog {
    private static final String TAG = "RKLOG";
    public static void console(String msg){
        Log.d(TAG, msg);
    }

    public static void tagConsole(String tag,String msg){
        Log.d(tag, msg);
    }
}
