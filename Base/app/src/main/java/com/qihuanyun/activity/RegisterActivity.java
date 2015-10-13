package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.romainpiel.shimmer.ShimmerTextView;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.utils.ExtUtils;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;
import com.wzl.vandan.dialog.VandaAlert;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivityActionBarNoNetWork implements View.OnClickListener {
    private TextView mTextViewRegisterProtocol;
    private TimeCount time;
    private Button mButtonMessageVerify;
    private Button mButtonNext;
    private EditText mEditTextMobile;
    private EditText mEditTextVerifyCode;
    private CheckBox mCheckBox;
    private Dialog mLoadingDialog;
    private String getMessageMobile;
    private Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private Matcher m;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.register);

        setTitle("注册");

        initViews();
    }

    private void initViews() {
        mButtonNext = (Button) findViewById(R.id.next);
        mCheckBox = (CheckBox) findViewById(R.id.check);
        mEditTextMobile = (EditText) findViewById(R.id.et_mobile);
        mEditTextVerifyCode = (EditText) findViewById(R.id.et_verify_code);
        mButtonMessageVerify = (Button) findViewById(R.id.get_verify_code);
        mTextViewRegisterProtocol = (TextView) findViewById(R.id.tv_register_protocol);
        mTextViewRegisterProtocol.setText(Html.fromHtml("<u>" + "注册协议" + "</u>"));

        mTextViewRegisterProtocol.setOnClickListener(this);
        mButtonMessageVerify.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);

        time = new TimeCount(60000, 1000);

        mLoadingDialog = VandaAlert.createLoadingDialog(this, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_text_menu, menu);
        View view = menu.findItem(R.id.right_text_menu).getActionView();
        if (view != null) {
            ShimmerTextView mShimmerTextView = (ShimmerTextView) view
                    .findViewById(R.id.tv_shimmer);
            mShimmerTextView.setTextColor(Color.parseColor("#2c69fe"));
            mShimmerTextView.setText("登录");
            mShimmerTextView.setTextSize(18f);
        }
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_verify_code:
                checkMobileExitsAndSendMessage();
                break;
            case R.id.tv_register_protocol:
                startActivity(new Intent(this, ProtocolActivity.class));
                break;
            case R.id.next:
                next();
                break;
        }
    }

    /**
     * 下一步
     */
    private void next() {
        if(ExtUtils.isEmpty(getMessageMobile)){
            ExtUtils.shortToast(this, "请先获取验证码");
            return;
        }

        if(ExtUtils.isEmpty(mEditTextMobile.getText().toString())){
            ExtUtils.shortToast(this, "请先获取验证码");
            return;
        }

        //校验号码是否有改变
        if(! getMessageMobile.equals(mEditTextMobile.getText().toString())){
            ExtUtils.shortToast(this, "号码改变，请重新获取手机号码");
            return;
        }

        if(ExtUtils.isEmpty(mEditTextVerifyCode.getText().toString())){
            ExtUtils.shortToast(this, "请填写验证码");
            return;
        }

        if(!mCheckBox.isChecked()){
            ExtUtils.shortToast(this, "请阅读注册协议");
            return;
        }

        mLoadingDialog.show();
        Map<String,String> map = new HashMap<>();
        map.put("mobile",mEditTextMobile.getText().toString());
        map.put("verifyCode",mEditTextVerifyCode.getText().toString());

        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/checkverify", CommonData.class, map, "next",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            startActivity(new Intent(RegisterActivity.this, CompleteRegisterActivity.class).putExtra("mobile", mEditTextMobile.getText().toString()));
                            finish();
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("code_expired".equals(response.error)) {
                                ExtUtils.shortToast(RegisterActivity.this, "验证码失效，请重新获取");
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
        RequestManager.requestData(Request.Method.GET, Urls.URL_PREFIX + "/is_exitsmobile?mobile=" + mEditTextMobile.getText().toString(), CommonData.class, null, "mobile_exits",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            if ("passport_exists".equals(response.msg)) {
                                //号码存在
                                ExtUtils.shortToast(RegisterActivity.this, "该号码已存在");
                                mLoadingDialog.dismiss();
                                return;
                            } else {
                                //号码不存在,可以发信息
                                sendMessage();
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

    private void sendMessage() {
        Map<String,String> map = new HashMap<>();
        map.put("mobile", mEditTextMobile.getText().toString());
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/verify-code", CommonData.class, map, "send_message",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            ExtUtils.shortToast(RegisterActivity.this, "验证短信已经发送，请耐心等候");
                            getMessageMobile = mEditTextMobile.getText().toString();
                            time.start();
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("illegal_value".equals(response.error)) {
                                ExtUtils.shortToast(RegisterActivity.this, "手机号码格式错误，请重新输入");
                            } else if ("no_respose".equals(response.error)) {
                                ExtUtils.shortToast(RegisterActivity.this, "系统出错，请稍后再试");
                            } else if ("send_fail".equals(response.error)) {
                                ExtUtils.shortToast(RegisterActivity.this, "短信发送失败，请稍候再试");
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
            mButtonMessageVerify.setText("(" + millisUntilFinished / 1000 + ")重新发送");
        }
    }

    /**
     * 重置验证码的按钮
     */
    public void resetVerifyButton() {
        time.cancel();
        mButtonMessageVerify.setBackgroundResource(R.mipmap.verify_button_normal);
        mButtonMessageVerify.setTextColor(getResources().getColor(android.R.color.white));
        mButtonMessageVerify.setText("发送验证码");
        mButtonMessageVerify.setClickable(true);
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
