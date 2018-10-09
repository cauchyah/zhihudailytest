package com.zhihudailytest.Bean

/**
 * Created by Administrator on 2016/7/19.
 */
class Theme {
    var others: List<Others>? = null

    class Others(var id: Int, var name: String?) {
        var thumbnail: String? = null
        var description: String? = null
        var isSelected: Boolean = false
    }


}

