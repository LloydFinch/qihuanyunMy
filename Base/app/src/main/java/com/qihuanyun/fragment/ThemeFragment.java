package com.qihuanyun.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.adapter.ThemeAdapter;
import com.qihuanyun.pojo.ThemeData;
import com.vanda.vandalibnetwork.fragment.BaseSwipeRefreshFragment;

import java.util.Map;

public class ThemeFragment extends BaseSwipeRefreshFragment<ThemeData, ThemeData.Theme> {

    private Context mContext;

    public static ThemeFragment newInstance(Context context) {
        ThemeFragment newFragment = new ThemeFragment();
        newFragment.mContext = context;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.swipe_ref_layout_theme, null);
        initArrayListData();
        initSwipeLayoutData(view, R.id.id_swipe_ly);
        initListViewData(view, R.id.id_listview);
        initPullLoadArrayAdaperData(new ThemeAdapter(mContext,
                R.layout.theme_list_item, mList));
        initData();
        startExecuteRequest(Request.Method.GET);
        return view;
    }

    @Override
    protected void addArrayListData(ThemeData response) {
        if (response != null && response.data != null) {
            setArrayListData(response.data);
            setDataItemCount(10);
        }
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX + "/themelist";
    }

    @Override
    protected String getRefDataUrl(int page, int size) {
        return Urls.URL_PREFIX + "/themelist?page="+page+"&size="+size;
    }

    @Override
    protected Class<ThemeData> getResponseDataClass() {
        return ThemeData.class;
    }

    @Override
    protected void processData(ThemeData response) {

        super.processData(response);
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }
}
