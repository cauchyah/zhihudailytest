package com.zhihudailytest.Bean

/**
 * Created by Administrator on 2016/10/31.
 */

class AdvInfo {
    var res: Int? = null//资源文件
    var url: String? = null//网络来源
    var title: String? = null//显示文本

    constructor(res: Int?) {
        this.res = res
    }

    constructor(res: Int?, url: String, title: String) {
        this.res = res
        this.url = url
        this.title = title
    }

    constructor(url: String) {
        this.url = url
    }
}
