package com.android.mobile.mywealth.framework;

import android.app.Application;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class NarutoApplication extends Application{
    /**
     * 上下文
     */
    private static NarutoApplication mInstance;

    /**
     *项目上下文
     */
    private NarutoApplicationContext mNarutoApplicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try{
            mNarutoApplicationContext = (NarutoApplicationContext)Class.forName("com.android.mobile.mywealth.framework.NarutoApplicationContextImpl").newInstance();
            mNarutoApplicationContext.attachContext(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public NarutoApplicationContext getNarutoApplicationContext() {
        return mNarutoApplicationContext;
    }

    public static NarutoApplication getInstance(){
        return mInstance;
    }
}
