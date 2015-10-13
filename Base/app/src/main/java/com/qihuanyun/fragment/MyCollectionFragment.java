package com.qihuanyun.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.activity.LoginActivity;
import com.qihuanyun.activity.MyCollectionActivity;
import com.qihuanyun.adapter.MyCollectionAdapter;
import com.qihuanyun.pojo.MyColllectionData;
import com.qihuanyun.utils.ExtUtils;
import com.vanda.vandalibnetwork.fragment.BaseSwipeRefreshFragment;

import java.util.Map;

public class MyCollectionFragment extends BaseSwipeRefreshFragment<MyColllectionData,MyColllectionData.Data> {

    private Context context;

    public static MyCollectionFragment newInstance(Context context) {
        MyCollectionFragment newFragment = new MyCollectionFragment();
        newFragment.context = context;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_ref_layout_theme, null);
        initArrayListData();
        initSwipeLayoutData(view, R.id.id_swipe_ly);
        initListViewData(view, R.id.id_listview);
        initPullLoadArrayAdaperData(new MyCollectionAdapter(context, 0, mList));
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mList.clear();
        startExecuteRequest(Request.Method.GET);
    }

    @Override
    protected void addArrayListData(MyColllectionData response) {
        if (response != null && response.data != null) {
            setArrayListData(response.data);
            setDataItemCount(10);
        }
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX + "/collect-list";
    }

    @Override
    protected String getRefDataUrl(int page, int size) {
        return String.format(Urls.URL_PREFIX + "/collect-list"+"?page=%d&size=%d",page,size);
    }

    @Override
    protected Class<MyColllectionData> getResponseDataClass() {
        return MyColllectionData.class;
    }

    @Override
    protected void processData(MyColllectionData response) {
        super.processData(response);
        if (response != null  && response.error != null){
            ExtUtils.shortToast(context, "请您登陆！");
            startActivity(new Intent(context, LoginActivity.class).putExtra("tag",1));
        }
    }

    @Override
    protected Map<String, String> getParamMap() {
        return null;
    }
}
