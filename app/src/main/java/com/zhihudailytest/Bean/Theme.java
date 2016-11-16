package com.zhihudailytest.Bean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Theme {
    private List<Others> others;

    public List<Others> getOthers() {
        return others;
    }

    public void setOthers(List<Others> others) {
        this.others = others;
    }
    public static class Others{
        private String thumbnail;
        private String description;
        private String name;
        private int id;
        private boolean selected;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Others(int id, String name){
            this.name=name;
            this.id=id;

        }
        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }


}

