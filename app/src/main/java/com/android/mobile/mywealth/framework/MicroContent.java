package com.android.mobile.mywealth.framework;

import android.content.SharedPreferences;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public interface MicroContent {
    /**
     * ±£´æ×´Ì¬
     *
     * @param editor
     */
    void saveState(SharedPreferences.Editor editor);

    /**
     * »Ö¸´×´Ì¬
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);
}
