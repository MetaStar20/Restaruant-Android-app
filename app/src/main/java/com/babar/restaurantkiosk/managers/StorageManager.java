package com.babar.restaurantkiosk.managers;

import android.content.Context;

import com.babar.restaurantkiosk.util.DebugLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/***********************************
 * Created by Babar on 12/12/2020.  *
 ***********************************/
public class StorageManager {
    private static StorageManager ourInstance;
    private FirebaseStorage firebaseStorage;
    private Context context;
    public static final String ROOT_REF = "admobi";

    public static StorageManager getInstance(Context context) {
        if(ourInstance==null) {
            ourInstance = new StorageManager(context);
        }
        return ourInstance;
    }

    private StorageManager(Context context) {
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    public Task<ListResult> getFolders(){
        return firebaseStorage.getReference(ROOT_REF).listAll();
    }

    public Task<ListResult> getFiles(String path){
        return firebaseStorage.getReference(ROOT_REF).child(path).listAll();
    }

    public Task<ListResult>getFolders(List<String> arrPaths){

        int size = arrPaths.size();
        StorageReference sRef =  firebaseStorage.getReference(ROOT_REF);
        for (int i = 0; i < size ; i++){
            DebugLog.console("arrpaths:" + arrPaths.get(size-i-1));
            sRef = sRef.child(arrPaths.get(size-i-1));
        }
        return  sRef.listAll();
    }
}
