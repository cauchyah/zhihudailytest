package com.zhihudailytest.Adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.zhihudailytest.Bean.Story
import com.zhihudailytest.R
import com.zhihudailytest.Utils.SectionItem
import com.zhihudailytest.Utils.ViewHolder

import java.util.ArrayList

/**
 * Created by Administrator on 2016/6/23.
 */
abstract class NewsSectionAdapter(listView: ListView) : BaseAdapter(), AdapterView.OnItemClickListener {
    private val mSections: MutableList<SectionItem<Story>>
    private val keyedSections: SparseArray<SectionItem<Story>>
    private val mContext: Context

    init {
        mContext = listView.context
        mSections = ArrayList()
        keyedSections = SparseArray()
        listView.onItemClickListener = this
    }

    fun addSection(title: String, items: List<Story>) {
        val section = SectionItem(title, items)
        val currentIndex = mSections.indexOf(section)
        if (currentIndex >= 0) {
            mSections.remove(section)
            mSections.add(currentIndex, section)
        } else {
            mSections.add(section)
        }
        reorderSection()
        notifyDataSetChanged()
    }

    private fun reorderSection() {
        keyedSections.clear()
        var startPosition = 0
        for (one in mSections) {
            keyedSections.put(startPosition, one)
            startPosition += one.count
        }
    }

    override fun getCount(): Int {
        var count = 0
        for (item in mSections) {
            count += item.count
        }
        return count
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderAtPosition(position))
            TYPE_HEADER
        else
            TYPE_ITEM
    }

    fun isHeaderAtPosition(position: Int): Boolean {
        for (i in 0 until keyedSections.size()) {
            if (position == keyedSections.keyAt(i)) {
                return true
            }
        }
        return false
    }


    override fun getItem(position: Int): Any? {
        return findSectionItemAtPosition(position)
    }

    fun findSectionItemAtPosition(position: Int): Story? {
        var firstIndex: Int
        var endIndex: Int
        for (i in 0 until keyedSections.size()) {
            firstIndex = keyedSections.keyAt(i)
            endIndex = firstIndex + keyedSections.valueAt(i).count
            if (position > firstIndex && position < endIndex) {
                val index = position - firstIndex - 1
                return keyedSections.valueAt(i).getItem(index)
            }
        }
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        when (getItemViewType(position)) {
            TYPE_HEADER -> return getHeaderView(position, convertView, parent)

            TYPE_ITEM -> return getItemView(position, convertView, parent)
            else -> return convertView
        }


    }

    private fun getItemView(position: Int, convertView: View, parent: ViewGroup): View {
        /* if (convertView==null){
            convertView=inflater.inflate(R.layout.layout_list_item,parent,false);

        }
        T item=findSectionItemAtPosition(position);
        TextView content= (TextView) convertView.findViewById(R.id.title);
        content.setText(item.toString());*/
        val holder = ViewHolder.getViewHolder(mContext, R.layout.layout_list_item, position, convertView, parent)
        val item = findSectionItemAtPosition(position)

        holder.setText(R.id.title, item!!.title!!)
        Glide.with(mContext)
                .load(item.images!![0])
                .crossFade()
                .fitCenter()
                .into(holder.getView<View>(R.id.image) as ImageView)
        return holder.convertView
    }

    private fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_header, parent, false)

        }
        val item = keyedSections.get(position)
        val headerView = convertView!!.findViewById<View>(R.id.date) as TextView
        headerView.text = item.title
        return convertView
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = findSectionItemAtPosition(position)
        if (item != null) {
            onSectionClick(item)
        }
    }

    abstract fun onSectionClick(item: Story)

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return !isHeaderAtPosition(position)
    }

    companion object {
        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1
    }
}
