package com.android.mobile.mywealth.notification;

/**
 * �ص��ӿ�
 * ʹ�÷�����Activity�Լ�Fragment ʵ�ִ˽ӿڣ�Ȼ�������������ڷ����н���ע�ᷴע�ᣬ��post���ú󣬵�ǰҳ��ͻ�ִ��onDataChanged����
 * Created by xinming.xxm on 2016/5/5.
 */
public interface ISubscriberCallback<T> {
    //
    void onDataChanged(T data);
}
