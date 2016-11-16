package com.zhihudailytest;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;


/**
 * Created by Administrator on 2016/7/4.
 */
public class MyApplication extends Application {
   // public static RequestQueue Queue;
    public static Context ApplicationContext;



    @Override
    public void onCreate() {
        super.onCreate();
       // Queue= Volley.newRequestQueue(this);
        ApplicationContext=getApplicationContext();

       initUmeng();

    }

    private void initUmeng(){
        PlatformConfig.setSinaWeibo("1974037783","a0a0de5929df815359ee6ffaa6b23ebc");
        PlatformConfig.setQQZone("1105580660","N5T4Bs5pVnpSByHk");
        PlatformConfig.setWeixin("wx1795351c295a744a","961e16485a4e3967ae98d61ba1ab0e91");
        Config.REDIRECT_URL="http://sns.whalecloud.com/sina2/callback";

    }
}
