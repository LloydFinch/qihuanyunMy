package com.qihuanyun.pojo;

import java.util.ArrayList;

public class GameContentData {

    public ArrayList<Data> data;
    public class Data{
        public int id;
        public String title;
        public String summary;
        public String category;
        public int type;
        public String url;
        public int playCounter;
        public int downloadCounter;
        public int recommend;
        public int banner;
        public int display;
        public String imgUrl;
        public byte realSize;
        public String packageName;

        public Data(int id,String title,String summary,String imgUrl,String url,int type){
            this.id = id;
            this.title = title;
            this.summary = summary;
            this.imgUrl = imgUrl;
            this.url = url;
            this.type = type;
        }
    }
}
