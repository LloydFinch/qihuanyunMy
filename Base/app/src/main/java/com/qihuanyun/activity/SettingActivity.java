package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qihuanyun.R;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;
import com.vanda.vandalibnetwork.staticdata.StaticData;
import com.wzl.vandan.dialog.VandaAlert;

public class SettingActivity extends BaseActivityActionBarNoNetWork implements View.OnClickListener{

    private Button mButtonLogout;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting);

        setTitle("设置");

        mButtonLogout = (Button) findViewById(R.id.logout);
        mButtonLogout.setOnClickListener(this);
        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");

        if(StaticData.sp.contains("MagicLogin"))
            mButtonLogout.setVisibility(View.VISIBLE);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        mLoadingDialog.show();
        RequestManager.myCookieStore.clear();
        SharedPreferences.Editor et = StaticData.sp.edit();
        et.remove("MagicLogin");
        et.commit();
        mLoadingDialog.dismiss();
        finish();
    }
}
