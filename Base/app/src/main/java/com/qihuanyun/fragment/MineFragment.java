package com.qihuanyun.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qihuanyun.R;
import com.qihuanyun.activity.DownloadActivity;
import com.qihuanyun.activity.LoaclVideoActivity;
import com.qihuanyun.activity.LoginActivity;
import com.qihuanyun.activity.ModifyPasswordActivity;
import com.qihuanyun.activity.MyCollectionActivity;
import com.qihuanyun.activity.RegisterActivity;
import com.qihuanyun.activity.SettingActivity;
import com.vanda.vandalibnetwork.staticdata.StaticData;

public class MineFragment extends Fragment {
    private Context mContext;
    private Button loginButton;
    private Button RegisterButton;
    private LinearLayout mLinearLayoutLogined;
    private RelativeLayout mRelativeLayoutNotLogin;
    private TextView mTextViewMobile;
    private LinearLayout mLinearLayoutChangePassword;

    private LinearLayout downLoadClickLayout,localVideLayout,myCollectLayout,settingLayout;
    private ImageView listClickImage1,listClickImage2,listClickImage3,listClickImage4;
    public static MineFragment newInstance(Context context) {
        MineFragment newFragment = new MineFragment();
        newFragment.mContext = context;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine, null);
        loginButton = (Button) view.findViewById(R.id.button_login);
        RegisterButton = (Button) view.findViewById(R.id.button_sign);

        mLinearLayoutLogined = (LinearLayout) view.findViewById(R.id.layout_have_logined);
        mRelativeLayoutNotLogin = (RelativeLayout) view.findViewById(R.id.layout_not_login);
        mTextViewMobile = (TextView) view.findViewById(R.id.text_user_phone);

        downLoadClickLayout = (LinearLayout) view.findViewById(R.id.layout_local_download);
        localVideLayout = (LinearLayout) view.findViewById(R.id.layout_local_video);
        myCollectLayout = (LinearLayout) view.findViewById(R.id.layout_mycollection);
        settingLayout = (LinearLayout) view.findViewById(R.id.layout_setting);
        mLinearLayoutChangePassword = (LinearLayout) view.findViewById(R.id.button_changePW);

        listClickImage1 = (ImageView) view.findViewById(R.id.list_click_image1);
        listClickImage2 = (ImageView) view.findViewById(R.id.list_click_image2);
        listClickImage3 = (ImageView) view.findViewById(R.id.list_click_image3);
        listClickImage4 = (ImageView) view.findViewById(R.id.list_click_image4);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
        downLoadClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listClickImage2.setVisibility(View.INVISIBLE);
                listClickImage1.setVisibility(View.VISIBLE);
                listClickImage3.setVisibility(View.INVISIBLE);
                listClickImage4.setVisibility(View.INVISIBLE);
                startActivity(new Intent(mContext, DownloadActivity.class));
            }
        });
        localVideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listClickImage2.setVisibility(View.VISIBLE);
                listClickImage1.setVisibility(View.INVISIBLE);
                listClickImage3.setVisibility(View.INVISIBLE);
                listClickImage4.setVisibility(View.INVISIBLE);
                startActivity(new Intent(mContext, LoaclVideoActivity.class));
            }
        });
        myCollectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listClickImage2.setVisibility(View.INVISIBLE);
                listClickImage1.setVisibility(View.INVISIBLE);
                listClickImage3.setVisibility(View.VISIBLE);
                listClickImage4.setVisibility(View.INVISIBLE);
                mContext.startActivity(new Intent(mContext, MyCollectionActivity.class));
            }
        });
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listClickImage2.setVisibility(View.INVISIBLE);
                listClickImage1.setVisibility(View.INVISIBLE);
                listClickImage3.setVisibility(View.INVISIBLE);
                listClickImage4.setVisibility(View.VISIBLE);
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
            }
        });
        mLinearLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ModifyPasswordActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(StaticData.sp.contains("MagicLogin")){
            mTextViewMobile.setText(StaticData.sp.getString("mobile",""));
            mLinearLayoutLogined.setVisibility(View.VISIBLE);
            mRelativeLayoutNotLogin.setVisibility(View.GONE);
        }else{
            mLinearLayoutLogined.setVisibility(View.GONE);
            mRelativeLayoutNotLogin.setVisibility(View.VISIBLE);
        }
    }
}
