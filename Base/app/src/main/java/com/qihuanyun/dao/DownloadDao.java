package com.qihuanyun.dao;

import android.content.Context;

import com.qihuanyun.database.SQLiteHelperOrm;

/**
 * 业务bean
 */
public class DownloadDao {
    private SQLiteHelperOrm openHelper;

    public DownloadDao(Context context) {
        openHelper = new SQLiteHelperOrm(context);
    }


}
