package com.android.mobile.mywealth.framework.service.impl;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.service.CommonService;
import com.android.mobile.mywealth.framework.service.MicroService;
import com.android.mobile.mywealth.framework.service.ServiceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class ServiceManagerImpl implements ServiceManager{

    private NarutoApplicationContext mNarutoApplication;

    /**
     * 服务
     */
    private Map<String, Object> mServices;
    private Map<String, String> mLazyServices;

    /**
     * 初始化服务容器（内存缓存）
     */
    public ServiceManagerImpl(){
        mServices = new ConcurrentHashMap<String, Object>();
        mLazyServices = new ConcurrentHashMap<String, String>();
    }

    @Override
    public void attachContext(NarutoApplicationContext applicationContext) {
        mNarutoApplication = applicationContext;
    }

    @Override
    public <T> boolean registerService(String className, T service) {
        if (service instanceof MicroService) {
            return null == mServices.put(className, service);
        } else if (service instanceof String) {
            return null == mLazyServices.put(className, (String)service);
        } else { // 未知类型
            return null == mServices.put(className, service);
        }
    }

    @Override
    public <T> T findServiceByInterface(String className) {
        if (mServices.containsKey(className)) {
            return (T) mServices.get(className);
        } else if (mLazyServices.containsKey(className)) {
            String defaultClassName = mLazyServices.get(className);
            if (TextUtils.isEmpty(defaultClassName)) {
                return null;
            }
            synchronized (defaultClassName) {
                // check service again for synchronized
                if (mServices.containsKey(className)) {
                    return (T) mServices.get(className);
                }

                CommonService service = null;
                try {
                    Class<?> clazz = mNarutoApplication
                            .getApplicationContext()
                            .getClassLoader()
                            .loadClass(defaultClassName);
                    service = (CommonService) clazz.newInstance();
                } catch (ClassNotFoundException e) {
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                if (service != null) {
                    service.attachContext(mNarutoApplication);
                    mServices.put(className, service);
                }
                return (T) service;
            }
        }
        return null;
    }

    @Override
    public void onDestroyService(MicroService microService) {
        if (null == microService) {
            return;
        }
        Set<String> keys = mServices.keySet();
        MicroService service = null;
        Object obj = null;
        for(String key : keys) {
            obj = mServices.get(key);
            if (obj instanceof MicroService) {
                service = (MicroService) obj;
                if (service == microService) {
                    mServices.remove(key);
                    break;
                }
            }
        }
    }

    @Override
    public void exit() {
        Object[] values = mServices.values().toArray();
        MicroService service = null;
        for (Object object : values) {
            if (object instanceof MicroService) {
                service = (MicroService) object;
                if (service.isActivated()) {
                    service.destroy(null);
                }
            }
        }
        mServices.clear();
        mLazyServices.clear();
    }

    @Override
    public void saveState(SharedPreferences.Editor editor) {
        for (Object object : mServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).saveState(editor);
            }
        }
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
        for (Object object : mServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).restoreState(preferences);
            }
        }
    }

    @Override
    public <T> T unregisterService(String interfaceName) {
        mLazyServices.remove(interfaceName);
        return (T)mServices.remove(interfaceName);
    }
}
