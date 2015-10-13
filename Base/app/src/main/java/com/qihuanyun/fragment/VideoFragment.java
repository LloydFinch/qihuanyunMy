package com.qihuanyun.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.adapter.VideoAdapter;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.GameTabData;
import com.qihuanyun.utils.ExtUtils;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.fragment.BaseSwipeRefreshFragment;

import java.util.ArrayList;
import java.util.Map;

public class VideoFragment extends BaseSwipeRefreshFragment<GameContentData,GameContentData.Data> {
    private Context context;
    private LinearLayout tabContent;
    private String key;
    private String url;

    public static VideoFragment newInstance(Context context) {
        VideoFragment newFragment = new VideoFragment();
        newFragment.context = context;
        newFragment.url = Urls.URL_PREFIX + "/getclassifycontents?"+"classifyid=2";
        return newFragment;
    }

    /**获取标题导航栏*/
    public void getTabTitle(){
        String myurl = Urls.URL_PREFIX +"/getclassify?"+"classifyid=2";
        RequestManager.requestData(Request.Method.GET, myurl, GameTabData.class, null, "title", new Response.Listener<GameTabData>() {
            @Override
            public void onResponse(final GameTabData response) {
                if (response != null && response.data != null) {
                    final ArrayList<View> arrayList = new ArrayList<>();
                    for (int i = 0; i < response.data.size()+2; i++) {
                        View tabView = LayoutInflater.from(context).inflate(R.layout.game_tab_item, null);
                        final TextView textView = (TextView) tabView.findViewById(R.id.tab_text);

                        if (i == 0){
                            textView.setText("排行榜");
                        }
                        else if (i == 1){
                            textView.setText("最新");
                        }else {
                            textView.setText(response.data.get(i-2).classifyName);
                        }

                        final Drawable drawable = getResources().getDrawable(R.mipmap.tab_click);
                        if (tabView != null) {
                            arrayList.add(textView);
                        }
                        assert drawable != null;
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        if (i == 0) {
                            key = "-1";
                            startExecuteRequest(Request.Method.GET);//执行网络请求，默认第一个
                            textView.setTextColor(getResources().getColor(R.color.common_text_color));
                            textView.setCompoundDrawables(null, null, null, drawable);
                        }

                        final int k = i;
                        textView.setOnClickListener(new View.OnClickListener() {
                            String thiskey;
                            @Override
                            public void onClick(View v) {
                                textView.setTextColor(getResources().getColor(R.color.common_text_color));
                                textView.setCompoundDrawables(null, null, null, drawable);
                                for (int j = 0; j < arrayList.size(); j++) {
                                    if (j != k) {
                                        TextView textView1 = (TextView) arrayList.get(j).findViewById(R.id.tab_text);
                                        textView1.setCompoundDrawables(null, null, null, null);
                                        textView1.setTextColor(getResources().getColor(R.color.white));
                                    }else {
                                        if (j == 0){
                                            thiskey = "-1";
                                        }else if (j == 1){
                                            thiskey = "-2";
                                        }else {
                                            thiskey = response.data.get(k-2).key;
                                        }
                                    }
                                }
                                updateGameUi(thiskey);//触发更新ui界面
                            }
                        });
                        tabContent.addView(tabView);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.shortToast(context, "网络错误，数据拉取失败！");
            }
        });
    }

    /**根据导航栏，刷新ui*/
    public void updateGameUi(String Uikey){
        key = Uikey;
        loadFirstPage(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video, null);
        tabContent = (LinearLayout) view.findViewById(R.id.video_tabcontent);

        initArrayListData();
        initSwipeLayoutData(view, R.id.id_swipe_ly);
        initListViewData(view, R.id.id_listview);
        initPullLoadArrayAdaperData(new VideoAdapter(context, R.layout.video_list_item, mList));
        initData();

        getTabTitle();

        return view;
    }

    @Override
    protected void addArrayListData(GameContentData response) {
        if (response != null && response.data != null){
            setArrayListData(response.data);
            setDataItemCount(10);
        }
    }

    @Override
    protected String getRefDataUrl(int page, int size) {
        return String.format(url+"&key="+
                key+"&page=%d&size=%d",page,size);
    }

    @Override
    protected String getRequestUrl() {
        return url+"&key="+key;
    }

    @Override
    protected void processData(GameContentData response) {
        super.processData(response);
    }

    @Override
    protected Class<GameContentData> getResponseDataClass() {
        return GameContentData.class;
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }
}
