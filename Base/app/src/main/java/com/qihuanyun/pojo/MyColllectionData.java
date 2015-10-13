package com.qihuanyun.pojo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/21.
 */
public class MyColllectionData {
    public ArrayList<Data> data;
    public String error;
    public class Data{
        public int id;
        public String title;
        public String summary;
        public String imgUrl;
        public String url;
        public int type;
    }
}
