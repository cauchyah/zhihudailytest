package com.zhihudailytest.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

import com.zhihudailytest.Fragment.DetailFragment

/**
 * Created by Administrator on 2016/7/16.
 */
class DetailAdapter(manager: FragmentManager, private val mData: List<Int>) : FragmentStatePagerAdapter(manager) {


    override fun getItem(position: Int): Fragment {
        return DetailFragment.getInstance(mData[position])
    }

    override fun getCount(): Int {
        return mData.size
    }

    fun getId(position: Int): Int {
        return mData[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        // fragmentMap.remove(position);
    }
}
