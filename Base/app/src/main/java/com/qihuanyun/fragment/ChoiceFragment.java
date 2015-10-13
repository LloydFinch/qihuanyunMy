package com.qihuanyun.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.activity.GameDetailActivity;
import com.qihuanyun.activity.MainActivity;
import com.qihuanyun.activity.VideoDetailActivity;
import com.qihuanyun.adapter.ImagePagerAdapter;
import com.qihuanyun.pojo.IndexData;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.ItemWorker;
import com.vanda.vandalibnetwork.fragment.BaseFragment;
import com.vanda.vandalibnetwork.staticdata.StaticData;
import com.vanda.vandalibnetwork.view.utils.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.wzl.vandan.dialog.VandaAlert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class ChoiceFragment extends BaseFragment<IndexData> {
    private LinearLayout mLinearLayoutVideos, mLinearLayoutGames;
    private RelativeLayout mRelativeLayoutBanner;
    private AutoScrollViewPager advViewPager;
    private CirclePageIndicator mIndicator;
    private ImagePagerAdapter advAdapter;
    private Dialog mLoadingDialog;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewVideoKey;
    private TextView mTextViewGameKey;

    public static ChoiceFragment newInstance(Context mContext) {
        ChoiceFragment newFragment = new ChoiceFragment();
        newFragment.mContext = mContext;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choice, null);
        initViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (! this.isHidden()) {
            startExecuteRequest(Request.Method.GET);
            mLoadingDialog.show();
        }
    }

    private void initViews(View view) {
        //初始化轮播banner
        mRelativeLayoutBanner = (RelativeLayout) view.findViewById(R.id.viewpager_relayout);
        ViewGroup.LayoutParams params = mRelativeLayoutBanner.getLayoutParams();
        params.width = StaticData.ScreenWidth;
        params.height = params.width / 2;
        mRelativeLayoutBanner.setLayoutParams(params);

        advViewPager = (AutoScrollViewPager) view.findViewById(R.id.home_fragment_viewpager);
        advViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
        advViewPager.setRoundTrip(true);
        advViewPager.startAutoScroll(3000);
        advViewPager.setScrollDurationFactor(4);

        mIndicator = (CirclePageIndicator) view.findViewById(R.id.home_fragment_circlepageindicator);
        DisplayMetrics metric = new DisplayMetrics();
        ((MainActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        if(metric.densityDpi <= 240){
            mIndicator.setRadius(6f);
        }else if(metric.densityDpi <= 320){
            mIndicator.setRadius(8f);
        }else if(metric.densityDpi <= 480){
            mIndicator.setRadius(10f);
        }else if(metric.densityDpi <= 560){
            mIndicator.setRadius(12f);
        }else if(metric.densityDpi <= 640){
            mIndicator.setRadius(14f);
        }else{
            mIndicator.setRadius(8f);
        }

        mLinearLayoutVideos = (LinearLayout) view.findViewById(R.id.ll_videos);
        mLinearLayoutGames = (LinearLayout) view.findViewById(R.id.ll_games);

        mLoadingDialog = VandaAlert.createLoadingDialog(mContext, "");

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorScheme(com.vanda.beivandalibnetworkv7bar.R.color.holo_blue_bright);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startExecuteRequest(Request.Method.GET);
            }
        });

        try {
            Field f = SwipeRefreshLayout.class.getDeclaredField("mTouchSlop");
            f.setAccessible(true);
            f.set(mSwipeRefreshLayout, StaticData.ScreenWidth/2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextViewVideoKey = (TextView) view.findViewById(R.id.tv_video_key);
        mTextViewGameKey = (TextView) view.findViewById(R.id.tv_game_key);
    }

    @Override
    protected void processData(IndexData response) {
        super.processData(response);
        if (response != null && response.data != null) {
            if (response.data.filmRecommends != null && response.data.filmRecommends.size() != 0) {
                videos(response.data.filmRecommends);
            }
            if (response.data.gameRecommends != null && response.data.gameRecommends.size() != 0) {
                games(response.data.gameRecommends);
            }
            if (response.data.bannerResults != null && response.data.bannerResults.size() != 0) {
                banners(response.data.bannerResults);
            }

            mTextViewVideoKey.setVisibility(View.VISIBLE);
            mTextViewGameKey.setVisibility(View.VISIBLE);

            mLoadingDialog.dismiss();
            if(mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 影视推荐显示
     * @param filmRecommends
     */
    private void videos(ArrayList<IndexData.VideoGame> filmRecommends) {
        mLinearLayoutVideos.removeAllViews();
        for (final IndexData.VideoGame video : filmRecommends) {
            final View videoItem = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, null);

            ItemWorker.videoItem(mContext, videoItem, video);

            videoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, VideoDetailActivity.class).putExtra("videoId", video.id));
                }
            });

            mLinearLayoutVideos.addView(videoItem);
        }
    }

    /**
     * 游戏推荐显示
     * @param gameRecommends
     */
    private void games(ArrayList<IndexData.VideoGame> gameRecommends) {
        mLinearLayoutGames.removeAllViews();
        for (final IndexData.VideoGame game : gameRecommends) {
            final View gameItem = LayoutInflater.from(mContext).inflate(R.layout.game_list_item, null);

            ItemWorker.gameItem(mContext, gameItem, game);

            gameItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, GameDetailActivity.class).putExtra("id", game.id));
                }
            });

            mLinearLayoutGames.addView(gameItem);
        }
    }

    /**
     * banner显示
     * @param bannerResults
     */
    private void banners(ArrayList<IndexData.Banner> bannerResults) {
        advAdapter = new ImagePagerAdapter(bannerResults, mContext, 1);
        advViewPager.setAdapter(advAdapter);
        mIndicator.setViewPager(advViewPager);
        advAdapter.notifyDataSetChanged();
    }

    @Override
    protected void errorData(VolleyError volleyError) {
        super.errorData(volleyError);
        ExtUtils.errorLog("-->volleyError", "" + volleyError);

        mLoadingDialog.dismiss();
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX + "/index";
    }

    @Override
    protected Class<IndexData> getResponseDataClass() {
        return IndexData.class;
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }
}
