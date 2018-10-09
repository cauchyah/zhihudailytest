package com.zhihudailytest.CustomUI

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

import com.zhihudailytest.R

/**
 * Created by Administrator on 2016/8/2.
 */
class LogoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    protected var mTextSize = sp2px(DEFAULT_TEXT_SIZE)
    protected var mTextColor = DEFAULT_TEXT_COLOR
    protected var mOffset = dp2px(DEFAULT_OFFSET)
    protected var mRingWidth = dp2px(DEFAULT_RING_WIDTH)
    protected var mRadius = dp2px(DEFAULT_RADIUS)
    private val mPaint: Paint

    init {
        mPaint = Paint()
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LogoView)
        /*mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);*/
        obtainAttributeSet2(ta)
    }

    private fun obtainAttributeSet2(ta: TypedArray?) {
        if (ta == null) return
        //
        //if (ta==null) return;
        //  mOffset= (int) ta.getDimension(R.styleable.LogoViewStyleable_offset,mOffset);
        mRadius = ta.getDimension(R.styleable.LogoView_radius, mRadius.toFloat()).toInt()
        mRingWidth = ta.getDimension(R.styleable.LogoView_ring_width, mRingWidth.toFloat()).toInt()
        mTextColor = ta.getColor(R.styleable.LogoView_text_color, mTextColor)
        mTextSize = ta.getDimension(R.styleable.LogoView_text_size, mTextSize.toFloat()).toInt()


        ta.recycle()
        mPaint.textSize = mTextSize.toFloat()
        mPaint.color = mTextColor
        mPaint.isAntiAlias = true
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)

        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
        // mRealWidth=getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        val size = View.MeasureSpec.getSize(heightMeasureSpec)
        var result = 0
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            val TextHeight = (mPaint.descent() - mPaint.ascent()).toInt()
            result = (paddingBottom + paddingTop
                    + Math.max(mRadius * 2 + mRingWidth, Math.abs(TextHeight)))
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val text = "知乎日报"
        val textWidth = mPaint.measureText(text).toInt()
        val left = ((width.toFloat() - (mRadius * 2).toFloat() - textWidth * 0.90f) / 2.0f).toInt()
        val top = (height / 2.0f - mRadius).toInt()
        val rel = RectF(left.toFloat(), top.toFloat(), (mRadius * 2 + left).toFloat(), (mRadius * 2 + top).toFloat())
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mRingWidth.toFloat()
        canvas.drawArc(rel, 85f, 245f, false, mPaint)
        mPaint.style = Paint.Style.FILL
        val x = left + mRadius * 2 - textWidth * 0.10f
        canvas.drawText(text, x, (mRadius * 2 + paddingTop).toFloat(), mPaint)

    }

    protected fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal.toFloat(),
                resources.displayMetrics).toInt()
    }

    protected fun sp2px(spVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal.toFloat(),
                resources.displayMetrics).toInt()
    }

    companion object {
        protected val DEFAULT_TEXT_SIZE = 28
        protected val DEFAULT_OFFSET = 10
        protected val DEFAULT_TEXT_COLOR = -0x1
        protected val DEFAULT_RING_WIDTH = 5
        protected val DEFAULT_RADIUS = 20
    }
}
