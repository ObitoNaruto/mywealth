package com.android.mobile.mywealth.framework;

import com.android.mobile.mywealth.framework.init.impl.BootLoaderImpl;
import com.android.mobile.mywealth.framework.service.MicroService;
import com.android.mobile.mywealth.framework.service.ServiceManager;
import com.android.mobile.mywealth.framework.service.ext.ExternalService;
import com.android.mobile.mywealth.framework.service.ext.ExternalServiceManager;
import com.android.mobile.mywealth.framework.service.impl.ServiceManagerImpl;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class NarutoApplicationContextImpl implements NarutoApplicationContext{
    /**
     * 上下文
     */
    private NarutoApplication mApplication;
    /**
     * 服务管理
     */
    private ServiceManager mServiceManager;

    @Override
    public void attachContext(NarutoApplication application) {
        mApplication = application;
        init();
    }

    @Override
    public NarutoApplication getApplicationContext() {
        return mApplication;
    }

    /**
     * 初始化
     */
    private void init(){
        mServiceManager = new ServiceManagerImpl();
        mServiceManager.attachContext(this);

        new BootLoaderImpl(NarutoApplicationContextImpl.this).load();
    }

    @Override
    public <T> boolean registerService(String className, T service) {
        return mServiceManager.registerService(className, service);
    }

    @Override
    public <T> T unregisterService(String interfaceName) {
        return mServiceManager.unregisterService(interfaceName);
    }

    @Override
    public <T> T findServiceByInterface(String className) {
        if (null != mServiceManager) {
            T t = mServiceManager.findServiceByInterface(className);
            if (null == t) {
                t = (T) getExtServiceByInterface(className);
            }
            return t;
        }
        return null;
    }

    @Override
    public <T extends ExternalService> T getExtServiceByInterface(String className) {
        if (null != mServiceManager) {
            ExternalServiceManager exm = mServiceManager
                    .findServiceByInterface(ExternalServiceManager.class.getName());
            if (null != exm) {
                return (T) exm.getExternalService(className);
            }
        }
        return null;
    }

    @Override
    public void onDestroyContent(MicroContent microContent) {
        if (microContent instanceof MicroService) {
            mServiceManager.onDestroyService((MicroService) microContent);
        }
    }
}
