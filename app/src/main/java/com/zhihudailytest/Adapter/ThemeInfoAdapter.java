package com.zhihudailytest.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhihudailytest.Activity.DetailActivity;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.Bean.ThemeInfo;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DataBaseDao;
import com.zhihudailytest.Utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ThemeInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context mContext;
    protected List<Story> mList;

    protected LayoutInflater layoutInflater;
    private View headerViewPager;

    protected final int ITEM = 0;

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

    public ThemeInfoAdapter(Context context, List<Story> list) {
        this.mContext = context;
        this.mList = list;
        this.layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER) {
            return new HeaderViewHolder(headerViewPager);
        }  else {
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
            if (story.getImages()==null) {
                ((ItemViewHolder) holder).relativeLayout.setVisibility(View.GONE);

            }
            else{
                ((ItemViewHolder) holder).relativeLayout.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(story.getImages()[0])
                        .skipMemoryCache(true)
                        .dontAnimate()

                        .error(R.drawable.load_error)
                        .into((ImageView) ((ItemViewHolder) holder).imageView);
            }

        }

    }



    @Override
    public int getItemCount() {


            return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position==0) return HEADER;
        else
            return ITEM;

    }

    public int getRealPosition(int original) {

         return original-1;

    }


    public ArrayList<Integer> getIds(){
        ArrayList<Integer> idList=new ArrayList<Integer>();
        for(Story one:mList){
                int id=one.getId();
                idList.add(id);
        }
        return  idList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView imageView;
        private ThemeInfoAdapter mAdapter;
        private TextView multi;
        private RelativeLayout relativeLayout;

        public ItemViewHolder(View itemView, ThemeInfoAdapter mAdapter) {
            super(itemView);

            this.mAdapter = mAdapter;
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            multi = (TextView) itemView.findViewById(R.id.multi);
            relativeLayout= (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            //跳转详情页面
            //存放id
            Story story=mList.get(getAdapterPosition() - 1);
            int currentId=story.getId();


            Bundle bundle=new Bundle();
            bundle.putIntegerArrayList("ids",getIds());
            bundle.putInt("current",currentId);
            DetailActivity.actionStart(mContext,bundle);
            if (!story.isReaded()){

                DataBaseDao dao=new DataBaseDao();
                dao.markRead(currentId);
                story.setReaded(true);
                notifyItemChanged(getAdapterPosition());
            }


        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
