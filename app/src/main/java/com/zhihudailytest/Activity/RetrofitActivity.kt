package com.zhihudailytest.Activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class RetrofitActivity : AppCompatActivity() {
    /* @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.glide)
    Button button;
    @OnClick(R.id.glide)void onGlide(View view){
        Glide.with(this)
                .load("http://pic2.zhimg.com/6d714ed960d980f254b2195ddea99236.jpg")
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true)
                .into(image);
    };
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_retrofit);
        //ButterKnife.bind(this);
        /* RetrofitManager.getInstance()
                .getBeforeNews("20160706")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsBean>() {
                    @Override
                    public void call(NewsBean newsBean) {
                        Toast.makeText(RetrofitActivity.this, newsBean.getDate()+"date,"
                                +newsBean.getStories().size()+"size,", Toast.LENGTH_SHORT).show();
                    }
                });*/
        /* RetrofitManager2.builder().getStoryDetail(8528689)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StoryDetail>() {
                    @Override
                    public void call(StoryDetail storyDetail) {
                       LogUtil.d (storyDetail.getTitle()+"title,"+"image:"
                                +storyDetail.getImage()+"image_souce"+storyDetail.getImage_source());
                    }
                });*/

        //RxJava+Retrofit
        /*  Retrofit mRetrofit=new Retrofit
                .Builder()
                .baseUrl("http://news-at.zhihu.com/api/4/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ZhihuService service=mRetrofit.create(ZhihuService.class);
        Observable<NewsBean> observer=service.getLastNews();
        observer.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsBean>() {
                    @Override
                    public void call(NewsBean newsBean) {
                        LogUtil.d(newsBean.getDate()+"date,"+newsBean.getStories().size()+"size");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });*/


        //原生调用
        /*
       Retrofit mRetrofit=new Retrofit
                .Builder()
                .baseUrl("http://news-at.zhihu.com/api/4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


      ZhihuService oneNew=mRetrofit.create(ZhihuService.class);
        Call<StoryDetail> call=oneNew.getNewDetail("3892357");
        call.enqueue(new Callback<StoryDetail>() {
            @Override
            public void onResponse(Call<StoryDetail> call, Response<StoryDetail> response) {
                StoryDetail detail=response.body();
                LogUtil.d((detail.getTitle()+"title,"+"image:"+detail.getImage()+"image_souce"+detail.getImage_source()));
            }

            @Override
            public void onFailure(Call<StoryDetail> call, Throwable t) {

            }
        });*/
        /* ZhihuService zhihuApi=mRetrofit.create(ZhihuService.class);
        Call<NewsBean> call=zhihuApi.getLastNews();
        call.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                NewsBean result=response.body();
                LogUtil.d(result.getDate()+"date,"+result.getStories().size()+"size");

            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {

            }
        });*/


    }
}
