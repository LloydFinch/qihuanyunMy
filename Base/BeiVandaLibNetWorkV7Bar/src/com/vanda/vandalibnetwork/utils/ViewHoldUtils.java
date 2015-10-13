package com.vanda.vandalibnetwork.utils;

import android.util.SparseArray;
import android.view.View;

public class ViewHoldUtils {
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T getById(View view, int id) {
		View childView = view.findViewById(id);
		return (T) childView;
	}
}
