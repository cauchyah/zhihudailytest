package com.zhihudailytest.Utils;

import android.util.SparseArray;

import com.zhihudailytest.Bean.Story;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class ReadUtil {
    public  static void isRead( List<Story> stories){
        if (stories.size()<1) return;
        DataBaseDao dao=new DataBaseDao();
        SparseArray<Integer> ids=dao.getRead();
        if (ids.size()>0){
            int i=0;
            for (Story one:stories){

                if(ids.get(one.getId())!=null){
                    one.setReaded(true);
                    i++;
                }
                if (i>=ids.size()) break;
            }
        }
    }

    public  static  void setRead(int id){
        DataBaseDao dao=new DataBaseDao();
        dao.markRead(id);
    }

}
