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

import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vanda.vandalibnetwork.daterequest.GsonRequest;
import com.vanda.vandalibnetwork.daterequest.RequestManager;

public abstract class BaseActivityNoActionBarActivity<T> extends Activity {

	@Override
	public void onStop() {
		super.onStop();
		RequestManager.cancelAll(BaseActivityNoActionBarActivity.this);
	}

	protected void executeRequest(Request<T> request) {
		RequestManager
				.addRequest(request, BaseActivityNoActionBarActivity.this);
	}

	protected void processData(T response) {
		RequestManager.cancelAll(BaseActivityNoActionBarActivity.this);
	}

	protected void errorData(VolleyError volleyError) {
		RequestManager.cancelAll(BaseActivityNoActionBarActivity.this);
	}

	protected abstract String getRequestUrl();

	protected abstract Class<T> getResponseDataClass();

	protected abstract Map<String, String> getParamMap();

	public void back(View v) {
		View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
		finish();
	}

	public void startExecuteRequest(int method) {
		executeRequest(new GsonRequest<T>(method, getRequestUrl(),
				getResponseDataClass(), getParamMap(),
				new Response.Listener<T>() {
					@Override
					public void onResponse(final T response) {
						processData(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						errorData(volleyError);
					}
				}));
	}

}
