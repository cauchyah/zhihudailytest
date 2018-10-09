package com.zhihudailytest.Utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.SparseArray

import com.zhihudailytest.Bean.Theme

import java.util.ArrayList

/**
 * Created by Administrator on 2016/7/19.
 */
class DataBaseDao {
    private val db: SQLiteDatabase
    private val helper: MyDataBaseHelper

    val read: SparseArray<Int>
        get() {
            val ids = SparseArray<Int>()
            val cursor = db.rawQuery("select * from readed", null)
            if (cursor.moveToFirst()) {

                do {
                    ids.put(cursor.getInt(1), cursor.getInt(1))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return ids
        }

    /**
     * 获取主题列表信息
     * @return
     */

    val themeList: List<Theme.Others>
        get() {
            val mData = ArrayList<Theme.Others>()
            val cursor = db.rawQuery("select * from theme ", null)

            if (cursor.moveToFirst()) {
                var id: Int
                var name: String
                var one: Theme.Others
                do {
                    id = cursor.getInt(1)
                    name = cursor.getString(2)
                    one = Theme.Others(id, name)
                    mData.add(one)

                } while (cursor.moveToNext())

            }
            cursor.close()
            return mData
        }

    init {
        helper = MyDataBaseHelper.instance
        db = helper.writableDatabase

    }

    /**
     * 插入主题列表信息
     * @param values
     */
    fun insertTheme(values: ContentValues) {
        db.insert("theme", null, values)

    }

    /**
     *
     * @param id
     * @return  1:新增加 2：已有
     */
    fun markRead(id: Int): Int {
        //db.rawQuery("insert into readed values(\"null\",\""+id+"\")",null);
        val cursor = db.rawQuery("select * from readed where story_id=$id", null)
        if (cursor.count < 1) {
            val values = ContentValues()
            values.put("story_id", id)
            db.insert("readed", null, values)
            cursor.close()
            return 1
        }
        cursor.close()
        return 2
    }

}
