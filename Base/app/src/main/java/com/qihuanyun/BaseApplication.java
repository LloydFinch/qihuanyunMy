package com.qihuanyun;

import android.content.Context;
import android.os.Environment;

import com.qihuanyun.activity.SubUnityPlayerActivity;
import com.qihuanyun.dao.FileService;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.FileUtil;
import com.qihuanyun.utils.ItemWorker;
import com.qihuanyun.utils.MobileUtils;
import com.qihuanyun.utils.StorageUtil;
import com.vanda.vandalibnetwork.application.AppData;
import com.vanda.vandalibnetwork.daterequest.RequestManager;

import net.tsz.afinal.http.HttpHandler;
import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseApplication extends AppData{

    /** 视频数据 SD卡缓存路径 */
    public static final String SOBF_CACHE_BASE = Environment.getExternalStorageDirectory()+"/qihuanyun";
    /** 视频截图缓冲路径 */
    public static final String SOBF_VIDEO_THUMB = SOBF_CACHE_BASE + "/thumb/";

    /**
     *  管理文件下载的线程
     *  key：下载文件名称的MD5
     *  value ： 下载该文件的Downloader
     */
//    public Map<String,Downloader> loaderMap;
    public Map<String,HttpHandler> handlerMap;
    /*当前正在下载和已经下载完成的文件记录*/
    public List<DownloadParcel> downloadMovieRecordList;
    public List<DownloadParcel> downloadGameRecordList;
    /*当前手机中所有非预装程序*/
    public List<String> appList;
    public static FileService mFileService;
    public static String basePath ;
    public static String MOVICE_PATH;
    public static String GAME_PATH;
    public ItemWorker mItemWorker;
    private static BaseApplication context;

    public static SubUnityPlayerActivity mSubUnityPlayerActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        ExtUtils.errorLog("---start------","start");
        RequestManager.newImageLoader();
        context = this;

        handlerMap = new HashMap();
        mFileService = new FileService(this);

        basePath = StorageUtil.getBiggestFreeStorage(this);
        MOVICE_PATH = basePath + "/qihuanyun/movie/";
        GAME_PATH = basePath + "/qihuanyun/game/";

        mItemWorker = new ItemWorker(getApplicationContext());

        init();
    }
    private void init() {
        //创建缓存目录
        FileUtil.createIfNoExists(SOBF_CACHE_BASE);
        FileUtil.createIfNoExists(SOBF_VIDEO_THUMB);
        FileUtil.createIfNoExists(MOVICE_PATH);
        FileUtil.createIfNoExists(GAME_PATH);
    }

//    public Map<String, HttpHandler> getLoaderMap() {
//        return handlerMap;
//    }


    public Map<String, HttpHandler> getHandlerMap() {
        return handlerMap;
    }

    public static Context getContext(){
        return context;
    }
    public static BaseApplication getInstance(){
        return context;
    }

    /**
     * 取消所有正在下载的线程
     */
    public void cancleAllLoaders(){
        if(handlerMap == null || handlerMap.entrySet().size() == 0) return;

        for (Map.Entry<String, HttpHandler> m : handlerMap.entrySet()) {
            m.getValue().stop();
            //更新状态
            mFileService.updateDownloadStatus(m.getKey(), 2,"");
        }

        handlerMap.clear();
    }

    /**
     * 根据指定的文件名，停止该文件的下载
     * @param md5String
     */
    public void cancleLoaderByFileName(String md5String){
        if(handlerMap == null || handlerMap.entrySet().size() == 0) return;

        if(handlerMap.containsKey(md5String)){
            handlerMap.get(md5String).stop();

            handlerMap.remove(md5String);
            mFileService.updateDownloadStatus(md5String, 2,"");
        }
    }

    /**
     * 重新加载数据
     * @param type
     */
    public void reLoadRecordsByType(final String type){
        new Thread(){
            @Override
            public void run() {
                if("game".equals(type)) {
                    downloadGameRecordList =  mFileService.getDownloadRecordsByType(type);
                }else {
                    downloadMovieRecordList = mFileService.getDownloadRecordsByType(type);
                }
            }
        }.start();
    }

    public void setDownloadMovieRecordList(List<DownloadParcel> downloadMovieRecordList) {
        this.downloadMovieRecordList = downloadMovieRecordList;
    }

    public List<DownloadParcel> getDownloadMovieRecordList() {
        if(downloadMovieRecordList == null)
            downloadMovieRecordList = mFileService.getDownloadRecordsByType("movie");
        return downloadMovieRecordList;
    }

    public void setDownloadGameRecordList(List<DownloadParcel> downloadGameRecordList) {
        this.downloadGameRecordList = downloadGameRecordList;
    }

    public List<DownloadParcel> getDownloadGameRecordList() {
        if(downloadGameRecordList == null)
            downloadGameRecordList = mFileService.getDownloadRecordsByType("game");
        return downloadGameRecordList;
    }

    public void setAppList(List<String> appList) {
        this.appList = appList;
    }

    public List<String> getAppList() {
        if(appList == null)
            appList = MobileUtils.getAllApps(this);
        return appList;
    }
}
