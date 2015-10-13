package com.qihuanyun.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.adapter.SearchAdapter;
import com.qihuanyun.pojo.SearchData;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.fragmentactivity.BaseSwipeRefreshSherlockFragmentActivity;

import java.util.Map;

/**
 * Created by Administrator on 2015/8/25.
 */
public class SearchActivity extends
        BaseSwipeRefreshSherlockFragmentActivity<SearchData,SearchData.Data>
        implements View.OnClickListener{

    private LinearLayout submitSearch;
    private ImageView deleteInput;
    private ImageView back;
    private EditText searchEditInput;

    public static String key = "";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search);
        actionBar.hide();
        setTitle("搜索");

        initArrayListData();
        initSwipeLayoutData(R.id.id_swipe_ly);
        initListViewData(R.id.id_listview);
        SearchAdapter searchAdapter = new SearchAdapter(this,0,mList);
        initPullLoadArrayAdaperData(searchAdapter);
        initData();
        initView();
    }

    private void initView(){
        submitSearch = (LinearLayout) findViewById(R.id.search_btn_back);
        searchEditInput = (EditText) findViewById(R.id.search_et_input);
        deleteInput = (ImageView) findViewById(R.id.search_iv_delete);
        back = (ImageView) findViewById(R.id.back);

        submitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = searchEditInput.getText().toString();
                mList.clear();
                startExecuteRequest(Request.Method.GET);
            }
        });
        back.setOnClickListener(this);
        deleteInput.setOnClickListener(this);
        searchEditInput.addTextChangedListener(new EditTextChangeListener());
        searchEditInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){/**搜索按钮*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()){
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(),0);
                    }
                    key = searchEditInput.getText().toString();
                    mList.clear();
                    startExecuteRequest(Request.Method.GET);
                    return  true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_iv_delete:
                searchEditInput.setText("");
                deleteInput.setVisibility(View.GONE);
                break;
            case R.id.back:
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
                break;
        }
    }

    private class EditTextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!"".equals(s.toString())) {
                deleteInput.setVisibility(View.VISIBLE);
            }else {
                deleteInput.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    @Override
    protected void addArrayListData(SearchData response) {
        if (response != null && response.data != null) {
            TextView textView = (TextView) findViewById(R.id.nocontent_show);
            if (response.data.size() != 0) {
                textView.setVisibility(View.INVISIBLE);
                findViewById(R.id.id_swipe_ly).setVisibility(View.VISIBLE);
                setArrayListData(response.data);
                setDataItemCount(10);
            }else {
                textView.setVisibility(View.VISIBLE);
                findViewById(R.id.id_swipe_ly).setVisibility(View.INVISIBLE);
                findViewById(R.id.search_layout).setBackgroundResource(R.color.common_bg_color);
            }
        }
    }

    @Override
    protected String getRefDataUrl(int page, int size) {
         return String.format(Urls.URL_PREFIX + "/fuzzy-search?key=" + key + "&page=%d&size=%d", page, size);
    }

    @Override
    protected String getRequestUrl() {
        return Urls.URL_PREFIX+"/fuzzy-search?key="+key;
    }

    @Override
    protected Class<SearchData> getResponseDataClass() {
        return SearchData.class;
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
