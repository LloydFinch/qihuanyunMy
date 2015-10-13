package com.qihuanyun.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.activity.DownloadActivity;
import com.qihuanyun.dao.DownloadProgressListener;
import com.qihuanyun.dao.Downloader;
import com.qihuanyun.dao.FileService;
import com.qihuanyun.utils.Md5Utils;
import com.qihuanyun.utils.MobileUtils;

import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.io.File;

public class DownloadService2 extends Service {
    private FileService mFileService;
    private static final int THREAD_NUMBER = 3;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mFileService = BaseApplication.mFileService;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            DownloadParcel download = intent.getParcelableExtra("downloadParcel");
            if(download != null) download(download);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final DownloadParcel download){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String path = BaseApplication.MOVICE_PATH;
                    if("game".equals(download.type)) path = BaseApplication.GAME_PATH;

                    final Downloader loader = new Downloader(DownloadService2.this, download.url, new File(path), THREAD_NUMBER,download.fileName);
                    download.fileSize = loader.getFileSize();
                    //创建下载记录
                    mFileService.createDownload(download,loader.getFileName());

                    //写入全局变量
//                    if(! BaseApplication.getInstance().getLoaderMap().containsKey(Md5Utils.MD5(download.id + download.title + download.type))){
//                        BaseApplication.getInstance().getLoaderMap().put(Md5Utils.MD5(download.id + download.title + download.type), loader);
//                    }

                    loader.download(new DownloadProgressListener(){
                        public void onDownloadSize(int size,String speed) {//可以实时得到文件下载的长度
                            /**
                             * 保证同一时刻只有一个线程访问
                             * 不然会造成错误
                             */
                            synchronized (DownloadService2.this){
                                download.progress = size;
                                download.isError = 0;//1表示无错误
                                download.speed = speed;
                                String title_md5 = Md5Utils.MD5(download.id + download.title + download.type);
                                sendBroadcast(new Intent(DownloadActivity.PROGRESS_RECEIVE).putExtra("downloadParcel", download));
                                mFileService.updateDownloadProgress(title_md5, size);

                                //下载完了
                                if(download.fileSize == size){
                                    //更新状态
                                    mFileService.updateDownloadStatus(title_md5, 1,download.type);

                                    //删除全局变量中的相关键值对
//                                    if(BaseApplication.getInstance().getLoaderMap().containsKey(title_md5)){
//                                        BaseApplication.getInstance().getLoaderMap().remove(title_md5);
//                                    }

                                    if("game".equals(download.type)){
                                        //安装
                                        MobileUtils.installApk(DownloadService2.this,BaseApplication.GAME_PATH + loader.getFileName());
                                    }
                                }
                            }
                        }});
                } catch (Exception e) {
                    download.isError = 1;//1表示有错误
                    mFileService.updateDownloadStatus(download.id + download.title + download.type,3,download.type);
                    sendBroadcast(new Intent(DownloadActivity.PROGRESS_RECEIVE).putExtra("downloadParcel", download));
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
