package com.android.mobile.mywealth.storage.internal;

import java.io.Serializable;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public interface IStorageController {
    void put(String key, String value);

    void putSerializable(String key, Serializable serializable);

    String get(String key);

    Serializable getSerializable(String key);

    boolean remove(String key);

    void close();

    void open();
}
