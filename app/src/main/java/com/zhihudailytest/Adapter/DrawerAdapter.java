package com.zhihudailytest.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.zhihudailytest.Activity.UmengActivity;
import com.zhihudailytest.Activity.UmengLoginActivity;
import com.zhihudailytest.Bean.Theme;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.MyApplication;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DataBaseDao;
import com.zhihudailytest.Utils.NetworkUtil;
import com.zhihudailytest.Utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/16.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private LayoutInflater layoutInflater;
    private Context mContext;
    private static final int HEADER_ITEM = 0;
    private static final int CONTENT_ITEM = 1;
    private int oldPosition;
    private List<Theme.Others> mData=new ArrayList<Theme.Others>();
    public interface HeaderOnClickListener{
        void onClick(View v);
    }
    public  interface ItemClickListener{
        void onItemClick(int position);
    };

    private ItemClickListener mItemClickListener;
    private HeaderOnClickListener headerOnClickListener;

    public HeaderOnClickListener getHeaderOnClickListener() {
        return headerOnClickListener;
    }

    public void setHeaderOnClickListener(HeaderOnClickListener headerOnClickListener) {
        this.headerOnClickListener = headerOnClickListener;
    }

    public ItemClickListener getmItemClickListener() {
        return mItemClickListener;
    }

    public void setmItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public  DrawerAdapter(Context context,int old){
        this.layoutInflater=LayoutInflater.from(context);
        this.mContext=context;
        this.oldPosition=old;

        loadData();

    }
    public void changeItem(int position,int old){
        mData.get(position-1).setSelected(true);
        mData.get(old-1).setSelected(false);
        notifyItemRangeChanged(position,1);
        notifyItemRangeChanged(old,1);

    }
    private void loadData(){
        final DataBaseDao dao=new DataBaseDao();
        List<Theme.Others> temp=dao.getThemeList();
        mData.add(new Theme.Others(-1,"首页"));
        if (temp.size()>0){
            //数据库有数据
            mData.addAll(temp);
            mData.get(oldPosition-1).setSelected(true);
            notifyItemRangeChanged(1, mData.size());
        }
        else {
            //去网络上取
            RetrofitManager.getInstance()
                    .getThemeList()
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Theme, Theme>() {

                        @Override
                        public Theme call(Theme theme) {
                            //更新数据库
                            ContentValues value = new ContentValues();
                            for (Theme.Others one : theme.getOthers()) {
                                value.put("theme_id", one.getId());
                                value.put("theme_name", one.getName());
                                dao.insertTheme(value);
                            }
                            return theme;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Theme>() {
                        @Override
                        public void call(Theme theme) {
                            mData.addAll(theme.getOthers());
                            mData.get(oldPosition-1).setSelected(true);
                            notifyItemRangeChanged(1, mData.size());
                            //notifyDataSetChanged();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType==CONTENT_ITEM) {
            View view = layoutInflater.inflate(R.layout.layout_drawer_content, parent, false);
            return new ItemViewHolder(view);
        }
        else{
            View view=layoutInflater.inflate(R.layout.layout_drawer_header,parent,false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position==0){
            HeaderViewHolder mHolder=(HeaderViewHolder)holder;
            SharedPreferences sp= SPUtils.getSP();
            mHolder.name.setText(sp.getString("name","请登录"));
            String url=sp.getString("avatar","");
            if (TextUtils.equals("",url)) {
                Glide.with(mContext)
                        .load(R.drawable.kaka)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(((HeaderViewHolder) holder).avatar);
                Glide.with(mContext)
                        .load(R.drawable.kaka)
                        .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext, 50))
                        .into(((HeaderViewHolder) holder).background);
            }
            else{
                Glide.with(mContext)
                        .load(url)
                        .skipMemoryCache(true)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(((HeaderViewHolder) holder).avatar);
                Glide.with(mContext)
                        .load(url)
                        . skipMemoryCache(true)
                        .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext, 50))
                        .into(((HeaderViewHolder) holder).background);
            }

        }
        else {
            if (position == 1) {
                ((ItemViewHolder) holder).theme.setText("首页");
                ((ItemViewHolder) holder).image.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).theme.setText(mData.get(position - 1).getName());
                ((ItemViewHolder) holder).image.setVisibility(View.GONE);
            }
            if (mData.get(position-1).isSelected())
                ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
            else {
                ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.web_view_bg));
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_ITEM;
        else return CONTENT_ITEM;

    }

    public int getThemeId(int position){
        return mData.get(position-1).getId();
    }
    public String getThemeTitle(int position){
        return mData.get(position-1).getName();
    }

    @Override
    public int getItemCount() {
        return mData.size()+1;
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView theme;
        private LinearLayout item;

        private CardView cardView;
        private ImageView image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            theme= (TextView) itemView.findViewById(R.id.theme);
           item= (LinearLayout) itemView.findViewById(R.id.item);
            cardView= (CardView) itemView.findViewById(R.id.cardview);
            image= (ImageView) itemView.findViewById(R.id.home) ;

           // itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.background));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            getmItemClickListener().onItemClick(getAdapterPosition());
           // Toast.makeText(mContext, "item click", Toast.LENGTH_SHORT).show();
        }
    }
    public class HeaderViewHolder extends ItemViewHolder{
        private TextView name;
        private ImageView avatar;
        private TextView collect;
        private TextView download;
        private LinearLayout home;
        private ImageView background;
        private RelativeLayout login;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            avatar= (ImageView) itemView.findViewById(R.id.avatar);
            collect= (TextView) itemView.findViewById(R.id.collect);
            download= (TextView) itemView.findViewById(R.id.download);
           // home= (LinearLayout) itemView.findViewById(R.id.homepage);
            background= (ImageView) itemView.findViewById(R.id.background);
            login= (RelativeLayout) itemView.findViewById(R.id.login);
            collect.setOnClickListener(this);
            download.setOnClickListener(this);
            //home.setOnClickListener(this);
            login.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            if (v==download){
                if (!NetworkUtil.isNetworkConnected()){
                    Toast.makeText(MyApplication.ApplicationContext, "网络连接错误，稍后再试。", Toast.LENGTH_SHORT).show();
                    return;
                }

                getHeaderOnClickListener().onClick(v);
            }
            else if (v==login){
                //点击头像
                Intent intent=new Intent(mContext, UmengLoginActivity.class);
                mContext.startActivity(intent);
            }
           // Toast.makeText(mContext, " header click", Toast.LENGTH_SHORT).show();

        }
    }


}
