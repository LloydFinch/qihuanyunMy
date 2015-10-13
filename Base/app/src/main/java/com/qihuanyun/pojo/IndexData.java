package com.qihuanyun.pojo;

import java.util.ArrayList;

public class IndexData extends CommonData{
    public Data data;

    public class Data{
        public ArrayList<VideoGame> filmRecommends;
        public ArrayList<VideoGame> gameRecommends;
        public ArrayList<Banner> bannerResults;
    }

    public class VideoGame{
        public int id;
        public String title;
        public String summary;
        public int category;
        public int type;
        public String url;
        public int playCounter;
        public int downloadCounter;
        public int recommend;
        public int banner;
        public int display;
        public String imgUrl;
        public String realSize;
        public String packageName;
    }

    public class Banner{
        public int contentId;
        public String title;
        public String imgUrl;
        public String ish5;
        public int category;
    }
}
