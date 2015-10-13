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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.pojo.IndexData;
import com.qihuanyun.pojo.VideoDetailData;
import com.qihuanyun.utils.DetailWorker;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.ItemWorker;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragmentactivity.BaseFragmentActivity;
import com.vanda.vandalibnetwork.staticdata.StaticData;
import com.wzl.vandan.dialog.VandaAlert;

import java.util.ArrayList;
import java.util.Map;

public class VideoDetailActivity extends BaseFragmentActivity<VideoDetailData>{

    private int videoId;
    private ImageView mImageViewTop;
    private TextView mTextViewTitle;
    private TextView mTextViewDownloadAndPlayCount;
    private TextView mTextViewSize;
    private LinearLayout mLinearLayoutCollect;
    private LinearLayout mLinearLayoutDownload;
    private LinearLayout mLinearLayoutPlay;
    private TextView mTextViewSummary;
    private LinearLayout mLinearLayoutImageContainer;
    private TextView mTextViewDetail;
    private LinearLayout mLinearLayoutRecommendContainer;
    private Dialog mLoadingDialog;
    private ScrollView mScrollView;
    private ImageView collectImage;
    private TextView collectText;
    private TextView mTextViewDetailKey;
    private TextView mTextViewRecommendKey;
    private LinearLayout mLinearLayoutButtonsController;
    private HorizontalScrollView mHorizontalScrollView;
    private boolean iscollected;
    private static Drawable mDefaultImageDrawable;
    private static Drawable mDefaultImageDrawableScroll;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.video_detail);
        setTitle("返回");

        videoId = getIntent().getIntExtra("videoId",0);
        
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startExecuteRequest(Request.Method.GET);
        mLoadingDialog.show();
        MobclickAgent.onResume(this);
    }

    private void initViews() {
        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mImageViewTop = (ImageView) findViewById(R.id.image);
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewDownloadAndPlayCount = (TextView) findViewById(R.id.download_play_times_value);
        mTextViewSize = (TextView) findViewById(R.id.size_value);
        mLinearLayoutCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mLinearLayoutDownload = (LinearLayout) findViewById(R.id.ll_download);
        mLinearLayoutPlay = (LinearLayout) findViewById(R.id.ll_play);
        mTextViewSummary = (TextView) findViewById(R.id.tv_summary);
        mLinearLayoutImageContainer = (LinearLayout) findViewById(R.id.ll_image_container);
        mTextViewDetail = (TextView) findViewById(R.id.tv_detail);
        mLinearLayoutRecommendContainer = (LinearLayout) findViewById(R.id.ll_recommend_container);
        mLinearLayoutButtonsController = (LinearLayout) findViewById(R.id.ll_buttons);
        collectImage = (ImageView) findViewById(R.id.collect_image);
        collectText = (TextView) findViewById(R.id.collect_textfont);
        mTextViewDetailKey = (TextView) findViewById(R.id.tv_detail_key);
        mTextViewRecommendKey = (TextView) findViewById(R.id.tv_recommend_key);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv);

        mDefaultImageDrawable = getResources().getDrawable(R.mipmap.list_default);
        mDefaultImageDrawableScroll = getResources().getDrawable(R.mipmap.detail_default);

        mLinearLayoutCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iscollected) {
                    undoCollect();
                } else {
                    doCollect();
                }
            }
        });
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
    protected void processData(VideoDetailData response) {
        if(response != null && response.data != null){
//            ViewGroup.LayoutParams params = mImageViewTop.getLayoutParams();
//            params.width = StaticData.ScreenWidth/3;
//            params.height = params.width;
            RequestManager.loadImage(
                    Urls.IMAGE_PREFIX + response.data.imgUrl,
                    RequestManager.getImageListener(mImageViewTop, 0,
                            mDefaultImageDrawable, mDefaultImageDrawable));
            mTextViewTitle.setText(response.data.title);
            mTextViewDownloadAndPlayCount.setText(response.data.uploadNum + "/" + response.data.playNum);

            mTextViewSize.setText(response.data.fileSize + "M");
            mTextViewDetail.setText(response.data.info);

            if(ExtUtils.isNotEmpty(response.data.summary)) {
                mTextViewSummary.setText("简介：" + response.data.summary);
                mTextViewSummary.setVisibility(View.VISIBLE);
            } else {
                mTextViewSummary.setVisibility(View.GONE);
            }

            if(ExtUtils.isNotEmpty(response.data.info)) {
                mTextViewDetail.setVisibility(View.VISIBLE);
                mTextViewDetailKey.setVisibility(View.VISIBLE);
            } else {
                mTextViewDetail.setVisibility(View.GONE);
                mTextViewDetailKey.setVisibility(View.GONE);
            }

            mTextViewRecommendKey.setVisibility(View.VISIBLE);

            iscollected = response.data.isCollect == 0 ? false : true;
            if (response.data.isCollect == 0){
                collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_heart));
                collectText.setText("收藏");

            } else {
                collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_collection));
                collectText.setText("取消");
            }

            if(response.data.tdList != null && response.data.tdList.size() != 0){
                mLinearLayoutRecommendContainer.removeAllViews();
                for(final IndexData.VideoGame recommand : response.data.tdList){
                    View view = LayoutInflater.from(this).inflate(R.layout.video_list_item,null);
                    ItemWorker.videoItem(this,view,recommand);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoId = recommand.id;
                            mLoadingDialog.show();
                            mScrollView.fullScroll(ScrollView.FOCUS_UP);
                            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            startExecuteRequest(Request.Method.GET);
                        }
                    });
                    mLinearLayoutRecommendContainer.addView(view);
                }
            }

            //controll buttons
            DetailWorker.videoDetailButtonController(this, mLinearLayoutButtonsController, response.data);

            //show scroll images
            mLinearLayoutImageContainer.removeAllViews();
            if(ExtUtils.isNotEmpty(response.data.imgList) && response.data.imgList.size() != 0)
                showScrollImages(response.data.imgList);
        }
        mLoadingDialog.dismiss();
        super.processData(response);
    }

    /**
     * 滚动图片的显示
     * @param imgList
     */
    private void showScrollImages(ArrayList<String> imgList) {
        for(String url : imgList){
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0,0,12,0);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(StaticData.ScreenWidth*2/3, StaticData.ScreenWidth*2/3);
            imageView.setLayoutParams(params);
            RequestManager.loadImage(
                    Urls.IMAGE_PREFIX + url,
                    RequestManager.getImageListener(imageView, 0,
                            mDefaultImageDrawableScroll, mDefaultImageDrawableScroll));
            mLinearLayoutImageContainer.addView(imageView);
        }
    }

    @Override
    protected void errorData(VolleyError volleyError) {
        super.errorData(volleyError);
        mLoadingDialog.dismiss();
        ExtUtils.errorLog("-->volleyError", "" + volleyError);
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX + "/theme-contentDetail?id=" + videoId;
    }

    @Override
    protected Class<VideoDetailData> getResponseDataClass() {
        return VideoDetailData.class;
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }

    /**
     * 收藏
     */
    public void doCollect(){
        String url = Urls.URL_PREFIX + "/theme-collect?id="+videoId;
        RequestManager.requestData(Request.Method.GET, url, CommonData.class, null, "collect"
                , new Response.Listener<CommonData>() {
            @Override
            public void onResponse(CommonData response) {
                if (response != null && response.msg != null) {
                    iscollected = true;
                    ExtUtils.shortToast(VideoDetailActivity.this, "收藏成功！");
                    collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_collection));
                    collectText.setText("取消");
                }
                if (response.error != null) {
                    switch (response.error) {
                        case "required_login":
                            ExtUtils.shortToast(VideoDetailActivity.this, "请您登陆！");
                            startActivity(new Intent(VideoDetailActivity.this, LoginActivity.class).putExtra("tag",1));
                            break;
                        case "failed":
                            ExtUtils.shortToast(VideoDetailActivity.this, "收藏失败！");
                            break;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.shortToast(VideoDetailActivity.this, "网络错误！");
            }
        });
    }

    /**
     * 取消收藏
     */
    public void undoCollect(){
        String url = Urls.URL_PREFIX + "/theme-descCollect?id="+videoId;
        RequestManager.requestData(Request.Method.GET, url, CommonData.class, null, "uncollect"
                , new Response.Listener<CommonData>() {
            @Override
            public void onResponse(CommonData response) {
                if (response != null && response.msg != null) {
                    iscollected = false;
                    ExtUtils.shortToast(VideoDetailActivity.this, "取消收藏成功！");
                    mLinearLayoutCollect.setBackground(getResources().getDrawable(R.mipmap.button_collection));
                    collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_heart));
                    collectText.setText("收藏");
                }
                if (response.error != null) {
                    switch (response.error) {
                        case "required_login":
                            ExtUtils.shortToast(VideoDetailActivity.this, "请您登陆！");
                            startActivity(new Intent(VideoDetailActivity.this,LoginActivity.class).putExtra("tag",1));
                            break;
                        case "failed":
                            ExtUtils.shortToast(VideoDetailActivity.this, "取消收藏失败！");
                            break;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.shortToast(VideoDetailActivity.this, "网络错误！");
            }
        });
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
