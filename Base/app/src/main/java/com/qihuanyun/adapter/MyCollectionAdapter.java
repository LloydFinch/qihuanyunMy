package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qihuanyun.R;
import com.qihuanyun.Urls;
import com.qihuanyun.activity.GameDetailActivity;
import com.qihuanyun.pojo.CommonData;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.GameViewHolder;
import com.qihuanyun.pojo.MyColllectionData;
import com.qihuanyun.pojo.VideoViewHolder;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.ItemWorker;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;
import com.vanda.vandalibnetwork.daterequest.RequestManager;
import com.wzl.vandan.dialog.VandaAlert;

import java.util.List;

public class MyCollectionAdapter extends PullLoadArrayAdaper<MyColllectionData.Data> {
    private Context context;
    private boolean flag;
    private List<MyColllectionData.Data> list;
    private int currentType;//当前item类型


    public MyCollectionAdapter(Context context, int resource, List<MyColllectionData.Data> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyColllectionData.Data data = getItem(position);

        currentType = list.get(position).type;

        //电影电影=2,游戏=3
        if (currentType == 2) {
            //电影
            VideoViewHolder mVideoViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
                mVideoViewHolder = new VideoViewHolder(convertView);
                convertView.setTag(mVideoViewHolder);
            } else {
                if((Integer)convertView.findViewById(R.id.title).getTag() == 3){
                    convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
                    mVideoViewHolder = new VideoViewHolder(convertView);
                    convertView.setTag(mVideoViewHolder);
                }else{
                    mVideoViewHolder = (VideoViewHolder) convertView.getTag();
                }
            }

            convertView.findViewById(R.id.title).setTag(2);

            ItemWorker.videoItem(context, mVideoViewHolder, new GameContentData().new Data(data.id, data.title, data.summary,data.imgUrl, data.url, data.type));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, GameDetailActivity.class);
                    it.putExtra("id", data.id);
                    context.startActivity(it);
                }
            });
        } else if (currentType == 3) {
            //游戏
            GameViewHolder mGameViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.game_list_item, null);
                mGameViewHolder = new GameViewHolder(convertView);
                convertView.setTag(mGameViewHolder);
            } else {
                if((Integer)convertView.findViewById(R.id.title).getTag() == 2){
                    convertView = LayoutInflater.from(context).inflate(R.layout.game_list_item, null);
                    mGameViewHolder = new GameViewHolder(convertView);
                    convertView.setTag(mGameViewHolder);
                }else{
                    mGameViewHolder = (GameViewHolder) convertView.getTag();
                }
            }

            convertView.findViewById(R.id.title).setTag(3);

            ItemWorker.gameItem(context, mGameViewHolder, new GameContentData().new Data(data.id, data.title, data.summary,data.imgUrl, data.url, data.type));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, GameDetailActivity.class);
                    it.putExtra("id", data.id);
                    context.startActivity(it);
                }
            });
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                VandaAlert.createTextViewDialog(context, "取消收藏", new VandaAlert.TextViewClickListener() {
                    @Override
                    public void onTextViewClickListener() {
                        cancelCollect(position);
                    }
                }).show();
                return false;
            }
        });

        return convertView;
    }

    /**
     * 滑动取消收藏
     * @param position
     */
    public void cancelCollect(final int position){
        String url = Urls.URL_PREFIX + "/theme-descCollect?id="+getItem(position).id;
        RequestManager.requestData(Request.Method.GET, url, CommonData.class, null, "cancelCollect"
                , new Response.Listener<CommonData>() {
            @Override
            public void onResponse(CommonData response) {
                if (response != null && response.msg != null) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
                if (response.error != null) {
                    ExtUtils.shortToast(context,"系统错误，请稍候再试");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ExtUtils.errorLog("VolleyError-->","VolleyError="+error.toString());
            }
        });
    }
}
