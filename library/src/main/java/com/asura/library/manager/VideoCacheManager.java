package com.asura.library.manager;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/***********************************
 * Created by Babar on 12/14/2020.  *
 ***********************************/
public class VideoCacheManager {
    private static  VideoCacheManager ourInstance;
    private static HttpProxyCacheServer proxy;
    private Context context;
    public static VideoCacheManager getInstance(Context context) {
        if(ourInstance==null)
            ourInstance = new VideoCacheManager(context);
        return ourInstance;
    }

    private VideoCacheManager(Context context) {
        this.context = context;
        proxy = new HttpProxyCacheServer(context);
    }
    public HttpProxyCacheServer getProxy(){
        return proxy;
    }
}
