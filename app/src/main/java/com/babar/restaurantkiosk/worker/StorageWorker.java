package com.babar.restaurantkiosk.worker;

import android.content.Context;
import android.net.Uri;

import com.babar.restaurantkiosk.App;
import com.babar.restaurantkiosk.managers.StorageManager;
import com.babar.restaurantkiosk.models.AdModel;
import com.babar.restaurantkiosk.util.DebugLog;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.util.RKUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/***********************************
 * Created by Babar on 12/17/2020.  *
 ***********************************/
public class StorageWorker extends Worker {
    public StorageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DebugLog.console("[StorageWorker] inside doWork() ");
            Task<ListResult> files = StorageManager.getInstance(App.getContext()).getFiles(LocalCache.getStorageFolder());
            Tasks.await(files);
            List<StorageReference> filesResult = files.getResult().getItems();
            List<AdModel> adModels = new ArrayList<>();
            for (StorageReference storageReference : filesResult) {
                AdModel adModel = new AdModel();
                Task<Uri> downloadURL = storageReference.getDownloadUrl();
                Uri downloadResult = Tasks.await(downloadURL);
                Task<StorageMetadata> metaData = storageReference.getMetadata();
                StorageMetadata fileMetaData = Tasks.await(metaData);
                DebugLog.console("[StorageWorker] inside doWork() "+fileMetaData.getContentType());
                String type = RKUtil.getFileTypeByContentType(fileMetaData.getContentType());
                adModel.setUrl(downloadResult.toString());
                adModel.setContentType(type);
                adModel.setName(fileMetaData.getName());
                adModels.add(adModel);
            }
            String gson = new Gson().toJson(adModels);
            LocalCache.setFiles(gson);
            setProgressAsync(new Data.Builder().putString("status","success").build());
        } catch (Exception e) {
            DebugLog.console("[StorageWorker] inside doWork() Exception : " + e.toString());
        }
        return Result.success();
    }
}
