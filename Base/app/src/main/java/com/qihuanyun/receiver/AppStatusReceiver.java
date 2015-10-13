package com.qihuanyun.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.utils.MobileUtils;

/**
 * 接收手机应用变化的广播接收器
 * 手机新增app
 * 手机删除app
 * 都会接收到广播，并做出处理
 */
public class AppStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //由于Receiver的声明周期很短，耗时操作在10秒以上会出现ANR，这里的操作，不能超过10秒
        /**
         * 这里会监听手机中的app新增和删除
         * 手机中每次app数量的变化，这里都会接受到通知
         * 当接收到通知后，重新获取appList
         */
        BaseApplication.getInstance().setAppList(MobileUtils.getAllApps(context));
    }
}
