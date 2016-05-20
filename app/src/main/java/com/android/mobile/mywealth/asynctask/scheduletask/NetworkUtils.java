package com.android.mobile.mywealth.asynctask.scheduletask;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class NetworkUtils {
    public static final String WIFI = "Wi-Fi";
    public static final String DEFAULT_WIFI_ADDRESS = "00-00-00-00-00-00";

    public NetworkUtils() {
    }

    public static boolean isConnectInternet(Context context) {
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conManager != null) {
            try {
                NetworkInfo e = conManager.getActiveNetworkInfo();
                if(e != null) {
                    return e.isAvailable();
                }
            } catch (Exception var3) {
                ;
            }
        }

        return false;
    }

    public static String[] getNetworkState(Context paramContext) {
        String[] arrayOfString = new String[]{"Unknown", "Unknown"};

        try {
            PackageManager e = paramContext.getPackageManager();
            if(e.checkPermission("android.permission.ACCESS_NETWORK_STATE", paramContext.getPackageName()) != 0) {
                arrayOfString[0] = "Unknown";
                return arrayOfString;
            }

            ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(localConnectivityManager == null) {
                arrayOfString[0] = "Unknown";
                return arrayOfString;
            }

            NetworkInfo localNetworkInfo1 = localConnectivityManager.getNetworkInfo(1);
            if(localNetworkInfo1 != null && localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED) {
                arrayOfString[0] = "Wi-Fi";
                return arrayOfString;
            }

            NetworkInfo localNetworkInfo2 = localConnectivityManager.getNetworkInfo(0);
            if(localNetworkInfo2 != null && localNetworkInfo2.getState() == NetworkInfo.State.CONNECTED) {
                arrayOfString[0] = "2G/3G";
                arrayOfString[1] = localNetworkInfo2.getSubtypeName();
                return arrayOfString;
            }
        } catch (Exception var6) {
            ;
        }

        return arrayOfString;
    }

    public static String getWifiAddress(Context context) {
        if(context != null) {
            WifiManager wifimanage = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            if(wifiinfo != null) {
                String address = wifiinfo.getMacAddress();
                if(TextUtils.isEmpty(address)) {
                    address = "00-00-00-00-00-00";
                }

                return address;
            } else {
                return "00-00-00-00-00-00";
            }
        } else {
            return "00-00-00-00-00-00";
        }
    }

    private static String _convertIntToIp(int i) {
        return (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
    }

    public static String getWifiIpAddress(Context context) {
        if(context != null) {
            try {
                WifiManager e = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiinfo = e.getConnectionInfo();
                if(wifiinfo != null) {
                    return _convertIntToIp(wifiinfo.getIpAddress());
                }

                return null;
            } catch (Exception var3) {
            }
        }

        return null;
    }

    public static boolean isWifi(Context context) {
        if(context != null) {
            try {
                if(getNetworkState(context)[0].equals("Wi-Fi")) {
                    return true;
                }
            } catch (Exception var2) {
            }
        }

        return false;
    }
}
