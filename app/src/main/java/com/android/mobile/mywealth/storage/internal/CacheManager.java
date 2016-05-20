package com.android.mobile.mywealth.storage.internal;

import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class CacheManager {
    private static final String TAG = CacheManager.class.getSimpleName();

    private IStorageController mStorageController;

    private static LruCache<String, CacheManager> storageCaches = new LruCache<String, CacheManager>(4);

    private CacheManager(String group) {
        mStorageController = new CachedSecurityDiskController(group);
        mStorageController.open();
    }

    private static final String DEFAULT_GROUP = "default";

    public static CacheManager getInstance() {
        CacheManager instance = storageCaches.get(DEFAULT_GROUP);
        if (instance == null) {
            instance = new CacheManager(DEFAULT_GROUP);
            storageCaches.put(DEFAULT_GROUP, instance);
        }
        return instance;
    }

    private String compoundUserKey(String key) {
        return "userId" + ":" + key;
    }

    private String get(String key, boolean userSpecified) {
        if (userSpecified) {
            key = compoundUserKey(key);
        }
        return mStorageController.get(key);
    }

    private void put(String key, String value, boolean userSpecified) {
        if (userSpecified) {
            key = compoundUserKey(key);
        }
        mStorageController.put(key, value);
    }

    public boolean putString(String key, String value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            //Sync storage
            long time = System.currentTimeMillis();
            put(key, value, true);
            Log.i(TAG, "write key = [" + key + "], value = ["
                    + value + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
            return true;
        }
        return false;
    }

    public String getString(String key) {

        if (TextUtils.isEmpty(key)) {
            return null;
        }

        long time = System.currentTimeMillis();
        String result = get(key, true);
        Log.i(TAG, "get key = [" + key + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
        return result;
    }

    public boolean putSerializable(String key, Serializable serializable) {
        if (!TextUtils.isEmpty(key) && serializable != null) {
            //Sync storage
            long time = System.currentTimeMillis();
            mStorageController.putSerializable(compoundUserKey(key), serializable);
            Log.i(TAG, "write key = [" + key + "], value = ["
                    + serializable + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
            return true;
        }
        return false;
    }

    public Serializable getSerializable(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        long time = System.currentTimeMillis();
        Serializable result = mStorageController.getSerializable(compoundUserKey(key));
        Log.i(TAG, "get key = [" + key + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
        return result;
    }

    @Deprecated
    public void putFastJsonObject(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            long time = System.currentTimeMillis();
            String jsonString = JSON.toJSONString(value);
            put(key, jsonString, true);
            Log.i(TAG, "write key = [" + key + "], object = ["
                    + value.getClass().getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
        }
    }

    public void putFastJsonObject(String key, Object value, boolean userSpecified) {
        if (!TextUtils.isEmpty(key) && value != null) {
            long time = System.currentTimeMillis();
            String jsonString = JSON.toJSONString(value);
            put(key, jsonString, userSpecified);
            Log.i(TAG, "write key = [" + key + "], object = ["
                    + value.getClass().getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
        }
    }

    @Deprecated
    public <T> T getFastJsonObject(String key, Class<T> type) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        long time = System.currentTimeMillis();
        String value = get(key, true);

        try {
            T result = JSON.parseObject(value, type);
            Log.i(TAG, "get key = [" + key + "], object = ["
                    + type.getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
            return result;
        } catch (Exception e) {
            if (value != null) {
                Log.e(TAG, "FAIL to deserialize key = [" + key + "], object = [" + type.getSimpleName() + "]");
            }
            return null;
        }
    }

    public <T> T getFastJsonObject(String key, Class<T> type, boolean userSpecified) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        long time = System.currentTimeMillis();
        String value = get(key, userSpecified);

        try {
            T result = JSON.parseObject(value, type);
            Log.i(TAG, "get key = [" + key + "], object = ["
                    + type.getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
            return result;
        } catch (Exception e) {
            if (value != null) {
                Log.e(TAG, "FAIL to deserialize key = [" + key + "], object = [" + type.getSimpleName() + "]");
            }
            return null;
        }
    }


    public void putFastJsonArray(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            long time = System.currentTimeMillis();
            String jsonString = JSON.toJSONString(value);
            put(key, jsonString, true);
            Log.i(TAG, "write key = [" + key + "], object = ["
                    + value.getClass().getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");

        }
    }

    public <T> List<T> getFastJsonArray(String key, Class<T> type) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        long time = System.currentTimeMillis();
        String value = get(key, true);

        try {
            List<T> result = JSON.parseArray(value, type);
            Log.i(TAG, "get key = [" + key + "], object = ["
                    + type.getSimpleName() + "] cost time = [" + (System.currentTimeMillis() - time) + "ms]");
            return result;
        } catch (Exception e) {
            if (value != null) {
                Log.e(TAG, "FAIL to deserialize key = [" + key + "], object = [" + type.getSimpleName() + "]");
            }
            return null;
        }
    }

    @Deprecated
    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return ;
        }
        mStorageController.remove(compoundUserKey(key));
    }

    public void remove(String key, boolean isUserSpecified) {
        if (TextUtils.isEmpty(key)) {
            return ;
        }

        if (isUserSpecified) {
            key = compoundUserKey(key);
        }
        mStorageController.remove(key);
    }

    @Override
    protected void finalize() throws Throwable {
        if (mStorageController != null) {
            mStorageController.close();
        }
        super.finalize();

    }
}
