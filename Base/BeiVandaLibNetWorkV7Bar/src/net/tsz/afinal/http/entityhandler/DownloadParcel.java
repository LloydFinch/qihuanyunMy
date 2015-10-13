package net.tsz.afinal.http.entityhandler;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadParcel implements Parcelable {
	public int _id;
	public int id;
	public int progress;
	public String type;
	public String url;
	public int fileSize;
	public int status;
	public String imageUrl;
	public String title;
	public int isError;//0没错 1有错
	public String speed;
	public String local_url;
	public String fileName;
	public String packageName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeInt(id);
		dest.writeString(type);
		dest.writeString(url);
		dest.writeInt(progress);
		dest.writeInt(fileSize);
		dest.writeInt(status);
		dest.writeString(imageUrl);
		dest.writeString(title);
		dest.writeInt(isError);
		dest.writeString(speed);
		dest.writeString(local_url);
		dest.writeString(fileName);
		dest.writeString(packageName);
	}

	public static final Creator<DownloadParcel> CREATOR = new Creator<DownloadParcel>() {
		public DownloadParcel createFromParcel(Parcel in) {
			return new DownloadParcel(in);
		}

		public DownloadParcel[] newArray(int size) {
			return new DownloadParcel[size];
		}
	};

	public DownloadParcel(Parcel in) {
		_id = in.readInt();
		id = in.readInt();
		type = in.readString();
		url = in.readString();
		progress = in.readInt();
		fileSize = in.readInt();
		status = in.readInt();
		imageUrl = in.readString();
		title = in.readString();
		isError = in.readInt();
		speed = in.readString();
		local_url = in.readString();
		fileName = in.readString();
		packageName = in.readString();
	}

	public DownloadParcel() {
		super();
	}

	public DownloadParcel(int id,String type,String url,String imageUrl,String title,String packageName,String fileName){
		this.id = id;
		this.type = type;
		this.url = url;
		this.imageUrl = imageUrl;
		this.title = title;
		this.packageName = packageName;
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "id="+id+",type="+type+",progress="+progress+",url="+url+",fileSize="+fileSize + ",status"+status + ",title="+title + " ,packageName="+packageName;
	}
}
