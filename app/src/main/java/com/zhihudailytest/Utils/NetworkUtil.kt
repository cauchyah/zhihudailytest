package com.zhihudailytest.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.zhihudailytest.MyApplication

/**
 * Created by Administrator on 2016/7/6.
 */
object NetworkUtil {
    val isNetworkConnected: Boolean
        get() {
            val connectivityManager = MyApplication.ApplicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isAvailable
        }

    val isWifiConnected: Boolean
        get() {
            val manager = MyApplication.ApplicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo ?: return false
            return networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    val isMobileConnected: Boolean
        get() {
            val manager = MyApplication.ApplicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo ?: return false
            return networkInfo.type == ConnectivityManager.TYPE_MOBILE
        }

    val connectedType: Int
        get() {
            val manager = MyApplication.ApplicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isAvailable) {
                networkInfo.type

            } else -1
        }
}
