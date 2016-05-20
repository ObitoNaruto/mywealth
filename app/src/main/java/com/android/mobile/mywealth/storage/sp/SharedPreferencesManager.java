package com.android.mobile.mywealth.storage.sp;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class SharedPreferencesManager {
    private static LruCache<String, APSharedPreferences> spList = new LruCache(30);

    public SharedPreferencesManager() {
    }

    public static synchronized APSharedPreferences getInstance(Context context, String groupId) {
        return getInstance(context, groupId, 0);
    }

    public static synchronized APSharedPreferences getInstance(Context context, String groupId, int mode) {
        if(context != null && !TextUtils.isEmpty(groupId)) {
            APSharedPreferences sp = (APSharedPreferences)spList.get(groupId);
            if(sp == null) {
                sp = new APSharedPreferences(context, groupId, mode);
                spList.put(groupId, sp);
            }

            return sp;
        } else {
            return null;
        }
    }
}
