package com.android.mobile.mywealth.framework.service;

import com.android.mobile.mywealth.framework.MicroDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class ServiceDescription extends MicroDescription{
    /**
     * ����ӿ�
     */
    private String mInterfaceClassName;

    /**
     * �Ƿ��ӳټ���
     *
     */
    private boolean isLazy = true;


    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    public String getInterfaceClass() {
        return mInterfaceClassName;
    }

    public void setInterfaceClass(String interfaceClassName) {
        mInterfaceClassName = interfaceClassName;
    }
}
