package com.android.mobile.mywealth.framework.init.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class BundleDao {
    public List<Bundle> getBundles() {
        List<Bundle> list = new ArrayList<Bundle>();

        Bundle bundle = new Bundle("demo", false, "com.android.mobile.mywealth.framework.demo");
        list.add(bundle);

        return list;
    }
}
