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
import com.qihuanyun.pojo.GameDetailData;
import com.qihuanyun.pojo.IndexData;
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

public class GameDetailActivity extends BaseFragmentActivity<GameDetailData>{

    private int id;
    private TextView textTitle,downloadTimes,textSize,downTexFont,collectFont;
    private ImageView headImage,collectImage,downloadImage;
    private LinearLayout collectLayout,downloadLayout;
    private TextView simpleIntroduction,detail;
    private ArrayList<ImageView> horizontalImages;
    private Dialog mLoadingDialog;
    private LinearLayout addContentLinearLayout;
    private LinearLayout mLinearLayoutImageContainer;
    private ScrollView mScrollView;
    private LinearLayout mLinearLayoutButtons;
    private HorizontalScrollView mHorizontalScrollView;
    private boolean isCollected;
    private TextView mTextViewDetailKey;
    private TextView mTextViewRecommendKey;
    private static Drawable mDefaultImageDrawable;
    private static Drawable mDefaultImageDrawableScroll;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.game_detail);
        id = getIntent().getIntExtra("id",0);
        setTitle("返回");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startExecuteRequest(Request.Method.GET);
        mLoadingDialog.show();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void initView(){
        mLoadingDialog = VandaAlert.createLoadingDialog(this,"");
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mLinearLayoutButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        textTitle = (TextView) findViewById(R.id.title);
        downloadTimes = (TextView) findViewById(R.id.download_play_times_value);
        textSize = (TextView) findViewById(R.id.size_value);
        collectFont = (TextView) findViewById(R.id.collect_textfont);
        downTexFont = (TextView) findViewById(R.id.dowload_textfont);

        mLinearLayoutImageContainer = (LinearLayout) findViewById(R.id.ll_image_container);
        collectLayout = (LinearLayout) findViewById(R.id.collect_button_layout);
        downloadLayout = (LinearLayout) findViewById(R.id.download_item_layout);

        simpleIntroduction = (TextView) findViewById(R.id.simple_introduce);
        detail = (TextView) findViewById(R.id.detail_introduce);

        headImage = (ImageView) findViewById(R.id.image);
        collectImage = (ImageView) findViewById(R.id.collect_image);
        downloadImage = (ImageView) findViewById(R.id.download_image);

        addContentLinearLayout = (LinearLayout) findViewById(R.id.recommend_addContent);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv);

        mDefaultImageDrawable = getResources().getDrawable(R.mipmap.list_default);
        mDefaultImageDrawableScroll = getResources().getDrawable(R.mipmap.detail_default);

        mTextViewDetailKey = (TextView) findViewById(R.id.tv_detail_key);
        mTextViewRecommendKey = (TextView) findViewById(R.id.tv_recommend_key);

        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollected) {
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
    protected String getRequestUrl() {
        return Urls.URL_PREFIX+"/theme-contentDetail?"+"id="+id;
    }

    @Override
    protected void processData(GameDetailData response) {
        if (response.data != null && response != null){
//            ViewGroup.LayoutParams params = headImage.getLayoutParams();
//            params.width = StaticData.ScreenWidth/3;
//            params.height = params.width;
            RequestManager.loadImage(
                    Urls.IMAGE_PREFIX + response.data.imgUrl,
                    RequestManager.getImageListener(headImage, 0,
                            mDefaultImageDrawable, mDefaultImageDrawable));
            textTitle.setText(response.data.title);
            downloadTimes.setText(response.data.uploadNum + "");
            textSize.setText(response.data.fileSize + "M");
            detail.setText(response.data.info);

            if(ExtUtils.isNotEmpty(response.data.summary)) {
                simpleIntroduction.setText("简介：" + response.data.summary);
                simpleIntroduction.setVisibility(View.VISIBLE);
            } else {
                simpleIntroduction.setVisibility(View.GONE);
            }

            if(ExtUtils.isNotEmpty(response.data.info)) {
                mTextViewDetailKey.setVisibility(View.VISIBLE);
                detail.setVisibility(View.VISIBLE);
            } else {
                detail.setVisibility(View.GONE);
                mTextViewDetailKey.setVisibility(View.GONE);
            }

            mTextViewRecommendKey.setVisibility(View.VISIBLE);

            if (response.data.isCollect == 0){
                isCollected = false;
                collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_heart));
                collectFont.setText("收藏");

            }else {
                isCollected = true;
                collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_collection));
                collectFont.setText("取消");
            }
        }
        if (response.data.tdList != null && response.data.tdList.size() != 0){
            addContentLinearLayout.removeAllViews();
            for(final IndexData.VideoGame recommand : response.data.tdList){
                View view = LayoutInflater.from(this).inflate(R.layout.game_list_item,null);
                ItemWorker.gameItem(this, view, recommand);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        id = recommand.id;
                        mLoadingDialog.show();
                        mScrollView.fullScroll(ScrollView.FOCUS_UP);
                        mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                        startExecuteRequest(Request.Method.GET);
                    }
                });

                addContentLinearLayout.addView(view);
            }
        }

        //控制按钮
        DetailWorker.gameDetailButtonController(this,mLinearLayoutButtons,response.data);

        //show scroll images
        mLinearLayoutImageContainer.removeAllViews();
        if(ExtUtils.isNotEmpty(response.data.imgList) && response.data.imgList.size() != 0)
            showScrollImages(response.data.imgList);

        mLoadingDialog.dismiss();
        super.processData(response);
    }

    /**
     * 显示滚动图片
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
        ExtUtils.errorLog("-volleyError-->",""+volleyError.toString());
    }

    @Override
    protected Class<GameDetailData> getResponseDataClass() {
        return GameDetailData.class;
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }

    /**
     * 收藏
     */
    public void doCollect(){
        String url = Urls.URL_PREFIX + "/theme-collect?id="+id;
        RequestManager.requestData(Request.Method.GET, url, CommonData.class, null, "collect"
                , new Response.Listener<CommonData>() {
            @Override
            public void onResponse(CommonData response) {
                if (response != null && response.msg != null){
                    isCollected = true;
                    ExtUtils.shortToast(GameDetailActivity.this,"收藏成功！");
                    collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_collection));
                    collectFont.setText("取消");
                }
                if (response.error != null){
                    switch (response.error){
                        case "required_login":
                            ExtUtils.shortToast(GameDetailActivity.this,"请您登陆！");
                            startActivity(new Intent(GameDetailActivity.this,LoginActivity.class).putExtra("tag",1));
                            break;
                        case "failed":
                            ExtUtils.shortToast(GameDetailActivity.this,"收藏失败！");
                            break;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.shortToast(GameDetailActivity.this,"网络错误！");
            }
        });
    }

    /**
     * 取消收藏
     */
    public void undoCollect(){
        String url = Urls.URL_PREFIX + "/theme-descCollect?id="+id;
        RequestManager.requestData(Request.Method.GET, url, CommonData.class, null, "uncollect"
                , new Response.Listener<CommonData>() {
            @Override
            public void onResponse(CommonData response) {
                if (response != null && response.msg != null){
                    isCollected = false;
                    ExtUtils.shortToast(GameDetailActivity.this,"取消收藏成功！");
                    collectImage.setBackground(getResources().getDrawable(R.mipmap.icon_heart));
                    collectFont.setText("收藏");
                }
                if (response.error != null){
                    switch (response.error){
                        case "required_login":
                            ExtUtils.shortToast(GameDetailActivity.this,"请您登陆！");
                            startActivity(new Intent(GameDetailActivity.this,LoginActivity.class).putExtra("tag",1));
                            break;
                        case "failed":
                            ExtUtils.shortToast(GameDetailActivity.this,"取消收藏失败！");
                            break;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.shortToast(GameDetailActivity.this,"网络错误！");
            }
        });
    }


}
