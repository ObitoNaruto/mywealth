package com.android.mobile.mywealth;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.android.mobile.mywealth.storage.internal.AppInfo;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class ObitoApplication extends Application{

    private static ObitoApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initAppInfo();
    }

    public static ObitoApplication getInstance(){
        return instance;
    }

    private void initAppInfo(){
        AppInfo.createInstance(getApplicationContext());
        AppInfo.getInstance().setReleaseType("");
        AppInfo.getInstance().setProductVersion(getVersionName());
    }

    private String getVersionName(){
        String versionName = null;
        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }
}
