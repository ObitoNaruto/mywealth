package com.android.mobile.mywealth.storage.internal;

import android.os.StatFs;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class SecurityLruDiskCache extends LruDiskCache{

    private static SecurityLruDiskCache INSTANCE;

    private SecurityLruDiskCache() {
        super();
    }

    public static synchronized SecurityLruDiskCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecurityLruDiskCache();
        }
        return INSTANCE;
    }

    @Override
    protected void init() {
        super.init();
        load();
    }

    private void load() {
        //获取磁盘缓存路径这里要注意，一定要提前初始化AppIntoUtils，向其传入上下文对象
        String path = AppInfo.getInstance().getCacheDirPath();
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSize() * ((long) statFs.getAvailableBlocks());
        setDirectory(path);

        long canUseSize = size - (512 * 1024);
        setMaxsize(canUseSize > 0 ? canUseSize : 512 * 1024);//留512K的空间，防止达到峰值
    }
}
