package com.android.mobile.mywealth.framework.service.ext;

import com.android.mobile.mywealth.framework.service.CommonService;
import com.android.mobile.mywealth.framework.service.ServiceDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class ExternalServiceManager extends CommonService{
    /**
     * 注册扩展服务
     */
    public abstract void registerExtnernalService(ServiceDescription serviceDescription);

    /**
     * 获取扩展服务
     * @param className 服务接口类
     * @return
     */
    public abstract ExternalService getExternalService(String className);

    /**
     * 创建ext服务
     * @param description
     * @return
     */
    public abstract boolean createExternalService(ServiceDescription description);

    /**
     * 注册ext服务，只register，不创建
     * @param description
     */
    public abstract void registerExternalServiceOnly(ServiceDescription description);
}
