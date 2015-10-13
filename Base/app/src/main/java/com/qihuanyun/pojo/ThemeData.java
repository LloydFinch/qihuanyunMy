package com.qihuanyun.pojo;

import java.util.ArrayList;

public class ThemeData {
    public String msg;
    public String error;
    public ArrayList<Theme> data;

    public class Theme{
        public int id;
        public String title;
        public int category;
        public int type;
        public int playCounter;
        public int downloadCounter;
        public int recommend;
        public int banner;
        public int display;
        public String imgUrl;
    }
}
