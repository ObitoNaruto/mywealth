package com.android.mobile.mywealth.notification;

/**
 * 回调接口
 * 使用方法：Activity以及Fragment 实现此接口，然后在其生命周期方法中进行注册反注册，当post调用后，当前页面就会执行onDataChanged方法
 * Created by xinming.xxm on 2016/5/5.
 */
public interface ISubscriberCallback<T> {
    //
    void onDataChanged(T data);
}
