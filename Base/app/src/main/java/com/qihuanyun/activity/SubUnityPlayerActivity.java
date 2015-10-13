package com.qihuanyun.activity;

import android.os.Bundle;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.mobilevr.video.UnityPlayerActivity;

/**
 * 继承UnityPlayerActivity，实现单例
 */
public class SubUnityPlayerActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.mSubUnityPlayerActivity = this;
    }
}
