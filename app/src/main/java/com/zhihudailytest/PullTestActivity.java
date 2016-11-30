package com.zhihudailytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PullTestActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.container)
    LinearLayout container;
    private LinearLayoutManager linearLayoutManager;
    private List<String> mData=new ArrayList<String>();
    private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_test);
        ButterKnife.bind(this);
        myAdapter=new MyAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        loadData();
    }
    private void loadData(){
        for (int i = 0; i < 25; i++) {
            mData.add("item="+i);
        }
        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends  RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(PullTestActivity.this).inflate(android.R.layout.simple_list_item_1,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mData.get(position));

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            text= (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
