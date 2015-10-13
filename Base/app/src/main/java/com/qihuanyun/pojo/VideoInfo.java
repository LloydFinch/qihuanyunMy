package com.qihuanyun.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.Serializable;

@DatabaseTable(tableName = "videoInfo")
public class VideoInfo implements Serializable {

	@DatabaseField(generatedId = true)
	public long _id;
	@DatabaseField
	public String displayName;
	@DatabaseField
	public String path;
	@DatabaseField
	public String Size;
	@DatabaseField
	public String time;

	@DatabaseField
	public long last_modify_time;


	@DatabaseField
	public String thumb_path;

	public VideoInfo(){

	}
	public VideoInfo(File f){
		displayName = f.getName();
		path = f.getAbsolutePath();
		last_modify_time = f.lastModified();
	}
}
