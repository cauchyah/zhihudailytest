package com.zhihudailytest.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.zhihudailytest.MyApplication;

/**
 * Created by Administrator on 2016/7/6.
 */
public class NetworkUtil {
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.ApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isAvailable();
    }

    public static boolean isWifiConnected() {
        ConnectivityManager manager= (ConnectivityManager) MyApplication.ApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return  networkInfo.getType()==ConnectivityManager.TYPE_WIFI;
    }
    public static boolean isMobileConnected(){
        ConnectivityManager manager= (ConnectivityManager) MyApplication.ApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.getType()==ConnectivityManager.TYPE_MOBILE;
    }

    public static int getConnectedType(){
        ConnectivityManager manager= (ConnectivityManager) MyApplication.ApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if (networkInfo!=null&&networkInfo.isAvailable()){
            return  networkInfo.getType();

        }
        return -1;
    }
}
