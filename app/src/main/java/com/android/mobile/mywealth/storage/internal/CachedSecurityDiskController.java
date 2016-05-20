package com.android.mobile.mywealth.storage.internal;

import android.util.LruCache;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class CachedSecurityDiskController extends SecurityDiskController{

    private LruCache<String, String> mCache = new LruCache<String, String>(10);

    public CachedSecurityDiskController(String name) {
        super(name);
    }

    @Override
    public void put(String key, String value) {
        //内存缓存
        mCache.put(getKey(key), value);
        //磁盘缓存
        super.put(key, value);
    }

    @Override
    public String get(String key) {
        //优先从内存缓存中读取
        String result = mCache.get(getKey(key));
        if (null == result) {
            //内存缓存中读取不到从磁盘缓存中读取
            result = super.get(key);
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        //清除缓存
        mCache.remove(getKey(key));
        return super.remove(key);
    }

    private String getKey(String key) {
        return generateKey(key) + mOwnerName;
    }
}
