package com.zhihudailytest.CustomUI

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.Toast

/**
 * Created by Administrator on 2016/11/30.
 */

class PullToLoadMoreView : LinearLayout, View.OnTouchListener {

    private var footerView: View? = null
    private var touchSlop: Int = 0
    private var hideFooterHeight: Int = 0
    private var footerParams: ViewGroup.MarginLayoutParams? = null
    private var recyclerView: RecyclerView? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var isOnce = false
    private var canPull = false
    private var downY: Float = 0.toFloat()
    private var mScoller: Scroller? = null

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
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mScoller = Scroller(context)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed && isOnce == false) {
            recyclerView = getChildAt(0) as RecyclerView
            footerView = getChildAt(0)
            hideFooterHeight = footerView!!.height
            footerParams = footerView!!.layoutParams as ViewGroup.MarginLayoutParams
            isOnce = true
            linearLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager
            recyclerView!!.setOnTouchListener(this)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        setIsScrollAble(event)
        if (canPull) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> downY = event.rawY
                MotionEvent.ACTION_MOVE -> {
                    val moveY = event.rawY
                    val distance = (moveY - downY).toInt()
                    /* if (distance>0&&footerParams.bottomMargin==-hideFooterHeight)
                        return  false;
                    if (Math.abs(distance)<touchSlop)
                        return false;*/
                    //footerParams.bottomMargin= (int) (hideFooterHeight+distance/2.0);
                    // footerView.setLayoutParams(footerParams);
                    mScoller!!.startScroll(0, 0, 0, distance)
                }
            }
        }
        return false
    }

    override fun computeScroll() {
        if (mScoller!!.computeScrollOffset()) {
            this.scrollTo(mScoller!!.currX, mScoller!!.currY)
            invalidate()
        }
    }

    private fun setIsScrollAble(event: MotionEvent) {
        val count = linearLayoutManager!!.itemCount
        val last = linearLayoutManager!!.findLastCompletelyVisibleItemPosition()
        // Toast.makeText(getContext(),"count"+count+";LAST"+last,Toast.LENGTH_SHORT).show();
        val moveY = event.rawY

        if (count - 1 == last && moveY - downY < 0) {
            Toast.makeText(context, "FOOTER", Toast.LENGTH_SHORT).show()
            canPull = true
        } else {
            /* if(footerParams.bottomMargin!=0){
                footerParams.bottomMargin=0;
                footerView.setLayoutParams(footerParams);
            }*/
            canPull = false
        }
        if (count == 0) {
            canPull = true
        }
        if (!canPull) {
            downY = event.rawY
        }

    }
}
