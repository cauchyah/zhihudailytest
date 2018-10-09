package com.zhihudailytest.CustomUI


import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.zhihudailytest.Bean.AdvInfo
import com.zhihudailytest.R

import java.lang.ref.WeakReference


/**
 * A simple [Fragment] subclass.
 */
class ViewPagerFragment : Fragment() {
    private var viewPager: ViewPager? = null
    private var indicatorLayout: LinearLayout? = null
    private var list: List<AdvInfo>? = null
    private var adapter: ViewPagerAdapter? = null
    private var isWheel = true
    private var lastTouchTime: Long = 0
    private var checkedColor: Int = 0
    private var uncheckColor: Int = 0

    private var currentPosition: Int = 0

    internal val task: Runnable = Runnable {
        if (isWheel) {
            //避免手动滑动后，马上翻页，应该等到下一个延迟时间才自动滑动
            if (System.currentTimeMillis() - lastTouchTime > delayTime - 500) {
                mHandler.sendEmptyMessage(WHEEL)
            } else {
                mHandler.sendEmptyMessage(WHEEL_WAIT)
            }
        }
    }
    private val mHandler = MyHandler(this)

    private var mListener: onImageClickListener? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater!!.inflate(R.layout.fragment_view_pager, container, false)
        viewPager = view.findViewById<View>(R.id.viewPager) as ViewPager
        indicatorLayout = view.findViewById<View>(R.id.indicatorLayout) as LinearLayout
        return view
    }

    fun setIndicator(position: Int, checkedColor: Int, uncheckColor: Int) {
        this.checkedColor = checkedColor
        this.uncheckColor = uncheckColor
        when (position) {
            LEFT -> indicatorLayout!!.gravity = Gravity.LEFT
            RIGHT -> indicatorLayout!!.gravity = Gravity.RIGHT
            CENTER -> indicatorLayout!!.gravity = Gravity.CENTER_HORIZONTAL
            else -> indicatorLayout!!.gravity = Gravity.CENTER_HORIZONTAL
        }
        drawIndicator()

    }

    private fun drawIndicator() {
        if (list!!.size < 1) indicatorLayout!!.visibility = View.GONE
        var dot: View
        val params = LinearLayout.LayoutParams(20, 20)
        params.leftMargin = 8
        for (i in 0 until list!!.size - 2) {
            dot = View(context)
            dot.setBackgroundResource(R.drawable.dot_normal)
            if (i == 0)
                dot.background.setColorFilter(this.checkedColor, PorterDuff.Mode.OVERLAY)
            else
                dot.background.setColorFilter(this.uncheckColor, PorterDuff.Mode.OVERLAY)
            dot.layoutParams = params
            indicatorLayout!!.addView(dot)
        }


    }

    fun setData(resIds: List<AdvInfo>) {
        this.list = resIds
        if (adapter == null) {
            adapter = ViewPagerAdapter()
        }
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                lastTouchTime = System.currentTimeMillis()
                currentPosition = position
                if (position < 1) {
                    currentPosition = list!!.size - 2
                } else if (position > list!!.size - 2) {
                    currentPosition = 1
                }
                selectIndicator(currentPosition - 1)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == 0) {
                    if (currentPosition == list!!.size - 2 || currentPosition == 1)
                        viewPager!!.setCurrentItem(currentPosition, false)
                }

            }
        })
        //设置第一张要显示的图片
        if (list != null && list!!.size > 1) {
            viewPager!!.currentItem = 1
        }

    }

    private fun selectIndicator(position: Int) {
        val count = indicatorLayout!!.childCount
        var view: View
        for (i in 0 until count) {
            view = indicatorLayout!!.getChildAt(i)
            if (i == position) {
                // view.setEnabled(true);
                view.background.setColorFilter(this.checkedColor, PorterDuff.Mode.OVERLAY)
            } else {
                // view.setEnabled(false);
                view.background.setColorFilter(this.uncheckColor, PorterDuff.Mode.OVERLAY)
            }
        }
    }

    fun notifyDataSetChange() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
            setWheel(true)
            setIndicator(CENTER, ContextCompat.getColor(context, R.color.checkedColor), ContextCompat.getColor(context, R.color.uncheckColor))
            viewPager!!.currentItem = 1
        }
    }

    private class MyHandler(fragment: ViewPagerFragment) : Handler() {

        private val weakReference: WeakReference<ViewPagerFragment>


        override fun handleMessage(msg: Message) {
            if (weakReference.get() != null) {
                val fraggment = weakReference.get()
                if (fraggment!!.list!!.size > 0) {
                    if (msg.what == WHEEL) {

                        val position = fraggment!!.viewPager!!.currentItem
                        fraggment.viewPager!!.currentItem = (position + 1) % fraggment.list!!.size
                        removeCallbacks(fraggment.task)
                        postDelayed(fraggment.task, delayTime.toLong())
                    } else if (msg.what == WHEEL_WAIT) {
                        removeCallbacks(fraggment!!.task)
                        postDelayed(fraggment.task, delayTime.toLong())
                    }
                }
            }
        }

        init {
            weakReference = WeakReference(fragment)
        }
    }

    fun setWheel(isWheel: Boolean) {
        this.isWheel = isWheel
        if (isWheel && list!!.size > 0) {
            mHandler.removeCallbacks(task)
            mHandler.postDelayed(task, delayTime.toLong())
        }
    }

    private inner class ViewPagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return list!!.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.header_view, null)

            val imageView = view.findViewById<View>(R.id.imageView) as ImageView
            val textView = view.findViewById<View>(R.id.title) as TextView
            val item = list!![position]
            textView.text = item.title
            if (item.res != null) {
                Glide.with(context)
                        .load(item.res)
                        .into(imageView)
            } else {
                Glide.with(context)
                        .load(item.url)
                        .into(imageView)
            }
            imageView.setOnClickListener {
                if (mListener != null) {
                    mListener!!.onClick(imageView, position, item)
                }
            }
            container.addView(view)
            return view

        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    interface onImageClickListener {
        fun onClick(view: View, position: Int, item: AdvInfo)
    }

    fun setOnImageClickListener(listener: onImageClickListener) {
        this.mListener = listener
    }

    companion object {

        private val WHEEL = 10
        private val WHEEL_WAIT = 11
        private val delayTime = 5000
        val RIGHT = 1
        val LEFT = 2
        val CENTER = 3
    }

}// Required empty public constructor
