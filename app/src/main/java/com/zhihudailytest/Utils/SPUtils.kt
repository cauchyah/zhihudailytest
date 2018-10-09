package com.zhihudailytest.Utils

import android.content.Context
import android.content.SharedPreferences

import com.zhihudailytest.MyApplication

/**
 * Created by Administrator on 2016/8/2.
 */
object SPUtils {
    val editor: SharedPreferences.Editor
        get() = MyApplication.ApplicationContext?.getSharedPreferences("daily_user", Context.MODE_PRIVATE)!!.edit()

    val sp: SharedPreferences
        get() = MyApplication.ApplicationContext?.getSharedPreferences("daily_user", Context.MODE_PRIVATE)!!
    val themeEditor: SharedPreferences.Editor
        get() = MyApplication.ApplicationContext?.getSharedPreferences("daily_theme", Context.MODE_PRIVATE)!!.edit()
    val themeSP: SharedPreferences
        get() = MyApplication.ApplicationContext?.getSharedPreferences("daily_theme", Context.MODE_PRIVATE)!!
}
