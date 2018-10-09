package com.zhihudailytest.CustomUI

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View

import com.zhihudailytest.Bean.Story
import com.zhihudailytest.R
import com.zhihudailytest.Utils.DateUtil

/**
 * Created by Administrator on 2016/12/4.
 */

class TitleItemdecoration(private val mContext: Context, private val mData: List<Story>) : RecyclerView.ItemDecoration() {
    private var mHeight = 30
    private var textSize = 14
    private val mPaint: Paint
    private val mBound: Rect

    init {
        mPaint = Paint()
        mBound = Rect()
        getPx()

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        if (position != 0) {
            if (position == 1) {
                outRect.set(0, mHeight, 0, 0)
            } else {
                if (mData[position - 1].date != null && mData[position - 1].date != mData[position - 2].date) {
                    outRect.set(0, mHeight, 0, 0)
                } else {
                    outRect.set(0, 0, 0, 0)
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val count = parent.childCount
        for (i in 0 until count) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            if (position > -1) {
                if (position == 0) {
                } else if (position == 1) {
                    drawTitleArea(c, left, right, params, 0, child)
                } else if (mData[position - 1].date != null && mData[position - 1].date != mData[position - 2].date) {
                    drawTitleArea(c, left, right, params, position - 1, child)
                }
            }

        }
    }

    private fun drawTitleArea(c: Canvas, left: Int, right: Int, params: RecyclerView.LayoutParams, position: Int, child: View) {
        val date = DateUtil.string2date(mData[position].date)
        mPaint.color = ContextCompat.getColor(mContext, R.color.background)
        c.drawRect(left.toFloat(), (child.top - mHeight - params.topMargin).toFloat(), right.toFloat(), (child.top - params.topMargin).toFloat(), mPaint)
        mPaint.textSize = textSize.toFloat()
        mPaint.getTextBounds(date, 0, date!!.length, mBound)
        mPaint.color = Color.parseColor("#a9a9a9")

        c.drawText(date, (child.paddingLeft + 15).toFloat(), (child.top - params.topMargin - (mHeight - mBound.height()) / 2).toFloat(), mPaint)
    }

    private fun getPx() {
        mHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHeight.toFloat(), mContext.resources.displayMetrics).toInt()
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat(), mContext.resources.displayMetrics).toInt()

    }

}
