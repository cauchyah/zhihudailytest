package com.zhihudailytest.Adapter

import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.zhihudailytest.Activity.DetailActivity
import com.zhihudailytest.Bean.Story
import com.zhihudailytest.Utils.DataBaseDao

import java.util.ArrayList

import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation

/**
 * Created by Administrator on 2016/7/5.
 */
class MyPagerAdapter(private val mContext: Context, private val mList: List<Story>) : PagerAdapter() {

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val iv = ImageView(mContext)
        val one = mList[position]
        Glide.with(mContext)
                .load(one.image)

                .bitmapTransform(CenterCrop(mContext),
                        VignetteFilterTransformation(
                                mContext, PointF(0.5f, 0.5f),
                                floatArrayOf(0.1f, 0.1f, 0.1f), 0.0f, 0.9f))
                .into(iv)

        container.addView(iv)
        iv.setOnClickListener {
            val story = mList[position]
            val currentId = story.id
            val idList = ArrayList<Int>()
            for (one in mList) {
                val id = one.id
                idList.add(id)
            }
            val bundle = Bundle()
            bundle.putIntegerArrayList("ids", idList)
            bundle.putInt("current", currentId)
            DetailActivity.actionStart(mContext, bundle)
            if (!story.isReaded) {

                val dao = DataBaseDao()
                dao.markRead(currentId)
                story.isReaded = true

            }
        }
        return iv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
