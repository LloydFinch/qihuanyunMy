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
package com.vanda.vandalibnetwork.utils;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * @author vanda 伍中联
 *
 */
public class CacheUtils {

	/**
	 * @param context
	 *            上下文参数，方法是创建一个以应用包名的缓存目录，如果不存在外部存储，使用内部存储 缓存目录为"/Android/data/"
	 *            + 包名 + "/cache/"
	 * @return File
	 */
	public static File getExternalCacheDir(final Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		if (Environment.getExternalStorageDirectory() != null) {
			return new File(Environment.getExternalStorageDirectory().getPath()
					+ cacheDir);
		}
		return new File(Environment.getRootDirectory().getPath() + cacheDir);
	}

	/**
	 * @param context
	 *            目的是返回 缓存路径
	 * @return
	 */
	public static String getExternalCachePath(final Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		if (Environment.getExternalStorageDirectory() != null)
			return Environment.getExternalStorageDirectory().getPath()
					+ cacheDir;
		return Environment.getRootDirectory().getPath() + cacheDir;
	}

	public static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

}
