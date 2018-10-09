package com.zhihudailytest.Activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView

import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.ShareBoardlistener
import com.zhihudailytest.Adapter.DetailAdapter
import com.zhihudailytest.Bean.Extra
import com.zhihudailytest.Bean.StoryDetail
import com.zhihudailytest.Fragment.DetailFragment
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.R
import com.zhihudailytest.Utils.DataBaseDao

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_detail.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.util.ArrayList

class DetailActivity : BaseActivity() {

    private var mAdapter: DetailAdapter? = null
    private var currentSelected: Int = 0




    private var ids: List<Int>? = null

    internal val displaylist = arrayOf(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
    private val mShareBoardlistener = ShareBoardlistener { snsPlatform, share_media ->
        val umImage = UMImage(this@DetailActivity, BitmapFactory.decodeResource(resources, R.drawable.zhihu_icon))
        val shareAction = ShareAction(this@DetailActivity)
        shareAction.platform = share_media
        shareAction.setCallback(mUmShareListener)//设置每个平台的点击事件
        shareAction.withTitle(story!!.title)
        shareAction.withText(story!!.title)
        shareAction.withTargetUrl(story!!.share_url)//点击分享内容打开的链接
        shareAction.withMedia(umImage)//附带的图片，音乐，视频等多媒体对象
        shareAction.share()//发起分享，调起微信，QQ，微博客户端进行分享。
    }
    /**
     * 友盟分享后事件监听器
     */
    private val mUmShareListener = object : UMShareListener {

        override fun onResult(platform: SHARE_MEDIA) {
            showToast("success")
            // TODO 分享成功
        }

        override fun onError(platform: SHARE_MEDIA, t: Throwable) {
            showToast("onError" + t.toString())
            // TODO 分享失败
        }

        override fun onCancel(platform: SHARE_MEDIA) {
            showToast("onCancel")
            // TODO 分享取消
        }

    }
    private var story: StoryDetail? = null
    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        ButterKnife.bind(this)
        //  Log.e("abcd","onCreate"+current);

        //StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.colorPrimary));
        setToolbar()
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setTranslucent3()
        val intent = intent
        val bundle = intent.extras
        ids = bundle!!.getIntegerArrayList("ids")
        val current = bundle.getInt("current")

        mAdapter = DetailAdapter(supportFragmentManager, (ids as ArrayList<Int>?)!!)

        viewPager!!.offscreenPageLimit = 1
        viewPager!!.adapter = mAdapter
        currentSelected = ids!!.indexOf(current)
        viewPager!!.currentItem = currentSelected
        loadExtra(ids!!.indexOf(current))
        setListener()


    }


    override fun initView() {

    }

    override fun setListener() {
        share!!.setOnClickListener(View.OnClickListener {
            // if (story==null) return;
            val fragment = mAdapter!!.instantiateItem(viewPager, currentSelected) as DetailFragment
                    ?: return@OnClickListener
            story = fragment.story
            if (story == null) {
                return@OnClickListener
            }
            val action = ShareAction(this@DetailActivity)
            action.setDisplayList(*displaylist)
            action.setShareboardclickCallback(mShareBoardlistener)//设置友盟集成的分享面板的点击监听回调
            action.open()//打开集成的分享面板
        })
        setNaviBack()
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                /// showToast(position+"position");
                currentSelected = position
                comment!!.text = "..."
                favor!!.text = "..."
                loadExtra(position)
                val id = ids!![position]
                Observable.just(0)
                        .subscribeOn(Schedulers.io())
                        .map {
                            val dao = DataBaseDao()
                            dao.markRead(id)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { integer ->
                            when (integer) {
                                1 -> {
                                    val intent = Intent("readChange")
                                    // intent.setAction("readChange");
                                    intent.putExtra("id", id)
                                    localBroadcastManager!!.sendBroadcast(intent)
                                }
                            }
                        }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


    }

    private fun loadExtra(position: Int) {
        subscription = RetrofitManager.instance.getExtra(mAdapter!!.getId(position))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ extra ->
                    comment!!.text = extra.comments.toString() + ""
                    favor!!.text = extra.popularity.toString() + ""
                }, { })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null && !subscription!!.isUnsubscribed)
            subscription!!.unsubscribe()
    }

    companion object {

        fun actionStart(context: Context, bundle: Bundle) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
