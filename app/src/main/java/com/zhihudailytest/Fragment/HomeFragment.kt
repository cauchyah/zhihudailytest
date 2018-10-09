package com.zhihudailytest.Fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.zhihudailytest.Activity.BaseActivity
import com.zhihudailytest.Adapter.MyPagerAdapter
import com.zhihudailytest.Adapter.NewsAdapter
import com.zhihudailytest.Bean.NewsBean
import com.zhihudailytest.Bean.Story
import com.zhihudailytest.CustomUI.Draw
import com.zhihudailytest.CustomUI.TitleItemdecoration
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.R
import com.zhihudailytest.Utils.DateUtil
import com.zhihudailytest.Utils.NetworkUtil
import com.zhihudailytest.Utils.ReadUtil

import java.lang.ref.WeakReference
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.fragment_home.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment() {

    private val mNewsData = ArrayList<Story>()
    private val mTopStoryData = ArrayList<Story>()
    private var mAdapter: NewsAdapter? = null
    private var headerView: View? = null
    private var mViewPager: ViewPager? = null
    private var mPagerAdapter: MyPagerAdapter? = null
    private var title: TextView? = null
    private var dotLayout: LinearLayout? = null
    private var lastTouchTime: Long = 0
    private var isLoading = false
    private var beforeDate: String? = null
    private val currentPage = 0
    private var root: View? = null
    private var lastCount = 0
    private var loadMore: Subscription? = null
    private var loadData: Subscription? = null
    private var selectPosition: Int = 0

    private val mHanlder = MyHandler(this)

    private val timeTask = Runnable {
        //避免手动滑动后，马上翻页，应该等到下一个延迟时间才自动滑动
        if (System.currentTimeMillis() - lastTouchTime > delayTime - 500) {
            mHanlder.sendEmptyMessage(WHEEL)
        } else {
            mHanlder.sendEmptyMessage(WHEEL_WAIT)
        }
    }

    internal override fun onBroadcastReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", -1)
        if (id == -1) return
        var i = -1
        for (one in mNewsData) {
            i++
            if (one.id == id) {
                one.isReaded = true
                break
            }

        }
        mAdapter!!.notifyItemChanged(i + 1)

    }

    internal class MyHandler(t: HomeFragment) : Handler() {
        private val reference: WeakReference<HomeFragment>

        init {
            reference = WeakReference(t)

        }

        override fun handleMessage(msg: Message) {
            if (reference.get() != null) {
                val fraggment = reference.get()
                if (fraggment!!.mTopStoryData.size > 0) {
                    if (msg.what == WHEEL) {

                        val position = fraggment.mViewPager!!.currentItem
                        fraggment.mViewPager!!.currentItem = (position + 1) % fraggment.mTopStoryData.size
                        removeCallbacks(fraggment.timeTask)
                        postDelayed(fraggment.timeTask, delayTime.toLong())
                    } else if (msg.what == WHEEL_WAIT) {
                        removeCallbacks(fraggment.timeTask)
                        postDelayed(fraggment.timeTask, delayTime.toLong())
                    }
                }

            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        root = inflater!!.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        initView()
        setListener()
        registerReceiver()
        setSwipeRefreshLayout(view)
    }

    override fun onResume() {
        super.onResume()
        mHanlder.removeCallbacks(timeTask)
        mHanlder.postDelayed(timeTask, delayTime.toLong())
    }

    override fun onPause() {
        mHanlder.removeCallbacks(timeTask)
        if (loadData != null && !loadData!!.isUnsubscribed)
            loadData!!.unsubscribe()
        if (loadMore != null && !loadMore!!.isUnsubscribed)
            loadMore!!.unsubscribe()
        super.onPause()
    }


    override fun onDoubleClick() {
        layoutManager!!.smoothScrollToPosition(newsRecyclerView!!, null, 0)
    }

    override fun loadData() {
        loadData = RetrofitManager.instance
                .lastNews
                .subscribeOn(Schedulers.io())
                .map { newsBean ->
                    val stories = newsBean.stories
                    if (stories != null) {
                        ReadUtil.isRead(stories)
                        for (item in stories) {
                            item.date = newsBean.date!!
                        }
                    }
                    newsBean
                }
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1 { newsBean ->
                    swipeRefreshLayout!!.isRefreshing = false
                    val stories = newsBean.stories
                    if (stories!!.size <= lastCount) return@Action1
                    lastCount = stories.size
                    beforeDate = newsBean.date//最后加载的日期
                    mNewsData.clear()
                    mNewsData.addAll(stories)
                    mTopStoryData.clear()
                    mTopStoryData.add(newsBean.top_stories!![newsBean.top_stories!!.size - 1])
                    mTopStoryData.addAll(newsBean.top_stories!!)
                    mTopStoryData.add(newsBean.top_stories!![0])
                    mPagerAdapter!!.notifyDataSetChanged()
                    mViewPager!!.currentItem = 1
                    mAdapter!!.notifyDataSetChanged()

                    initBanner()
                }, Action1 {
                    swipeRefreshLayout!!.isRefreshing = false
                    if (!NetworkUtil.isNetworkConnected) {
                        showToast("网络连接超时，稍后再试。")
                    }
                })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbar!!.title = "首页"
            onResume()
        } else {
            onPause()
        }
    }

    private fun initView() {
        toolbar = (getActivity() as BaseActivity).toolbar
        layoutManager = LinearLayoutManager(context)
        newsRecyclerView!!.layoutManager = layoutManager
        // 设置ItemAnimator
        newsRecyclerView!!.itemAnimator = DefaultItemAnimator()
        newsRecyclerView!!.addItemDecoration(TitleItemdecoration(context, mNewsData))
        // 设置固定大小
        newsRecyclerView!!.setHasFixedSize(true)
        headerView = LayoutInflater.from(context).inflate(R.layout.header_view, null)
        mViewPager = headerView!!.findViewById<View>(R.id.viewPager) as ViewPager
        title = headerView!!.findViewById<View>(R.id.title) as TextView
        dotLayout = headerView!!.findViewById<View>(R.id.dotLayout) as LinearLayout
        mAdapter = NewsAdapter(context, mNewsData)
        mAdapter!!.headerViewPager = headerView
        newsRecyclerView!!.adapter = mAdapter
        mPagerAdapter = MyPagerAdapter(context, mTopStoryData)
        mViewPager!!.adapter = mPagerAdapter
    }

    private fun setListener() {
        newsRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = layoutManager!!.findFirstVisibleItemPosition()
                val lastPosition = layoutManager!!.findLastVisibleItemPosition()
                val viewType = mAdapter!!.getItemViewType(position)
                if (viewType == 2) {
                    toolbar!!.title = "首页"
                } else if (position == 1) {
                    toolbar!!.title = "今日热闻"
                } else if (mNewsData[position - 1].date != mNewsData[position - 2].date) {

                    //日期
                    if (dy > 0)
                        toolbar!!.title = DateUtil.string2date(
                                mNewsData[mAdapter!!.getRealPosition(position)].date)
                    else {
                        var date = mNewsData[mAdapter!!.getRealPosition(position)].date
                        date = (Integer.valueOf(date) + 1).toString() + ""
                        toolbar!!.title = DateUtil.string2date(date)
                    }
                }
                if (dy > 0 && !isLoading && lastPosition == mNewsData.size) {
                    isLoading = true
                    loadMore()
                }
            }
        })
        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                lastTouchTime = System.currentTimeMillis()
                selectPosition = position
                if (position < 1)
                    selectPosition = mTopStoryData.size - 2
                else if (position > mTopStoryData.size - 2) {
                    selectPosition = 1
                }
                selected(selectPosition - 1)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == 0) {
                    if (selectPosition == mTopStoryData.size - 2 || selectPosition == 1) {
                        mViewPager!!.setCurrentItem(selectPosition, false)
                    }
                }
            }
        })

    }

    private fun initBanner() {
        if (dotLayout!!.childCount < 1)
            Draw.drawDot(context, dotLayout!!, mTopStoryData.size - 2)
        mViewPager!!.currentItem = currentPage
        selected(currentPage)
    }

    /**
     * 设置选中的项
     *
     * @param position
     */
    private fun selected(position: Int) {
        if (mTopStoryData.size != 0 && dotLayout!!.visibility == View.GONE) {
            dotLayout!!.visibility = View.VISIBLE
        }
        title!!.text = mTopStoryData[position].title
        for (i in 0 until dotLayout!!.childCount) {
            if (position == i)
                dotLayout!!.getChildAt(i).isEnabled = true
            else
                dotLayout!!.getChildAt(i).isEnabled = false

        }
    }

    private fun loadMore() {
        loadMore = RetrofitManager.instance
                .getBeforeNews(this!!.beforeDate!!)
                .subscribeOn(Schedulers.io())
                .map { newsBean ->
                    val stories = newsBean.stories
                    if (stories != null) {
                        ReadUtil.isRead(stories)
                        for (item in stories) {
                            item.date = newsBean.date!!
                        }
                    }
                    newsBean
                }
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ newsBean ->
                    val stories = newsBean.stories
                    beforeDate = newsBean.date//最后加载的日期
                    mNewsData.addAll(stories!!)
                    mAdapter!!.notifyDataSetChanged()
                    isLoading = false
                }, {
                    if (!NetworkUtil.isNetworkConnected) {
                        showToast("网络连接超时，稍后再试。")
                    }
                    isLoading = false
                })

    }

    companion object {
        private val WHEEL = 1
        private val WHEEL_WAIT = 2
        private val delayTime = 5000
        private val TAG = "abcd"
    }

}
