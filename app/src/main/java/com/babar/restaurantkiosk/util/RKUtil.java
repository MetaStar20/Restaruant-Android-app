package com.babar.restaurantkiosk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;

import com.babar.restaurantkiosk.managers.MyFDBManager;
import com.babar.restaurantkiosk.managers.MyWorkerManager;
import com.babar.restaurantkiosk.models.UserInfoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class RKUtil {

    public static final String DEFAULT_STORAGE_FOLDER = "test";
    public static final String DEFAULT_PASSWORD = "01101216";
    public static final int DEFAULT_DURATION = 5;
    public static final int TOUCH_WAITING_DURATION_KEY = 10;
    public static final int UPDATE_FLAG = 0;

    public static final String FS_STORAGE_COLLECTION = "storage";

    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
    }

    public static void getUpdateFlag(Context context, UserInfoModel userInfoModel) {
//        String deviceId = getDeviceId(context);
        MyFDBManager.getInstance(context).getUpdateFlagInfo(userInfoModel).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DebugLog.console("[MainActivity] Kkkkk() ");
                // This method is called once with the initial value and again
                        // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.i("updateFlag", "Value is: " + value);
//                if(value == 1)
                {
                    ConnectivityManager cm =
                            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if (isConnected)
                        MyWorkerManager.getInstance().oneTimeSendMessage();
//                    Log.i("updateFlag1", "Value is: " + value);
//                    UserInfoModel userInfoModel = new UserInfoModel(LocalCache.getPassword(), LocalCache.getDuration(), LocalCache.getStorageFolder(), deviceId, LocalCache.getTouchWaitingDuration(), RKUtil.UPDATE_FLAG);
//                    RKUtil.uploadDeviceInfo(context, userInfoModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("updateFlag", "Failed to read value.", error.toException());
            }
        });
    }



    public static void uploadDeviceInfo(Context context, UserInfoModel userInfoModel) {
        MyFDBManager.getInstance(context).updateUserInfo(userInfoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DebugLog.console("[SettingScreen] inside onSuccess() ");
                LocalCache.setFirstLaunchCompleted(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DebugLog.console("[SettingScreen] inside onFailure() " + e.toString());
            }
        });
    }


    public static String getFileTypeByContentType(String contentType) {
        switch (contentType){
            case "image/jpeg":
            case "image/png":
            case "image/jpg":
                return "IMAGE";

            case "video/mp4":
            case "video/3gpp":
                return "VIDEO";
        }

        return null;
    }

}
