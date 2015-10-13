package com.qihuanyun.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
/*
 *	通用工具类
 *	author:hexianhua
 *	2015-04-27 11:06:23
 */
public class ExtUtils {

    /**
     * Object判空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Object value) {
        if (value == null) return true;

        if (value instanceof String) {
            if (((String) value).length() == 0 || ((String) value) == "" || ((String) value).trim().length() == 0)
                return true;
        }

        if (value instanceof Collection) return ((Collection<? extends Object>) value).size() == 0;
        if (value instanceof Map)
            return ((Map<? extends Object, ? extends Object>) value).size() == 0;
        if (value instanceof CharSequence) return ((CharSequence) value).length() == 0;

        if (value instanceof Boolean) return false;
        if (value instanceof Number) return false;
        if (value instanceof Character) return false;
        if (value instanceof java.util.Date) return false;

        return false;
    }

    /**
     * Object判断非空
     *
     * @param object
     * @return
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * 判断是否连接网络
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 短Toast
     *
     * @param context
     * @param s
     */
    public static void shortToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长Toast
     *
     * @param context
     * @param s
     */
    public static void longToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    /**
     * 错误日志Log
     *
     * @param key
     * @param value
     */
    public static void errorLog(String key, String value) {
        Log.e("-----------" + key + "-----------", value);
    }

    /**
     * 信息日志Log
     *
     * @param key
     * @param value
     */
    public static void infoLog(String key, String value) {
        Log.i("-----------" + key + "-----------", value);
    }

    /**
     * 下载链接有中文的处理
     *
     * @param url
     * @return
     */
    public static String urlHandler(String url) {
        //先url还原
        if(url.contains("%20")){
            url = url.replace("%20"," ");
        }
        try {
            url = URLEncoder.encode(url, "utf-8").replaceAll("\\+", "%20");
            url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * 根据url获取文件的类型
     * 返回实例：.rmvb  .mp4
     *
     * @param url
     * @return
     */
    public static String getFileNameByUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * 给关键字上色
     *
     * @param context
     * @param wholeStr
     * @param highlightStr
     * @param color
     * @return
     */
    public static SpannableStringBuilder fillColor(
            Context context, String wholeStr, String highlightStr, int color) {
        List<Integer> sTextsStartList = new ArrayList<>();
        int sTextLength = highlightStr.length();
        int lengthFront = 0;//TODO 记录被找出后前面的字段的长度
        String temp = wholeStr;
        int start = -1;
                do {
                    start = temp.indexOf(highlightStr);
                    if (start != -1) {
                        start = start + lengthFront;
                        sTextsStartList.add(start);//TODO 记录检索到的位置
                        lengthFront = start + sTextLength;
                        temp = wholeStr.substring(lengthFront);//TODO 截取新的字符串，重新检索
                    }
                } while (start != -1);

                SpannableStringBuilder spBuilder = new SpannableStringBuilder(wholeStr);
                color = context.getResources().getColor(color);
//                CharacterStyle charaStyle = new ForegroundColorSpan(color);
                for (Integer i : sTextsStartList) {
                    spBuilder.setSpan(new ForegroundColorSpan(color), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (spBuilder != null) {
                    return spBuilder;
                } else {
                    return null;
                }

    }
}


