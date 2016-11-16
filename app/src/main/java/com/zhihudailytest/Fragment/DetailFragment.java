package com.zhihudailytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.zhihudailytest.Bean.StoryDetail;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SPUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends BaseFragment {

    @Bind(R.id.title)
    TextView title;
    private View view;
    @Bind(R.id.image)
     ImageView imageView;
    @Bind(R.id.webView)
     WebView webView;
    @Bind(R.id.source)
     TextView source;
    private StoryDetail story=null;

    private  int id;
    public static DetailFragment getInstance(int id)
    {
        DetailFragment fragment= new DetailFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  LogUtil.d("onCreate");
    }

    @Override
    public void onDoubleClick() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle=getArguments();
        id=bundle.getInt("id",0);
      //  LogUtil.d("onAttach"+id);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // int id=savedInstanceState.getInt("id");
      //  LogUtil.d("onActivityCreated");
    }

    public   DetailFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_detail, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initView();
        setSwipeRefreshLayout(view);
        setListener();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            loadData();
    }

    public StoryDetail getStory() {
        return story;
    }

    private void setListener() {

    }

    private void initView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return super.shouldOverrideUrlLoading(view, url);
                Uri uri=Uri.parse(url);
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            }
        });
        webView.getSettings().setDefaultTextEncodingName("utf-8");
      //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscription!=null&&!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private Subscription subscription;
    protected void loadData(){
        subscription=RetrofitManager.getInstance()
                .getStoryDetail(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<StoryDetail>() {
                    @Override
                    public void call(StoryDetail storyDetail) {
                        swipeRefreshLayout.setRefreshing(false);
                        story=storyDetail;
                       if (storyDetail.getImage()!=null){
                            source.setText(storyDetail.getImage_source());
                            title.setText(storyDetail.getTitle());
                            if(!loadImage(storyDetail.getId())){
                                Glide.with(DetailFragment.this)
                                        .load(storyDetail.getImage())
                                        .skipMemoryCache(true)
                                        .bitmapTransform(new CenterCrop(getContext())
                                                , new VignetteFilterTransformation(getContext(), new PointF(0.5f, 0.5f),
                                                        new float[]{0.1f, 0.1f, 0.1f}, 0.0f, 0.87f))

                                        .into(imageView);

                            }
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String str : storyDetail.getCss()) {
                            builder.append("<link rel=\"stylesheet\" href=\"" + str + "\" type=\"text/css\" />");
                        }
                        SharedPreferences sp= SPUtils.getThemeSP();
                        String string;
                        if (sp.getBoolean("isNight",false)){
                           // builder.append("<div style=\"background-color:#424242;color:#c60\" ");
                            builder.append(storyDetail.getBody());
                            //builder.append("</div>");
                            string=builder.toString();
                           string=string.replace("class=\"main-wrap content-wrap\"", "class=\"main-wrap content-wrap \" style=\"background-color:#424242;color:#000\"");
                           string=string.replace("class=\"author\"", "class=\"author\" style=\"color:#bbb\"");
                           string=string.replace("class=\"question-title\"", "class=\"question-title\" style=\"color:#b8b8b8\"");
                           string=string.replace("class=\"bio\"","class=\"bio\" style=\"color:#bbb\"");
                           string=string.replace("class=\"view-more\"","class=\"view-more\" style=\"color:#c60\"");
                        }
                        else {
                            builder.append(storyDetail.getBody());
                            string=builder.toString();
                        }
                        webView.loadData(
                                string
                                        , "text/html;charset=UTF-8", null);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "加载失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    void onBroadcastReceive(Context context, Intent intent) {

    }

    private boolean loadImage(int id){
        File root=new File(getContext().getCacheDir()+"/image/"+"image"+id+"file");
        if (!root.exists()) return false;
        Glide.with(DetailFragment.this)
                .load(root)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(new CenterCrop(getContext())
                        , new VignetteFilterTransformation(getContext(), new PointF(0.5f, 0.5f),
                                new float[]{0.1f, 0.1f, 0.1f}, 0.0f, 0.87f))
                .into(imageView);
        return  true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription!=null&&!subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}

