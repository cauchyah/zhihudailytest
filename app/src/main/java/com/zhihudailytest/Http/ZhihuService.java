package com.zhihudailytest.Http;

import android.support.annotation.Nullable;

import com.zhihudailytest.Bean.Extra;
import com.zhihudailytest.Bean.NewsBean;
import com.zhihudailytest.Bean.StoryDetail;
import com.zhihudailytest.Bean.Theme;
import com.zhihudailytest.Bean.ThemeInfo;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/6.
 */
public interface ZhihuService {

    /*@GET("news/latest")
    Call<NewsBean> getLastNews();

    @GET("news/{id}")
    Call<StoryDetail> getNewDetail(@Path("id")String id);*/


    ///RxJava
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @GET("news/latest")
    Observable<NewsBean> getLastNews();

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/{id}")
    Observable<StoryDetail> getStoryDetail(@Path("id")int id);

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/before/{date}")
    Observable<NewsBean> getBeforeNews(@Path("date")String date);

    @Headers(RetrofitManager.CACHE_CONTROL_AGE+RetrofitManager.CACHE_STALE_SHORT)
    @GET("story-extra/{id}")
    Observable<Extra>getExtra(@Path("id")int id);

    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("themes")
    Observable<Theme> getThemeList();

    ///RxJava
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @GET("theme/{id}")
    Observable<ThemeInfo> getThemeInfo(@Path("id")int id);


    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("news/{id}")
    Observable<Nullable> getBigImage(String url);
}
