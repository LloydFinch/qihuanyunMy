package com.qihuanyun.mobilevr.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.unity3d.player.UnityPlayer;

public class UnityPlayerActivity extends Activity
{
	private String url;
	public UnityPlayerActivity unityPlayerActivity;
	public UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra("url");
		unityPlayerActivity = this;

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);

		playMovic();

		setCallBack(new QuitInterface());
		if (mUnityPlayer.getSettings().getBoolean("hide_status_bar", true))
		{
			getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                       WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();
	}

	public void playMovic(){
		Log.e("------播放的url-------","url="+url);
		//本地视频，有file://   以此判断
		if (url.contains("file://")) {
			//播放本地视频
			Log.e("------local------","播放本地视频");
			UnityPlayer.UnitySendMessage("_Interface","PlayLocalMovie",url);
		} else {
			//播放网络视频
			Log.e("------network------","播放网络视频");
			UnityPlayer.UnitySendMessage("_Interface","PlayInternetMovie",url);
		}
	}
//
	// Quit Unity
//	@Override protected void onDestroy ()
//	{
//		mUnityPlayer.quit();
//		super.onDestroy();
//	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

    private static U3dNetInterface m_u3dinterface;
	/**
	 * @param传入接口类，不要实现unityPlayActivity
	 */
	public static void setCallBack(U3dNetInterface args){
		m_u3dinterface=args;
	}
	/**
	 *  调用，隐藏触发消息
	 */
	public static void QuitPlayer(){
		if(m_u3dinterface!=null)m_u3dinterface.excute();

	}
	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)   {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.moveTaskToBack(true);
			return true;
		}

		return super.onKeyDown(keyCode,event);
	}
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

	public class QuitInterface implements U3dNetInterface {
		public void excute(){
			Log.e("回调成功！", "OK!");
			unityPlayerActivity.moveTaskToBack(true);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		url = intent.getStringExtra("url");

		playMovic();
	}
}
