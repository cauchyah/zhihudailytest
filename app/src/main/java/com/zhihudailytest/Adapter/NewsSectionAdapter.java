package com.zhihudailytest.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SectionItem;
import com.zhihudailytest.Utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public abstract class NewsSectionAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private static  final int TYPE_HEADER=0;
    private static  final int TYPE_ITEM=1;
    private List<SectionItem<Story>> mSections;
    private SparseArray<SectionItem<Story>> keyedSections;
    private Context mContext;
    public NewsSectionAdapter(ListView listView){
        mContext=listView.getContext();
        mSections=new ArrayList<SectionItem<Story>>();
        keyedSections=new SparseArray<SectionItem<Story>>();
        listView.setOnItemClickListener(this);
    }
    public void addSection(String title,List<Story> items){
        SectionItem<Story> section=new SectionItem<Story>(title,items);
        int currentIndex=mSections.indexOf(section);
        if (currentIndex>=0){
            mSections.remove(section);
            mSections.add(currentIndex,section);
        }else{
            mSections.add(section);
        }
        reorderSection();
        notifyDataSetChanged();
    }

    private void reorderSection() {
        keyedSections.clear();
        int startPosition=0;
        for (SectionItem<Story> one:mSections){
            keyedSections.put(startPosition,one);
            startPosition+=one.getCount();
        }
    }

    @Override
    public int getCount() {
        int count=0;
        for (SectionItem<Story> item:mSections){
            count+=item.getCount();
        }
        return count;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderAtPosition(position))
            return TYPE_HEADER;
        else return TYPE_ITEM;
    }

    public boolean isHeaderAtPosition(int position){
        for (int i=0;i<keyedSections.size();i++){
            if (position==keyedSections.keyAt(i)){
                return true;
            }
        }
        return  false;
    }



    @Override
    public Object getItem(int position) {
        return findSectionItemAtPosition(position);
    }
    public Story findSectionItemAtPosition(int position){
        int firstIndex,endIndex;
        for (int i=0;i<keyedSections.size();i++){
            firstIndex=keyedSections.keyAt(i);
            endIndex=firstIndex+keyedSections.valueAt(i).getCount();
            if (position>firstIndex&& position<endIndex){
                int index=position-firstIndex-1;
                return keyedSections.valueAt(i).getItem(index);
            }
        }
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)){
            case TYPE_HEADER:
                return  getHeaderView(position,convertView,parent);

            case TYPE_ITEM:
                return  getItemView(position,convertView,parent);
            default:return  convertView;
        }


    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
       /* if (convertView==null){
            convertView=inflater.inflate(R.layout.layout_list_item,parent,false);

        }
        T item=findSectionItemAtPosition(position);
        TextView content= (TextView) convertView.findViewById(R.id.title);
        content.setText(item.toString());*/
        ViewHolder holder=ViewHolder.getViewHolder(mContext,R.layout.layout_list_item,position,convertView,parent);
        Story item=findSectionItemAtPosition(position);

        holder.setText(R.id.title,item.getTitle());
        Glide.with(mContext)
                .load(item.getImages()[0])
                .crossFade()
                .fitCenter()
                .into((ImageView) holder.getView(R.id.image));
        return holder.getConvertView();
    }

    private View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(R.layout.layout_header,parent,false);

        }
        SectionItem<Story> item=keyedSections.get(position);
        TextView headerView= (TextView) convertView.findViewById(R.id.date);
        headerView.setText(item.getTitle());
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Story item=findSectionItemAtPosition(position);
            if (item!=null){
                onSectionClick(item);
            }
    }
    public abstract void onSectionClick(Story item);

    @Override
    public boolean areAllItemsEnabled() {
        return  false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeaderAtPosition(position);
    }
}
