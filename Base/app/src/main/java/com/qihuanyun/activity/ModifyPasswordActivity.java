package com.qihuanyun.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class ModifyPasswordActivity extends BaseActivityActionBarNoNetWork{
    private EditText mEditTextOldPassword;
    private EditText mEditTextPassword;
    private TextView mTextViewMobile;
    private CheckBox mCheckBox;
    private Button mButton;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.modify_password);

        setTitle("修改密码");

        initViews();
    }

    private void initViews() {
        mButton = (Button) findViewById(R.id.complete);
        mEditTextOldPassword = (EditText) findViewById(R.id.et_old_password);
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

        mTextViewMobile = (TextView) findViewById(R.id.tv_mobile);
        if (StaticData.sp.contains("mobile")) {
            mTextViewMobile.setText(StaticData.sp.getString("mobile",""));
        }
    }

    private void submit(){
        //判空
        if(ExtUtils.isEmpty(mEditTextOldPassword.getText().toString())){
            ExtUtils.shortToast(this,"请输入原密码");
            return;
        }
        if(ExtUtils.isEmpty(mEditTextPassword.getText().toString())){
            ExtUtils.shortToast(this,"新密码不能为空！");
            return;
        }
        //判断长度
        if(mEditTextOldPassword.getText().toString().length() < 6){
            ExtUtils.shortToast(this,"原密码最少是6个字符！");
            return;
        }
        if(mEditTextPassword.getText().toString().length() < 6){
            ExtUtils.shortToast(this,"新密码最少6个字符！");
            return;
        }

        mLoadingDialog.show();
        Map<String,String> map = new HashMap<>();
        map.put("old", mEditTextOldPassword.getText().toString());
        map.put("pwd", mEditTextPassword.getText().toString());
        RequestManager.requestData(Request.Method.POST, Urls.URL_PREFIX + "/change-pwd", RegisterData.class, map, "change-pwd",
                new Response.Listener<RegisterData>() {
                    @Override
                    public void onResponse(RegisterData response) {
                        mLoadingDialog.dismiss();
                        if (response != null && ExtUtils.isNotEmpty(response.msg)) {
                            ExtUtils.shortToast(ModifyPasswordActivity.this, "密码修改成功！");

//                            if (RequestManager.CURRENT_CONTEXT != null) {
//                                CookieStore store = (CookieStore) RequestManager.CURRENT_CONTEXT
//                                        .getAttribute(ClientContext.COOKIE_STORE);
//                                if (store != null)
//                                    System.out.println("cookieStore:" + store.toString());
//                                for (Cookie cookie : store.getCookies()) {
//                                    RequestManager.myCookieStore.addCookie(cookie);
//                                }
//                                SharedPreferences.Editor et1 = StaticData.sp.edit();
//                                et1.putBoolean("MagicLogin", true);
//                                et1.commit();
//                            }
//
                            finish();
                        } else if (response != null && ExtUtils.isNotEmpty(response.error)) {
                            if ("locked_passport".equals(response.error)) {
                                ExtUtils.shortToast(ModifyPasswordActivity.this, "账号被锁！");
                                return;
                            }
                            if ("weak_password".equals(response.error)) {
                                ExtUtils.shortToast(ModifyPasswordActivity.this, "密码过于简单！");
                                return;
                            }
                            if ("incorrect_password".equals(response.error)) {
                                ExtUtils.shortToast(ModifyPasswordActivity.this, "原密码不正确！");
                                return;
                            }
                            if ("no_password".equals(response.error)) {
                                ExtUtils.shortToast(ModifyPasswordActivity.this, "没有输入密码！");
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideSoftInput(IBinder windowToken) {
        if(windowToken != null){
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }


}
