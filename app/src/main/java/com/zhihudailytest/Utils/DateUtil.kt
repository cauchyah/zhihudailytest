package com.zhihudailytest.Utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by Administrator on 2016/7/4.
 */
object DateUtil {

    fun string2date(original: String): String? {
        val format = SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE)

        val d1: Date
        try {
            d1 = format.parse(original)

            // Date nowDate=new Date();
            /* Date now=format.parse(format.format(new Date()));
            LogUtil.d(format.format(now));*/
            val differ = differOfDate(d1)

            if (differ < 0) {
                val f = SimpleDateFormat("MM月dd日  E", Locale.SIMPLIFIED_CHINESE)

                return f.format(d1)
            } else return if (differ <= 1) {
                "今日热闻"
            } else {
                SimpleDateFormat("yy年M月d日 E", Locale.SIMPLIFIED_CHINESE).format(d1)
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null


    }

    fun differOfDate(one: Date): Int {
        val c1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        c1.time = one
        val c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        // c2.setTime(two);
        //  LogUtil.d(c1.get(Calendar.DAY_OF_YEAR)+"date");
        //LogUtil.d(c2.get(Calendar.DAY_OF_YEAR)+"now");
        return c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR)
    }

    fun lastDay(now: String): Int {

        return Integer.valueOf(now) - 1
    }

}
