package com.android.mobile.mywealth.storage.internal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class LruDiskCache extends DiskCache{
    @Override
    protected void init() {
        //使用linkedhashmap来做lru缓存
        mEntities = new LinkedHashMap<String, Entity>(10, 0.75f, true) {
            private static final long serialVersionUID = -2365362316950114365L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Entity> eldest) {
                if (mSize >= mMaxsize) {
                    mSize -= eldest.getValue().getSize();
                    evict(eldest.getKey());//整理缓存
                    return true;
                } else {
                    return false;
                }
            }
        };

        //使用额外的HashMap将entity对象分组
        mGroup = new HashMap<String, Set<Entity>>();
    }

    /**
     * 整理缓存
     */
    private void evict(String url) {
        removeCacheFile(url);
    }
}
