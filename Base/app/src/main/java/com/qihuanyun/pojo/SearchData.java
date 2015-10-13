package com.qihuanyun.pojo;

import java.util.ArrayList;

public class SearchData {
    public ArrayList<Data> data;

    public class Data{
        public int id;
        public String title;
        public String summary;
        public String imgUrl;
        public String url;
        public int type;
    }
}
