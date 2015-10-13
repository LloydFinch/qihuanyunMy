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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.romainpiel.shimmer.ShimmerTextView;
import com.vanda.beivandalibnetworkv7bar.R;
import com.vanda.vandalibnetwork.application.AppData;

public class BaseActivityActionBarNoNetWork extends ActionBarActivity {

	private FinishBroadcastReceiver mFinishBroadcastReceiver;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initActionBar();

		IntentFilter filter = new IntentFilter();
		filter.addAction(AppData.FLAG_FINISH_ACTIVITY);
		mFinishBroadcastReceiver = new FinishBroadcastReceiver();
		registerReceiver(mFinishBroadcastReceiver, filter);
	}
	
	protected ActionBar actionBar;
	private ShimmerTextView mActionBarTitle;

	private void initActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setDisplayShowCustomEnabled(true);
		View view = View.inflate(this, R.layout.actionbar_title, null);
		mActionBarTitle = (ShimmerTextView) view.findViewById(R.id.tv_shimmer);
		mActionBarTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		actionBar.setCustomView(view);
	}

	public void setTitle(int resId) {
		mActionBarTitle.setText(resId);
	}

	public void setTitle(CharSequence text) {
		mActionBarTitle.setText(text);
	}

	private class FinishBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

	public void back(){
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mFinishBroadcastReceiver);
	}
}
