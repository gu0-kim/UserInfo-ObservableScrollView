/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gu.userinfo.observablescroll;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

public abstract class FlexibleSpaceWithImageBaseFragment<S extends Scrollable> extends BaseFragment
    implements ObservableScrollViewCallbacks {

    public static final String ARG_SCROLL_Y = "ARG_SCROLL_Y";
    private static final String ARG_FRAGMENT_NAME = "FRAGMENT_NAME";
    private static final String TAG = "TAG";

    public void setArguments(int scrollY) {
        if (0 <= scrollY) {
            Bundle args = new Bundle();
            args.putInt(ARG_SCROLL_Y, scrollY);
            setArguments(args);
        }
    }

    public void setArguments(int scrollY, int name) {
        if (0 <= scrollY) {
            Bundle args = new Bundle();
            args.putInt(ARG_SCROLL_Y, scrollY);
            args.putInt(ARG_FRAGMENT_NAME, name);
            setArguments(args);
        }
    }

    public void setScrollY(int scrollY, int threshold) {
        View view = getView();
        if (view == null) {
            return;
        }
        Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        scrollView.scrollVerticallyTo(scrollY);
    }

    public boolean isScrollTop() {
        View view = getView();
        if (view == null) {
            return false;
        }
        Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return false;
        }
        return scrollView.getCurrentScrollY() == 0;
    }

    protected void updateFlexibleSpace(int scrollY) {
        updateFlexibleSpace(scrollY, getView());
    }

    protected abstract void updateFlexibleSpace(int scrollY, View view);

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (getView() == null) {
            return;
        }
        if (!firstScroll)
            updateFlexibleSpace(scrollY, getView());
    }

    @Override
    public final void onDownMotionEvent() {
        // We don't use this callback in this pattern.
    }

    @Override
    public final void onUpOrCancelMotionEvent(ScrollState scrollState) {
        // We don't use this callback in this pattern.
        Log.e(TAG, "---onUpOrCancelMotionEvent: " + scrollState);
    }

    protected S getScrollable() {
        View view = getView();
        return view == null ? null : (S) view.findViewById(R.id.scroll);
    }
}
