package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qihuanyun.activity.VideoDetailActivity;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.VideoViewHolder;
import com.qihuanyun.utils.ItemWorker;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;

import java.util.List;

public class VideoAdapter extends PullLoadArrayAdaper<GameContentData.Data> {
    private List<GameContentData.Data> list;
    private int resour;
    private Context context;

    public VideoAdapter(Context context, int textViewResourceId, List<GameContentData.Data> objects) {
        super(context, textViewResourceId, objects);
        this.list = objects;
        this.resour = textViewResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GameContentData.Data data = getItem(position);
        VideoViewHolder videoViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(resour,null);
            videoViewHolder = new VideoViewHolder(convertView);
            convertView.setTag(videoViewHolder);
        }else {
            videoViewHolder = (VideoViewHolder) convertView.getTag();
        }

        ItemWorker.videoItem(context,videoViewHolder,data);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, VideoDetailActivity.class).putExtra("videoId",data.id));
            }
        });
        return convertView;
    }
}
