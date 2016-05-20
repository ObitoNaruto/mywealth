package com.android.mobile.mywealth.framework.service.impl;

import com.android.mobile.mywealth.framework.NarutoApplication;
import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.service.MicroService;
import com.android.mobile.mywealth.framework.service.ServicesLoader;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class CommonServiceLoadAgent implements ServicesLoader{

    protected NarutoApplicationContext narutoApplicationContext;

    public CommonServiceLoadAgent(){
        narutoApplicationContext = NarutoApplication.getInstance().getNarutoApplicationContext();
    }

    public void preLoad(){

    }

    public void postLoad(){

    }



    @Override
    public final void load() {
        preLoad();

        //初始化各种服务


        postLoad();
    }

    @Override
    public void afterBootLoad() {

    }

    public final void registerService(String serviceName, MicroService service) {
        service.attachContext(narutoApplicationContext);
        narutoApplicationContext.registerService(serviceName, service);
    }

    public final void registerLazyService(String serviceName, String className) {
        narutoApplicationContext.registerService(serviceName, className);
    }
}
