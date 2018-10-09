package com.zhihudailytest.Fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast

import com.zhihudailytest.Activity.BaseActivity
import com.zhihudailytest.R
import com.zhihudailytest.Utils.NetworkUtil

/**
 * Created by Administrator on 2016/7/25.
 */
abstract class BaseFragment : Fragment() {
    protected var localBroadcastManager: LocalBroadcastManager?=null
    internal var receiver: ReadChangeReceiver? = null
    // @Bind(R.id.swipeRefreshLayout)
    protected var swipeRefreshLayout: SwipeRefreshLayout?=null
    protected var toolbar: Toolbar? = null
    var layoutManager: LinearLayoutManager? = null
        protected set
    protected var activity: BaseActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    abstract fun onDoubleClick()


    protected fun setSwipeRefreshLayout(view: View) {
        swipeRefreshLayout = view.findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout!!.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary))
        val listener = SwipeRefreshLayout.OnRefreshListener {
            if (NetworkUtil.isNetworkConnected)
                loadData()
            else
                swipeRefreshLayout!!.isRefreshing = false
        }
        swipeRefreshLayout!!.setOnRefreshListener(listener)
        //手动下拉刷新
        swipeRefreshLayout!!.post {
            swipeRefreshLayout!!.isRefreshing = true
            loadData()
        }
    }

    internal abstract fun loadData()

    protected fun registerReceiver() {
        val intentFilter = IntentFilter("readChange")
        localBroadcastManager = LocalBroadcastManager.getInstance(context)
        receiver = ReadChangeReceiver()
        localBroadcastManager!!.registerReceiver(receiver, intentFilter)
    }

    internal inner class ReadChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onBroadcastReceive(context, intent)

        }
    }

    internal abstract fun onBroadcastReceive(context: Context, intent: Intent)

    protected fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null)
            localBroadcastManager!!.unregisterReceiver(receiver)
    }
}
