package com.zhihudailytest.Bean

/**
 * Created by Administrator on 2016/7/4.
 */
class TopStory {
    var title: String? = null
    var ga_prefix: String? = null
    var type: Int = 0
    var id: Int = 0
    var image: String? = null

    constructor() {}
    constructor(title: String) {
        this.title = title
    }
}
