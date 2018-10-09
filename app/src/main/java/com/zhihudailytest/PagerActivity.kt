package com.zhihudailytest

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_pager.*

class PagerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        ButterKnife.bind(this)
        val adapter = MyPagerAdapter(this)
        viewPager!!.adapter = adapter
        tab!!.setBackgroundColor(ContextCompat.getColor(this, R.color.aqua))
        tab!!.tabIndicatorColor = ContextCompat.getColor(this, R.color.colorPrimary)
        //tabs.setV
    }

    internal inner class MyPagerAdapter(private val mContext: Context) : PagerAdapter() {

        override fun getCount(): Int {
            return 8
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageTitle(position: Int): CharSequence {

            return "position$position"
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val image = ImageView(mContext)
            container.addView(image)
            Glide.with(mContext)
                    .load(R.drawable.kaka)
                    .into(image)
            return image
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

    }
}
