package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.Md5Utils;
import com.qihuanyun.utils.MobileUtils;
import com.roamer.slidelistview.SlideBaseAdapter;
import com.roamer.slidelistview.SlideListView;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;
import com.wzl.vandan.dialog.VandaAlert;

import net.tsz.afinal.http.entityhandler.DownloadParcel;

import java.io.File;
import java.util.List;

public class DownloadActivity extends BaseActivityActionBarNoNetWork {
    public static final String PROGRESS_RECEIVE = "PROGRESS_RECEIVE";
    private ProgressBroadcast mProgressBroadcast;
    public List<DownloadParcel> mList;
    private SlideListView mListView;
    private DownloadAdapter mDownloadAdapter;
    private Dialog mLoadingDialog;
    private static Drawable mDefaultImageDrawable;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            handlerBroadcastMessage(msg);
        }
    };

    private synchronized void handlerBroadcastMessage(Message msg) {
        DownloadParcel download = (DownloadParcel) msg.obj;
        switch (msg.what) {
            case 0:
                //正常
                if(mList == null || mList.size() == 0) break;
                for (DownloadParcel d : mList) {
                    if (d.id == download.id) {
                        d.progress = download.progress;
                        d.speed = download.speed;
                        if (download.fileSize == download.progress) {
                            d.status = 1;
                        }
                        break;
                    }
                }
                if(mDownloadAdapter != null)
                    mDownloadAdapter.notifyDataSetChanged();
                break;
            case 1:
                //出错了
                break;
        }
    }

    private Handler listHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mDownloadAdapter = new DownloadAdapter(DownloadActivity.this);
            mListView.setAdapter(mDownloadAdapter);
        }
    };

    private Handler refreshListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoadingDialog.dismiss();
            mDownloadAdapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.downloads);

        setTitle("返回");
        mListView = (SlideListView) findViewById(R.id.my_listview);
        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");

        mProgressBroadcast = new ProgressBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRESS_RECEIVE);
        registerReceiver(mProgressBroadcast, filter);

        mDefaultImageDrawable = getResources().getDrawable(R.mipmap.list_default);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                mList = BaseApplication.mFileService.getAllDownloadRecord();
                if(mList == null || mList.size() == 0) return;

                listHandler.sendEmptyMessage(0);
            }
        }.start();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private class DownloadAdapter extends SlideBaseAdapter {


        public DownloadAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }


        @Override
        public DownloadParcel getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {

                convertView = createConvertView(position);
                viewHolder = new ViewHolder();
                viewHolder.mButtonDelete = (Button) convertView.findViewById(R.id.right_back_delete);
                viewHolder.mButtonControl = (Button) convertView.findViewById(R.id.b_control);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.title);
                viewHolder.mTextViewPercent = (TextView) convertView.findViewById(R.id.percent);
                viewHolder.mTextViewSpeed = (TextView) convertView.findViewById(R.id.speed);
                viewHolder.mTextViewComplete = (TextView) convertView.findViewById(R.id.complete);
                viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                viewHolder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_progress);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            ViewGroup.LayoutParams params = viewHolder.mImageView.getLayoutParams();
//            params.width = StaticData.ScreenWidth/3;
//            params.height = params.width;
            RequestManager.loadImage(
                    mList.get(position).imageUrl,
                    RequestManager.getImageListener(viewHolder.mImageView, 0,
                            mDefaultImageDrawable, mDefaultImageDrawable));

            viewHolder.mProgressBar.setMax(mList.get(position).fileSize);
            viewHolder.mProgressBar.setProgress(mList.get(position).progress);
            viewHolder.mTextViewTitle.setText(mList.get(position).title);
            float result = (float) mList.get(position).progress / (float) mList.get(position).fileSize;
            int p = (int) (result * 100);
            viewHolder.mTextViewPercent.setText(p + "%");
            viewHolder.mTextViewSpeed.setText("下载速度:" + (ExtUtils.isEmpty(mList.get(position).speed) ? 0 : mList.get(position).speed) + "kb/s");

            if (mList.get(position).status == 1) {
                if ("game".equals(mList.get(position).type)) {
                    //判断是否已经安装
                    // 0代表未安装，1代表已经安装
                    int isInstall = 0;
                    for (String packageName : BaseApplication.getInstance().getAppList()) {
                        if (packageName.equals(mList.get(position).packageName)) {
                            //已经安装
                            isInstall = 1;
                            break;
                        }
                    }
                    if (isInstall == 0) {
                        //未安装 提示安装
                        viewHolder.mTextViewComplete.setText("下载完成");
                        viewHolder.mButtonControl.setBackground(getResources().getDrawable(R.mipmap.button_play));
                        viewHolder.mButtonControl.setText("安装");
                        viewHolder.mButtonControl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MobileUtils.installApk(DownloadActivity.this, mList.get(position).local_url + mList.get(position).fileName);
                            }
                        });
                    } else {
                        viewHolder.mTextViewComplete.setText("安装完成");
                        viewHolder.mButtonControl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //启动应用
                                MobileUtils.openApp(DownloadActivity.this, mList.get(position).packageName);
                            }
                        });
                    }
                } else {
                    viewHolder.mTextViewComplete.setText("下载完成");
                    viewHolder.mButtonControl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(DownloadActivity.this, SubUnityPlayerActivity.class).putExtra("url", "file://" + mList.get(position).local_url + mList.get(position).fileName));
                        }
                    });
                }

                viewHolder.mButtonControl.setVisibility(View.VISIBLE);
                viewHolder.mTextViewComplete.setVisibility(View.VISIBLE);
                viewHolder.mRelativeLayout.setVisibility(View.INVISIBLE);
            }

            if (viewHolder.mButtonDelete != null) {

                viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLoadingDialog.show();
                        if(mList.get(position).status != 1){
                            //未完成，结束下载线程
                            BaseApplication.getInstance().cancleLoaderByFileName(Md5Utils.MD5(mList.get(position).id + mList.get(position).title + mList.get(position).type));
                            BaseApplication.mFileService.delete(mList.get(position).url);
                        }

                        new Thread(){
                            @Override
                            public void run() {
                                //删除数据库记录
                                BaseApplication.mFileService.deleteDownload(mList.get(position)._id, mList.get(position).type);

                                //删除下载文件
                                File file = new File(mList.get(position).local_url + mList.get(position).fileName);
                                if(file.exists()) file.delete();
                                mList.remove(position);

                                refreshListHandler.sendEmptyMessage(0);
                            }
                        }.start();
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getFrontViewId(int position) {
            return R.layout.download_list_item;
        }

        @Override
        public int getLeftBackViewId(int position) {
            return 0;
        }

        @Override
        public int getRightBackViewId(int position) {
            return R.layout.download_item_right_back;
        }

        private class ViewHolder {
            Button mButtonDelete;
            Button mButtonControl;
            ImageView mImageView;
            TextView mTextViewTitle;
            TextView mTextViewPercent;
            TextView mTextViewSpeed;
            TextView mTextViewComplete;
            ProgressBar mProgressBar;
            RelativeLayout mRelativeLayout;
        }
    }

    public class ProgressBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handlerBroadcast(intent);
        }
    }

    private synchronized void handlerBroadcast(Intent intent) {
        DownloadParcel download = intent.getParcelableExtra("downloadParcel");
        if (download == null) return;

        Message msg = new Message();
        msg.what = download.isError;
        msg.obj = download;
        handler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        if (mProgressBroadcast != null)
            unregisterReceiver(mProgressBroadcast);
        super.onDestroy();
    }
}
