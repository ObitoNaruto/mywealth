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
        //��ȡ���̻���·������Ҫע�⣬һ��Ҫ��ǰ��ʼ��AppIntoUtils�����䴫�������Ķ���
        String path = AppInfo.getInstance().getCacheDirPath();
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSize() * ((long) statFs.getAvailableBlocks());
        setDirectory(path);

        long canUseSize = size - (512 * 1024);
        setMaxsize(canUseSize > 0 ? canUseSize : 512 * 1024);//��512K�Ŀռ䣬��ֹ�ﵽ��ֵ
    }
}
