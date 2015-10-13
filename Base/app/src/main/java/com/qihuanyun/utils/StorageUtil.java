package com.qihuanyun.utils;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;

public class StorageUtil {
	public static String getSecondStoragePath(Context context){
		StorageManager sm = (StorageManager)context.getSystemService(context.STORAGE_SERVICE);
		String path = "";
    	//emulated
    	try {
			String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new  Class[0]).invoke(sm, new  Object[]{});
			for(String p : paths){
				if(! p.contains("emulated")){
					path = p;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    	return path;
	}
	
	public static String getPrimaryStoragePath(Context context){
		StorageManager sm = (StorageManager)context.getSystemService(context.STORAGE_SERVICE);
		String path = "";
    	//emulated
    	try {
			String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new  Class[0]).invoke(sm, new  Object[]{});
			for(String p : paths){
				if(p.contains("emulated")){
					path = p;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    	return path;
	}
	
	public static String getBiggestFreeStorage(Context context){
		StorageManager sm = (StorageManager)context.getSystemService(context.STORAGE_SERVICE);
		String path = "";
		int biggest = 0;
    	
    	try {
			String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new  Class[0]).invoke(sm, new  Object[]{});
			for(String p : paths){
				StatFs sf = new StatFs(p); 
	            int availCount = sf.getAvailableBlocks(); 
	            if(availCount > biggest){
	            	biggest = availCount;
	            	path = p;
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    	return path;
	}
	
	public static String[] getAllStorage(Context context){
		StorageManager sm = (StorageManager)context.getSystemService(context.STORAGE_SERVICE);
		String[] paths = null;
		try {
			paths = (String[]) sm.getClass().getMethod("getVolumePaths", new  Class[0]).invoke(sm, new  Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return paths;
	}
}
