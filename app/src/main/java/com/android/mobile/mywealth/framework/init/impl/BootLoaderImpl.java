package com.android.mobile.mywealth.framework.init.impl;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.init.BootLoader;
import com.android.mobile.mywealth.framework.service.ServicesLoader;
import com.android.mobile.mywealth.framework.service.ext.ExternalServiceManager;
import com.android.mobile.mywealth.framework.service.ext.ExternalServiceManagerImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class BootLoaderImpl implements BootLoader{
    private List<Bundle> mBundles;

    private NarutoApplicationContext mNarutoApplicaitonContext;
    private ServicesLoader mServiceLoader;

    public BootLoaderImpl(NarutoApplicationContext narutoApplicationContext){
        mNarutoApplicaitonContext = narutoApplicationContext;
        mBundles = new ArrayList<Bundle>();
    }

    @Override
    public NarutoApplicationContext getContext() {
        return mNarutoApplicaitonContext;
    }

    @Override
    public void load() {
        Application application = mNarutoApplicaitonContext.getApplicationContext();

        //读取metaData
        String agentCommonServiceLoad = null;
//        String agentEntryPkgName = null;
//        try {
//            ApplicationInfo appInfo = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
//            agentCommonServiceLoad = appInfo.metaData.getString("agent.commonservice.load");
//            agentEntryPkgName = appInfo.metaData.getString("agent.entry.pkgname");
//        } catch (Exception e1) {
//        }

        if (TextUtils.isEmpty(agentCommonServiceLoad)) {
            agentCommonServiceLoad = "com.android.mobile.mywealth.framework.service.impl.CommonServiceLoadAgent";
        }

        //step1. 首先初始化外部服务管理
        ExternalServiceManager externalServiceManager = new ExternalServiceManagerImpl();
        externalServiceManager.attachContext(mNarutoApplicaitonContext);
        mNarutoApplicaitonContext.registerService(ExternalServiceManager.class.getName(), externalServiceManager);

        //step2. 然后初始化框架中提供的所有基础服务
        try {
            Class<?> clazz = application.getClassLoader().loadClass(agentCommonServiceLoad);
            mServiceLoader = (ServicesLoader) clazz.newInstance();
            mServiceLoader.load();
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        if (Runtime.getRuntime().availableProcessors() > 2) {

            final HandlerThread loadServiceThread = new HandlerThread("name");
            loadServiceThread.start();
            Handler handler = new Handler(loadServiceThread.getLooper());

            handler.post(new Runnable() {
                public void run() {
                    try {
                        CookieSyncManager.createInstance(mNarutoApplicaitonContext.getApplicationContext());
                    } catch (Throwable e) {
                    }
                    if (mServiceLoader != null) {
                        mServiceLoader.afterBootLoad();
                    }
                }
            });

//            //初始化框架中提供的所有基础服务
//            new CommonServiceLoadHelper(this).loadServices();

            new BundleLoadHelper(this).loadBundleDefinitions();
        }
    }
}
