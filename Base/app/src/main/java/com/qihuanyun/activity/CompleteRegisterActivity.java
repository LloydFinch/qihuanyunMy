package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.RegisterData;
import com.qihuanyun.utils.ExtUtils;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;
import com.vanda.vandalibnetwork.staticdata.StaticData;
import com.wzl.vandan.dialog.VandaAlert;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegisterActivity extends BaseActivityActionBarNoNetWork{
    private String mobile;
    private EditText mEditTextNickname;
    private EditText mEditTextPassword;
    private CheckBox mCheckBox;
    private Button mButton;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.complete_register);

        setTitle("设定密码");
        mobile = getIntent().getStringExtra("mobile");

        initViews();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initViews() {
        mButton = (Button) findViewById(R.id.complete);
        mEditTextNickname = (EditText) findViewById(R.id.et_nickname);
        mEditTextPassword = (EditText) findViewById(R.id.et_password);
        mCheckBox = (CheckBox) findViewById(R.id.visible);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mEditTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }

                if (ExtUtils.isNotEmpty(mEditTextPassword.getText().toString()))
                    mEditTextPassword.setSelection(mEditTextPassword.getText().toString().length());
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");
    }

    private void submit(){
        //判空
        if(ExtUtils.isEmpty(mEditTextNickname.getText().toString())){
            ExtUtils.shortToast(this,"请输入昵称");
            return;
        }
        if(ExtUtils.isEmpty(mEditTextPassword.getText().toString())){
            ExtUtils.shortToast(this,"请输入密码");
            return;
        }
        //判断长度
        if(mEditTextNickname.getText().toString().length() < 2){
            ExtUtils.shortToast(this,"昵称最少2个字符");
            return;
        }
        if(mEditTextPassword.getText().toString().length() < 6){
            ExtUtils.shortToast(this,"密码最少6个字符");
            return;
        }

        mLoadingDialog.show();
        Map<String,String> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("name",mEditTextNickname.getText().toString());
        map.put("pwd",mEditTextPassword.getText().toString());
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/register", RegisterData.class, map, "register",
                new Response.Listener<RegisterData>() {
                    @Override
                    public void onResponse(RegisterData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            SharedPreferences.Editor et = StaticData.sp.edit();
                            et.putInt("userId", response.data.userId);
                            et.putString("mobile", response.data.mobile);
                            et.putString("custName", response.data.custName);
                            et.putString("token", response.data.token);
                            et.commit();

                            if (RequestManager.CURRENT_CONTEXT != null) {
                                CookieStore store = (CookieStore) RequestManager.CURRENT_CONTEXT
                                        .getAttribute(ClientContext.COOKIE_STORE);
                                if (store != null)
                                    System.out.println("cookieStore:" + store.toString());
                                for (Cookie cookie : store.getCookies()) {
                                    RequestManager.myCookieStore.addCookie(cookie);
                                }
                                SharedPreferences.Editor et1 = StaticData.sp.edit();
                                et1.putBoolean("MagicLogin", true);
                                et1.commit();
                            }

                            startActivity(new Intent(CompleteRegisterActivity.this,MainActivity.class));
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("illegal_mobile".equals(response.error)) {
                                ExtUtils.shortToast(CompleteRegisterActivity.this, "电话号码错误");
                                return;
                            }
                            if ("weak_password".equals(response.error)) {
                                ExtUtils.shortToast(CompleteRegisterActivity.this, "密码过于简单");
                                return;
                            }
                            if ("passport_exists".equals(response.error)) {
                                ExtUtils.shortToast(CompleteRegisterActivity.this, "该账号已存在");
                                return;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mLoadingDialog.dismiss();
                        ExtUtils.errorLog("-->VolleyError", "" + error.toString());
                    }
                });

    }
}
