package com.qihuanyun.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.utils.ExtUtils;

/**
 * 网络连接状态改变接收器
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();

        if(ExtUtils.isEmpty(activeInfo)){
            //断网了，暂停下载任务
            if(BaseApplication.getInstance().getHandlerMap().entrySet().size() != 0){
                ExtUtils.shortToast(context,"网络中断，下载任务已暂停");
            }
            BaseApplication.getInstance().cancleAllLoaders();
        }else{
            //有网了,如果有暂停的任务，则开始下载
        }
    }
}
