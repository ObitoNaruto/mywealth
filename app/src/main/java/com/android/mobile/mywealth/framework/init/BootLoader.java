package com.android.mobile.mywealth.framework.init;

import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.init.impl.Bundle;

import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface BootLoader {

    /**
     * ��ȡ������
     * @return
     */
    NarutoApplicationContext getContext();
    /**
     * ����
     */
    void load();

}
