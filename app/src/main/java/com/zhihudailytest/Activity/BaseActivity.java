package com.zhihudailytest.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SPUtils;
import com.zhihudailytest.Utils.SystemStatusManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/7/4.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected LocalBroadcastManager localBroadcastManager;
    protected FragmentManager fragmentManager;
    protected FragmentTransaction fragmentTransaction;
    protected LoginOrLogoutListener listener;

    public LoginOrLogoutListener getListener() {
        return listener;
    }

    private LogBroadcast receive;

    public void setListener(LoginOrLogoutListener listener) {
        this.listener = listener;
        receive = new LogBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("login");
        filter.addAction("logout");
        localBroadcastManager.registerReceiver(receive, filter);
    }

    public interface LoginOrLogoutListener {
        void onReceive(Intent intent, Context context);
    }


    class LogBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            listener.onReceive(intent, context);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (SPUtils.getThemeSP().getBoolean("isNight", false)) {
            //夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            //日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // setOverflowShowingAlways();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receive != null)
            localBroadcastManager.unregisterReceiver(receive);
    }

    //初始化view
    protected abstract void initView();

    //绑定listener
    protected abstract void setListener();

    protected void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    protected Toolbar toolbar;

    protected void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    protected void setNaviBack() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void setTranslucent(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
          /*  // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);*/
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }


    }

    protected void setTranslucent3() {
        //&& Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
              /*  getWindow().addFlags(
		                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
            SystemStatusManager tintManager = new SystemStatusManager(this);
            tintManager.setStatusBarTintEnabled(true);
            // 设置状态栏的颜色
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
    }

    /**
     * 修复有物理menu键盘不能显示menu选项
     */
    protected void setOverflowShowingAlways() {
        ViewConfiguration vg = ViewConfiguration.get(this);
        try {
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(vg, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @return 状态栏矩形条
     */
    protected View createStatusView(AppCompatActivity activity) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        return statusView;
    }

    protected void setTranslucent2(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // 生成一个状态栏大小的矩形
                View statusView = createStatusView(activity);
                // 添加 statusView 到布局中
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                decorView.addView(statusView);
                ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
                rootView.setFitsSystemWindows(true);
                rootView.setClipToPadding(true);
            }
        }
    }

    protected void setDrawerTranslucent(DrawerLayout mDrawerContainer, AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            // localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //将侧边栏顶部延伸至status bar
                // mDrawerContainer.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                mDrawerContainer.setClipToPadding(false);
            }
        }
    }
}
