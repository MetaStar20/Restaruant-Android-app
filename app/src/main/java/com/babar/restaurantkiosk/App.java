package com.babar.restaurantkiosk;

import android.app.Application;
import android.content.Context;

import com.babar.restaurantkiosk.managers.MyWorkerManager;

import androidx.work.WorkManager;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        //MyWorkerManager.getInstance().scheduleSendMessage();
    }
}
