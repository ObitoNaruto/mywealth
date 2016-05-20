package com.android.mobile.mywealth.storage.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class APSharedPreferences {
    private Context sContext = null;
    private String mGroup = "obito_default_sp";
    private int mMode = Context.MODE_PRIVATE;
    private SharedPreferences.Editor edit = null;

    protected APSharedPreferences(Context context, String group, int mode) {
        if (context != null){
            sContext = context.getApplicationContext();
        }
        mGroup = group;
        mMode = mode;
    }

    public synchronized void init() {
        if (sContext != null) {
            if (edit == null) {
                edit = sContext.getSharedPreferences(getGroup(), mMode).edit();
            }
        }
    }

    public boolean getBoolean(String key, boolean defValue) {
        return getBoolean(getGroup(), key, defValue);
    }

    public String getString(String key, String defValue) {
        return getString(getGroup(), key, defValue);
    }

    public int getInt(String key, int defValue) {
        return getInt(getGroup(), key, defValue);
    }

    public long getLong(String key, long defValue) {
        return getLong(getGroup(), key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return getFloat(getGroup(), key, defValue);
    }

    public boolean putInt(String key, int value) {
        return putInt(getGroup(), key, value);
    }

    public boolean putBoolean(String key, boolean value) {
        return putBoolean(getGroup(), key, value);
    }

    public boolean putString(String key, String value) {
        return putString(getGroup(), key, value);
    }

    public boolean putLong(String key, long value) {
        return putLong(getGroup(), key, value);
    }

    public boolean putFloat(String key, float value) {
        return putFloat(getGroup(), key, value);
    }

    public boolean contains(String key) {
        return contains(getGroup(), key);
    }

    public boolean commit() {
        if (edit != null) {
            return edit.commit();
        }
        return false;
    }

    public boolean remove(String key){
        if (edit != null && !TextUtils.isEmpty(key)) {
            edit.remove(key);
            return true;
        }
        return false;
    }

    public boolean clear(){
        if (edit != null) {
            edit.clear();
            return true;
        }
        return false;
    }

    public Map<String, ?> getAll(){
        if (sContext != null) {
            return sContext.getSharedPreferences(getGroup(), mMode).getAll();
        }
        return null;
    }

    private boolean contains(String name, String key){
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).contains(key);
        }
        return false;
    }

    private boolean getBoolean(String name, String key, boolean defValue) {
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).getBoolean(key,
                    defValue);
        }
        return defValue;
    }

    private String getString(String name, String key, String defValue) {
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).getString(key,
                    defValue);
        }
        return defValue;
    }

    private int getInt(String name, String key, int defValue) {
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).getInt(key,
                    defValue);
        }
        return defValue;
    }

    private long getLong(String name, String key, long defValue) {
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).getLong(key,
                    defValue);
        }
        return defValue;
    }

    private float getFloat(String name, String key, float defValue) {
        if (sContext != null) {
            return sContext.getSharedPreferences(name, mMode).getFloat(key,
                    defValue);
        }
        return defValue;
    }

    private boolean putInt(String name, String key, int value) {//NOSONAR
        if (edit != null) {
            edit.putInt(key, value);
            return true;
        }
        return false;
    }

    private boolean putBoolean(String name, String key, boolean value) {//NOSONAR
        if (edit != null) {
            edit.putBoolean(key, value);
            return true;
        }
        return false;
    }

    private boolean putString(String name, String key, String value) {//NOSONAR
        if (edit != null) {
            edit.putString(key, value);
            return true;
        }
        return false;
    }

    private boolean putLong(String name, String key, long value) {//NOSONAR
        if (edit != null) {
            edit.putLong(key, value);
            return true;
        }
        return false;
    }

    private boolean putFloat(String name, String key, float value) {//NOSONAR
        if (edit != null) {
            edit.putFloat(key, value);
            return true;
        }
        return false;
    }


    private String getGroup() {
        return mGroup;
    }
}
