package com.zhihudailytest.Utils


import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.orhanobut.logger.Logger

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by Administrator on 2016/7/1.
 * 用于json请求，返回一个解析后的bean对象或 其中T是请求的bean
 */
class JsonRequest<T>(method: Int, url: String, private val tClass: Class<T>, listener: Response.Listener<T>, errorListener: Response.ErrorListener) : Request<T>(method, url, errorListener) {
    private val listener: Response.Listener<*>
    private val mGson: Gson

    init {
        this.listener = listener
        mGson = Gson()

    }

    /**
     *
     * @param url
     * @param clazz  bean对象
     * @param listener
     * @param errorListener
     */
    constructor(url: String, clazz: Class<T>, listener: Response.Listener<T>, errorListener: Response.ErrorListener) : this(Request.Method.GET, url, clazz, listener, errorListener) {}


    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {

            Logger.init("json")
            val jsonString = String(response.data, Charset.forName("UTF-8"))
            Logger.json(jsonString)
            return Response.success(mGson.fromJson(jsonString, tClass), HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        }

    }

    override fun deliverResponse(response: T) {
//        listener.onResponse(response)
    }
}
