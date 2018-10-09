package com.zhihudailytest.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast

import com.zhihudailytest.R
import com.zhihudailytest.Utils.SPUtils
import com.zhihudailytest.Utils.SystemStatusManager

import java.lang.reflect.Field

/**
 * Created by Administrator on 2016/7/4.
 */
abstract class BaseActivity : AppCompatActivity() {
    var localBroadcastManager: LocalBroadcastManager?=null
     var fragmentManager: FragmentManager? = null
     var fragmentTransaction: FragmentTransaction? = null
     var listener: LoginOrLogoutListener?=null

    private var receive: LogBroadcast? = null

    var toolbar: Toolbar?=null




    fun setLoginListener(listener: LoginOrLogoutListener) {
        this.listener = listener
        receive = LogBroadcast()
        val filter = IntentFilter()
        filter.addAction("login")
        filter.addAction("logout")
        localBroadcastManager!!.registerReceiver(receive, filter)
    }

    interface LoginOrLogoutListener {
        fun onReceive(intent: Intent, context: Context)
    }


    internal inner class LogBroadcast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            listener!!.onReceive(intent, context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (SPUtils.themeSP.getBoolean("isNight", false)) {
            //夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            //日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // setOverflowShowingAlways();
        localBroadcastManager = LocalBroadcastManager.getInstance(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (receive != null)
            localBroadcastManager!!.unregisterReceiver(receive)
    }

    //初始化view
    protected abstract fun initView()

    //绑定listener
    protected abstract fun setListener()

    protected fun showToast(string: String) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
    }

    fun setToolbar() {
        toolbar = findViewById<View>(R.id.toolBar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar!!.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

    }

    protected fun setNaviBack() {
        toolbar!!.setNavigationOnClickListener { finish() }
    }

    protected fun setTranslucent(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            /*  // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);*/
            val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            rootView.fitsSystemWindows = true
            rootView.clipToPadding = true
        }


    }

    protected fun setTranslucent3() {
        //&& Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 透明导航栏
            /*  getWindow().addFlags(
		                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
            val tintManager = SystemStatusManager(this)
            tintManager.isStatusBarTintEnabled = true
            // 设置状态栏的颜色
            tintManager.setStatusBarTintResource(R.color.colorPrimary)
            window.decorView.fitsSystemWindows = true
        }
    }

    /**
     * 修复有物理menu键盘不能显示menu选项
     */
    protected fun setOverflowShowingAlways() {
        val vg = ViewConfiguration.get(this)
        try {
            val menuKeyField = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.isAccessible = true
            menuKeyField.setBoolean(vg, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @return 状态栏矩形条
     */
    protected fun createStatusView(activity: AppCompatActivity): View {
        // 获得状态栏高度
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)

        // 绘制一个和状态栏一样高的矩形
        val statusView = View(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight)
        statusView.layoutParams = params
        statusView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        return statusView
    }

    protected fun setTranslucent2(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // 生成一个状态栏大小的矩形
                val statusView = createStatusView(activity)
                // 添加 statusView 到布局中
                val decorView = activity.window.decorView as ViewGroup
                decorView.addView(statusView)
                val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
                rootView.fitsSystemWindows = true
                rootView.clipToPadding = true
            }
        }
    }

    protected fun setDrawerTranslucent(mDrawerContainer: DrawerLayout, activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            // localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //将侧边栏顶部延伸至status bar
                // mDrawerContainer.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                mDrawerContainer.clipToPadding = false
            }
        }
    }
}
