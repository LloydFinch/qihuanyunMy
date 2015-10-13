package com.qihuanyun.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.activity.SubUnityPlayerActivity;
import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.pojo.GameDetailData;
import com.qihuanyun.pojo.VideoDetailData;
import com.qihuanyun.service.DownloadService;
import com.vanda.vandalibnetwork.daterequest.RequestManager;

import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.util.HashMap;

/**
 * 详情页面的按钮控制
 */
public class DetailWorker {

    /**
     * 影视详情页面按钮处理方法
     *
     * @param context
     * @param view
     * @param video
     */
    public static void videoDetailButtonController(final Context context, final View view, final VideoDetailData.Data video) {
        /**
         * 生成文件名，生成规则：title_id.mp4  例如：大圣归来_17.mp4
         * 获取视频类型
         */
        //文件名拼接
        final String fileName = ExtUtils.getFileNameByUrl(video.url);

        //处理url中文问题
        final String downloadUrl = ExtUtils.urlHandler(video.url);

        /*获取该视频在本地是否有下载记录，如果有下载记录，则获取该记录的状态*/
        int status = -1;
        String localPath = "";
        for (DownloadParcel movie : BaseApplication.getInstance().getDownloadMovieRecordList()) {
            if (movie.id == video.id) {
                status = movie.status;
                localPath = movie.local_url + movie.fileName;
                break;
            }
        }

        switch (status) {
            case 0://正在下载
                view.findViewById(R.id.ll_download).setVisibility(View.GONE);
                view.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.open_or_downloading)).setText("正在下载");
                view.findViewById(R.id.open_or_downloading).setClickable(false);
                break;
            case 1://下载完成
                view.findViewById(R.id.ll_download).setVisibility(View.GONE);
                view.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.open_or_downloading)).setText("打开");
                final String finalLocalPath = localPath;
                view.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url","file://"+ finalLocalPath));
                    }
                });
                break;
            case 2://暂停
                view.findViewById(R.id.ll_download).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.open_or_downloading)).setText("继续下载");
                view.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                view.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                view.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) view.findViewById(R.id.open_or_downloading)).setText("正在下载");
                        view.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        view.findViewById(R.id.open_or_downloading).setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            case 3://出错
                view.findViewById(R.id.ll_download).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.open_or_downloading)).setText("继续下载");
                view.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                view.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                view.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) view.findViewById(R.id.open_or_downloading)).setText("正在下载");
                        view.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        view.findViewById(R.id.open_or_downloading).setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            default://默认
                //没有下载记录，显示下载按钮
                view.findViewById(R.id.ll_download).setVisibility(View.VISIBLE);
                view.findViewById(R.id.open_or_downloading).setVisibility(View.GONE);
                break;
        }

        view.findViewById(R.id.ll_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载计数
                recordDowdloadOrPlayCount(video.id,2);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    view.findViewById(R.id.ll_download).setVisibility(View.GONE);
                    view.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.open_or_downloading)).setText("正在下载");
                    view.findViewById(R.id.open_or_downloading).setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });

        view.findViewById(R.id.ll_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放计数
                recordDowdloadOrPlayCount(video.id,1);
                context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url", downloadUrl));
            }
        });
    }

    /**
     * 游戏详情页面按钮处理方法
     *
     * @param context
     * @param view
     * @param game
     */
    public static void gameDetailButtonController(final Context context, final View view, final GameDetailData.Data game) {
        final String fileName = ExtUtils.getFileNameByUrl(game.url);

        //处理url中文问题
        final String downloadUrl = ExtUtils.urlHandler(game.url);

        int status = -1;
        String localPath = "";
        String appPackageName = "";
        for (DownloadParcel item : BaseApplication.getInstance().getDownloadGameRecordList()) {
            if (item.id == game.id) {
                status = item.status;
                localPath = item.local_url + item.fileName;
                appPackageName = item.packageName;
                break;
            }
        }

        switch (status) {
            case 0://正在下载
                view.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                view.findViewById(R.id.open).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.open)).setText("正在下载");
                view.findViewById(R.id.open).setClickable(false);
                break;
            case 1://下载完成
                view.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                //检查是否已经安装
                int isInstall = 0; // 0代表未安装，1代表已经安装
                for (String packageName : BaseApplication.getInstance().getAppList()) {
                    if (packageName.equals(appPackageName)) {
                        //已经安装
                        isInstall = 1;
                        view.findViewById(R.id.install).setVisibility(View.GONE);
                        view.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        final String finalAppPackageName = appPackageName;
                        view.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MobileUtils.openApp(context, finalAppPackageName);
                            }
                        });
                        break;
                    }
                }
                if (isInstall == 0) {
                    view.findViewById(R.id.open).setVisibility(View.GONE);
                    view.findViewById(R.id.install).setVisibility(View.VISIBLE);
                    final String finalLocalPath = localPath;
                    view.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobileUtils.installApk(context, finalLocalPath);
                        }
                    });
                }
                break;
            case 2://暂停
                view.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                view.findViewById(R.id.install).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.install)).setText("继续下载");
                view.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.install).setVisibility(View.GONE);
                        view.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.open)).setText("正在下载");
                        view.findViewById(R.id.open).setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.pakname, fileName)));
                    }
                });
                break;
            case 3://出错
                view.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                view.findViewById(R.id.install).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.install)).setText("继续下载");
                view.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.install).setVisibility(View.GONE);
                        view.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.open)).setText("正在下载");
                        view.findViewById(R.id.open).setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.pakname, fileName)));
                    }
                });
                break;
            default:
                view.findViewById(R.id.download_item_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.open).setVisibility(View.GONE);
                view.findViewById(R.id.install).setVisibility(View.GONE);
                break;
        }

        view.findViewById(R.id.download_item_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDowdloadOrPlayCount(game.id,2);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    view.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.open).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.open)).setText("正在下载");
                    view.findViewById(R.id.open).setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.pakname, fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });
    }


    private static void recordDowdloadOrPlayCount(final int id,final int type){
        HashMap<String,String> map = new HashMap<>();
        map.put("contentId",id+"");
        map.put("type",type+"");
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/downloadmovie", CommonData.class, map, "submit_count",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        ExtUtils.infoLog("DetailWorker",id + "submit count success " + type);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ExtUtils.errorLog("DetailWorker", id + "submit count fail " + type);
                    }
                });
    }
}
