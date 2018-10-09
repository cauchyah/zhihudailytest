package com.zhihudailytest.Fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.zhihudailytest.Activity.BaseActivity
import com.zhihudailytest.Adapter.ThemeInfoAdapter
import com.zhihudailytest.Bean.Story
import com.zhihudailytest.Bean.ThemeInfo
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.R
import com.zhihudailytest.Utils.ReadUtil

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_theme.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
class ThemeFragment : BaseFragment() {
    internal var id: Int = 0
    private var title: String? = null
    private var root: View? = null
    private var mAdapter: ThemeInfoAdapter? = null
    private var mData: MutableList<Story>? = null
    private var headerView: View? = null
    private var editor: LinearLayout? = null
    private var titleTextView: TextView? = null
    private var imageView: ImageView? = null
    private var editorText: TextView? = null
    private var loadData: Subscription? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val bundle = arguments
        id = bundle.getInt("id", 0)
        title = bundle.getString("title", "")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater!!.inflate(R.layout.fragment_theme, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        initView()
        registerReceiver()
        setSwipeRefreshLayout(view)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onResume()
            toolbar!!.title = title
        } else {
            onPause()
        }
    }

    override fun onPause() {
        super.onPause()
        if (loadData != null && !loadData!!.isUnsubscribed)
            loadData!!.unsubscribe()
    }

    override fun onDoubleClick() {
        layoutManager!!.smoothScrollToPosition(recyclerView!!, null, 0)
    }

    override fun loadData() {
        loadData = RetrofitManager.instance
                .getThemeInfo(id)
                .subscribeOn(Schedulers.io())
                .map { themeInfo ->
                    ReadUtil.isRead(themeInfo.stories!!)
                    themeInfo
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ themeInfo ->
                    swipeRefreshLayout!!.isRefreshing = false
                    mData!!.clear()
                    mData!!.addAll(themeInfo.stories!!)
                    mAdapter!!.notifyDataSetChanged()
                    initHeader(themeInfo)
                }, { swipeRefreshLayout!!.isRefreshing = false })
    }

    private fun initHeader(themeInfo: ThemeInfo) {
        Glide.with(this@ThemeFragment)
                .load(themeInfo.background)
                .centerCrop()
                .placeholder(R.drawable.image_top_default)
                .skipMemoryCache(true)
                .into(imageView!!)
        editorText!!.visibility = View.VISIBLE
        titleTextView!!.text = themeInfo.description
        val count = editor!!.childCount
        for (i in 1 until count) {
            editor!!.removeViewAt(i)
        }
        for (one in themeInfo.editors!!) {
            val image = ImageView(context)
            val params = LinearLayout.LayoutParams(
                    context.resources.getDimensionPixelSize(R.dimen.image_width),
                    context.resources.getDimensionPixelSize(R.dimen.image_width))
            params.setMargins(8, 0, 0, 0)
            image.layoutParams = params

            editor!!.addView(image)
            Glide.with(this@ThemeFragment)
                    .load(one.avatar)
                    .bitmapTransform(CropCircleTransformation(context))
                    .skipMemoryCache(true)
                    .into(image)
        }

    }


    private fun initView() {
        toolbar = (getActivity() as BaseActivity).toolbar
        toolbar!!.title = title
        headerView = LayoutInflater.from(context).inflate(R.layout.theme_header_view, null)

        titleTextView = headerView!!.findViewById<View>(R.id.title) as TextView
        editor = headerView!!.findViewById<View>(R.id.editor) as LinearLayout
        imageView = headerView!!.findViewById<View>(R.id.imageView) as ImageView
        editorText = headerView!!.findViewById<View>(R.id.editorText) as TextView
        layoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        mData = ArrayList()
        mAdapter = ThemeInfoAdapter(context, mData!!)
        mAdapter!!.headerViewPager = headerView
        recyclerView!!.adapter = mAdapter
    }


    internal override fun onBroadcastReceive(context: Context, intent: Intent) {
        // showToast("i had received");
        // LogUtil.d("i had received i had receivedi had received");
        val id = intent.getIntExtra("id", -1)
        val ids = mAdapter!!.ids
        val index = ids.indexOf(id)
        if (index == -1) return
        val story = mData!![index]
        story.isReaded = true
        mAdapter!!.notifyItemChanged(index + 1)
    }

    companion object {
        fun getInstance(id: Int, title: String): ThemeFragment {
            val fragment = ThemeFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putString("title", title)
            fragment.arguments = bundle
            return fragment
        }
    }
}// Required empty public constructor
