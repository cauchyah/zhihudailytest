package com.zhihudailytest.Utils

/**
 * Created by Administrator on 2016/6/23.
 */
class SectionItem<T>(val title: String, private val items: List<T>?) {
    val count: Int
        get() = if (items == null) 1 else 1 + items.size

    fun getItem(position: Int): T {
        return items!![position]
    }

    override fun equals(o: Any?): Boolean {
        return if (o != null && o is SectionItem<*>) {
            o.title == title
        } else false
    }
}
