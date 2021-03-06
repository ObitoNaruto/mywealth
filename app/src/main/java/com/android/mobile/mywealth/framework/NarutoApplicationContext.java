package com.android.mobile.mywealth.framework;

import com.android.mobile.mywealth.framework.service.ext.ExternalService;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface NarutoApplicationContext {
    /**
     * 依附android上下文
     * @param application
     */
    void attachContext(NarutoApplication application);

    /**
     * 获取android上下文
     * @return
     */
    NarutoApplication getApplicationContext();

    /**
     *注册服务
     * @param className 服务接口类名
     * @param service 服务
     * @param <T>
     * @return
     */
    <T> boolean registerService(String className, T service);

    <T> T unregisterService(String interfaceName);

    /**
     * 查找服务
     * @param className 服务接口类名
     * @param <T>
     * @return
     */
    <T> T findServiceByInterface(String className);

    /**
     * 通过服务接口获取外部服务
     * @param className
     * @param <T>
     * @return
     */
    <T extends ExternalService> T getExtServiceByInterface(String className);

    /**
     * 销毁
     * @param microContent
     */
    void onDestroyContent(MicroContent microContent);
}
