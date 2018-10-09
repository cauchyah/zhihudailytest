package com.zhihudailytest.Utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup

/**
 * Created by Administrator on 2016/7/29.
 */
object StatusBarCompat {
    private val INVALID_VAL = -1
    private val COLOR_DEFAULT = Color.parseColor("#20000000")

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun compat(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (color != INVALID_VAL) {
                activity.window.statusBarColor = color
            }
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            var tempColor = COLOR_DEFAULT
            val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            if (color != INVALID_VAL)
                tempColor = color
            val statusView = View(activity)
            val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))

            statusView.setBackgroundColor(tempColor)
            contentView.addView(statusView, params)
        }
    }

    fun compat(activity: Activity) {
        compat(activity, INVALID_VAL)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
            result = context.resources.getDimensionPixelSize(resourceId)
        return result
    }
}
