package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.IndexData;
import com.qihuanyun.pojo.ThemeDetailData;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.ItemWorker;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseFragmentActivity;
import com.vanda.vandalibnetwork.staticdata.StaticData;
import com.wzl.vandan.dialog.VandaAlert;

import java.util.Map;

public class ThemeDetailActivity extends BaseFragmentActivity<ThemeDetailData> {
    private int themeId;
    private ImageView mImageViewImage;
    private TextView mTextViewTitle;
    private TextView mTextViewSummary;
    private LinearLayout mLinearLayoutThemeRecommendContainer;
    private static Drawable mDefaultImageDrawable;
    private Dialog mLoadingDialog;
    private TextView mTextViewContentKey;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.theme_detail);

        setTitle("返回");

        themeId = getIntent().getIntExtra("themeId", -1);

        initViews();

        startExecuteRequest(Request.Method.GET);
        mLoadingDialog.show();
    }

    private void initViews() {
        mImageViewImage = (ImageView) findViewById(R.id.image);
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewSummary = (TextView) findViewById(R.id.tv_summary);
        mTextViewContentKey = (TextView) findViewById(R.id.tv_content_key);
        mLinearLayoutThemeRecommendContainer = (LinearLayout) findViewById(R.id.ll_theme_recommend_container);

        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");

        mDefaultImageDrawable = getResources().getDrawable(R.mipmap.theme_detail_default);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_icons_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_collect) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        if (item.getItemId() == R.id.menu_download) {
            startActivity(new Intent(this, DownloadActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void processData(ThemeDetailData response) {
        super.processData(response);
        if (response != null & response.data != null) {
            ViewGroup.LayoutParams params = mImageViewImage.getLayoutParams();
            params.width = StaticData.ScreenWidth;
            params.height = params.width/ 2;
            RequestManager.loadImage(
                    Urls.IMAGE_PREFIX + response.data.imgUrl,
                    RequestManager.getImageListener(mImageViewImage, 0,
                            mDefaultImageDrawable, mDefaultImageDrawable));

            mTextViewTitle.setText(response.data.title);
            if(ExtUtils.isNotEmpty(response.data.summary))
                mTextViewSummary.setText("主题馆介绍：" + response.data.summary);

            mTextViewContentKey.setVisibility(View.VISIBLE);

            if (response.data.tdList != null && response.data.tdList.size() != 0) {
                for (final IndexData.VideoGame recommand : response.data.tdList) {
                    View view = null;
                    if (recommand.type == 2) {
                        //电影
                        view = LayoutInflater.from(this).inflate(R.layout.video_list_item, null);
                        ItemWorker.videoItem(this, view, recommand);
                    } else if (recommand.type == 3) {
                        //游戏
                        view = LayoutInflater.from(this).inflate(R.layout.game_list_item, null);
                        ItemWorker.gameItem(this, view, recommand);
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (recommand.type == 2) {
                                startActivity(new Intent(ThemeDetailActivity.this, VideoDetailActivity.class).putExtra("videoId", recommand.id));
                            } else {
                                startActivity(new Intent(ThemeDetailActivity.this, GameDetailActivity.class).putExtra("id", recommand.id));
                            }
                        }
                    });

                    mLinearLayoutThemeRecommendContainer.addView(view);
                }
            }
        }
        mLoadingDialog.dismiss();
    }

    @Override
    protected void errorData(VolleyError volleyError) {
        super.errorData(volleyError);
        mLoadingDialog.dismiss();
        ExtUtils.errorLog("-->volleyError", "" + volleyError);
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX + "/theme-detail?themeId=" + themeId;
    }

    @Override
    protected Class<ThemeDetailData> getResponseDataClass() {
        return ThemeDetailData.class;
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
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
