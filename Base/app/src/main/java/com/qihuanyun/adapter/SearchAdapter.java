package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qihuanyun.R;
import com.qihuanyun.activity.GameDetailActivity;
import com.qihuanyun.activity.SearchActivity;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.GameViewHolder;
import com.qihuanyun.pojo.SearchData;
import com.qihuanyun.pojo.VideoViewHolder;
import com.qihuanyun.utils.ExtUtils;
import com.qihuanyun.utils.ItemWorker;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;

import java.util.List;

public class SearchAdapter extends PullLoadArrayAdaper<SearchData.Data> {
    private Context context;
    private boolean flag = false;
    private List<SearchData.Data> list;
    private int currentType;//当前item类型

    public SearchAdapter(Context context, int resource, List<SearchData.Data> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SearchData.Data data = getItem(position);
        currentType = list.get(position).type;

        //电影电影=2,游戏=3
        if (currentType == 2) {
            //电影
            VideoViewHolder mVideoViewHolder = null;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
                mVideoViewHolder = new VideoViewHolder(convertView);
                convertView.setTag(mVideoViewHolder);
            }else {
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

            if(ExtUtils.isNotEmpty(SearchActivity.key)) {
                mVideoViewHolder.title.setText(ExtUtils.fillColor(context, data.title, SearchActivity.key, R.color.red));
                mVideoViewHolder.desc.setText(ExtUtils.fillColor(context, data.summary, SearchActivity.key, R.color.red));
            }
        } else if (currentType == 3) {
            //游戏
            GameViewHolder mGameViewHolder = null;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.game_list_item, null);
                mGameViewHolder = new GameViewHolder(convertView);
                convertView.setTag(mGameViewHolder);
            }else {
                if((Integer)convertView.findViewById(R.id.title).getTag() == 2){
                    convertView = LayoutInflater.from(context).inflate(R.layout.game_list_item, null);
                    mGameViewHolder = new GameViewHolder(convertView);
                    convertView.setTag(mGameViewHolder);
                }else{
                    mGameViewHolder = (GameViewHolder) convertView.getTag();
                }
            }

            convertView.findViewById(R.id.title).setTag(3);

            ItemWorker.gameItem(context, mGameViewHolder, new GameContentData().new Data(data.id, data.title, data.summary, data.imgUrl,data.url, data.type));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, GameDetailActivity.class);
                    it.putExtra("id",data.id);
                    context.startActivity(it);
                }
            });

            if(ExtUtils.isNotEmpty(SearchActivity.key)) {
                mGameViewHolder.title.setText(ExtUtils.fillColor(context, data.title, SearchActivity.key, R.color.red));
                mGameViewHolder.desc.setText(ExtUtils.fillColor(context, data.summary, SearchActivity.key, R.color.red));
            }
        }

        return convertView;
    }
}
