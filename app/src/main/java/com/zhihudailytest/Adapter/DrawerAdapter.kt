package com.zhihudailytest.Adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.zhihudailytest.Activity.UmengLoginActivity
import com.zhihudailytest.Bean.Theme
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.MyApplication
import com.zhihudailytest.R
import com.zhihudailytest.Utils.DataBaseDao
import com.zhihudailytest.Utils.NetworkUtil
import com.zhihudailytest.Utils.SPUtils

import java.util.ArrayList

import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.w3c.dom.Text
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

/**
 * Created by Administrator on 2016/7/16.
 */
class DrawerAdapter(private val mContext: Context, private val oldPosition: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater: LayoutInflater
    private val mData = ArrayList<Theme.Others>()

    private var mItemClickListener: ItemClickListener? = null
    var headerOnClickListener: HeaderOnClickListener? = null

    interface HeaderOnClickListener {
        fun onClick(v: View)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun getmItemClickListener(): ItemClickListener? {
        return mItemClickListener
    }

    fun setmItemClickListener(mItemClickListener: ItemClickListener) {
        this.mItemClickListener = mItemClickListener
    }

    init {
        this.layoutInflater = LayoutInflater.from(mContext)

        loadData()

    }

    fun changeItem(position: Int, old: Int) {
        mData[position - 1].isSelected = true
        mData[old - 1].isSelected = false
        notifyItemRangeChanged(position, 1)
        notifyItemRangeChanged(old, 1)

    }

    private fun loadData() {
        val dao = DataBaseDao()
        val temp = dao.themeList
        mData.add(Theme.Others(-1, "首页"))
        if (temp.size > 0) {
            //数据库有数据
            mData.addAll(temp)
            mData[oldPosition - 1].isSelected = true
            notifyItemRangeChanged(1, mData.size)
        } else {
            //去网络上取
            RetrofitManager.instance
                    .themeList
                    .subscribeOn(Schedulers.io())
                    .map { theme ->
                        //更新数据库
                        val value = ContentValues()
                        for (one in theme.others!!) {
                            value.put("theme_id", one.id)
                            value.put("theme_name", one.name)
                            dao.insertTheme(value)
                        }
                        theme
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ theme ->
                        mData.addAll(theme.others!!)
                        mData[oldPosition - 1].isSelected = true
                        notifyItemRangeChanged(1, mData.size)
                        //notifyDataSetChanged();
                    }, { })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == CONTENT_ITEM) {
            val view = layoutInflater.inflate(R.layout.layout_drawer_content, parent, false)
            return ItemViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.layout_drawer_header, parent, false)
            return HeaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position == 0) {
            val mHolder = holder as HeaderViewHolder
            val sp = SPUtils.sp
            mHolder.name.text = sp.getString("name", "请登录")
            val url = sp.getString("avatar", "")
            if (TextUtils.equals("", url)) {
                Glide.with(mContext)
                        .load(R.drawable.kaka)
                        .bitmapTransform(CropCircleTransformation(mContext))
                        .into(holder.avatar)
                Glide.with(mContext)
                        .load(R.drawable.kaka)
                        .bitmapTransform(CenterCrop(mContext), BlurTransformation(mContext, 50))
                        .into(holder.background)
            } else {
                Glide.with(mContext)
                        .load(url)
                        .skipMemoryCache(true)
                        .bitmapTransform(CropCircleTransformation(mContext))
                        .into(holder.avatar)
                Glide.with(mContext)
                        .load(url)
                        .skipMemoryCache(true)
                        .bitmapTransform(CenterCrop(mContext), BlurTransformation(mContext, 50))
                        .into(holder.background)
            }

        } else {
            if (position == 1) {
                (holder as ItemViewHolder).theme!!.text = "首页"
                holder.theme!!.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.menu_home), null, null, null)
            } else {
                (holder as ItemViewHolder).theme!!.text = mData[position - 1].name
                holder.theme!!.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            if (mData[position - 1].isSelected)
                (holder.itemView as LinearLayout).setBackgroundColor(ContextCompat.getColor(mContext, R.color.background))
            else {
                (holder.itemView as LinearLayout).setBackgroundColor(ContextCompat.getColor(mContext, R.color.web_view_bg))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            HEADER_ITEM
        else
            CONTENT_ITEM

    }

    fun getThemeId(position: Int): Int {
        return mData[position - 1].id
    }

    fun getThemeTitle(position: Int): String {
        return mData[position - 1].name!!
    }

    override fun getItemCount(): Int {
        return mData.size + 1
    }


    open inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var theme: TextView?


        init {

            var value: View? = itemView.findViewById<View>(R.id.theme)
            if (value != null) {
                theme = value as TextView
            } else {
                theme = null
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            getmItemClickListener()!!.onItemClick(adapterPosition)
        }
    }

    inner class HeaderViewHolder(itemView: View) : ItemViewHolder(itemView) {
        internal val name: TextView
        internal val avatar: ImageView
        internal val collect: TextView
        internal val download: TextView
        internal val home: LinearLayout? = null
        internal val background: ImageView
        internal val login: RelativeLayout

        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
            avatar = itemView.findViewById<View>(R.id.avatar) as ImageView
            collect = itemView.findViewById<View>(R.id.collect) as TextView
            download = itemView.findViewById<View>(R.id.download) as TextView
            // home= (LinearLayout) itemView.findViewById(R.id.homepage);
            background = itemView.findViewById<View>(R.id.backgroundImage) as ImageView
            login = itemView.findViewById<View>(R.id.login) as RelativeLayout
            collect.setOnClickListener(this)
            download.setOnClickListener(this)
            //home.setOnClickListener(this);
            login.setOnClickListener(this)
        }

        override fun onClick(v: View) {

            if (v === download) {
                if (!NetworkUtil.isNetworkConnected) {
                    Toast.makeText(MyApplication.ApplicationContext, "网络连接错误，稍后再试。", Toast.LENGTH_SHORT).show()
                    return
                }

                headerOnClickListener!!.onClick(v)
            } else if (v === login) {
                //点击头像
                val intent = Intent(mContext, UmengLoginActivity::class.java)
                mContext.startActivity(intent)
            }
            // Toast.makeText(mContext, " header click", Toast.LENGTH_SHORT).show();

        }
    }

    companion object {
        private val HEADER_ITEM = 0
        private val CONTENT_ITEM = 1
    }


}
