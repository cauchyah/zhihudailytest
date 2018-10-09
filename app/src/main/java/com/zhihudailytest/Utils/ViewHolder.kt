package com.zhihudailytest.Utils

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Administrator on 2016/6/30.
 */
class ViewHolder(context: Context, layoutId: Int, var position: Int, parent: ViewGroup) {
    val convertView: View
    private val mViews: SparseArray<View>
    private var temp: View? = null

    init {
        this.convertView = LayoutInflater.from(context).inflate(layoutId, parent, false)
        this.convertView.tag = this
        mViews = SparseArray()

    }

    fun <T : View> getView(resId: Int): T {
        temp = mViews.get(position)
        //Log.i("abcd", "getView: "+mPosition);
        if (temp != null) {
            return (temp as T?)!!
        } else {
            temp = convertView.findViewById(resId)
            mViews.put(position, temp)
            return (temp as T?)!!
        }
    }

    fun setText(resId: Int, text: String): ViewHolder {
        (convertView.findViewById<View>(resId) as TextView).text = text
        return this
    }

    fun setImage(resId: Int, imageRes: Int): ViewHolder {
        (convertView.findViewById<View>(resId) as ImageView).setImageResource(imageRes)
        return this
    }

    companion object {

        fun getViewHolder(context: Context, layoutId: Int,
                          position: Int, convertView: View?, parent: ViewGroup): ViewHolder {
            if (convertView == null) {
                return ViewHolder(context, layoutId, position, parent)
            } else {
                val holder = convertView.tag as ViewHolder
                holder.position = position
                return holder
            }
        }
    }
}
