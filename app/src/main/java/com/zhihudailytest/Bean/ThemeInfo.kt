package com.zhihudailytest.Bean

/**
 * Created by Administrator on 2016/7/25.
 */
class ThemeInfo {
    var stories: List<Story>? = null
    var background: String? = null
    var editors: List<Editors>? = null
    var image: String? = null
    var description: String? = null

    inner class Editors {
        var url: String? = null
        var bio: String? = null
        var avatar: String? = null
        var id: Int = 0
        var name: String? = null
    }
}
