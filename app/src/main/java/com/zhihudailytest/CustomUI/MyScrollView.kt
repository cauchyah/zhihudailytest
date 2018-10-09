package com.zhihudailytest.CustomUI

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * Created by Administrator on 2016/7/9.
 */
class MyScrollView : ScrollView {
    private var lastScrollY: Int = 0
    private var mScrollListener: OnScrollListener? = null
    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val scrollY = this@MyScrollView.scrollY
            if (scrollY != lastScrollY) {
                lastScrollY = scrollY
                this.sendMessageDelayed(this.obtainMessage(), 5)
            }
            if (mScrollListener != null)
                mScrollListener!!.onScroll(scrollY)


        }
    }

    interface OnScrollListener {
        fun onScroll(scrollY: Int)
    }

    fun getmScrollListener(): OnScrollListener? {
        return mScrollListener
    }

    fun setmScrollListener(mScrollListener: OnScrollListener) {
        this.mScrollListener = mScrollListener
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mScrollListener != null) {
            lastScrollY = this.scrollY
            mScrollListener!!.onScroll(lastScrollY)
        } else if (ev.action == MotionEvent.ACTION_UP) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(), 5)
        }
        return super.onTouchEvent(ev)
    }
}
