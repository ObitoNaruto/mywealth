package com.android.mobile.mywealth.storage.internal;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class ObitoStorage {
    private static ObitoStorage sInstance = null;
    private final CacheManager mCacheManager;

    private ObitoStorage() {
        mCacheManager = CacheManager.getInstance();
    }

    public static ObitoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ObitoStorage();
        }
        return sInstance;
    }

    public ObitoModel get(String queryType) {
        return mCacheManager.getFastJsonObject(StorageKeyConstants.OBITO_TEST + queryType,
                ObitoModel.class);
    }

    public void put(String queryType, ObitoModel data) {
        mCacheManager.putFastJsonObject(StorageKeyConstants.OBITO_TEST + queryType, data);
    }
}
