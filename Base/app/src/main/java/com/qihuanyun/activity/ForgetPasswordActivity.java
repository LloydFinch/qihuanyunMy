package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.qihuanyun.pojo.CommonData;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends BaseActivityActionBarNoNetWork implements View.OnClickListener{
    private TimeCount time;
    private Button mButtonMessageVerify;
    private Button mButtonSubmit;
    private EditText mEditTextMobile;
    private EditText mEditTextPassword;
    private EditText mEditTextCode;
    private CheckBox mCheckBox;
    private Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private Matcher m;
    private Dialog mLoadingDialog;
    private String getMessageMobile;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.forget_password);

        setTitle("忘记密码");

        initViews();
    }

    private void initViews() {
        mEditTextMobile = (EditText) findViewById(R.id.et_mobile);
        mEditTextPassword = (EditText) findViewById(R.id.et_password);
        mEditTextCode = (EditText) findViewById(R.id.et_verify_code);
        mButtonMessageVerify = (Button) findViewById(R.id.get_verify_code);
        mButtonSubmit = (Button) findViewById(R.id.submit);
        mButtonMessageVerify.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");

        time = new TimeCount(60000, 1000);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_verify_code:
                checkMobileExitsAndSendMessage();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void checkMobileExitsAndSendMessage() {
        //参数判断无误之后再提交
        if (ExtUtils.isEmpty(mEditTextMobile.getText().toString())) {
            ExtUtils.shortToast(this, "请输入手机号码");
            return;
        }
        //校验手机号码的格式
        m = p.matcher(mEditTextMobile.getText().toString());
        if (!m.matches()) {
            ExtUtils.shortToast(this, "请输入正确的手机号码");
            return;
        }

        mLoadingDialog.show();
        Map<String,String> map = new HashMap<>();
        map.put("mobile", mEditTextMobile.getText().toString());
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/verify-code", CommonData.class, map, "send_message",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            ExtUtils.shortToast(ForgetPasswordActivity.this, "验证短信已经发送，请耐心等候");
                            getMessageMobile = mEditTextMobile.getText().toString();
                            time.start();
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("illegal_value".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "手机号码格式错误，请重新输入");
                            } else if ("no_respose".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "系统出错，请稍后再试");
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

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            resetVerifyButton();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mButtonMessageVerify.setBackgroundResource(R.mipmap.verify_button_selected);
            mButtonMessageVerify.setTextColor(getResources().getColor(R.color.common_text_color));
            mButtonMessageVerify.setClickable(false);
            mButtonMessageVerify.setText("("+millisUntilFinished / 1000 + ")重新发送");
        }
    }

    public void resetVerifyButton(){
        time.cancel();
        mButtonMessageVerify.setBackgroundResource(R.mipmap.verify_button_normal);
        mButtonMessageVerify.setTextColor(getResources().getColor(android.R.color.white));
        mButtonMessageVerify.setText("发送验证码");
        mButtonMessageVerify.setClickable(true);
    }

    private void submit(){
        if(ExtUtils.isEmpty(getMessageMobile)){
            ExtUtils.shortToast(this,"请先获取验证码");
            return;
        }else{
            if(! getMessageMobile.equals(mEditTextMobile.getText().toString())){
                ExtUtils.shortToast(this,"提交的号码不一致，请重新获取验证码");
                return;
            }
        }

        if(ExtUtils.isEmpty(mEditTextCode.getText().toString())){
            ExtUtils.shortToast(this,"请输入验证码");

            return;
        }

        if(ExtUtils.isEmpty(mEditTextPassword.getText().toString())){
            ExtUtils.shortToast(this,"请输入密码");
            return;
        }
        if(mEditTextPassword.getText().toString().length() < 6){
            ExtUtils.shortToast(this,"密码最少6个字符");
            return;
        }

        mLoadingDialog.show();
        Map<String,String> map = new HashMap<>();
        map.put("account", mEditTextMobile.getText().toString());
        map.put("verifyCode", mEditTextCode.getText().toString());
        map.put("pwd", mEditTextPassword.getText().toString());
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/reset-pwd", RegisterData.class, map, "reset-pwd",
                new Response.Listener<RegisterData>() {
                    @Override
                    public void onResponse(RegisterData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            ExtUtils.shortToast(ForgetPasswordActivity.this, "密码修改成功");
                            finish();
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("code_expired".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "验证码失效");
                                return;
                            }
                            if ("weak_password".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "密码过于简单");
                                return;
                            }
                            if ("no_record".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "该账号不存在");
                                return;
                            }
                            if ("locked_passport".equals(response.error)) {
                                ExtUtils.shortToast(ForgetPasswordActivity.this, "该账号已被锁");
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
