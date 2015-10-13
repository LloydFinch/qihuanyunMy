package com.qihuanyun.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihuanyun.BaseApplication;
import com.qihuanyun.R;
import com.qihuanyun.database.DbHelper;
import com.qihuanyun.pojo.VideoInfo;
import com.qihuanyun.utils.FileUtil;
import com.qihuanyun.utils.StorageUtil;
import com.roamer.slidelistview.SlideBaseAdapter;
import com.roamer.slidelistview.SlideListView;
import com.umeng.analytics.MobclickAgent;
import com.vanda.vandalibnetwork.fragmentactivity.BaseActivityActionBarNoNetWork;
import com.vanda.vandalibnetwork.utils.BitmapUtils;
import com.wzl.vandan.dialog.VandaAlert;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2015/8/19.
 */
public class LoaclVideoActivity extends BaseActivityActionBarNoNetWork {

    private String[] allStorages;
    private List<VideoInfo> mVideoList = new ArrayList<VideoInfo>();
    private SlideListView mListView;
    private FileAdapter mFileAdapter;
    private Dialog vandaAlert;
    private ScanVideoTask myTask;
    private TextView textView;

    private DbHelper<VideoInfo> mDbHelper;
    private Map<String,Object> map = new HashMap<String,Object>(2);

    private boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("本地视频");
        setContentView(R.layout.local_video_main);
        mDbHelper = new DbHelper<VideoInfo>();
        mVideoList = mDbHelper.queryForAll(VideoInfo.class);      //从数据库获取数据记录

        mListView = (SlideListView) findViewById(R.id.my_listview);
        mFileAdapter = new FileAdapter(this);
        mListView.setAdapter(mFileAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(LoaclVideoActivity.this, SubUnityPlayerActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("oneshot", 0);
//                intent.putExtra("configchange", 0);
//                Uri uri = Uri.fromFile(new File(mVideoList.get(position).path));
//                intent.setDataAndType(uri, "video/*");
                intent.putExtra("url", "file://" + mVideoList.get(position).path);
                startActivity(intent);
            }
        });

        vandaAlert = VandaAlert.createLoadingDialog(LoaclVideoActivity.this,"正在扫描...");
        vandaAlert.setCanceledOnTouchOutside(false);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (vandaAlert != null){
                vandaAlert.dismiss();
            }
            if (isRunning) {
                VandaAlert.CreateOKorNODialog(this, "正在扫描本地视频，确定要离开吗？",
                        new VandaAlert.OnOk() {
                            @Override
                            public void setOk(Dialog dialog) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                    isRunning = false;
                                    finish();
                                }
                            }

                            @Override
                            public void setCancel(Dialog dialog) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        }).show();
            }else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_video_menu, menu);
        final View view = menu.findItem(R.id.local_video_scan_menu).getActionView();
        if (view != null){
            textView = (TextView) view.findViewById(R.id.right_title_text);
//            textView.setTextColor(getResources().getColor(R.color.common_text_color));
//            textView.setText("扫描视频");


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vandaAlert.show();
                    view.setClickable(false);
                    allStorages = StorageUtil.getAllStorage(LoaclVideoActivity.this);
                    myTask = new ScanVideoTask();
                    myTask.execute();
                    isRunning = true;

                }
            });
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isRunning) {
                VandaAlert.CreateOKorNODialog(this, "正在扫描本地视频，确定要离开吗？",
                        new VandaAlert.OnOk() {
                            @Override
                            public void setOk(Dialog dialog) {
                                dialog.dismiss();
                                isRunning = false;
                                finish();
                            }

                            @Override
                            public void setCancel(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
            }else {
                finish();
            }
        }
        return false;
    }

    /** 扫描SD卡 */
    private class ScanVideoTask extends AsyncTask<Void, VideoInfo, Void> {

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub

            Toast.makeText(LoaclVideoActivity.this, "扫描结束！", Toast.LENGTH_LONG).show();
            textView.setClickable(true);
            vandaAlert.dismiss();
            isRunning = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(final String path : allStorages){
                getVideoFile(new File(path));
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(VideoInfo... values) {
            mVideoList.add(values[0]);
            mDbHelper.create(values[0]);
            mFileAdapter.notifyDataSetChanged();
        }

        /** 遍历所有文件夹，查找出视频文件 */
        private void getVideoFile(File file) {
            file.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {

                    String name = file.getName();


                    if (name.endsWith(".mp4")
                            || name.endsWith(".3gp")
                            || name.endsWith(".wmv")
                            || name.endsWith(".rm")
                            || name.endsWith(".rmvb")
                            || name.endsWith(".mov")
                            || name.endsWith(".avi")
                            || name.endsWith(".3gp")
                            || name.endsWith(".flv")) {
                        VideoInfo vi = new VideoInfo();
                        vi.displayName = file.getName();
                        vi.path = file.getAbsolutePath();
                        try {
                            vi.Size = FileUtil.getAutoFileOrFilesSize(vi.path);
                            Log.e("----video-size------", "size=" + vi.Size);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //时间
                        Date curretTime = new Date(file.lastModified());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
                        vi.time = format.format(curretTime);


                        if (!isExists(vi)) {                //如果文件在数据库不存在，入库并获取缩略图
                            Bitmap bitmap = null;
                            bitmap = getVideoThumbnail(vi.path, 120, 120, MediaStore.Video.Thumbnails.MINI_KIND);
                            if (bitmap == null)
                                bitmap = BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.default_video_img);//默认图片

                            //将缩略图存到视频当前路径
                            File thum = new File(BaseApplication.SOBF_VIDEO_THUMB, UUID.randomUUID().toString());

                            if (thum.exists()) {
                                thum.delete();
                            }

                            vi.thumb_path = thum.getAbsolutePath();

                            try {
                                FileOutputStream iStream = null;
                                iStream = new FileOutputStream(thum);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, iStream);
                                //iStream.flush();
                                Log.e("写入成功！", "写入成功！");
                                iStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            publishProgress(vi);
                        }

                        return true;
                    } else if (file.isDirectory() && isRunning) {
                        //屏蔽qq和微信目录
                        if (name.equalsIgnoreCase("tencent")) {
                            return true;
                        } else {
                            getVideoFile(file);
                        }
                    }

                    return false;
                }
            });
        }



    }



    /**
     * 获取视频缩略图
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 判断数据库是否存在该数据
     * */
    private boolean isExists(VideoInfo videoInfo){
        map.put("path",videoInfo.path);
        map.put("time", videoInfo.time);
        if (mDbHelper.exists(videoInfo,map)){
            return true;
        }else {
            return false;
        }
    }

    private class FileAdapter extends SlideBaseAdapter {


        public FileAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mVideoList.size();
        }


        @Override
        public VideoInfo getItem(int position) {
            // TODO Auto-generated method stub
            return mVideoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final VideoInfo f = getItem(position);
            if (convertView == null) {

                convertView = createConvertView(position);
                viewHolder = new ViewHolder();
                viewHolder.delete = (Button) convertView.findViewById(R.id.right_back_delete);
                viewHolder.title = ((TextView) convertView.findViewById(R.id.video_title));
                viewHolder.time = ((TextView) convertView.findViewById(R.id.video_time));
                viewHolder.size = ((TextView) convertView.findViewById(R.id.video_size));
                viewHolder.imageView = ((ImageView)convertView.findViewById(R.id.video_img));

                convertView.setTag(viewHolder);
            }else {
                 viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(f.displayName);


            viewHolder.time.setText(f.time);
            viewHolder.size.setText(f.Size);
            Bitmap bitmap = BitmapUtils.decodeBitmapFromSDCard(f.thumb_path,80,80);
            //如果本地缓存图片被删除了，重新获取缩略图
            if (bitmap != null) {
                viewHolder.imageView.setImageBitmap(bitmap);
            }else {
                bitmap = getVideoThumbnail(f.path, 80, 80, MediaStore.Video.Thumbnails.MINI_KIND);
                if (bitmap == null)
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.default_video_img);//默认图片

                viewHolder.imageView.setImageBitmap(bitmap);

            }


            if (viewHolder.delete != null){

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDbHelper.remove(mVideoList.get(position));
                        mVideoList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getFrontViewId(int position) {
            return R.layout.local_video_item_font;
        }

        @Override
        public int getLeftBackViewId(int position) {
            return 0;
        }

        @Override
        public int getRightBackViewId(int position) {
            return R.layout.local_video_item_right_back;
        }

        private class ViewHolder{
            Button delete;
            TextView title;
            TextView size;
            TextView time;
            ImageView imageView;
        }

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
