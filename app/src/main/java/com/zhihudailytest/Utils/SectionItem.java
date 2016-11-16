package com.zhihudailytest.Utils;

import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class SectionItem<T> {

    private String title;
    private List<T> items;

    public SectionItem(String title, List<T> items){
        this.title=title;
        this.items=items;
    }

    public String getTitle(){
        return title;
    }
    public int getCount(){
        return (items==null?1:1+items.size());
    }
    public T getItem(int position){
        return items.get(position);
    }

    @Override
    public boolean equals(Object o) {
        if (o!=null&&o instanceof  SectionItem){
            return ((SectionItem)o).getTitle().equals(title);
        }
        return false;
    }
}
