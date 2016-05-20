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
        //�ڴ滺��
        mCache.put(getKey(key), value);
        //���̻���
        super.put(key, value);
    }

    @Override
    public String get(String key) {
        //���ȴ��ڴ滺���ж�ȡ
        String result = mCache.get(getKey(key));
        if (null == result) {
            //�ڴ滺���ж�ȡ�����Ӵ��̻����ж�ȡ
            result = super.get(key);
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        //�������
        mCache.remove(getKey(key));
        return super.remove(key);
    }

    private String getKey(String key) {
        return generateKey(key) + mOwnerName;
    }
}
