package com.zhihudailytest.Utils

import com.orhanobut.logger.Logger

/**
 * Created by Administrator on 2016/7/4.
 */
object LogUtil {
    private val isDebug = true

    fun i(tag: String, msg: String) {
        if (isDebug)

            android.util.Log.i(tag, msg)
    }

    fun v(tag: String, msg: String) {
        if (isDebug)
            android.util.Log.v(tag, msg)
    }

    fun e(tag: String, msg: String) {
        if (isDebug)
            android.util.Log.e(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (isDebug)
            android.util.Log.w(tag, msg)
    }

    fun d(msg: String) {
        if (isDebug)
            Logger.d(msg)

    }

    fun d(message: String, vararg args: Any) {
        if (isDebug)

            Logger.d(message, *args)
    }

    fun json(json: String) {
        if (isDebug)
            Logger.json(json)
    }
}
