package com.zhihudailytest.Utils;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2016/7/4.
 */
public class LogUtil {
    private static boolean isDebug=true;

    public static void i(String tag,String msg){
        if(isDebug)

            android.util.Log.i(tag,msg);
    }

    public static void v(String tag,String msg){
        if(isDebug)
            android.util.Log.v(tag,msg);
    }

    public static void e(String tag,String msg){
        if(isDebug)
            android.util.Log.e(tag,msg);
    }
    public static void w(String tag,String msg){
        if(isDebug)
            android.util.Log.w(tag,msg);
    }
    public static void d(String msg){
        if(isDebug)
            Logger.d(msg);

    }
    public static void d(String message, Object... args){
        if(isDebug)

            Logger.d(message,args);
    }
    public static void json(String json){
        if(isDebug)
         Logger.json(json);
    }
}
