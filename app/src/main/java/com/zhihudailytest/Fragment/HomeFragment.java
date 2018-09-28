package com.zhihudailytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihudailytest.Activity.BaseActivity;
import com.zhihudailytest.Adapter.MyPagerAdapter;
import com.zhihudailytest.Adapter.NewsAdapter;
import com.zhihudailytest.Bean.NewsBean;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.CustomUI.Draw;
import com.zhihudailytest.CustomUI.TitleItemdecoration;
import com.zhihudailytest.CustomUI.ViewPagerFragment;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DateUtil;
import com.zhihudailytest.Utils.LogUtil;
import com.zhihudailytest.Utils.NetworkUtil;
import com.zhihudailytest.Utils.ReadUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    private static final int  WHEEL = 1;
    private static final int WHEEL_WAIT = 2;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    private List<Story> mNewsData = new ArrayList<Story>();
    private List<Story> mTopStoryData = new ArrayList<Story>();
    private NewsAdapter mAdapter;
    private View headerView;
    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;
    private TextView title;
    private LinearLayout dotLayout;
    private static final int delayTime = 5000;
    private long lastTouchTime;
    private boolean isLoading = false;
    private String beforeDate;
    private int currentPage = 0;
    private View root;
    private static final String TAG = "abcd";
    private int lastCount = 0;
    private Subscription loadMore;
    private Subscription loadData;
    private  int selectPosition;
    @Override
    void onBroadcastReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);
        if (id == -1) return;
        int i = -1;
        for (Story one : mNewsData) {
            i++;
            if (one.getId() == id) {
                one.setReaded(true);
                break;
            }

        }
        mAdapter.notifyItemChanged(i + 1);

    }

    static class MyHandler extends Handler {
        private WeakReference<HomeFragment> reference;

        public MyHandler(HomeFragment t) {
            reference = new WeakReference<HomeFragment>(t);

        }

        @Override
        public void handleMessage(Message msg) {
            if (reference.get()!= null) {
                HomeFragment fraggment = reference.get();
                if (fraggment.mTopStoryData.size() > 0) {
                    if (msg.what == WHEEL) {

                        int position = fraggment.mViewPager.getCurrentItem();
                        fraggment.mViewPager.setCurrentItem((position + 1) % fraggment.mTopStoryData.size());
                        removeCallbacks(fraggment.timeTask);
                        postDelayed(fraggment.timeTask, delayTime);
                    } else if (msg.what == WHEEL_WAIT) {
                        removeCallbacks(fraggment.timeTask);
                        postDelayed(fraggment.timeTask, delayTime);
                    }
                }

            }

        }
    }

    private MyHandler mHanlder = new MyHandler(this);

    private Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            //避免手动滑动后，马上翻页，应该等到下一个延迟时间才自动滑动
            if (System.currentTimeMillis() - lastTouchTime > delayTime - 500) {
                mHanlder.sendEmptyMessage(WHEEL);
            } else {
                mHanlder.sendEmptyMessage(WHEEL_WAIT);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        setListener();
        registerReceiver();
        setSwipeRefreshLayout(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHanlder.removeCallbacks(timeTask);
        mHanlder.postDelayed(timeTask, delayTime);
    }

    @Override
    public void onPause() {
        mHanlder.removeCallbacks(timeTask);
        if (loadData != null && !loadData.isUnsubscribed())
            loadData.unsubscribe();
        if (loadMore != null && !loadMore.isUnsubscribed())
            loadMore.unsubscribe();
        super.onPause();
    }


    @Override
    public void onDoubleClick() {
        mLinearLayoutManager.smoothScrollToPosition(newsRecyclerView, null, 0);
    }

    protected void loadData() {
        loadData = RetrofitManager.getInstance()
                .getLastNews()
                .subscribeOn(Schedulers.io())
                .map(new Func1<NewsBean, NewsBean>() {
                    @Override
                    public NewsBean call(NewsBean newsBean) {
                        List<Story> stories = newsBean.getStories();
                        if (stories != null) {
                            ReadUtil.isRead(stories);
                            for (Story item:stories){
                                item.setDate(newsBean.getDate());
                            }
                        }
                        return newsBean;
                    }
                })
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsBean>() {
                    @Override
                    public void call(NewsBean newsBean) {
                        swipeRefreshLayout.setRefreshing(false);
                        List<Story> stories = newsBean.getStories();
                        if (stories.size() <= lastCount) return;
                        lastCount = stories.size();
                        beforeDate = newsBean.getDate();//最后加载的日期
                        mNewsData.clear();
                        mNewsData.addAll(stories);
                        mTopStoryData.clear();
                        mTopStoryData.add(newsBean.getTop_stories().get(newsBean.getTop_stories().size()-1));
                        mTopStoryData.addAll(newsBean.getTop_stories());
                        mTopStoryData.add(newsBean.getTop_stories().get(0));
                        mPagerAdapter.notifyDataSetChanged();
                        mViewPager.setCurrentItem(1);
                        mAdapter.notifyDataSetChanged();

                        initBanner();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (!NetworkUtil.isNetworkConnected()) {
                            showToast("网络连接超时，稍后再试。");
                        }
                    }
                });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            toolbar.setTitle("首页");
            onResume();
        } else {
            onPause();
        }
    }

    private void initView() {
        toolbar = ((BaseActivity) getActivity()).getToolbar();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        newsRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置ItemAnimator
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newsRecyclerView.addItemDecoration(new TitleItemdecoration(getContext(),mNewsData));
        // 设置固定大小
        newsRecyclerView.setHasFixedSize(true);
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_view, null);
        mViewPager = (ViewPager) headerView.findViewById(R.id.viewPager);
        title = (TextView) headerView.findViewById(R.id.title);
        dotLayout = (LinearLayout) headerView.findViewById(R.id.dotLayout);
        mAdapter = new NewsAdapter(getContext(), mNewsData);
        mAdapter.setHeaderViewPager(headerView);
        newsRecyclerView.setAdapter(mAdapter);
        mPagerAdapter = new MyPagerAdapter(getContext(), mTopStoryData);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void setListener() {
        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = mLinearLayoutManager.findFirstVisibleItemPosition();
                int lastPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                int viewType = mAdapter.getItemViewType(position);
                if (viewType == 2) {
                    toolbar.setTitle("首页");
                }
                else if (position==1){
                    toolbar.setTitle("今日热闻");
                }
                else if (!mNewsData.get(position-1).getDate().equals(mNewsData.get(position-2).getDate())) {

                    //日期
                    if (dy > 0)
                        toolbar.setTitle(DateUtil.string2date(
                                mNewsData.get(mAdapter.getRealPosition(position)).getDate()));
                    else {
                        String date = mNewsData.get(mAdapter.getRealPosition(position)).getDate();
                        date = (Integer.valueOf(date) + 1) + "";
                        toolbar.setTitle(DateUtil.string2date(date));
                    }
                }
                if (dy > 0 && !isLoading && lastPosition == mNewsData.size()) {
                    isLoading = true;
                    loadMore();
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                lastTouchTime = System.currentTimeMillis();
                selectPosition=position;
                if (position<1)
                    selectPosition=mTopStoryData.size()-2;
                else if (position>mTopStoryData.size()-2){
                    selectPosition=1;
                }
                selected(selectPosition-1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==0){
                    if (selectPosition==mTopStoryData.size()-2||selectPosition==1){
                        mViewPager.setCurrentItem(selectPosition,false);
                    }
                }
            }
        });

    }
    private void initBanner() {
        if (dotLayout.getChildCount() < 1)
            Draw.drawDot(getContext(), dotLayout, mTopStoryData.size()-2);
        mViewPager.setCurrentItem(currentPage);
        selected(currentPage);
    }

    /**
     * 设置选中的项
     *
     * @param position
     */
    private void selected(int position) {
        if (mTopStoryData.size() != 0 && dotLayout.getVisibility() == View.GONE) {
            dotLayout.setVisibility(View.VISIBLE);
        }
        title.setText(mTopStoryData.get(position).getTitle());
        for (int i = 0; i < dotLayout.getChildCount(); i++) {
            if (position == i) dotLayout.getChildAt(i).setEnabled(true);
            else dotLayout.getChildAt(i).setEnabled(false);

        }
    }

    private void loadMore() {
        loadMore = RetrofitManager.getInstance()
                .getBeforeNews(beforeDate)
                .subscribeOn(Schedulers.io())
                .map(new Func1<NewsBean, NewsBean>() {
                    @Override
                    public NewsBean call(NewsBean newsBean) {
                        List<Story> stories = newsBean.getStories();
                        if (stories != null) {
                            ReadUtil.isRead(stories);
                            for (Story item:stories){
                                item.setDate(newsBean.getDate());
                            }
                        }
                        return newsBean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsBean>() {
                    @Override
                    public void call(NewsBean newsBean) {
                        List<Story> stories = newsBean.getStories();
                        beforeDate = newsBean.getDate();//最后加载的日期
                        mNewsData.addAll(stories);
                        mAdapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!NetworkUtil.isNetworkConnected()) {
                            showToast("网络连接超时，稍后再试。");
                        }
                        isLoading = false;
                    }
                });

    }

}
