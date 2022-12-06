package com.babar.restaurantkiosk.managers;



import com.babar.restaurantkiosk.App;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.worker.StorageWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/***********************************
 * Created by Babar on 8/28/2020.  *
 ***********************************/
public class MyWorkerManager {
    private static final MyWorkerManager ourInstance = new MyWorkerManager();

    public static final String SCHEDULE_SEND_MESSAGE_TAG = "SCHEDULE_SEND_MESSAGE_TAG";
    public static final String ONE_TIME_SEND_MESSAGE_TAG = "ONE_TIME_SEND_MESSAGE_TAG";


    public static MyWorkerManager getInstance() {
        return ourInstance;
    }

    private MyWorkerManager() {
        
    }

    public void scheduleSendMessage(){
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(StorageWorker.class, 5, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag(SCHEDULE_SEND_MESSAGE_TAG)
                .build();

        WorkManager.getInstance(App.getContext())
                .enqueueUniquePeriodicWork(SCHEDULE_SEND_MESSAGE_TAG, ExistingPeriodicWorkPolicy.KEEP,periodicWorkRequest);
    }

    public Operation oneTimeSendMessage(){
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest periodicWorkRequest = new OneTimeWorkRequest.Builder(StorageWorker.class)
                .setConstraints(constraints)
                .addTag(ONE_TIME_SEND_MESSAGE_TAG)
                .build();
        return WorkManager.getInstance(App.getContext()).enqueueUniqueWork(ONE_TIME_SEND_MESSAGE_TAG, ExistingWorkPolicy.KEEP,periodicWorkRequest);
    }

}
