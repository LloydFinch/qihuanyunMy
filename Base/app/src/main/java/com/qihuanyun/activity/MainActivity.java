package com.qihuanyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.fragment.ChoiceFragment;
import com.qihuanyun.fragment.GameFragment;
import com.qihuanyun.fragment.MineFragment;
import com.qihuanyun.fragment.ThemeFragment;
import com.qihuanyun.fragment.VideoFragment;
import com.qihuanyun.utils.ExtUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mChoiceFragment;
    private Fragment mThemeFragment;
    private Fragment mGameFragment;
    private Fragment mVideoFragment;
    private Fragment mMineFragment;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.main;
    }

    /**
     * 防止crash
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_icons_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_collect) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }
        if (item.getItemId() == R.id.menu_download) {
            startActivity(new Intent(MainActivity.this, DownloadActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        mChoiceFragment = ChoiceFragment.newInstance(this);
        mThemeFragment = ThemeFragment.newInstance(this);
        mGameFragment = GameFragment.newInstance(this);
        mVideoFragment = VideoFragment.newInstance(this);
        mMineFragment = MineFragment.newInstance(this);

        mFragmentTransaction.add(R.id.main_content, mChoiceFragment);
        mFragmentTransaction.add(R.id.main_content, mThemeFragment);
        mFragmentTransaction.add(R.id.main_content, mGameFragment);
        mFragmentTransaction.add(R.id.main_content, mVideoFragment);
        mFragmentTransaction.add(R.id.main_content, mMineFragment);
        mFragmentTransaction.show(mChoiceFragment);
        mFragmentTransaction.hide(mThemeFragment);
        mFragmentTransaction.hide(mGameFragment);
        mFragmentTransaction.hide(mVideoFragment);
        mFragmentTransaction.hide(mMineFragment);
        mFragmentTransaction.commit();

        mRadioGroup = (RadioGroup) findViewById(R.id.main_radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int position) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                switch (position) {
                    case R.id.radio_choice:
                        controlFragments(transaction, 1);
                        break;
                    case R.id.radio_theme:
                        controlFragments(transaction, 2);
                        break;
                    case R.id.radio_game:
                        controlFragments(transaction, 3);
                        break;
                    case R.id.radio_video:
                        controlFragments(transaction, 4);
                        break;
                    case R.id.radio_mine:
                        controlFragments(transaction, 5);
                        break;
                }
            }
        });
    }

    /**
     * 底部菜单点击控制
     * @param transaction
     * @param position
     */
    private void controlFragments(FragmentTransaction transaction, int position) {
        switch (position) {
            case 1:
                transaction.show(mChoiceFragment);
                transaction.hide(mThemeFragment);
                transaction.hide(mGameFragment);
                transaction.hide(mVideoFragment);
                transaction.hide(mMineFragment);
                break;
            case 2:
                transaction.show(mThemeFragment);
                transaction.hide(mChoiceFragment);
                transaction.hide(mGameFragment);
                transaction.hide(mVideoFragment);
                transaction.hide(mMineFragment);
                break;
            case 3:
                transaction.show(mGameFragment);
                transaction.hide(mThemeFragment);
                transaction.hide(mChoiceFragment);
                transaction.hide(mVideoFragment);
                transaction.hide(mMineFragment);
                break;
            case 4:
                transaction.show(mVideoFragment);
                transaction.hide(mThemeFragment);
                transaction.hide(mChoiceFragment);
                transaction.hide(mGameFragment);
                transaction.hide(mMineFragment);
                break;
            case 5:
                transaction.show(mMineFragment);
                transaction.hide(mThemeFragment);
                transaction.hide(mChoiceFragment);
                transaction.hide(mVideoFragment);
                transaction.hide(mGameFragment);
                break;
        }

        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将当前正在下载的电影和apk，全部标注为暂停状态
        BaseApplication.getInstance().cancleAllLoaders();
        //将播放实例关闭
        if (ExtUtils.isNotEmpty(BaseApplication.mSubUnityPlayerActivity))
            BaseApplication.mSubUnityPlayerActivity.mUnityPlayer.quit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitBy2Click();
        }
        return false;
    }

    private static Boolean isExit = false;

    /**
     * 双击退出
     */
    private void exitBy2Click() {
        Timer tExit = null;

        if (isExit == false) {
            isExit = true; // 准备退出
            ExtUtils.shortToast(this, " 再按一次退出程序 ");
            tExit = new Timer();

            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
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
}
