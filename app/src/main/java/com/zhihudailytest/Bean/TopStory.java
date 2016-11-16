package com.zhihudailytest.Bean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class TopStory {
    private String title;
   private String ga_prefix;
    private int type;
    private int id;
    private String image;
    public TopStory(){}
    public TopStory(String title){
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
