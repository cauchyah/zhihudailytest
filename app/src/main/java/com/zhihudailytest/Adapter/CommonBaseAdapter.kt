package com.zhihudailytest.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.zhihudailytest.Utils.ViewHolder

/**
 * Created by Administrator on 2016/7/4.
 */
abstract class CommonBaseAdapter<T>(protected var mContext: Context, protected var mDatas: List<T>, private val mLayoutRes: Int) : BaseAdapter() {
    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItem(position: Int): T {
        return mDatas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val holder = ViewHolder.getViewHolder(
                mContext, mLayoutRes, position, convertView, parent)
        val item = mDatas[position]
        /*holder.setText(R.id.name,bean.getName()).setText(R.id.desc,bean.getDesc());*/
        initView(holder, item)

        return holder.convertView
    }

    abstract fun initView(holder: ViewHolder, item: T)
}

