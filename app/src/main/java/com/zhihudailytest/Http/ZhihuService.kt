package com.zhihudailytest.Http

import android.support.annotation.Nullable
import com.zhihudailytest.Bean.Extra
import com.zhihudailytest.Bean.NewsBean
import com.zhihudailytest.Bean.StoryDetail
import com.zhihudailytest.Bean.Theme
import com.zhihudailytest.Bean.ThemeInfo

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import rx.Observable

/**
 * Created by Administrator on 2016/7/6.
 */
interface ZhihuService {

    /*@GET("news/latest")
    Call<NewsBean> getLastNews();

    @GET("news/{id}")
    Call<StoryDetail> getNewDetail(@Path("id")String id);*/


    ///RxJava
    @get:Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @get:GET("news/latest")
    val lastNews: Observable<NewsBean>

    @get:Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @get:GET("themes")
    val themeList: Observable<Theme>

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/{id}")
    fun getStoryDetail(@Path("id") id: Int): Observable<StoryDetail>

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/before/{date}")
    fun getBeforeNews(@Path("date") date: String): Observable<NewsBean>

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @GET("story-extra/{id}")
    fun getExtra(@Path("id") id: Int): Observable<Extra>

    ///RxJava
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @GET("theme/{id}")
    fun getThemeInfo(@Path("id") id: Int): Observable<ThemeInfo>


    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/{id}")
    fun getBigImage(url: String): Observable<Nullable>
}
