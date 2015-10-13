package com.qihuanyun.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.database.SQLiteHelperOrm;
import net.tsz.afinal.http.entityhandler.DownloadParcel;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.Md5Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务bean
 *
 */
public class FileService {
    private SQLiteHelperOrm openHelper;

    public FileService(Context context) {
        openHelper = new SQLiteHelperOrm(context);
    }
    /**
     * 获取每条线程已经下载的文件长度
     * @param path
     * @return
     */
    public synchronized Map<Integer, Integer> getData(String path){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        Map<Integer, Integer> data = null;
        db.beginTransaction();
        try{
            cursor = db.rawQuery("select threadid, downlength from filedownlog where downpath=?", new String[]{path});
            data = new HashMap<Integer, Integer>();
            while(cursor.moveToNext()){
                data.put(cursor.getInt(0), cursor.getInt(1));
            }

            db.setTransactionSuccessful();
        }finally {
            cursor.close();
            db.endTransaction();
        }

        db.close();

        return data;
    }
    /**
     * 保存每条线程已经下载的文件长度
     * @param path
     * @param map
     */
    public synchronized void save(String path,  Map<Integer, Integer> map){//int threadid, int position
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Map.Entry<Integer, Integer> entry : map.entrySet()){
                db.execSQL("insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
                        new Object[]{path, entry.getKey(), entry.getValue()});
            }
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
        }

        db.close();
    }
    /**
     * 实时更新每条线程已经下载的文件长度
     * @param path
     * @param map
     */
    public synchronized void update(String path, Map<Integer, Integer> map){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Map.Entry<Integer, Integer> entry : map.entrySet()){
                db.execSQL("update filedownlog set downlength=? where downpath=? and threadid=?",
                        new Object[]{entry.getValue(), path, entry.getKey()});
            }
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
        }

        db.close();
    }
    /**
     * 当文件下载完成后，删除对应的下载记录
     * @param path
     */
    public synchronized void delete(String path){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.execSQL("delete from filedownlog where downpath=?", new Object[]{path});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();
    }

    /**
     * 创建文件下载记录
     *
     * @param downloadParcel
     */
    public synchronized void createDownload(DownloadParcel downloadParcel, String fileName) {
        if (getDownload(downloadParcel.id + downloadParcel.title + downloadParcel.type) != null)
            return;

        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            int time = (int) (new Date().getTime() / 1000);
            String localPath = BaseApplication.MOVICE_PATH;
            if ("game".equals(downloadParcel.type))
                localPath = BaseApplication.GAME_PATH;
            db.execSQL("insert into download(id,imageUrl,title, title_md5, size,create_time,update_time,status,network_url,local_url,progress,type,file_name) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{downloadParcel.id, downloadParcel.imageUrl, downloadParcel.title, Md5Utils.MD5(downloadParcel.id + downloadParcel.title + downloadParcel.type), downloadParcel.fileSize, time, time, 0, downloadParcel.url, localPath, 0, downloadParcel.type, fileName});

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();

        //重新加载数据
        if ("game".equals(downloadParcel.type)) {
            BaseApplication.getInstance().reLoadRecordsByType("game");
        } else {
            BaseApplication.getInstance().reLoadRecordsByType("movie");
        }
    }

    /**
     * 查询文件记录
     *
     * @param md5String = id + title + type
     */
    public synchronized DownloadParcel getDownload(String md5String) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        DownloadParcel download = null;
        db.beginTransaction();

        try{
            cursor = db.rawQuery("select * from download where title_md5=?", new String[]{Md5Utils.MD5(md5String)});

            while (cursor.moveToNext()) {
                download = new DownloadParcel();
                download._id = cursor.getInt(cursor.getColumnIndex("_id"));
                download.id = cursor.getInt(cursor.getColumnIndex("id"));
                download.progress = cursor.getInt(cursor.getColumnIndex("progress"));
                download.type = cursor.getString(cursor.getColumnIndex("type"));
                download.url = cursor.getString(cursor.getColumnIndex("network_url"));
                download.title = cursor.getString(cursor.getColumnIndex("title"));
                download.fileSize = cursor.getInt(cursor.getColumnIndex("size"));
                download.status = cursor.getInt(cursor.getColumnIndex("status"));
                download.packageName = cursor.getString(cursor.getColumnIndex("package_name"));
            }

            db.setTransactionSuccessful();
        }finally {
            cursor.close();
            db.endTransaction();
        }

        db.close();

        return download;
    }

    /**
     * 更新下载进度 加锁
     * 如果不加锁，多个线程可能会同时进入这个方法，db对象
     * 会被回收，当执行execSQL的时候，由于db对象已经被GC回收，
     * 就会出现内存异常，相关请查看synchronized
     *
     * @param md5String
     * @param progress
     */
    public synchronized void updateDownloadProgress(String md5String, int progress) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int time = (int) (new Date().getTime() / 1000);
            db.execSQL("update download set progress=?,update_time=? where title_md5=?", new Object[]{progress, time, md5String});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();
    }

    /**
     * 更新状态 加锁
     *
     * @param md5String
     * @param status
     * @param type
     */
    public synchronized void updateDownloadStatus(String md5String, int status, String type) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            int time = (int) (new Date().getTime() / 1000);
            db.execSQL("update download set status=?,update_time=? where title_md5=?", new Object[]{status, time, md5String});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();

        if (ExtUtils.isNotEmpty(type))
            BaseApplication.getInstance().reLoadRecordsByType(type);
    }

    /**
     * 获取所有下载记录
     */
    public synchronized List<DownloadParcel> getAllDownloadRecord() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        List<DownloadParcel> list = new ArrayList<DownloadParcel>();
        db.beginTransaction();
        try {
            cursor = db.rawQuery("select * from download", null);

            while (cursor.moveToNext()) {
                DownloadParcel download = new DownloadParcel();
                download._id = cursor.getInt(cursor.getColumnIndex("_id"));
                download.id = cursor.getInt(cursor.getColumnIndex("id"));
                download.progress = cursor.getInt(cursor.getColumnIndex("progress"));
                download.type = cursor.getString(cursor.getColumnIndex("type"));
                download.url = cursor.getString(cursor.getColumnIndex("network_url"));
                download.imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                download.title = cursor.getString(cursor.getColumnIndex("title"));
                download.local_url = cursor.getString(cursor.getColumnIndex("local_url"));
                download.fileName = cursor.getString(cursor.getColumnIndex("file_name"));
                download.fileSize = cursor.getInt(cursor.getColumnIndex("size"));
                download.status = cursor.getInt(cursor.getColumnIndex("status"));
                download.packageName = cursor.getString(cursor.getColumnIndex("package_name"));
                list.add(download);
            }

            db.setTransactionSuccessful();
        }finally {
            cursor.close();
            db.endTransaction();
        }

        db.close();

        return list;
    }

    /**
     * 根据类型获取下载记录
     */
    public synchronized List<DownloadParcel> getDownloadRecordsByType(String type) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = null;
        List<DownloadParcel> list = new ArrayList<DownloadParcel>();
        db.beginTransaction();
        try {
            cursor = db.rawQuery("select * from download where type=?", new String[]{type});

            DownloadParcel download;
            while (cursor.moveToNext()) {
                download = new DownloadParcel();
                download._id = cursor.getInt(cursor.getColumnIndex("_id"));
                download.id = cursor.getInt(cursor.getColumnIndex("id"));
                download.progress = cursor.getInt(cursor.getColumnIndex("progress"));
                download.type = cursor.getString(cursor.getColumnIndex("type"));
                download.url = cursor.getString(cursor.getColumnIndex("network_url"));
                download.imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                download.title = cursor.getString(cursor.getColumnIndex("title"));
                download.local_url = cursor.getString(cursor.getColumnIndex("local_url"));
                download.fileName = cursor.getString(cursor.getColumnIndex("file_name"));
                download.fileSize = cursor.getInt(cursor.getColumnIndex("size"));
                download.status = cursor.getInt(cursor.getColumnIndex("status"));
                download.packageName = cursor.getString(cursor.getColumnIndex("package_name"));
                list.add(download);
            }
            db.setTransactionSuccessful();
        }finally {
            cursor.close();
            db.endTransaction();
        }

        db.close();

        return list;
    }

    /**
     * 删除文件下载记录
     * @param id
     */
    public synchronized void deleteDownload(int id,String type){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.execSQL("delete from download where _id=?", new Object[]{id});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();

        if (ExtUtils.isNotEmpty(type))
            BaseApplication.getInstance().reLoadRecordsByType(type);
    }

    /**
     * 更新apk的包名
     * @param id
     * @param packageName
     */
    public synchronized void saveApkPackageName(int id, String packageName) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int time = (int) (new Date().getTime() / 1000);
            db.execSQL("update download set package_name=?,update_time=? where id=?", new Object[]{packageName, time, id});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();

        BaseApplication.getInstance().reLoadRecordsByType("game");
    }
}