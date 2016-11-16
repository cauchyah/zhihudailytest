package com.zhihudailytest.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhihudailytest.MyApplication;

/**
 * Created by Administrator on 2016/8/2.
 */
public class SPUtils {
    public static SharedPreferences.Editor getEditor(){
        SharedPreferences.Editor editor= MyApplication.ApplicationContext
                .getSharedPreferences("daily_user", Context.MODE_PRIVATE).edit();
        return editor;
    }

    public static SharedPreferences getSP(){
        SharedPreferences sharedPreferences=MyApplication.ApplicationContext
                .getSharedPreferences("daily_user",Context.MODE_PRIVATE);
        return sharedPreferences;
    }
    public static  SharedPreferences.Editor getThemeEditor(){
        SharedPreferences.Editor editor= MyApplication.ApplicationContext
                .getSharedPreferences("daily_theme", Context.MODE_PRIVATE).edit();
        return editor;
    }
    public static SharedPreferences getThemeSP(){
        SharedPreferences sharedPreferences=MyApplication.ApplicationContext
                .getSharedPreferences("daily_theme",Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}
