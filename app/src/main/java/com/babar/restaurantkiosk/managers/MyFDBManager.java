package com.babar.restaurantkiosk.managers;

import android.content.Context;

import com.babar.restaurantkiosk.models.UserInfoModel;
import com.babar.restaurantkiosk.util.RKUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class MyFDBManager {
    private static MyFDBManager ourInstance;
    private FirebaseDatabase firebaseDatabase;
    private Context context;
    public static MyFDBManager getInstance(Context context) {
        if(ourInstance==null) {
            ourInstance = new MyFDBManager(context);
        }
        return ourInstance;
    }

    private MyFDBManager(Context context) {
        this.context = context;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseDatabase.setPersistenceEnabled(true);

    }

    public Task<Void> updateUserInfo(UserInfoModel userInfoModel){
        return firebaseDatabase.getReference(userInfoModel.getDeviceId()).setValue(userInfoModel);
    }

    public DatabaseReference getUpdateFlagInfo(UserInfoModel userInfoModel){
        return firebaseDatabase.getReference(userInfoModel.getDeviceId() + "/updateFlag");
    }

    public DatabaseReference getWiFiFlagInfo(UserInfoModel userInfoModel){
        return firebaseDatabase.getReference(userInfoModel.getDeviceId() + "/wifiFlag");
    }
}
