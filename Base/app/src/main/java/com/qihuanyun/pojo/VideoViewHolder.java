package com.qihuanyun.pojo;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qihuanyun.R;

public class VideoViewHolder {
    public ImageView imageView;
    public TextView title;
    public TextView desc;
    public LinearLayout mLinearLayoutDownload;
    public LinearLayout mLinearLayoutplay;
    public TextView mTextViewOpenOrDownload;

    public VideoViewHolder(View convertView){
        imageView = (ImageView) convertView.findViewById(R.id.image);
        title = (TextView) convertView.findViewById(R.id.title);
        desc = (TextView) convertView.findViewById(R.id.description);
        mLinearLayoutDownload = (LinearLayout) convertView.findViewById(R.id.ll_download);
        mLinearLayoutplay = (LinearLayout) convertView.findViewById(R.id.ll_play);
        mTextViewOpenOrDownload = (TextView) convertView.findViewById(R.id.open_or_downloading);
    }
}
