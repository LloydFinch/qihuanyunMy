package com.qihuanyun.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 *	手机工具类
 *	author:hexianhua
 *	2015-08-30 11:06:23
 */
public class MobileUtils {

	/**
	 * 获取手机中所有非预装程序
	 * @param context
	 * @return
	 */
	public static List<String> getAllApps(Context context) {

		List<String> apps = new ArrayList<String>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> packlist = pManager.getInstalledPackages(0);
		for (int i = 0; i < packlist.size(); i++) {
			PackageInfo pak = packlist.get(i);

			// 判断是否为非系统预装的应用程序
			// 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
			// if()里的值如果<=0则为自己装的程序，否则为系统工程自带
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 添加自己已经安装的应用程序
				apps.add(pak.applicationInfo.packageName);
			}

		}
		return apps;
	}

	/**
	 * 安装apk
	 * @param context
	 * @param path
	 */
	public static void installApk(Context context,String path){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 根据app的包名打开app
	 * @param context
	 * @param packageName
	 */
	public static void openApp(Context context,String packageName){
		try{
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 根据apk路径获取包名
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getPackageNameByApk(Context context , String path){
		String packageName = "";
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			appInfo = info.applicationInfo;
			packageName = appInfo.packageName;
		}

		return packageName;
	}
}
