/***********************************************************************************************************************
 * 
 * Copyright (C) 2014, 2015 by sunnsoft (http://www.sunnsoft.com)
 * http://www.sunnsoft.com/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package com.vanda.vandalibnetwork.arrayadapter;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class PullLoadArrayAdaper<T> extends ArrayAdapter<T> {

	protected boolean pullLoad;

	public boolean isPullLoad() {
		return pullLoad;
	}

	public void setPullLoad(boolean pullLoad) {
		this.pullLoad = pullLoad;
	}

	public PullLoadArrayAdaper(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public PullLoadArrayAdaper(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public PullLoadArrayAdaper(Context context, int resource,
			int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public PullLoadArrayAdaper(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public PullLoadArrayAdaper(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
	}

	public PullLoadArrayAdaper(Context context, int textViewResourceId,
			T[] objects) {
		super(context, textViewResourceId, objects);
	}
}
