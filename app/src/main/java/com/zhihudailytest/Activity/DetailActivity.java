package com.zhihudailytest.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.zhihudailytest.Adapter.DetailAdapter;
import com.zhihudailytest.Bean.Extra;
import com.zhihudailytest.Bean.StoryDetail;
import com.zhihudailytest.Fragment.DetailFragment;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DataBaseDao;
import com.zhihudailytest.Utils.LogUtil;
import com.zhihudailytest.Utils.ReadUtil;
import com.zhihudailytest.Utils.StatusBarCompat;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DetailActivity extends BaseActivity {

    private DetailAdapter mAdapter;
    private   int currentSelected;

    public static   void actionStart(Context context,Bundle bundle){
        Intent intent=new Intent(context,DetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.favor)
    TextView favor;
    @Bind(R.id.comment)
    TextView comment;
    @Bind(R.id.collect)
    TextView collect;
    @Bind(R.id.share)
    TextView share;


    private  List<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
      //  Log.e("abcd","onCreate"+current);

        //StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.colorPrimary));
        setToolbar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTranslucent3();
       Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
         ids=bundle.getIntegerArrayList("ids");
        int current=bundle.getInt("current");

        mAdapter=new DetailAdapter(getSupportFragmentManager(),ids);

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(mAdapter);
        currentSelected=ids.indexOf(current);
        viewPager.setCurrentItem(currentSelected);
        loadExtra(ids.indexOf(current));
       setListener();


    }


    @Override
    protected void initView() {

    }

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            };
    private ShareBoardlistener mShareBoardlistener=new ShareBoardlistener() {
        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage umImage=new UMImage(DetailActivity.this,BitmapFactory.decodeResource(getResources(),R.drawable.zhihu_icon));
            ShareAction shareAction = new ShareAction(DetailActivity.this);
            shareAction.setPlatform(share_media);
            shareAction.setCallback(mUmShareListener);//设置每个平台的点击事件
            shareAction.withTitle(story.getTitle());
            shareAction.withText(story.getTitle());
            shareAction.withTargetUrl(story.getShare_url());//点击分享内容打开的链接
            shareAction.withMedia(umImage);//附带的图片，音乐，视频等多媒体对象
            shareAction.share();//发起分享，调起微信，QQ，微博客户端进行分享。
        }
    };
    /**
     * 友盟分享后事件监听器
     */
    private UMShareListener mUmShareListener = new UMShareListener() {

        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToast("success");
            // TODO 分享成功
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("onError"+t.toString());
            // TODO 分享失败
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("onCancel");
            // TODO 分享取消
        }

    };
    private StoryDetail story;
    @Override
    protected void setListener() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (story==null) return;
                DetailFragment fragment= (DetailFragment) mAdapter.instantiateItem(viewPager,currentSelected);
                if (fragment==null) {return;}
                story=fragment.getStory();
                if (story==null){
                    return;
                }
                ShareAction action = new ShareAction(DetailActivity.this);
                action.setDisplayList(displaylist);
                action.setShareboardclickCallback(mShareBoardlistener);//设置友盟集成的分享面板的点击监听回调
                action.open();//打开集成的分享面板
            }
        });
        setNaviBack();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               /// showToast(position+"position");
                currentSelected=position;
                comment.setText("...");
                favor.setText("...");
                loadExtra(position);
                final  int id=ids.get(position);
                Observable.just(0)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer integer) {
                                DataBaseDao dao=new DataBaseDao();
                                return dao.markRead(id);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                switch (integer){
                                    case 1:
                                        Intent intent=new Intent("readChange");
                                        // intent.setAction("readChange");
                                        intent.putExtra("id",id);
                                        localBroadcastManager.sendBroadcast(intent);
                                        break;
                                }
                            }
                        });
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
   private Subscription subscription;
    private void loadExtra(int position){
         subscription=RetrofitManager.getInstance().getExtra(mAdapter.getId(position))
                .subscribeOn(Schedulers.io())
                 .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Extra>() {
                    @Override
                    public void call(Extra extra) {
                        comment.setText(extra.getComments()+"");
                        favor.setText(extra.getPopularity()+"");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription!=null&&!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
