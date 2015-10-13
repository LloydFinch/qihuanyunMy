package com.qihuanyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.MobileUtils;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.staticdata.StaticData;

public class SplashActivity extends FragmentActivity {

    private static final String FIRST_LOGIN_FLAG = "isTheFirstTimeToLogin";
    private Boolean isTheFirstTimeToLogin;
    private static final long sleepTime = 2000;
    private String fileLinkUrl;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {

                if (ExtUtils.isNotEmpty(fileLinkUrl)) {
                    startActivity(new Intent(SplashActivity.this, SubUnityPlayerActivity.class).putExtra("url", "file://"+fileLinkUrl));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (ExtUtils.isNotEmpty(getIntent().getData())) {
            fileLinkUrl = getIntent().getData().getPath();
        }

        isTheFirstTimeToLogin = StaticData.sp.getBoolean(FIRST_LOGIN_FLAG, true);

        new Thread() {
            @Override
            public void run() {
                try {
                    long before = System.currentTimeMillis();

                    /**
                     * 获取下载记录
                     * 获取当前手机中非预装程序
                     */
                    BaseApplication.getInstance().setDownloadMovieRecordList(BaseApplication.mFileService.getDownloadRecordsByType("movie"));
                    BaseApplication.getInstance().setDownloadGameRecordList(BaseApplication.mFileService.getDownloadRecordsByType("game"));
                    BaseApplication.getInstance().setAppList(MobileUtils.getAllApps(SplashActivity.this));

                    long after = System.currentTimeMillis();

                    if ((after - before) < sleepTime) {
                        Thread.sleep(sleepTime - (after - before));
                    }

                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
