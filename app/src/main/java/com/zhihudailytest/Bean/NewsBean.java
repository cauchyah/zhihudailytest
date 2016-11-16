package com.zhihudailytest.Bean;

import com.zhihudailytest.Utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class NewsBean {
    private String date;
   private List<Story> stories;
  private List<Story> top_stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<Story> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<Story> top_stories) {
        this.top_stories = top_stories;
    }
}
