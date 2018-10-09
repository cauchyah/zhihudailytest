package com.zhihudailytest.Utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.zhihudailytest.MyApplication

/**
 * Created by Administrator on 2016/7/19.
 */
class MyDataBaseHelper(private val mContext: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(mContext, name, factory, version) {
    private val CREATE_THEME = ("create table Theme (id integer primary key autoincrement,"
            + "theme_id integer ,"
            + "theme_name char(20))")
    // +"subscribed integer)";
    private val CREATE_READED = "create table Readed (id integer primary key autoincrement," + "story_id integer)"


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_THEME)
        db.execSQL(CREATE_READED)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


    }

    companion object {
        private var mInstance: MyDataBaseHelper? = null

        val instance: MyDataBaseHelper
            get() {
                if (mInstance == null) {
                    synchronized(MyDataBaseHelper::class.java) {
                        if (mInstance == null) {
                            mInstance = MyDataBaseHelper(MyApplication.ApplicationContext!!, "daily_new", null, 1)
                        }
                    }
                }
                return mInstance!!

            }
    }
}
