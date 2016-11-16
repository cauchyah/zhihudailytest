package com.zhihudailytest.Utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhihudailytest.MyApplication;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static  MyDataBaseHelper mInstance;
    private final String CREATE_THEME="create table Theme (id integer primary key autoincrement,"
            +"theme_id integer ,"
            +"theme_name char(20))";
           // +"subscribed integer)";
    private final String CREATE_READED="create table Readed (id integer primary key autoincrement,"
                                        +"story_id integer)";
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext=context;
    }

    public static MyDataBaseHelper getInstance(){
        if (mInstance==null){
            synchronized (MyDataBaseHelper.class){
                if (mInstance==null){
                    mInstance=new MyDataBaseHelper(MyApplication.ApplicationContext,"daily_new",null,1);
                }
            }
        }
        return  mInstance;

    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_THEME);
        db.execSQL(CREATE_READED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
