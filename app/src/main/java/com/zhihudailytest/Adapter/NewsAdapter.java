package com.zhihudailytest.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.zhihudailytest.Activity.DetailActivity;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.Bean.TopStory;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DataBaseDao;
import com.zhihudailytest.Utils.DateUtil;
import com.zhihudailytest.Utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context mContext;
    protected List<Story> mList;

    protected LayoutInflater layoutInflater;
    private View headerViewPager;

    protected final int ITEM = 0;
    protected final int DATE = 1;
    protected final int HEADER = 2;
    protected OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(ItemViewHolder holder, int position);
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public View getHeaderViewPager() {
        return headerViewPager;
    }

    public void setHeaderViewPager(View headerViewPager) {
        this.headerViewPager = headerViewPager;
        notifyItemInserted(0);
    }

    public NewsAdapter(Context context, List<Story> list) {
        this.mContext = context;
        this.mList = list;
        this.layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER) {
            return new HeaderViewHolder(headerViewPager);
        } else if (viewType == DATE) {

            View view = layoutInflater.inflate(R.layout.news_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.news_item, parent, false);
            return new ItemViewHolder(view, this);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0)
            return;
        int realPosition = getRealPosition(position);
        Story story = mList.get(realPosition);
        if (holder instanceof ItemViewHolder) {
            //新闻item

            ((ItemViewHolder) holder).title.setText(story.getTitle());
            if (story.isReaded()){
                ((ItemViewHolder) holder).title.setTextColor(ContextCompat.getColor(mContext,R.color.readed));
            }
            else{
                ((ItemViewHolder) holder).title.setTextColor(ContextCompat.getColor(mContext,R.color.unread));
            }
            Glide.with(mContext)
                    .load(story.getImages()[0])
                    .skipMemoryCache(true)

                    .dontAnimate()

                    .error(R.drawable.load_error)
                    .into((ImageView) ((ItemViewHolder) holder).imageView);
            if (story.isMultipic()) {
                ((ItemViewHolder) holder).multi.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).multi.setVisibility(View.GONE);
            }
        } else if (holder instanceof DateViewHolder) {
            //头部Header
            ((DateViewHolder) holder).dateTextView.setText(DateUtil.string2date(story.getDate())
            );

        }

    }

    @Override
    public int getItemCount() {

       if (headerViewPager == null) {
            return mList.size();
        }
        else
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

       if (position==0&&headerViewPager!=null) return HEADER;
        else if (position==0) return DATE;
        else if (mList.get(position-1).getDate().equals("")){
            return ITEM;
        }
        else{
            return DATE;
        }



    }

    public int getRealPosition(int original) {

       if (headerViewPager == null) {
            return  original;
        }
        else return original-1;

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView imageView;
        private NewsAdapter mAdapter;
        private TextView multi;

        public ItemViewHolder(View itemView, NewsAdapter mAdapter) {
            super(itemView);

            this.mAdapter = mAdapter;
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            multi = (TextView) itemView.findViewById(R.id.multi);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

                //跳转详情页面
            //存放id
            Story story=mList.get(getAdapterPosition() - 1);
            int currentId=story.getId();
            ArrayList<Integer> idList=new ArrayList<Integer>();
            for(Story one:mList){
                if (one.getDate().equals("")){
                    int id=one.getId();
                    idList.add(id);
                }
            }

            Bundle bundle=new Bundle();
            bundle.putIntegerArrayList("ids",idList);
            bundle.putInt("current",currentId);
               DetailActivity.actionStart(mContext,bundle);
            if (!story.isReaded()){

                DataBaseDao dao=new DataBaseDao();
                dao.markRead(currentId);
                story.setReaded(true);
                notifyItemChanged(getAdapterPosition());
            }






            //}

        }
    }
    public class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date);

        }
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
