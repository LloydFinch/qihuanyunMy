package com.qihuanyun.pojo;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qihuanyun.R;

public class GameViewHolder {
    public ImageView imageView;
    public TextView title;
    public TextView desc;
    public LinearLayout mLinearLayoutDownload;
    public TextView mTextViewOpen;
    public TextView mTextViewInstall;

    public GameViewHolder(View convertView){
        imageView = (ImageView) convertView.findViewById(R.id.image);
        title = (TextView) convertView.findViewById(R.id.title);
        desc = (TextView) convertView.findViewById(R.id.description);
        mLinearLayoutDownload = (LinearLayout) convertView.findViewById(R.id.download_item_layout);
        mTextViewOpen = (TextView) convertView.findViewById(R.id.open);
        mTextViewInstall = (TextView) convertView.findViewById(R.id.install);
    }
}
