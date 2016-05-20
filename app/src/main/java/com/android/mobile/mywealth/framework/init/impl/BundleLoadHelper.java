package com.android.mobile.mywealth.framework.init.impl;

import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.init.BootLoader;
import com.android.mobile.mywealth.framework.service.BaseMetaInfo;
import com.android.mobile.mywealth.framework.service.ServiceDescription;
import com.android.mobile.mywealth.framework.service.ext.ExternalServiceManager;

import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class BundleLoadHelper {
    private BootLoader mBootLoader;
    private NarutoApplicationContext mNarutoApplicationContext;
    private ExternalServiceManager mExternalServiceManager;

    public BundleLoadHelper(BootLoader bootLoader){
        mBootLoader = bootLoader;
        mNarutoApplicationContext = mBootLoader.getContext();
        mExternalServiceManager = mNarutoApplicationContext.findServiceByInterface(ExternalServiceManager.class.getName());
    }

    public void loadBundleDefinitions() {
        try {
            BundleDao bundleDao = new BundleDao();
            List<Bundle> bundleList = bundleDao.getBundles();
            for (Bundle bundle : bundleList) {
                loadBundle(bundle);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadBundle(Bundle bundle) throws ClassNotFoundException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException{
        BaseMetaInfo baseMetaInfo = null;
        try {
            String pkg = bundle.getPackageName();
            baseMetaInfo = (BaseMetaInfo) Class.forName(pkg + ".MetaInfo").newInstance();
        } catch (Exception e) {
            //打印出错信息
        }

        if (null == baseMetaInfo) {
            return;
        }

        // Load service
        List<ServiceDescription> services = baseMetaInfo.getServices();
        if (null != services && services.size() > 0) {
            for (ServiceDescription serviceDescription : services) {
                if (null == serviceDescription) {
                    continue;
                }
                mExternalServiceManager.registerExternalServiceOnly(serviceDescription);
            }
        }
    }

}
