package com.zhihudailytest.Utils;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/7/4.
 */
public class DateUtil {

    public static String string2date(String original){
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);

        Date d1;
        try {
            d1 = format.parse(original);

           // Date nowDate=new Date();
           /* Date now=format.parse(format.format(new Date()));
            LogUtil.d(format.format(now));*/
            int differ=differOfDate(d1);

            if (differ<0){
                SimpleDateFormat f=new SimpleDateFormat("MM月dd日  E",Locale.SIMPLIFIED_CHINESE);

                return f.format(d1);
            }
            else if (differ<=1){
                return "今日热闻";
            }
            else {
                return  new SimpleDateFormat("yy年M月d日 E",Locale.SIMPLIFIED_CHINESE).format(d1);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;




    }

    public static int differOfDate(Date one){
        Calendar c1=Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        c1.setTime(one);
        Calendar c2=Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
       // c2.setTime(two);
      //  LogUtil.d(c1.get(Calendar.DAY_OF_YEAR)+"date");
        //LogUtil.d(c2.get(Calendar.DAY_OF_YEAR)+"now");
        return c1.get(Calendar.DAY_OF_YEAR)-c2.get(Calendar.DAY_OF_YEAR);
    }

    public static int lastDay(String now){

        return Integer.valueOf(now)-1;
    }

}
