package com.qihuanyun.pojo;

import java.util.ArrayList;

public class VideoDetailData extends CommonData{
    public Data data;

    public class Data{
        public int id;
        public String url;
        public String imgUrl;
        public int category;
        public String fileUrl;
        public int uploadNum;
        public int playNum;
        public String fileSize;
        public String pakname;
        public String title;
        public int isCollect;
        public String summary;
        public int type;
        public String info;
        public ArrayList<String> imgList;
        public ArrayList<IndexData.VideoGame> tdList;
    }
}
