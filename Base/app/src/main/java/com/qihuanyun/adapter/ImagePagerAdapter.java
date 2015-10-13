package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.activity.GameDetailActivity;
import com.qihuanyun.activity.HtmlActivity;
import com.qihuanyun.activity.ThemeDetailActivity;
import com.qihuanyun.activity.VideoDetailActivity;
import com.qihuanyun.pojo.IndexData;
import com.qihuanyun.utils.ExtUtils;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter implements IconPagerAdapter {

    private LayoutInflater inflater;
    private ArrayList<IndexData.Banner> imagesResource;
    private Drawable mDefaultImageDrawable;

    private Context context;
    private int flag = 1;

    public ImagePagerAdapter(ArrayList<IndexData.Banner> images, Context ctx,
                             int flag) {
        context = ctx;
        this.flag = flag;
        this.imagesResource = images;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDefaultImageDrawable = ctx.getResources().getDrawable(R.mipmap.banner_default);
    }

    public void setImageUrls(String[] images) {
        // this.images = images;
        notifyDataSetChanged();
    }

    public void setImageView(View view, int position) {
        // final ImageView mImageView = (ImageView)
        // view.findViewById(R.id.image);
        // imageLoader.displayImage(Urls.URL_IMAGE_PREFIX + images[position],
        // mImageView, DaoImagesApplication.DEFAULT_IMAGE_OPTIONS);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    // @Override
    // public void setPrimaryItem(View container, int position, Object object) {
    // // setImageView(mViewlist.get(position), position);
    // // notifyDataSetChanged();
    // }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imagesResource != null ? imagesResource.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {

        View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
                false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

        RequestManager.loadImage(
                Urls.IMAGE_PREFIX + imagesResource.get(position).imgUrl,
                RequestManager.getImageListener(imageView, 0,
                        mDefaultImageDrawable, mDefaultImageDrawable));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesResource.get(position).category == 2) {
                    //电影
                    context.startActivity(new Intent(context, VideoDetailActivity.class).putExtra("videoId", imagesResource.get(position).contentId));
                } else if (imagesResource.get(position).category == 3) {
                    //游戏
                    context.startActivity(new Intent(context, GameDetailActivity.class).putExtra("id", imagesResource.get(position).contentId));
                } else if (imagesResource.get(position).category == 4 && ExtUtils.isNotEmpty(imagesResource.get(position).ish5)) {
                    //游戏
                    Intent intent = new Intent(context, HtmlActivity.class);
                    intent.putExtra("url", imagesResource.get(position).ish5);
                    intent.putExtra("title", imagesResource.get(position).title);
                    context.startActivity(intent);
                }else if (imagesResource.get(position).category == 1) {
                    context.startActivity(new Intent(context, ThemeDetailActivity.class).putExtra("themeId", imagesResource.get(position).contentId));
                }
            }
        });

        view.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getIconResId(int index) {
        return 0;
    }

    public void setCount(ArrayList<IndexData.Banner> images) {
        this.imagesResource = images;
        if (this.imagesResource != null && this.imagesResource.size() == 3) {
            this.imagesResource.add(this.imagesResource.get(0));
        }
        notifyDataSetChanged();
    }

}
