package com.zhihudailytest.CustomUI

import android.content.Context
import android.view.View
import android.widget.LinearLayout

import com.zhihudailytest.R

/**
 * Created by Administrator on 2016/7/7.
 */
object Draw {
    fun drawDot(context: Context, dotLayout: LinearLayout, size: Int) {
        var dot: View? = null
        val params = LinearLayout.LayoutParams(20, 20)
        params.leftMargin = 8
        for (i in 0 until size) {
            dot = View(context)
            dot.isEnabled = false
            dot.setBackgroundResource(R.drawable.dot_selector)
            dot.layoutParams = params
            dotLayout.addView(dot)
        }
    }
}
