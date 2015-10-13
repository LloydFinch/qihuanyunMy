package com.qihuanyun.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.activity.DownloadActivity;
import com.qihuanyun.dao.FileService;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.Md5Utils;
import com.qihuanyun.utils.MobileUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    private FileService mFileService;
    private static final int TIME_INTERVAL = 2000;

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
        if (intent != null) {
            DownloadParcel download = intent.getParcelableExtra("downloadParcel");
            if (download != null) download(download);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final DownloadParcel download) {
        getFileSizeAndSave(download);
        final String title_md5 = Md5Utils.MD5(download.id + download.title + download.type);

        FinalHttp fh = new FinalHttp();
        AjaxCallBack<DownloadParcel> ajax = new AjaxCallBack<DownloadParcel>() {

            @Override
            public void onStart() {
                super.onStart();
                ExtUtils.shortToast(DownloadService.this,download.title + "正在下载...");
            }

            @Override
            public void onLoading(long count, long current, DownloadParcel downloadParcel) {
                synchronized (DownloadService.this) {
                    download.progress = (int) current;
                    download.isError = 0;//1表示无错误
                    download.speed = downloadParcel.speed;
                    sendBroadcast(new Intent(DownloadActivity.PROGRESS_RECEIVE).putExtra("downloadParcel", download));
                    mFileService.updateDownloadProgress(title_md5, (int) current);
                }
            }

            @Override
            public void onSuccess(final DownloadParcel f) {
                super.onSuccess(f);
                //更新状态
                mFileService.updateDownloadStatus(title_md5, 1, download.type);

                //删除全局变量中的相关键值对
                if (BaseApplication.getInstance().getHandlerMap().containsKey(title_md5)) {
                    BaseApplication.getInstance().getHandlerMap().remove(title_md5);
                }

                if ("game".equals(download.type)) {
                    //获取apk报名并保存
                    new Thread(){
                        @Override
                        public void run() {
                            mFileService.saveApkPackageName(f.id, MobileUtils.getPackageNameByApk(DownloadService.this, BaseApplication.GAME_PATH + download.fileName));
                        }
                    }.start();

                    //安装
                    MobileUtils.installApk(DownloadService.this, BaseApplication.GAME_PATH + download.fileName);
                }

                ExtUtils.shortToast(DownloadService.this, f.title + "下载完成");
            }

            @Override
            public void onFailure(Throwable t, int errorNo,
                                  String strMsg, DownloadParcel downloadParcel) {
                super.onFailure(t, errorNo, strMsg, downloadParcel);
                ExtUtils.shortToast(DownloadService.this, download.title + " 下载出错");
                download.isError = 1;//1表示有错误
                mFileService.updateDownloadStatus(download.id + download.title + download.type, 3, download.type);
                sendBroadcast(new Intent(DownloadActivity.PROGRESS_RECEIVE).putExtra("downloadParcel", download));
            }
        };
        ajax.progress(true, TIME_INTERVAL);

        String path = BaseApplication.MOVICE_PATH;
        if ("game".equals(download.type)) path = BaseApplication.GAME_PATH;

        HttpHandler<DownloadParcel> handler = fh.specificDownload(
                download.url, null, path + download.fileName,
                true, ajax, download);

        if (!BaseApplication.getInstance().getHandlerMap().containsKey(Md5Utils.MD5(download.id + download.title + download.type))) {
            BaseApplication.getInstance().getHandlerMap().put(Md5Utils.MD5(download.id + download.title + download.type), handler);
        }
    }

    private void getFileSizeAndSave(final DownloadParcel download) {
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(download.url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    download.fileSize = conn.getContentLength();

                    mFileService.createDownload(download, download.fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
