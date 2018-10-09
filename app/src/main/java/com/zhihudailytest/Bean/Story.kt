package com.zhihudailytest.Bean

/**
 * Created by Administrator on 2016/7/4.
 */
class Story {
    var title: String? = null
    var ga_prefix: String? = null
    var type: Int = 0
    var id: Int = 0
    var images: Array<String>? = null
    var date = ""
    var isMultipic: Boolean = false
    var isReaded: Boolean = false
    var image: String? = null//top story 专用

    constructor(date: String) {
        this.date = date

    }

    constructor() {}

}
