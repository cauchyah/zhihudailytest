package com.zhihudailytest.Fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.zhihudailytest.Bean.StoryDetail
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.R
import com.zhihudailytest.Utils.SPUtils

import java.io.File

import butterknife.BindView
import butterknife.ButterKnife
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation
import kotlinx.android.synthetic.main.fragment_detail.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : BaseFragment() {


    var story: StoryDetail? = null
        private set

    internal var id: Int = 0


    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  LogUtil.d("onCreate");
    }

    override fun onDoubleClick() {

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val bundle = arguments
        id = bundle.getInt("id", 0)
        //  LogUtil.d("onAttach"+id);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // int id=savedInstanceState.getInt("id");
        //  LogUtil.d("onActivityCreated");
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_detail, container, false)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        initView()
        setSwipeRefreshLayout(view)
        setListener()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            loadData()
    }

    private fun setListener() {

    }

    private fun initView() {
        webView!!.settings.javaScriptEnabled = true
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //return super.shouldOverrideUrlLoading(view, url);
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                return true
            }
        }
        webView!!.settings.defaultTextEncodingName = "utf-8"
        //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

    }

    override fun onStop() {
        super.onStop()
        if (subscription != null && !subscription!!.isUnsubscribed)
            subscription!!.unsubscribe()
    }

    override fun loadData() {
        subscription = RetrofitManager.instance
                .getStoryDetail(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { storyDetail ->
                            swipeRefreshLayout!!.isRefreshing = false
                            story = storyDetail
                            if (storyDetail.image != null) {
                                source!!.text = storyDetail.image_source
                                title!!.text = storyDetail.title
                                if (!loadImage(storyDetail.id)) {
                                    Glide.with(this@DetailFragment)
                                            .load(storyDetail.image)
                                            .skipMemoryCache(true)
                                            .bitmapTransform(CenterCrop(context), VignetteFilterTransformation(context, PointF(0.5f, 0.5f),
                                                    floatArrayOf(0.1f, 0.1f, 0.1f), 0.0f, 0.87f))

                                            .into(image!!)

                                }
                            }
                            val builder = StringBuilder()
                            for (str in storyDetail.css!!) {
                                builder.append("<link rel=\"stylesheet\" href=\"$str\" type=\"text/css\" />")
                            }
                            val sp = SPUtils.themeSP
                            var string: String
                            if (sp.getBoolean("isNight", false)) {
                                // builder.append("<div style=\"background-color:#424242;color:#c60\" ");
                                builder.append(storyDetail.body)
                                //builder.append("</div>");
                                string = builder.toString()
                                string = string.replace("class=\"main-wrap content-wrap\"", "class=\"main-wrap content-wrap \" style=\"background-color:#424242;color:#000\"")
                                string = string.replace("class=\"author\"", "class=\"author\" style=\"color:#bbb\"")
                                string = string.replace("class=\"question-title\"", "class=\"question-title\" style=\"color:#b8b8b8\"")
                                string = string.replace("class=\"bio\"", "class=\"bio\" style=\"color:#bbb\"")
                                string = string.replace("class=\"view-more\"", "class=\"view-more\" style=\"color:#c60\"")
                            } else {
                                builder.append(storyDetail.body)
                                string = builder.toString()
                            }
                            webView!!.loadData(
                                    string, "text/html;charset=UTF-8", null)
                        }, {
                    swipeRefreshLayout!!.isRefreshing = false
                    Toast.makeText(context, "加载失败，请检查网络连接", Toast.LENGTH_SHORT).show()
                })
    }

    internal override fun onBroadcastReceive(context: Context, intent: Intent) {

    }

    private fun loadImage(id: Int): Boolean {
        val root = File(context.cacheDir.toString() + "/image/" + "image" + id + "file")
        if (!root.exists()) return false
        Glide.with(this@DetailFragment)
                .load(root)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(CenterCrop(context), VignetteFilterTransformation(context, PointF(0.5f, 0.5f),
                        floatArrayOf(0.1f, 0.1f, 0.1f), 0.0f, 0.87f))
                .into(image!!)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }

    companion object {
        fun getInstance(id: Int): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}// Required empty public constructor

