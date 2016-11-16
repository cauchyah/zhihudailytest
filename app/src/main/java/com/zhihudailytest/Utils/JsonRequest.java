package com.zhihudailytest.Utils;



import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/7/1.
 * 用于json请求，返回一个解析后的bean对象或 其中T是请求的bean
 */
public class JsonRequest<T> extends Request<T> {
    private Response.Listener listener;
    private  Class<T> tClass;
    private Gson mGson;

    public JsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener){
            super(method,url,errorListener);
        this.listener=listener;
        this.tClass=clazz;
        mGson=new Gson();

    }

    /**
     *
     * @param url
     * @param clazz  bean对象
     * @param listener
     * @param errorListener
     */
    public JsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener){
        this(Method.GET,url,clazz,listener,errorListener);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            Logger.init("json");
            String jsonString=new String(response.data,"UTF-8");
            Logger.json(jsonString);
            return Response.success(mGson.fromJson(jsonString,tClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
