package com.qihuanyun.pojo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/28.
 */
public class GameDetailData {
    public Data data;
    public class Data{
        public int id;
        public String url;
        public String imgUrl;
        public int category;
        public String fileUrl;
        public String title;
        public int uploadNum;
        public int playNum;
        public String fileSize;
        public String pakname;
        public int isCollect;
        public String summary;
        public int type;
        public String info;
        public ArrayList<String> imgList;
        public ArrayList<IndexData.VideoGame> tdList;
    }
}
