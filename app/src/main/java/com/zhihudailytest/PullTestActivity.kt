package com.zhihudailytest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_pull_test.*

class PullTestActivity : AppCompatActivity() {


    private var linearLayoutManager: LinearLayoutManager? = null
    private val mData = ArrayList<String>()
    private var myAdapter: MyAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_test)
        ButterKnife.bind(this)
        myAdapter = MyAdapter()
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = myAdapter
        loadData()
    }

    private fun loadData() {
        for (i in 0..24) {
            mData.add("item=$i")
        }
        myAdapter!!.notifyDataSetChanged()
    }

    private inner class MyAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(this@PullTestActivity).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = mData[position]

        }

        override fun getItemCount(): Int {
            return mData.size
        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val text: TextView

        init {
            text = itemView.findViewById<View>(android.R.id.text1) as TextView
        }
    }
}
