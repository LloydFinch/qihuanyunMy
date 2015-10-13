package com.qihuanyun.activity;

import android.os.Bundle;

import com.qihuanyun.R;
import com.qihuanyun.fragment.MyCollectionFragment;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;

public class MyCollectionActivity extends BaseActivityActionBarNoNetWork {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.collect);
        setTitle("我的收藏");

        initView();
    }

    private void initView() {
        android.support.v4.app.FragmentTransaction t = this.getSupportFragmentManager()
                .beginTransaction();
        MyCollectionFragment mMyCollectFragment = MyCollectionFragment.newInstance(this);
        t.replace(R.id.content, mMyCollectFragment);
        t.commit();
    }

    /**
     * 防止crash
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
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
