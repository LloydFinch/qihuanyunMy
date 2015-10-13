package com.qihuanyun.pojo;

import java.util.ArrayList;

public class ThemeDetailData extends CommonData{
    public Data data;

    public class Data{
        public int id;
        public String title;
        public String summary;
        public String imgUrl;
        public ArrayList<IndexData.VideoGame> tdList;
    }
}
