package com.zhy.imageloader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhy.utils.CommonAdapter;

public class MyAdapter extends CommonAdapter<String> {


	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static ArrayList<String> mSelectedImage = new ArrayList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	
	private Context mContext;
	private Button mButton;
	private int maxCount;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath,Button button,int maxCount,ArrayList<String> choosedList) {
		super(context, mDatas, itemLayoutId);
		this.mContext = context;
		this.mDirPath = dirPath;
		this.mButton = button;
		this.maxCount = maxCount;
		this.mSelectedImage = choosedList;
	}

	@Override
	public void convert(final com.zhy.utils.ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else {
					//未选择图片
					if(mSelectedImage.size() < maxCount){
						mSelectedImage.add(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					}else{
						Toast.makeText(mContext, "最多只能选择"+maxCount+"张图片", Toast.LENGTH_SHORT).show();
					}
				}
				mButton.setText("完成("+mSelectedImage.size()+"/"+maxCount+")");
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
	}
}
