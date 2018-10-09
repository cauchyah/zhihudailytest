package com.zhihudailytest.CustomUI

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by Administrator on 2016/7/18.
 */
class NoPaddingCardView : CardView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Optional: Prevent pre-L from adding inner card padding
        preventCornerOverlap = false
        // Optional: make Lollipop and above add shadow padding to match pre-L padding
        useCompatPadding = true
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        // FIX shadow padding
        if (params is ViewGroup.MarginLayoutParams) {
// layoutParams.bottomMargin -= (getPaddingBottom() - getContentPaddingBottom());
            //layoutParams.bottomMargin = -7;
            params.leftMargin -= paddingLeft - contentPaddingLeft
            params.rightMargin -= paddingRight - contentPaddingRight
            // layoutParams.topMargin -= (getPaddingTop() - getContentPaddingTop());
            //layoutParams.topMargin = -5;
        }

        super.setLayoutParams(params)
    }
}
