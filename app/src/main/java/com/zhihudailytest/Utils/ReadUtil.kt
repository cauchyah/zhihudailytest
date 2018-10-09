package com.zhihudailytest.Utils

import android.util.SparseArray

import com.zhihudailytest.Bean.Story

/**
 * Created by Administrator on 2016/7/25.
 */
object ReadUtil {
    fun isRead(stories: List<Story>) {
        if (stories.size < 1) return
        val dao = DataBaseDao()
        val ids = dao.read
        if (ids.size() > 0) {
            var i = 0
            for (one in stories) {

                if (ids.get(one.id) != null) {
                    one.isReaded = true
                    i++
                }
                if (i >= ids.size()) break
            }
        }
    }

    fun setRead(id: Int) {
        val dao = DataBaseDao()
        dao.markRead(id)
    }

}
