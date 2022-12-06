package com.babar.restaurantkiosk.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.asura.library.posters.Poster;
import com.asura.library.posters.RemoteImage;
import com.asura.library.posters.RemoteVideo;
import com.asura.library.views.PosterSlider;
import com.babar.restaurantkiosk.R;
import com.babar.restaurantkiosk.managers.MyWorkerManager;
import com.babar.restaurantkiosk.models.AdModel;
import com.babar.restaurantkiosk.models.UserInfoModel;
import com.babar.restaurantkiosk.ui.dialogues.AdminPasswordDialog;
import com.babar.restaurantkiosk.util.DebugLog;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.util.RKUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


public class MainActivity extends AppCompatActivity implements AdminPasswordDialog.AdminPassListener {
    private int count = 0;
    private long startMillis = 0;
    private int TAP_COUNT = 10;
    private int WAIT_SECONDS = 15 * 1000;
    private int DEFAULT_VIDEO_DISPLAY_TIME = 500*1000;
    private int DEFAULT_DISPLAY_TIME = 5*1000;
    List<Poster> posters = new ArrayList<>();

    private PosterSlider posterSlider;
    private ConstraintLayout root;
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        currentApiVersion = android.os.Build.VERSION.SDK_INT;
//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        // This work only for android 4.4+
//        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//
//            // Code below is to handle presses of Volume up or Volume down.
//            // Without this, after pressing volume buttons, the navigation bar will
//            // show up and won't hide
//            final View decorView = getWindow().getDecorView();
//            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
//                    {
//                        decorView.setSystemUiVisibility(flags);
//                    }
//                }
//            });
//        }

        // This allow that app is with lock stte only in the login screen.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        fullScreen();
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        posterSlider = (PosterSlider) findViewById(R.id.poster_slider);
        String deviceId = RKUtil.getDeviceId(this);
        UserInfoModel userInfoModel = new UserInfoModel(RKUtil.DEFAULT_PASSWORD, RKUtil.DEFAULT_DURATION, RKUtil.DEFAULT_STORAGE_FOLDER, deviceId, RKUtil.TOUCH_WAITING_DURATION_KEY, RKUtil.UPDATE_FLAG);
//        if (!LocalCache.isFirstLaunchCompleted()) {
//            String deviceId = RKUtil.getDeviceId(this);
//            DebugLog.console("[SettingScreen] inside onCreate() deviceId : " + deviceId);
//            UserInfoModel userInfoModel = new UserInfoModel(RKUtil.DEFAULT_PASSWORD, RKUtil.DEFAULT_DURATION, RKUtil.DEFAULT_STORAGE_FOLDER, deviceId);
//            RKUtil.uploadDeviceInfo(this, userInfoModel);
//        }
//            RKUtil.getUpdateFlag(this, userInfoModel);
        Log.i("flag=>", String.valueOf(LocalCache.isFirstLaunchCompleted()));
        DEFAULT_DISPLAY_TIME = LocalCache.getDuration() * 1000;
        WAIT_SECONDS = LocalCache.getTouchWaitingDuration() * 1000;
//        startActivity(new Intent(MainActivity.this,SettingScreen.class));
//        finish();
        String adFiles = LocalCache.getFiles();
        if (adFiles != null) {
            updateUI();
        }
        refreshFiles();

        if (!isMyAppLauncherDefault()){
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
//
////            finish();
//            Log.d("TAG", "launchAppChooser()");
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//            PackageManager packageManager = this.getPackageManager();
//            ComponentName componentName = new ComponentName(this, com.babar.restaurantkiosk.ui.FakeLauncherActivity.class);
//            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//
//            Intent selector = new Intent(Intent.ACTION_MAIN);
//            selector.addCategory(Intent.CATEGORY_HOME);
//            selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            this.startActivity(selector);
//
//            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
//
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
	
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        count = 0;
        // Simply Do noting!
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        count = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String adFiles = LocalCache.getFiles();
        if (adFiles != null) {
            updateUI();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask();
        }

    }

    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    private void fullScreen() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_HOME:
                case KeyEvent.KEYCODE_BACK:
                    count = 0;
                    break;
                case KeyEvent.KEYCODE_APP_SWITCH:
                    Log.i("KEYCODE_APP_SWITCH", "KEYCODE_APP_SWITCH");
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        DebugLog.console("[MainActivity] inside asdasdassdfjfglkjfjg() ");
        long time = System.currentTimeMillis();
        if (startMillis == 0 || (time - startMillis > WAIT_SECONDS) || count>=TAP_COUNT) {
            startMillis = time;
            count = 1;
        } else {
            count++;
        }
        if (count >= TAP_COUNT) {
            showDialog();
        }
    }

    private void showDialog() {
        AdminPasswordDialog changeDisplayTimeDialog = new AdminPasswordDialog(this, this);
        changeDisplayTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changeDisplayTimeDialog.show();
    }
    @Override
    public void onPassword(String value) {
        startActivity(new Intent(MainActivity.this, SettingScreen.class));
        finish();
    }

    private void refreshFiles() {

        WorkManager.getInstance(this).getWorkInfosByTagLiveData(MyWorkerManager.SCHEDULE_SEND_MESSAGE_TAG).observe(this,workInfos -> {
           if(workInfos!=null && workInfos.size()>0) {
               DebugLog.console("[MainActivity] inside refreshFiles() " + workInfos.get(0));
               if (workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED) {
                   updateUI();
               }
           }
        });
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(MyWorkerManager.ONE_TIME_SEND_MESSAGE_TAG).observe(this,workInfos -> {
            if(workInfos!=null  && workInfos.size()>0) {
                DebugLog.console("[MainActivity] inside refreshFiles() " + workInfos.get(0));
                if (workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED) {
                    updateUI();
                }
            }
        });
    }


    private synchronized void updateUI(){
        DebugLog.console("[MainActivity] inside updateUI() ");
        posters = new ArrayList<>();

        String adFiles = LocalCache.getFiles();
        if (adFiles != null) {
            Log.d("TAG", "setupTimer: ");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<AdModel>>() {
            }.getType();
            List<AdModel> adModels = gson.fromJson(adFiles, listType);
            for (AdModel adModel : adModels) {
                if (adModel.getContentType().equalsIgnoreCase("IMAGE")) {
                    Log.d("TAG_", "IMAGE: ");
                    if(adModel.getName().contains("_d")) {
                        int start_index = adModel.getName().indexOf("_d");
                        int end_index = adModel.getName().indexOf(".");
                        String delay = adModel.getName().substring(start_index+2,end_index);
                        int image_duration = Integer.parseInt(delay)*1000;
                        posters.add(new RemoteImage(adModel.getUrl(), image_duration));
                    }else{
                        posters.add(new RemoteImage(adModel.getUrl(),DEFAULT_DISPLAY_TIME ));
                    }
                } else if (adModel.getContentType().equalsIgnoreCase("VIDEO")) {
					posters.add(new RemoteVideo(Uri.parse(adModel.getUrl()),DEFAULT_VIDEO_DISPLAY_TIME));

//                    Log.d("TAG", "Video: ");
//                    if(adModel.getName().contains("_d")) {
//                        int start_index = adModel.getName().indexOf("_d");
//                        int end_index = adModel.getName().indexOf(".");
//                        String delay = adModel.getName().substring(start_index+2,end_index);
//                        int video_duration = Integer.parseInt(delay)*1000;
//                        posters.add(new RemoteVideo(Uri.parse(adModel.getUrl()), DEFAULT_VIDEO_DISPLAY_TIME));
//                    }else{
//                        posters.add(new RemoteVideo(Uri.parse(adModel.getUrl()), DEFAULT_VIDEO_DISPLAY_TIME ));
//                    }
                }

            }

            if (posters.size() != posterSlider.posters.size()){
                this.setPosters();
            }else {
                for (int i = 0; i < posters.size() - 1; i++){
                    Poster poster = posters.get(i);
                    String url = "";
                    boolean isExist = false;

                    if (poster instanceof RemoteImage) {
                        RemoteImage img = (RemoteImage)poster;
                        url = img.getUrl();
                    }else {
                        RemoteVideo vid = (RemoteVideo)poster;
                        url = vid.getUri().toString();
                    }
                    for (Poster p : posterSlider.posters){
                        String exist_url = "";
                        if (p instanceof RemoteImage) {
                            RemoteImage img = (RemoteImage)p;
                            exist_url = img.getUrl();
                        }else {
                            RemoteVideo vid = (RemoteVideo)p;
                            exist_url = vid.getUri().toString();
                        }
                        if (url.equalsIgnoreCase(exist_url)) isExist = true;
                    }
                    if (!isExist) {
                        this.setPosters();
                        break;
                    }
                }
                Log.e("tetmp", "temp");
            }

        }
    }

    private void setPosters(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                posterSlider.setPosters(posters);
            }
        });
    }
}