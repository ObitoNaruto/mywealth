package com.android.mobile.mywealth.storage.internal;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.UUID;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class AppInfo {
    public static final String VERSION = "product_version";
    private static AppInfo mInstance;
    private final Context mContext;

    /**
     * ActivityManager
     */
    private ActivityManager mActivityManager;

    private String mProductID;

    /**
     * ����汾
     */
    private String mProductVersion;

    private String mChannels;

    private String mReleaseType;

    private String mAppName;

    /**
     * �Ƿ���԰汾
     */
    private boolean mDebuggable;
    /**
     * ��ǰ���еĽ���ID
     */
    private int mPid;

    private String mAwid;

    private SharedPreferences mSharedPreferences;

    private AppInfo(Context context) {
        mContext = context;

        // ��Զ�����һ��
        init();
    }

    /**
     * ��ȡӦ�õĹ����Լ�������Ϣʵ��
     *
     * @return ���ܼ�¼��
     */
    public static AppInfo getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(
                    "AppManager must be created by calling createInstance(Context context)");
        return mInstance;
    }

    /**
     * ����Ӧ�õĹ����Լ�������Ϣʵ��
     *
     * @param context
     *            ������
     * @return
     */
    public static synchronized AppInfo createInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppInfo(context);
        }
        return mInstance;
    }

    public String getReleaseType() {
        return mReleaseType;
    }

    public void setReleaseType(String releaseType) {
        mReleaseType = releaseType;
    }

    /**
     * ���˰汾��
     *
     * @param versionName
     * @return
     */
    private String clearVersionName(String versionName) {
        if (versionName.contains("ctch1")) {
            versionName = versionName.replace("ctch1", "");
        }
        return versionName;
    }

    /**
     * ��ʼ��
     */
    private void init() {
        try {
            String tpackageName = mContext.getPackageName();
            Log.d("AppInfo", "getPackageName " + tpackageName);

            mSharedPreferences = mContext.getSharedPreferences(tpackageName
                    + "_config", Context.MODE_PRIVATE);
            String version = mSharedPreferences.getString(VERSION, null);
            PackageInfo mPackageInfo = mContext.getPackageManager()
                    .getPackageInfo(tpackageName, 0);
            mProductVersion = clearVersionName(mPackageInfo.versionName);
            if (null != version && compareVersion(version, mProductVersion)) {
                mProductVersion = version;
            }

            ApplicationInfo applicationInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // development mode
                mDebuggable = true;
            }

            mAppName = (String) mContext.getPackageManager()
                    .getApplicationLabel(applicationInfo);

            mActivityManager = (ActivityManager) mContext
                    .getSystemService(Context.ACTIVITY_SERVICE);
            mPid = android.os.Process.myPid();
            mProductID = "";// mContext.getString(R.string.useragent);
            mChannels = "alipay";// CacheSet.getInstance(mContext).getString("channels");
            mReleaseType="";

            //	mAwid = UUID.randomUUID().toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppManager", "init: " + e == null ? "" : e.getMessage());
        }
    }

    /**
     * �Ƚϰ汾
     *
     * @param version
     * @param mProductVersion
     * @return
     */
    private boolean compareVersion(String version, String mProductVersion) {
        String[] versions = version.split("\\.");
        String[] productVersions = mProductVersion.split("\\.");
        for (int i = 0; i < versions.length; i++) {
            int v1 = Integer.parseInt(versions[i]);
            int v2 = Integer.parseInt(productVersions[i]);
            if (v1 > v2) {
                return true;
            } else if (v1 == v2) {
                continue;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * �Ƿ��ǿ���״̬
     */
    public boolean isDebuggable() {
        return mDebuggable;
    }

    /**
     * ��ȡ��ǰ���еĽ���ID
     *
     * @return ����ID
     */
    public int getPid() {
        return mPid;
    }

    public void setProductVersion(String version) {
        if (null != version) {
            mSharedPreferences.edit().putString(VERSION, version).apply();
            mProductVersion = version;
        }
    }

    /**
     * �ָ���汾
     */
    public void recoverProductVersion() {
        mSharedPreferences.edit().remove(VERSION).apply();
    }

    public void setChannels(String channels) {
        mSharedPreferences.edit().putString("channels", channels).apply();
        mChannels = channels;
    }

    public void setProductID(String productId) {
        mSharedPreferences.edit().putString("productId", productId).apply();
        mProductID = productId;
    }

    public String getProductID() {
        if (mProductID.equals("")) {
            return "Android-container";
        } else {
            return mProductID;
        }
    }

    public String getmProductVersion() {
        return mProductVersion;
    }

    public String getAppName(){
        return mAppName;
    }

    public String getmChannels() {
        return mChannels;
    }

    public String getmAwid() {
        if (mAwid != null) {
            return mAwid;
        }
        synchronized (this) {
            if (mAwid != null) {
                return mAwid;
            }
            mAwid = UUID.randomUUID().toString();
        }
        return mAwid;
    }

    /**
     * ��ȡ��ǰ��ռ�ڴ�
     *
     * @return ��ǰ��ռ�ڴ�
     */
    public long getTotalMemory() {
        android.os.Debug.MemoryInfo[] mems = mActivityManager
                .getProcessMemoryInfo(new int[] { mPid });
        return mems[0].getTotalPrivateDirty();
    }

    /**
     * ��ȡӦ�õ�data/data/....FileĿ¼
     *
     * @return FileĿ¼
     */
    public String getFilesDirPath() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    /**
     * ��ȡӦ�õ�data/data/....CacheĿ¼
     *
     * @return CacheĿ¼
     */
    public String getCacheDirPath() {
        return mContext.getCacheDir().getAbsolutePath();
    }
}
