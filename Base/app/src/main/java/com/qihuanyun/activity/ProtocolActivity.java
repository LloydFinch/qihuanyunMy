package com.qihuanyun.activity;

import android.os.Bundle;

import com.qihuanyun.R;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;

public class ProtocolActivity extends BaseActivityActionBarNoNetWork{

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.protocol);

        setTitle("注册协议");
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
