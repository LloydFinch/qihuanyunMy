/***********************************************************************************************************************
 * 
 * Copyright (C) 2014, 2015 by sunnsoft (http://www.sunnsoft.com)
 * http://www.sunnsoft.com/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package com.vanda.vandalibnetwork.fragmentactivity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.etsy.android.grid.StaggeredGridView;
import com.vanda.beivandalibnetworkv7bar.R;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;
import com.vanda.vandalibnetwork.daterequest.GsonRequest;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.utils.Pagination;
import com.vanda.vandalibnetwork.view.LoadingFooter;

public abstract class BaseSwipeRefreshGridViewFragmentActivity<T, K> extends
		BaseFragmentActivity<T> implements SwipeRefreshLayout.OnRefreshListener {

	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected StaggeredGridView mGridView;
	protected PullLoadArrayAdaper<K> mPullLoadArrayAdaper;
	protected ArrayList<K> mArrayList;
	protected List<K> mList;
	protected Pagination mPagination = new Pagination(0); // use for pagination
	protected int mDataItemCount = 0;
	protected LoadingFooter mLoadingFooter;

	@SuppressWarnings("deprecation")
	protected void initSwipeLayoutData(int resId) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(resId);
		mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
	}

	protected void initListViewData(int resId) {
		mGridView = (StaggeredGridView) findViewById(resId);
	}

	protected void initPullLoadArrayAdaperData(
			PullLoadArrayAdaper<K> mPullLoadArrayAdaper) {
		this.mPullLoadArrayAdaper = mPullLoadArrayAdaper;
	}

	protected void initArrayListData() {
		this.mList = new ArrayList<K>();
		this.mArrayList = new ArrayList<K>();
	}

	protected void setArrayListData(ArrayList<K> mArrayList) {
		this.mArrayList = mArrayList;
	}

	protected void setDataItemCount(int mDataItemCount) {
		this.mDataItemCount = mDataItemCount;
	}

	protected void initData() {
//		AnimationAdapter animationAdapter = new CardsAnimationAdapter(
//				mPullLoadArrayAdaper);
//		animationAdapter.setListView(mListView);
		mLoadingFooter = new LoadingFooter(this);
		mLoadingFooter.setState(LoadingFooter.State.TheEnd);
		mGridView.addFooterView(mLoadingFooter.getView());
		mGridView.setAdapter(mPullLoadArrayAdaper);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.i("mLoadingFooter.getState()->",
				// mLoadingFooter.getState()
				// + "");
				if (mLoadingFooter.getState() == LoadingFooter.State.Loading
						|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
					return;
				}
				if (firstVisibleItem + visibleItemCount >= totalItemCount
						&& totalItemCount != 0
						&& totalItemCount != mGridView.getHeaderViewsCount()
								+ mGridView.getFooterViewsCount()
						&& mGridView.getCount() > 0) {
					loadNextPage();
				}
			}
		});
	}

	@Override
	public void onStart() {
		mSwipeRefreshLayout
				.setOnRefreshListener(BaseSwipeRefreshGridViewFragmentActivity.this);
		super.onStart();
	}

	/**
	 * 这个方法必须处理父类的mArrayList和mDataItemCount
	 */
	protected abstract void addArrayListData(T response);

	protected void processResult() {
	};

	@Override
	protected void processData(T response) {
		super.processData(response);
		addArrayListData(response);
		mPagination = new Pagination(mArrayList.size(), mDataItemCount, 20);
		setRefreshMode(mPagination);
		mPullLoadArrayAdaper.addAll(mArrayList);
		mPullLoadArrayAdaper.notifyDataSetChanged();
		processResult();
	}

	@Override
	protected void errorData(VolleyError volleyError) {
		super.errorData(volleyError);
		mLoadingFooter.getView().setVisibility(View.GONE);
	}

	protected abstract String getRefDataUrl(int page, int size);

	protected void loadData(final boolean nextPage) {
		if (!mSwipeRefreshLayout.isRefreshing() && !nextPage) {
			mSwipeRefreshLayout.setRefreshing(true);
		}
		String url = getRefDataUrl(mPagination.page
				+ (nextPage == true ? 1 : 0), mPagination.size);
		if (nextPage) {
			mPagination.page += 1;
		}
		Request<T> mLoadRequestData = new GsonRequest<T>(Method.GET, url,
				getResponseDataClass(), null, new Response.Listener<T>() {

					@Override
					public void onResponse(T response) {
						RequestManager
								.cancelAll(BaseSwipeRefreshGridViewFragmentActivity.this);
						if (!nextPage) {
							mSwipeRefreshLayout.setRefreshing(false);
							mPullLoadArrayAdaper.clear();
							mArrayList.clear();
						} else {
							mArrayList.clear();
						}
						addArrayListData(response);
						if (!nextPage) {
							mPagination = null;
							mPagination = new Pagination(mArrayList.size(),
									mDataItemCount, 20);
							setRefreshMode(mPagination);
						} else {
							mPagination.updateLoaded(mArrayList.size());
							setRefreshMode(mPagination);
						}
						mPullLoadArrayAdaper.addAll(mArrayList);
						mPullLoadArrayAdaper.notifyDataSetChanged();
						processResult();
						if (mOnPullDownRefresh != null)
							mOnPullDownRefresh.onPullDownRefreshComplete();
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						RequestManager
								.cancelAll(BaseSwipeRefreshGridViewFragmentActivity.this);
						if (!nextPage) {
							mSwipeRefreshLayout.setRefreshing(false);
						} else {
							mLoadingFooter.getView().setVisibility(View.GONE);
						}
						if (mOnPullDownRefresh != null)
							mOnPullDownRefresh.onPullDownRefreshComplete();
					}
				});
		RequestManager.addRequest(mLoadRequestData,
				BaseSwipeRefreshGridViewFragmentActivity.this);
	}

	protected void setRefreshMode(Pagination page) {
		mLoadingFooter.setState(page.hasMore() ? LoadingFooter.State.Idle
				: LoadingFooter.State.TheEnd, 3000);
	}

	protected void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		if (mOnPullDownRefresh != null)
			mOnPullDownRefresh.onPullDownRefreshing();
		loadData(true);
	}

	protected void loadFirstPage(final boolean nextPage) {
		loadData(false);
	}

	public void loadFirstPageAndScrollToTop() {
//		ListViewUtils.smoothScrollListViewToTop(mGridView);
		mPagination.page = 1;
		loadFirstPage(false);
	}

	public void loadFirstPageAndScrollToTop(int page) {
//		ListViewUtils.smoothScrollListViewToTop(mListView);
		mPagination.page = page;
		loadFirstPage(false);
	}

	@Override
	public void onRefresh() {
		if (mOnPullDownRefresh != null)
			mOnPullDownRefresh.onPullDownRefreshing();
		mPagination.page = 1;
		loadFirstPage(false);
	}

	protected OnPullDownRefresh mOnPullDownRefresh;

	public void setOnPullDownRefresh(OnPullDownRefresh mOnPullDownRefresh) {
		this.mOnPullDownRefresh = mOnPullDownRefresh;
	}

	public interface OnPullDownRefresh {
		public void onPullDownRefreshComplete();

		public void onPullDownRefreshing();
	}

}
