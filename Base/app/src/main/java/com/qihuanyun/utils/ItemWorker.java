package com.qihuanyun.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.activity.SubUnityPlayerActivity;
import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.GameViewHolder;
import com.qihuanyun.pojo.IndexData;
import com.qihuanyun.pojo.VideoViewHolder;
import com.qihuanyun.service.DownloadService;
import com.vanda.vandalibnetwork.daterequest.RequestManager;

import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.util.HashMap;

/**
 * 列表显示Item的一个工具类
 * 封装了Item的业务逻辑
 * item的点击事件不在此类处理
 */
public class ItemWorker {
    private Context context;
    private static Drawable mDefaultImageDrawable;

    public ItemWorker(Context context){
        this.context = context;
        mDefaultImageDrawable = context.getResources().getDrawable(R.mipmap.list_default);
    }

    /**
     * 影视Item处理方法(for add container)
     *
     * @param context
     * @param videoItem
     * @param video
     */
    public static void videoItem(final Context context, final View videoItem, final IndexData.VideoGame video) {
//        ViewGroup.LayoutParams params = videoItem.findViewById(R.id.image).getLayoutParams();
//        params.width = StaticData.ScreenWidth*3/10;
//        params.height = params.width;
        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + video.imgUrl,
                RequestManager.getImageListener((ImageView)videoItem.findViewById(R.id.image), 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));
        ((TextView) videoItem.findViewById(R.id.title)).setText(video.title);
        ((TextView) videoItem.findViewById(R.id.description)).setText(video.summary);

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
                videoItem.findViewById(R.id.ll_download).setVisibility(View.GONE);
                videoItem.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("正在下载");
                videoItem.findViewById(R.id.open_or_downloading).setClickable(false);
                break;
            case 1://下载完成
                videoItem.findViewById(R.id.ll_download).setVisibility(View.GONE);
                videoItem.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("打开");
                final String finalLocalPath = localPath;
                videoItem.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url","file://"+ finalLocalPath));
                    }
                });
                break;
            case 2://暂停
                videoItem.findViewById(R.id.ll_download).setVisibility(View.GONE);
                ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("继续下载");
                videoItem.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                videoItem.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                videoItem.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("正在下载");
                        videoItem.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        videoItem.findViewById(R.id.open_or_downloading).setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            case 3://出错
                videoItem.findViewById(R.id.ll_download).setVisibility(View.GONE);
                ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("继续下载");
                videoItem.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                videoItem.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                videoItem.findViewById(R.id.open_or_downloading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("正在下载");
                        videoItem.findViewById(R.id.open_or_downloading).setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        videoItem.findViewById(R.id.open_or_downloading).setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            default://默认
                //没有下载记录，显示下载按钮
                break;
        }

        videoItem.findViewById(R.id.ll_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载计数
                recordDowdloadOrPlayCount(video.id,2);

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    videoItem.findViewById(R.id.ll_download).setVisibility(View.GONE);
                    videoItem.findViewById(R.id.open_or_downloading).setVisibility(View.VISIBLE);
                    ((TextView) videoItem.findViewById(R.id.open_or_downloading)).setText("正在下载");
                    videoItem.findViewById(R.id.open_or_downloading).setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "",fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });

        videoItem.findViewById(R.id.ll_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放计数
                recordDowdloadOrPlayCount(video.id, 1);
                context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url", downloadUrl));
            }
        });
    }

    /**
     * 游戏Item处理方法(for add container)
     *
     * @param context
     * @param gameItem
     * @param game
     */
    public static void gameItem(final Context context, final View gameItem, final IndexData.VideoGame game) {
//        ViewGroup.LayoutParams params = gameItem.findViewById(R.id.image).getLayoutParams();
//        params.width = StaticData.ScreenWidth*3/10;
//        params.height = params.width;
        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + game.imgUrl,
                RequestManager.getImageListener((ImageView)gameItem.findViewById(R.id.image), 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));
        ((TextView) gameItem.findViewById(R.id.title)).setText(game.title);
        ((TextView) gameItem.findViewById(R.id.description)).setText(game.summary);

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
                gameItem.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                gameItem.findViewById(R.id.open).setVisibility(View.VISIBLE);
                ((TextView) gameItem.findViewById(R.id.open)).setText("正在下载");
                gameItem.findViewById(R.id.open).setClickable(false);
                break;
            case 1://下载完成
                gameItem.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                //检查是否已经安装
                int isInstall = 0; // 0代表未安装，1代表已经安装
                for (String packageName : BaseApplication.getInstance().getAppList()) {
                    if (packageName.equals(appPackageName)) {
                        //已经安装
                        isInstall = 1;
                        gameItem.findViewById(R.id.install).setVisibility(View.GONE);
                        gameItem.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        final String finalAppPackageName = appPackageName;
                        gameItem.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MobileUtils.openApp(context, finalAppPackageName);
                            }
                        });
                        break;
                    }
                }
                if (isInstall == 0) {
                    gameItem.findViewById(R.id.open).setVisibility(View.GONE);
                    gameItem.findViewById(R.id.install).setVisibility(View.VISIBLE);
                    final String finalLocalPath = localPath;
                    gameItem.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobileUtils.installApk(context, finalLocalPath);
                        }
                    });
                }
                break;
            case 2://暂停
                gameItem.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                gameItem.findViewById(R.id.install).setVisibility(View.VISIBLE);
                ((TextView) gameItem.findViewById(R.id.install)).setText("继续下载");
                gameItem.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gameItem.findViewById(R.id.install).setVisibility(View.GONE);
                        gameItem.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        ((TextView) gameItem.findViewById(R.id.open)).setText("正在下载");
                        gameItem.findViewById(R.id.open).setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                    }
                });
                break;
            case 3://出错
                gameItem.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                gameItem.findViewById(R.id.install).setVisibility(View.VISIBLE);
                ((TextView) gameItem.findViewById(R.id.install)).setText("继续下载");
                gameItem.findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gameItem.findViewById(R.id.install).setVisibility(View.GONE);
                        gameItem.findViewById(R.id.open).setVisibility(View.VISIBLE);
                        ((TextView) gameItem.findViewById(R.id.open)).setText("正在下载");
                        gameItem.findViewById(R.id.open).setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                    }
                });
                break;
            default:
                break;
        }

        gameItem.findViewById(R.id.download_item_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载计数
                recordDowdloadOrPlayCount(game.id, 2);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    gameItem.findViewById(R.id.download_item_layout).setVisibility(View.GONE);
                    gameItem.findViewById(R.id.open).setVisibility(View.VISIBLE);
                    ((TextView) gameItem.findViewById(R.id.open)).setText("正在下载");
                    gameItem.findViewById(R.id.open).setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });
    }

    /**
     * 影视Item处理方法(for listview)
     *
     * @param context
     * @param videoViewHolder
     * @param video
     */
    public static void videoItem(final Context context, final VideoViewHolder videoViewHolder, final GameContentData.Data video) {
//        ViewGroup.LayoutParams params = videoViewHolder.imageView.getLayoutParams();
//        params.width = StaticData.ScreenWidth*3/10;
//        params.height = params.width;
        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + video.imgUrl,
                RequestManager.getImageListener(videoViewHolder.imageView, 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));
        videoViewHolder.title.setText(video.title);
        videoViewHolder.desc.setText(video.summary);

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
                videoViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.VISIBLE);
                videoViewHolder.mTextViewOpenOrDownload.setText("正在下载");
                videoViewHolder.mTextViewOpenOrDownload.setClickable(false);
                break;
            case 1://下载完成
                videoViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.VISIBLE);
                videoViewHolder.mTextViewOpenOrDownload.setText("打开");
                final String finalLocalPath = localPath;
                videoViewHolder.mTextViewOpenOrDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url", "file://" + finalLocalPath));
                    }
                });
                break;
            case 2://暂停
                videoViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                videoViewHolder.mTextViewOpenOrDownload.setText("继续下载");
                videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.VISIBLE);
                videoViewHolder.mTextViewOpenOrDownload.setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                videoViewHolder.mTextViewOpenOrDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoViewHolder.mTextViewOpenOrDownload.setText("正在下载");
                        videoViewHolder.mTextViewOpenOrDownload.setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        videoViewHolder.mTextViewOpenOrDownload.setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            case 3://出错
                videoViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                videoViewHolder.mTextViewOpenOrDownload.setText("继续下载");
                videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.VISIBLE);
                videoViewHolder.mTextViewOpenOrDownload.setBackground(context.getResources().getDrawable(R.mipmap.button_download));
                videoViewHolder.mTextViewOpenOrDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoViewHolder.mTextViewOpenOrDownload.setText("正在下载");
                        videoViewHolder.mTextViewOpenOrDownload.setBackground(context.getResources().getDrawable(R.mipmap.button_open));
                        videoViewHolder.mTextViewOpenOrDownload.setClickable(false);

                        //改变此记录在数据库的状态，并重新获取影视的全局list
                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(video.id + video.title + "movie"), 0, "movie");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                    }
                });
                break;
            default://默认
                //没有下载记录，显示下载按钮
                videoViewHolder.mLinearLayoutDownload.setVisibility(View.VISIBLE);
                videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.GONE);
                break;
        }

        videoViewHolder.mLinearLayoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载计数
                recordDowdloadOrPlayCount(video.id,2);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    videoViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                    videoViewHolder.mTextViewOpenOrDownload.setVisibility(View.VISIBLE);
                    videoViewHolder.mTextViewOpenOrDownload.setText("正在下载");
                    videoViewHolder.mTextViewOpenOrDownload.setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(video.id, "movie", downloadUrl, Urls.IMAGE_PREFIX + video.imgUrl, video.title, "", fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });

        videoViewHolder.mLinearLayoutplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放计数
                recordDowdloadOrPlayCount(video.id,1);
                context.startActivity(new Intent(context, SubUnityPlayerActivity.class).putExtra("url", downloadUrl));
            }
        });
    }

    /**
     * 游戏Item处理方法(for listview)
     *
     * @param context
     * @param mGameViewHolder
     * @param game
     */
    public static void gameItem(final Context context, final GameViewHolder mGameViewHolder, final GameContentData.Data game) {
//        ViewGroup.LayoutParams params = mGameViewHolder.imageView.getLayoutParams();
//        params.width = StaticData.ScreenWidth*3/10;
//        params.height = params.width;
        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + game.imgUrl,
                RequestManager.getImageListener(mGameViewHolder.imageView, 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));
        mGameViewHolder.title.setText(game.title);
        mGameViewHolder.desc.setText(game.summary);

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
                mGameViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                mGameViewHolder.mTextViewOpen.setVisibility(View.VISIBLE);
                mGameViewHolder.mTextViewOpen.setText("正在下载");
                mGameViewHolder.mTextViewOpen.setClickable(false);
                break;
            case 1://下载完成
                mGameViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                //检查是否已经安装
                int isInstall = 0; // 0代表未安装，1代表已经安装
                for (String packageName : BaseApplication.getInstance().getAppList()) {
                    if (packageName.equals(appPackageName)) {
                        //已经安装
                        isInstall = 1;
                        mGameViewHolder.mTextViewOpen.setVisibility(View.VISIBLE);
                        mGameViewHolder.mTextViewInstall.setVisibility(View.GONE);
                        final String finalAppPackageName = appPackageName;
                        mGameViewHolder.mTextViewOpen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MobileUtils.openApp(context, finalAppPackageName);
                            }
                        });
                        break;
                    }
                }
                if (isInstall == 0) {
                    mGameViewHolder.mTextViewOpen.setVisibility(View.GONE);
                    mGameViewHolder.mTextViewInstall.setVisibility(View.VISIBLE);
                    final String finalLocalPath = localPath;
                    mGameViewHolder.mTextViewInstall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobileUtils.installApk(context, finalLocalPath);
                        }
                    });
                }
                break;
            case 2://暂停
                mGameViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                mGameViewHolder.mTextViewInstall.setVisibility(View.VISIBLE);
                mGameViewHolder.mTextViewInstall.setText("继续下载");
                mGameViewHolder.mTextViewInstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGameViewHolder.mTextViewInstall.setVisibility(View.GONE);
                        mGameViewHolder.mTextViewOpen.setVisibility(View.VISIBLE);
                        mGameViewHolder.mTextViewOpen.setText("正在下载");
                        mGameViewHolder.mTextViewOpen.setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                    }
                });
                break;
            case 3://出错
                mGameViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                mGameViewHolder.mTextViewInstall.setVisibility(View.VISIBLE);
                mGameViewHolder.mTextViewInstall.setText("继续下载");
                mGameViewHolder.mTextViewInstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGameViewHolder.mTextViewInstall.setVisibility(View.GONE);
                        mGameViewHolder.mTextViewOpen.setVisibility(View.VISIBLE);
                        mGameViewHolder.mTextViewOpen.setText("正在下载");
                        mGameViewHolder.mTextViewOpen.setClickable(false);

                        BaseApplication.mFileService.updateDownloadStatus(Md5Utils.MD5(game.id + game.title + "game"), 0, "game");

                        context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                    }
                });
                break;
            default:
                mGameViewHolder.mLinearLayoutDownload.setVisibility(View.VISIBLE);
                mGameViewHolder.mTextViewOpen.setVisibility(View.GONE);
                mGameViewHolder.mTextViewInstall.setVisibility(View.GONE);
                break;
        }

        mGameViewHolder.mLinearLayoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载计数
                recordDowdloadOrPlayCount(game.id,2);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    mGameViewHolder.mLinearLayoutDownload.setVisibility(View.GONE);
                    mGameViewHolder.mTextViewOpen.setVisibility(View.VISIBLE);
                    mGameViewHolder.mTextViewOpen.setText("正在下载");
                    mGameViewHolder.mTextViewOpen.setClickable(false);

                    context.startService(new Intent(context, DownloadService.class).putExtra("downloadParcel", new DownloadParcel(game.id, "game", downloadUrl, Urls.IMAGE_PREFIX + game.imgUrl, game.title, game.packageName, fileName)));
                } else {
                    ExtUtils.shortToast(context, "无存储设备，无法下载");
                }
            }
        });
    }

    private static void recordDowdloadOrPlayCount(final int id,final int type){
        HashMap<String,String> map = new HashMap<>();
        map.put("contentid",id+"");
        map.put("type",type+"");
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/downloadmovie", CommonData.class, map, "submit_count",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        ExtUtils.infoLog("DetailWorker", id + "submit count success " + type);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ExtUtils.errorLog("DetailWorker", id + "submit count fail " + type);
                    }
                });
    }
}
