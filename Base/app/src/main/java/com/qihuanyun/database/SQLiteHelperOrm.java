package com.qihuanyun.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qihuanyun.BaseApplication;
import com.qihuanyun.exception.Logger;
import com.qihuanyun.pojo.VideoInfo;

import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/22.
 */
public class SQLiteHelperOrm extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "sobf.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelperOrm(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteHelperOrm() {
        super(BaseApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, VideoInfo.class);
        } catch (SQLException e) {
            Logger.e(e);
        }

        /*----hexh------*/
        //单个文件下载记录表
        database.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");
        //用户下载记录
        /**
         * status
         * 0正在下载
         * 1下载完成
         * 2暂停
         * 3出错
         */
        database.execSQL("CREATE TABLE IF NOT EXISTS download (_id integer primary key autoincrement,id integer,imageUrl varchar(100),title varchar(100),title_md5 varchar(100), size INTEGER, create_time INTEGER,update_time INTEGER,status INTEGER,network_url varchar(100),local_url varchar(100),progress INTEGER,type varchar(10),file_name varchar(100),package_name varchar(100))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, VideoInfo.class, true);

            /*----hexh------*/
            database.execSQL("DROP TABLE IF EXISTS filedownlog");
            database.execSQL("DROP TABLE IF EXISTS download");

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Logger.e(e);
        }
    }
}
