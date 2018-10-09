package com.zhihudailytest.Http


import com.zhihudailytest.Bean.Extra
import com.zhihudailytest.Bean.NewsBean
import com.zhihudailytest.Bean.StoryDetail
import com.zhihudailytest.Bean.Theme
import com.zhihudailytest.Bean.ThemeInfo
import com.zhihudailytest.MyApplication
import com.zhihudailytest.Utils.NetworkUtil

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

/**
 * Created by Administrator on 2016/7/6.
 */
class RetrofitManager private constructor() {
    private val mService: ZhihuService
    private val mRewriteCacheCtrollerInterceptor = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtil.isNetworkConnected) {
            //没网走缓存
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        val response = chain.proceed(request)
        if (NetworkUtil.isNetworkConnected) {
            val cacheControl = request.cacheControl().toString()
            response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build()
        } else {
            response.newBuilder()
                    .header("Cache-Control", "public,only-if-cached,max-stale=$CACHE_STALE_LONG")
                    .removeHeader("Pragma")
                    .build()
        }
    }

    val lastNews: Observable<NewsBean>
        get() = mService.lastNews
    val themeList: Observable<Theme>
        get() = mService.themeList

    init {
        initOkHttpClien()
        val mRetrofit = Retrofit.Builder()
                .client(mOkHttpClient!!)
                .baseUrl(ZHIHU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        mService = mRetrofit.create(ZhihuService::class.java)
    }

    private fun initOkHttpClien() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        if (mOkHttpClient == null) {
            synchronized(RetrofitManager::class.java) {
                if (mOkHttpClient == null) {

                    val cache = Cache(File(MyApplication.ApplicationContext!!.cacheDir, "HttpCache"), (100 * 1024 * 1024).toLong())
                    mOkHttpClient = OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(mRewriteCacheCtrollerInterceptor)
                            .addNetworkInterceptor(mRewriteCacheCtrollerInterceptor)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build()
                }
            }
        }

    }

    fun getStoryDetail(id: Int): Observable<StoryDetail> {
        return mService.getStoryDetail(id)
    }

    fun getBeforeNews(date: String): Observable<NewsBean> {
        return mService.getBeforeNews(date)
    }

    fun getExtra(id: Int): Observable<Extra> {
        return mService.getExtra(id)
    }

    fun getThemeInfo(id: Int): Observable<ThemeInfo> {
        return mService.getThemeInfo(id)
    }

    companion object {
        private val ZHIHU_BASE_URL = "http://news-at.zhihu.com/api/4/"
        //短缓存有效期为1分钟
        const val CACHE_STALE_SHORT = 60
        //长缓存有效期为7天
        const val CACHE_STALE_LONG = 60 * 60 * 24 * 7
        const val CACHE_CONTROL_AGE = "Cache-Control: public, max-age="

        //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
        val CACHE_CONTROL_CACHE = "only-if-cached, max-stale=$CACHE_STALE_LONG"
        //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
        val CACHE_CONTROL_NETWORK = "max-age=0"
        private var mOkHttpClient: OkHttpClient? = null
        private var ourInstance: RetrofitManager? = null

        val instance: RetrofitManager
            get() {
                if (ourInstance == null) {
                    synchronized(RetrofitManager::class.java) {
                        if (ourInstance == null)
                            ourInstance = RetrofitManager()
                    }
                }
                return ourInstance!!
            }
    }


}
