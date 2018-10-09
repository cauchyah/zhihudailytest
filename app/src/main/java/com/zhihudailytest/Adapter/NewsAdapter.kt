package com.zhihudailytest.Adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.zhihudailytest.Activity.DetailActivity
import com.zhihudailytest.Bean.Story
import com.zhihudailytest.R
import com.zhihudailytest.Utils.DataBaseDao

import java.util.ArrayList

/**
 * Created by Administrator on 2016/7/4.
 */
class NewsAdapter(protected var mContext: Context, protected var mList: List<Story>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var layoutInflater: LayoutInflater
    var headerViewPager: View? = null
        set(headerViewPager) {
            field = headerViewPager
            notifyItemInserted(0)
        }

     val ITEM = 0
     val HEADER = 2
     var mOnItemClickListener: OnItemClickListener?=null


    interface OnItemClickListener {
        fun onItemClick(holder: ItemViewHolder, position: Int)
    }



    fun setmOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    init {
        this.layoutInflater = LayoutInflater.from(mContext)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == HEADER) {
            return HeaderViewHolder(this.headerViewPager!!)
        } else {
            val view = layoutInflater.inflate(R.layout.news_item, parent, false)
            return ItemViewHolder(view, this)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0)
            return
        val realPosition = getRealPosition(position)
        val story = mList[realPosition]
        if (holder is ItemViewHolder) {
            //新闻item

            holder.title.text = story.title
            if (story.isReaded) {
                holder.title.setTextColor(ContextCompat.getColor(mContext, R.color.readed))
            } else {
                holder.title.setTextColor(ContextCompat.getColor(mContext, R.color.unread))
            }
            if (story.images != null && story.images!!.size > 0) {
                Glide.with(mContext)
                        .load(story.images!![0])
                        .skipMemoryCache(true)

                        .dontAnimate()

                        .error(R.drawable.load_error)
                        .into(holder.imageView)
            }
            if (story.isMultipic) {
                holder.multi.visibility = View.VISIBLE
            } else {
                holder.multi.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {

        return if (this.headerViewPager == null) {
            mList.size
        } else
            mList.size + 1
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == 0 && this.headerViewPager != null) HEADER else ITEM


    }

    fun getRealPosition(original: Int): Int {

        return if (this.headerViewPager == null) {
            original
        } else
            original - 1

    }

    inner class ItemViewHolder(itemView: View, private val mAdapter: NewsAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

         val title: TextView
         val imageView: ImageView
         val multi: TextView

        init {
            title = itemView.findViewById<View>(R.id.title) as TextView
            imageView = itemView.findViewById<View>(R.id.image) as ImageView
            multi = itemView.findViewById<View>(R.id.multi) as TextView
            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View) {

            //跳转详情页面
            //存放id
            val story = mList[adapterPosition - 1]
            val currentId = story.id
            val idList = ArrayList<Int>()
            for (one in mList) {
                val id = one.id
                idList.add(id)
            }

            val bundle = Bundle()
            bundle.putIntegerArrayList("ids", idList)
            bundle.putInt("current", currentId)
            DetailActivity.actionStart(mContext, bundle)
            if (!story.isReaded) {

                val dao = DataBaseDao()
                dao.markRead(currentId)
                story.isReaded = true
                notifyItemChanged(adapterPosition)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
