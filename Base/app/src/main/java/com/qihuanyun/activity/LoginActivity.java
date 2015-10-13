package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.romainpiel.shimmer.ShimmerTextView;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.pojo.RegisterData;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.views.LoginErrorDialog;
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

public class LoginActivity extends BaseActivityActionBarNoNetWork implements View.OnClickListener {

    private TextView mTextViewForgetPassword;
    private Button mButtonLogin;
    private EditText mEditTextMobile, mEditTextPassword;
    private Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private Matcher m;
    private Dialog mLoadingDialog;
    private int tag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tag = getIntent().getIntExtra("tag",0);

        setTitle("登录");

        initViews();
    }

    private void initViews() {
        mTextViewForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mButtonLogin = (Button) findViewById(R.id.login);
        mEditTextMobile = (EditText) findViewById(R.id.et_mobile);
        mEditTextPassword = (EditText) findViewById(R.id.et_password);
        mTextViewForgetPassword.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);

        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");

        if (StaticData.sp.contains("mobile")) {
            mEditTextMobile.setText(StaticData.sp.getString("mobile",""));
            mEditTextMobile.setSelection(StaticData.sp.getString("mobile","").length());
        }
        mEditTextMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //失去焦点的时候，判断号码是否存在
                    //先判断号码是否格式正确，如果正确了，才发起请求去查询
                    if(ExtUtils.isEmpty(mEditTextMobile.getText().toString())) return;
                    m = p.matcher(mEditTextMobile.getText().toString());
                    if (!m.matches()) return;

                    checkMobile();
                }
            }
        });
    }

    /**
     * 检查号码是否存在
     */
    private void checkMobile() {
        RequestManager.requestData(Request.Method.GET, Urls.URL_PREFIX + "/is_exitsmobile?mobile=" + mEditTextMobile.getText().toString(), CommonData.class, null, "mobile_exits",
                new Response.Listener<CommonData>() {
                    @Override
                    public void onResponse(CommonData response) {
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            if ("passport_not_exists".equals(response.msg)) {
                                //号码不存在，弹框
                                mobileNotExitErrorDialog();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ExtUtils.errorLog("-->VolleyError", "" + error.toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_text_menu, menu);
        View view = menu.findItem(R.id.right_text_menu).getActionView();
        if (view != null) {
            ShimmerTextView mShimmerTextView = (ShimmerTextView) view
                    .findViewById(R.id.tv_shimmer);
            mShimmerTextView.setTextColor(Color.parseColor("#2c69fe"));
            mShimmerTextView.setText("注册");
            mShimmerTextView.setTextSize(18f);
        }
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forget_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.login:
                login();
                break;
        }
    }

    private void login() {
        if(ExtUtils.isEmpty(mEditTextMobile.getText().toString())){
            ExtUtils.shortToast(this,"请输入手机号");
            return;
        }
        if(ExtUtils.isEmpty(mEditTextPassword.getText().toString())){
            ExtUtils.shortToast(this,"请输入密码");
            return;
        }
        mLoadingDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mEditTextMobile.getText().toString());
        map.put("pwd", mEditTextPassword.getText().toString());

        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/login", RegisterData.class, map, "login",
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

                            ExtUtils.shortToast(LoginActivity.this, "登陆成功");
                            if(tag == 1){
                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            switch (response.error) {
                                case "insufficient_params":
                                    loginFailDialog("登陆失败，请重新尝试");
                                    break;
                                case "bad_request":
                                    loginFailDialog("登陆失败，请重新尝试");
                                    break;
                                case "no_record":
                                    mobileNotExitErrorDialog();
                                    break;
                                case "locked_passport":
                                    loginFailDialog("账号已被锁定，请稍候再试");
                                    break;
                                case "incorrect_password":
                                    passwordInCorrectDialog();
                                    break;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mLoadingDialog.dismiss();
                        loginFailDialog("登陆失败，请重新尝试");
                        ExtUtils.errorLog("-->VolleyError", "" + error.toString());
                    }
                });
    }

    private void loginFailDialog(String error) {
        Dialog dialog = LoginErrorDialog.CreateSingleDialog(LoginActivity.this, error, "确定", new LoginErrorDialog.SingleOnClickListener() {
            @Override
            public void onSingleClickListener(Dialog mDialog) {
                mDialog.dismiss();
            }
        });
        dialog.show();
    }

    private void mobileNotExitErrorDialog() {
        Dialog dialog = LoginErrorDialog.CreatePolesDialog(LoginActivity.this, "您输入的手机号码尚未注册，点击确定进入注册流程", "取消", "确定", new LoginErrorDialog.PolesOnClickListener() {
            @Override
            public void onPositiveClickListener(Dialog mDialog) {
                mDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }

            @Override
            public void onNegativeClickListener(Dialog mDialog) {
                mDialog.dismiss();
            }
        });
        dialog.show();
    }

    private void passwordInCorrectDialog() {
        Dialog dialog = LoginErrorDialog.CreatePolesDialog(LoginActivity.this, "用户名或密码错误，请重试", "找回密码", "重新输入", new LoginErrorDialog.PolesOnClickListener() {
            @Override
            public void onPositiveClickListener(Dialog mDialog) {
                mDialog.dismiss();
                mEditTextPassword.setText("");
            }

            @Override
            public void onNegativeClickListener(Dialog mDialog) {
                mDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        dialog.show();
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
