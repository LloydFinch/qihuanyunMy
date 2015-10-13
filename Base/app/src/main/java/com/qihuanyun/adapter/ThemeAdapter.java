package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.activity.ThemeDetailActivity;
import com.qihuanyun.pojo.ThemeData;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.vanda.vandalibnetwork.staticdata.StaticData;

import java.util.ArrayList;
import java.util.List;

public class ThemeAdapter extends PullLoadArrayAdaper<ThemeData.Theme> {

    private int viewResId;
    private Context mContext;
    private List<ThemeData.Theme> list;
    private static Drawable mDefaultImageDrawable;

    public ThemeAdapter(Context context, int viewResId,
                        List<ThemeData.Theme> data) {
        super(context, viewResId, data);
        mContext = context;
        this.viewResId = viewResId;
        list = data;
        mDefaultImageDrawable = context.getResources().getDrawable(R.mipmap.theme_list_default);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ThemeData.Theme theme = list.get(position);
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(viewResId, null);
            mViewHold = new ViewHold(convertView);
            convertView.setTag(mViewHold);
        } else {
            mViewHold = (ViewHold) convertView.getTag();
        }

        mViewHold.mTextView.setText(theme.title);
        ViewGroup.LayoutParams params = mViewHold.mImageView.getLayoutParams();
        params.width = StaticData.ScreenWidth;
        params.height = params.width / 2;
        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + theme.imgUrl,
                RequestManager.getImageListener(mViewHold.mImageView, 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ThemeDetailActivity.class).putExtra("themeId", theme.id));
            }
        });

        return convertView;
    }

    private ViewHold mViewHold;

    private class ViewHold {
        private TextView mTextView;
        private ImageView mImageView;

        public ViewHold(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.title);
            mImageView = (ImageView) convertView.findViewById(R.id.image);
        }
    }
}
