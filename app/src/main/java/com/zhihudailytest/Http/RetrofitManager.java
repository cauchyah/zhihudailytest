package com.zhihudailytest.Http;


import android.support.annotation.Nullable;

import com.zhihudailytest.Bean.Extra;
import com.zhihudailytest.Bean.NewsBean;
import com.zhihudailytest.Bean.StoryDetail;
import com.zhihudailytest.Bean.Theme;
import com.zhihudailytest.Bean.ThemeInfo;
import com.zhihudailytest.MyApplication;
import com.zhihudailytest.Utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/6.
 */
public class RetrofitManager {
    private  static final  String ZHIHU_BASE_URL="http://news-at.zhihu.com/api/4/";
    //短缓存有效期为1分钟
    public static final int CACHE_STALE_SHORT = 60;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;
    public static final String CACHE_CONTROL_AGE = "Cache-Control: public, max-age=";

    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_LONG;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    public static final String CACHE_CONTROL_NETWORK = "max-age=0";
    private final ZhihuService mService;
    private static OkHttpClient mOkHttpClient;
    private static RetrofitManager ourInstance ;

    public static RetrofitManager getInstance() {
        if (ourInstance==null){
            synchronized (RetrofitManager.class){
                if (ourInstance==null)
                    ourInstance=new RetrofitManager();
            }
        }
        return ourInstance;
    }

    private RetrofitManager() {
        initOkHttpClien();
        Retrofit mRetrofit=new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(ZHIHU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService=mRetrofit.create(ZhihuService.class);
    }

    private void initOkHttpClien() {
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient==null){
            synchronized (RetrofitManager.class){
                if (mOkHttpClient==null) {

                    Cache cache=new Cache(new File(MyApplication.ApplicationContext.getCacheDir(),"HttpCache"),100*1024*1024);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(mRewriteCacheCtrollerInterceptor)
                            .addNetworkInterceptor(mRewriteCacheCtrollerInterceptor)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build();
                }
            }
        }

    }
    private Interceptor mRewriteCacheCtrollerInterceptor=new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request=chain.request();
            if (!NetworkUtil.isNetworkConnected()){
                //没网走缓存
                request=request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response=chain.proceed(request);
            if (NetworkUtil.isNetworkConnected()){
                String cacheControl=request.cacheControl().toString();
                return response.newBuilder()
                        .header("Cache-Control",cacheControl)
                        .removeHeader("Pragma")
                        .build();
            }
            else{
                return  response.newBuilder()
                        .header("Cache-Control","public,only-if-cached,max-stale="+CACHE_STALE_LONG)
                        .removeHeader("Pragma")
                        .build();
            }

        }
    };

    public Observable<NewsBean> getLastNews(){
        return mService.getLastNews();
    }
    public Observable<StoryDetail> getStoryDetail(int id){
        return mService.getStoryDetail(id);
    }
    public Observable<NewsBean>getBeforeNews(String date){
        return mService.getBeforeNews(date);
    }
    public Observable<Extra>getExtra(int id){return  mService.getExtra(id);}
    public Observable<Theme>getThemeList(){return  mService.getThemeList();}
    public Observable<ThemeInfo>getThemeInfo(int id){return  mService.getThemeInfo(id);}


}
