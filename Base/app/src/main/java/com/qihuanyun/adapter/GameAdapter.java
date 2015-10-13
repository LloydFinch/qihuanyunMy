package com.qihuanyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qihuanyun.activity.GameDetailActivity;
import com.qihuanyun.pojo.GameContentData;
import com.qihuanyun.pojo.GameViewHolder;
import com.qihuanyun.utils.ItemWorker;
import com.vanda.vandalibnetwork.arrayadapter.PullLoadArrayAdaper;

import java.util.List;

public class GameAdapter extends PullLoadArrayAdaper<GameContentData.Data> {
    private List<GameContentData.Data> myList;
    private int res;
    private Context context;

    public GameAdapter(Context context, int resource, List<GameContentData.Data> list) {
        super(context, resource, list);
        this.myList = list;
        this.res = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GameContentData.Data data = getItem(position);
        GameViewHolder mGameViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(res,null);
            mGameViewHolder = new GameViewHolder(convertView);
            convertView.setTag(mGameViewHolder);
        }else {
            mGameViewHolder = (GameViewHolder) convertView.getTag();
        }

        ItemWorker.gameItem(context, mGameViewHolder, data);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, GameDetailActivity.class);
                it.putExtra("id",data.id);
                context.startActivity(it);
            }
        });
        return convertView;
    }
}
