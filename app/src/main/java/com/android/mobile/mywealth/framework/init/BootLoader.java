package com.android.mobile.mywealth.framework.init;

import com.android.mobile.mywealth.framework.NarutoApplicationContext;
import com.android.mobile.mywealth.framework.init.impl.Bundle;

import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public interface BootLoader {

    /**
     * 获取上下文
     * @return
     */
    NarutoApplicationContext getContext();
    /**
     * 加载
     */
    void load();

}
