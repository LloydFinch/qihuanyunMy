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
package com.vanda.vandalibnetwork.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.vanda.vandalibnetwork.cookiestore.PersistentCookieStore;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.staticdata.StaticData;

public class AppData extends Application {
	public static Context sContext;
	public static final String FLAG_FINISH_ACTIVITY = "FINISH_ACTIVITY";

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) sContext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		StaticData.ScreenWidth = metric.widthPixels;         //程序启动时获取手机的宽度，这个为图片适配做全局配置准备
		RequestManager.myCookieStore = new PersistentCookieStore(sContext); //
		StaticData.sp = getApplicationContext().getSharedPreferences(
				sContext.getPackageName(), Activity.MODE_PRIVATE);
		RequestManager.cookieContext(true); // login out clean HttpContext;
	}

	public static Context getContext() {
		return sContext;
	}

}
