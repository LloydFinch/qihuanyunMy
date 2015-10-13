package com.vanda.vandalibnetwork.view.utils;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

/**
 * Auto Scroll View Pager
 * <ul>
 * <strong>Basic Setting and Usage</strong>
 * <li>{@link #startAutoScroll()} start auto scroll, or
 * {@link #startAutoScroll(int)} start auto scroll delayed</li>
 * <li>{@link #stopAutoScroll()} stop auto scroll</li>
 * <li>{@link #setInterval(long)} set auto scroll time in milliseconds, default
 * is {@link #DEFAULT_INTERVAL}</li>
 * </ul>
 * <ul>
 * <strong>Advanced Settings and Usage</strong>
 * <li>{@link #setDirection(int)} set auto scroll direction</li>
 * <li>{@link #setCycle(boolean)} set whether automatic cycle when auto scroll
 * reaching the last or first item, default is true</li>
 * <li>{@link #setSlideBorderMode(int)} set how to process when sliding at the
 * last or first item</li>
 * <li>{@link #setStopScrollWhenTouch(boolean)} set whether stop auto scroll
 * when touching, default is true</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-30
 */
@SuppressLint("HandlerLeak")
public class AutoScrollViewPager extends ViewPager {

	public static final int DEFAULT_INTERVAL = 3500;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	/** do nothing when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_NONE = 0;
	/** cycle when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_CYCLE = 1;
	/** deliver event to parent when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

	/** auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL} **/
	private long interval = DEFAULT_INTERVAL;
	/** auto scroll direction, default is {@link #RIGHT} **/
	private int direction = RIGHT;
	/**
	 * whether automatic cycle when auto scroll reaching the last or first item,
	 * default is true
	 **/
	private boolean isCycle = true;
	/** whether stop auto scroll when touching, default is true **/
	private boolean stopScrollWhenTouch = true;
	/**
	 * how to process when sliding at the last or first item, default is
	 * {@link #SLIDE_BORDER_MODE_NONE}
	 **/
	private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
	/** whether animating when auto scroll at the last or first item **/
	private boolean isBorderAnimation = true;
	private boolean roundTrip = false;
	private Handler handler;
	private boolean isAutoScroll = false;
	private boolean isStopByTouch = false;
	private float touchX = 0f, downX = 0f;
	private CustomDurationScroller scroller = null;

	public static final int SCROLL_WHAT = 0;

	private static final boolean DEFAULT_BOUNDARY_CASHING = true;

	OnPageChangeListener mOuterPageChangeListener;
	private LoopPagerAdapterWrapper mAdapter;
	private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;

	/**
	 * helper function which may be used when implementing FragmentPagerAdapter
	 * 
	 * @param position
	 * @param count
	 * @return (position-1)%count
	 */
	public static int toRealPosition(int position, int count) {
		position = position - 1;
		if (position < 0) {
			position += count;
		} else {
			position = position % count;
		}
		return position;
	}

	/**
	 * If set to true, the boundary views (i.e. first and last) will never be
	 * destroyed This may help to prevent "blinking" of some views
	 * 
	 * @param flag
	 */
	public void setBoundaryCaching(boolean flag) {
		mBoundaryCaching = flag;
		if (mAdapter != null) {
			mAdapter.setBoundaryCaching(flag);
		}
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		mAdapter = new LoopPagerAdapterWrapper(adapter);
		mAdapter.setBoundaryCaching(mBoundaryCaching);
		super.setAdapter(mAdapter);
		setCurrentItem(0, true);
	}

	@Override
	public PagerAdapter getAdapter() {
		return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
	}

	@Override
	public int getCurrentItem() {
		return mAdapter != null ? mAdapter.toRealPosition(super
				.getCurrentItem()) : 0;
	}

	/*
	 * realItem是autoscrollviewpager的位置，便于我们的控�? * @see
	 * android.support.v4.view.ViewPager#setCurrentItem(int, boolean)
	 * 若此时item==图片的数量，那么将指向第�?��图片
	 */
	public void setCurrentItem(int item, boolean smoothScroll) {
		int realItem = mAdapter.toInnerPosition(item);
		super.setCurrentItem(realItem, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		if (getCurrentItem() != item) {
			setCurrentItem(item, true);
		}
	}

	public void setRoundTrip(boolean roundTrip) {
		this.roundTrip = roundTrip;
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOuterPageChangeListener = listener;
	};

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		private float mPreviousOffset = -1;
		private float mPreviousPosition = -1;

		@Override
		public void onPageSelected(int position) {

			int realPosition = mAdapter.toRealPosition(position);

			if (mPreviousPosition != realPosition) {
				mPreviousPosition = realPosition;
				if (mOuterPageChangeListener != null) {
					mOuterPageChangeListener.onPageSelected(realPosition);
				}
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			int realPosition = position;
			if (mAdapter != null) {
				realPosition = mAdapter.toRealPosition(position);
				if (positionOffset == 0
						&& mPreviousOffset == 0
						&& (position == 0 || position == mAdapter.getCount() - 1)) {
					setCurrentItem(realPosition, false);
				}
			}

			mPreviousOffset = positionOffset;
			if (mOuterPageChangeListener != null) {
				if (realPosition != mAdapter.getRealCount() - 1) {
					mOuterPageChangeListener.onPageScrolled(realPosition,
							positionOffset, positionOffsetPixels);
				} else {
					if (positionOffset > .5) {
						mOuterPageChangeListener.onPageScrolled(0, 0, 0);
					} else {
						mOuterPageChangeListener.onPageScrolled(realPosition,
								0, 0);
					}
				}
			}
		}

		// Viewpager.SCROLL_STATE_IDLE:表示该视图已处于停止状�?，没有动画过�? @Override
		public void onPageScrollStateChanged(int state) {
			if (mAdapter != null) {
				int position = AutoScrollViewPager.super.getCurrentItem();
				int realPosition = mAdapter.toRealPosition(position);
				if (state == ViewPager.SCROLL_STATE_IDLE
						&& (position == 0 || position == mAdapter.getCount() - 1)) {
					setCurrentItem(realPosition, false);
				}
			}
			if (mOuterPageChangeListener != null) {
				mOuterPageChangeListener.onPageScrollStateChanged(state);
			}
		}
	};

	public AutoScrollViewPager(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public AutoScrollViewPager(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	private void init(Context context) {
		super.setOnPageChangeListener(onPageChangeListener);
		handler = new MyHandler();
		setViewPagerScroller();
	}

	/**
	 * start auto scroll, first scroll delay time is {@link #getInterval()}
	 */
	public void startAutoScroll() {
		isAutoScroll = true;
		sendScrollMessage(interval);
	}

	/**
	 * start auto scroll
	 * 
	 * @param delayTimeInMills
	 *            first scroll delay time
	 */
	public void startAutoScroll(int delayTimeInMills) {
		isAutoScroll = true;
		sendScrollMessage(delayTimeInMills);
	}

	/**
	 * stop auto scroll
	 */
	public void stopAutoScroll() {
		isAutoScroll = false;
		handler.removeMessages(SCROLL_WHAT);
	}

	/**
	 * set the factor by which the duration of sliding animation will change
	 */
	public void setScrollDurationFactor(double scrollFactor) {
		scroller.setScrollDurationFactor(scrollFactor);
	}

	private void sendScrollMessage(long delayTimeInMills) {
		/** remove messages before, keeps one message is running at most **/
		handler.removeMessages(SCROLL_WHAT);
		handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
	}

	/**
	 * set ViewPager scroller to change animation duration when sliding
	 */
	private void setViewPagerScroller() {
		try {
			Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
			scrollerField.setAccessible(true);
			Field interpolatorField = ViewPager.class
					.getDeclaredField("sInterpolator");
			interpolatorField.setAccessible(true);
			scroller = new CustomDurationScroller(getContext(),
					(Interpolator) interpolatorField.get(null));
			scroller.setScrollDurationFactor(10.0);
			scrollerField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * scroll only once
	 */
	public void scrollOnce() {
		PagerAdapter adapter = getAdapter();// 是viewpager的adapter
		int currentItem = getCurrentItem();// 是autoscrollviewpager的位�?
		int totalCount;
		// totalCount=adapter.getCount()指的是图片的多少
		if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
			return;
		}
		/**
		 * 若向左，直接指向�?���?��，所以realPosition=totalCount-1;
		 * 若向右，指向下一张图片，若这时�?图片已经过了�?���?��了，这时候super.getCurrentItem()获得�? *
		 * totalCount,那么realPosition+1为totalCount;正好是存放第�?��图片，即realPosition=0;
		 * nextItem继续添加，此时position=1;
		 */

		int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
		if (nextItem == -1) {//
			if (isCycle) {
				if (this.roundTrip) {
					direction = RIGHT;
					currentItem = 1;
					setCurrentItem(currentItem, isBorderAnimation);
				} else {
					// int position =
					// AutoScrollViewPager.super.getCurrentItem();
					// int realPosition = mAdapter.toRealPosition(position);
					setCurrentItem(adapter.getCount() - 1, false);
					// setCurrentItem(totalCount-1,isBorderAnimation);
				}
			}
		} else if (nextItem == totalCount) {
			if (isCycle) {
				if (this.roundTrip) {
					direction = LEFT;
					currentItem = totalCount - 2;
					setCurrentItem(currentItem, isBorderAnimation);
				} else {
					// int position =
					// AutoScrollViewPager.super.getCurrentItem();
					// int realPosition = mAdapter.toRealPosition(position);
					// setCurrentItem(0,
					// isBorderAnimation);这样的话，每次跳转到第一页时候都会经历后几页，所以这里要多加两个view
					setCurrentItem(0, false);
				}
				// setCurrentItem(realPosition+1, isBorderAnimation);
				// setCurrentItem(0, isBorderAnimation);
			}
		} else {
			setCurrentItem(nextItem, true);
		}
	}

	/**
	 * <ul>
	 * if stopScrollWhenTouch is true
	 * <li>if event is down, stop auto scroll.</li>
	 * <li>if event is up, start auto scroll again.</li>
	 * </ul>
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (stopScrollWhenTouch) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN && isAutoScroll) {
				isStopByTouch = true;
//				 setScrollDurationFactor(2);
				stopAutoScroll();
			} else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
//				 setScrollDurationFactor(10);
				startAutoScroll();
			}
		}

		if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT
				|| slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
			touchX = ev.getX();
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				downX = touchX;
			}
			int currentItem = getCurrentItem();
			PagerAdapter adapter = getAdapter();
			int pageCount = adapter == null ? 0 : adapter.getCount();
			/**
			 * current index is first one and slide to right or current index is
			 * last one and slide to left.<br/>
			 * if slide border mode is to parent, then
			 * requestDisallowInterceptTouchEvent false.<br/>
			 * else scroll to last one when current item is first one, scroll to
			 * first one when current item is last one.
			 */
			if ((currentItem == 0 && downX <= touchX)
					|| (currentItem == pageCount - 1 && downX >= touchX)) {
				if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					if (pageCount > 1) {
						setCurrentItem(pageCount - currentItem - 1,
								isBorderAnimation);
					}
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				return super.onTouchEvent(ev);
			}
		}
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(ev);
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// if(mActivity.get() == null) {
			// return;
			// }
			super.handleMessage(msg);

			switch (msg.what) {
			case SCROLL_WHAT:
				scrollOnce();
				sendScrollMessage(interval);
			default:
				break;
			}
		}
	}

	/**
	 * get auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 * 
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * set auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 * 
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * get auto scroll direction
	 * 
	 * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
	 */
	public int getDirection() {
		return (direction == LEFT) ? LEFT : RIGHT;
	}

	/**
	 * set auto scroll direction
	 * 
	 * @param direction
	 *            {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * whether automatic cycle when auto scroll reaching the last or first item,
	 * default is true
	 * 
	 * @return the isCycle
	 */
	public boolean isCycle() {
		return isCycle;
	}

	/**
	 * set whether automatic cycle when auto scroll reaching the last or first
	 * item, default is true
	 * 
	 * @param isCycle
	 *            the isCycle to set
	 */
	public void setCycle(boolean isCycle) {
		this.isCycle = isCycle;
	}

	/**
	 * whether stop auto scroll when touching, default is true
	 * 
	 * @return the stopScrollWhenTouch
	 */
	public boolean isStopScrollWhenTouch() {
		return stopScrollWhenTouch;
	}

	/**
	 * set whether stop auto scroll when touching, default is true
	 * 
	 * @param stopScrollWhenTouch
	 */
	public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
		this.stopScrollWhenTouch = stopScrollWhenTouch;
	}

	/**
	 * get how to process when sliding at the last or first item
	 * 
	 * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE},
	 *         {@link #SLIDE_BORDER_MODE_TO_PARENT},
	 *         {@link #SLIDE_BORDER_MODE_CYCLE}, default is
	 *         {@link #SLIDE_BORDER_MODE_NONE}
	 */
	public int getSlideBorderMode() {
		return slideBorderMode;
	}

	/**
	 * set how to process when sliding at the last or first item
	 * 
	 * @param slideBorderMode
	 *            {@link #SLIDE_BORDER_MODE_NONE},
	 *            {@link #SLIDE_BORDER_MODE_TO_PARENT},
	 *            {@link #SLIDE_BORDER_MODE_CYCLE}, default is
	 *            {@link #SLIDE_BORDER_MODE_NONE}
	 */
	public void setSlideBorderMode(int slideBorderMode) {
		this.slideBorderMode = slideBorderMode;
	}

	/**
	 * whether animating when auto scroll at the last or first item, default is
	 * true
	 * 
	 * @return
	 */
	public boolean isBorderAnimation() {
		return isBorderAnimation;
	}

	/**
	 * set whether animating when auto scroll at the last or first item, default
	 * is true
	 * 
	 * @param isBorderAnimation
	 */
	public void setBorderAnimation(boolean isBorderAnimation) {
		this.isBorderAnimation = isBorderAnimation;
	}
}
