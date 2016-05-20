package com.android.mobile.mywealth.storage.internal;

import java.io.Serializable;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class SecurityDiskController implements IStorageController{
    private final SecurityLruDiskCache mCache;

    private final String mGroupName;
    protected final String mOwnerName = "mywealth";
    private static final long MAX_EXPIRATION = 60 * 60 * 12 * 30;

    public SecurityDiskController(String name) {
        mCache = SecurityLruDiskCache.getInstance();
        mGroupName = name;
    }

    protected String generateKey(String key) {
        return mGroupName + "@" + key;
    }

    @Override
    public void put(String key, String value) {
        mCache.put(mOwnerName, mGroupName, generateKey(key), value.getBytes(),
                System.currentTimeMillis(), MAX_EXPIRATION, "string");
    }

    @Override
    public void putSerializable(String key, Serializable serializable) {
        mCache.putSerializable(mOwnerName, mGroupName, generateKey(key), serializable,
                System.currentTimeMillis(), MAX_EXPIRATION, "serializable");
    }

    @Override
    public String get(String key) {
        try {
            byte[] result = mCache.get(mOwnerName, generateKey(key));
            if (result != null) {
                return new String(result);
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public Serializable getSerializable(String key) {
        try {
            Serializable serializable = mCache.getSerializable(mOwnerName, generateKey(key));
            if (serializable != null) {
                return  serializable;
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public boolean remove(String key) {
        mCache.remove(generateKey(key));
        return true;
    }

    @Override
    public void close() {
        mCache.close();
    }

    @Override
    public void open() {
        mCache.open();
    }
}
