package com.android.mobile.mywealth.framework.demo;

import com.android.mobile.mywealth.framework.demo.service.impl.ExtTextServiceImpl;
import com.android.mobile.mywealth.framework.service.BaseMetaInfo;
import com.android.mobile.mywealth.framework.service.ServiceDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class MetaInfo extends BaseMetaInfo{

    public MetaInfo(){
        super();

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("ExtTextService").
                setClassName(ExtTextServiceImpl.class.getName());
        serviceDescription.setInterfaceClass(ExtTextServiceImpl.class.getName());
        serviceDescription.setLazy(true);
        addService(serviceDescription);
    }
}
