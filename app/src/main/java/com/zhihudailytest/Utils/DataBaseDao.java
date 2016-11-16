package com.zhihudailytest.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.zhihudailytest.Bean.Theme;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DataBaseDao {
    private final SQLiteDatabase db;
    private MyDataBaseHelper helper;


    public  DataBaseDao(){
        helper=MyDataBaseHelper.getInstance();
        db=helper.getWritableDatabase();

    }

    /**
     * 插入主题列表信息
     * @param values
     */
    public void insertTheme(ContentValues values){
        db.insert("theme",null,values);

    }

    public SparseArray<Integer> getRead(){
        SparseArray<Integer> ids=new SparseArray<Integer>();
        Cursor cursor=db.rawQuery("select * from readed",null);
        if (cursor.moveToFirst()){

            do {
                ids.put(cursor.getInt(1),cursor.getInt(1));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ids;
    }

    /**
     *
     * @param id
     * @return  1:新增加 2：已有
     */
    public int  markRead(int id){
        //db.rawQuery("insert into readed values(\"null\",\""+id+"\")",null);
        Cursor cursor=db.rawQuery("select * from readed where story_id="+id,null);
        if (cursor.getCount()<1){
        ContentValues values=new ContentValues();
        values.put("story_id",id);
        db.insert("readed",null,values);
            cursor.close();
            return 1;
        }
        cursor.close();
        return 2;
    }

    /**
     * 获取主题列表信息
     * @return
     */

    public  List<Theme.Others> getThemeList(){
         List<Theme.Others> mData=new ArrayList<Theme.Others>();
        Cursor cursor=db.rawQuery("select * from theme ",null);

        if (cursor.moveToFirst()){
            int id;
            String name;
            Theme.Others one;
            do{
                 id=cursor.getInt(1);
                 name=cursor.getString(2);
                one=new Theme.Others(id,name);
                mData.add(one);

            }while (cursor.moveToNext());

        }
        cursor.close();
       return mData;
    }

}
