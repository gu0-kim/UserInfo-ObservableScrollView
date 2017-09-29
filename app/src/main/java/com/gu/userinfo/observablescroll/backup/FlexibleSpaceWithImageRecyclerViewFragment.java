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

package com.gu.userinfo.observablescroll.backup;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gu.observableviewlibrary.ObservableRecyclerView;
import com.gu.observableviewlibrary.ScrollUtils;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.cloudmusic.CloudMusicTabActivity;
import com.nineoldandroids.view.ViewHelper;


public class FlexibleSpaceWithImageRecyclerViewFragment extends FlexibleSpaceWithImageBaseFragment<ObservableRecyclerView> {
    private static final String TAG = "TAG";
    private boolean first = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flexiblespacewithimagerecyclerview, container, false);
        final ObservableRecyclerView recyclerView = view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        final View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, container, false);
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, flexibleSpaceImageHeight));
        setDummyDataWithHeader(recyclerView, headerView);

        // TouchInterceptionViewGroup should be a parent view other than ViewPager.
        // This is a workaround for the issue #117:
        // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
        recyclerView.setTouchInterceptionViewGroup(view.findViewById(R.id.fragment_root));
        int scrollY = ((CloudMusicTabActivity) getActivity()).getScrollY();
        // Scroll to the specified offset after layout
        recoveryScrollState(recyclerView, scrollY);
        updateFlexibleSpace(scrollY, view);
        recyclerView.setScrollViewCallbacks(this);
        return view;
    }

    private void recoveryScrollState(final ObservableRecyclerView recyclerView, int scrollY) {
        ScrollUtils.addOnGlobalLayoutListener(recyclerView, () -> {
            int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
            int offset = scrollY % flexibleSpaceImageHeight;
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm != null && lm instanceof LinearLayoutManager) {
                ((LinearLayoutManager) lm).scrollToPositionWithOffset(0, -offset);
            }
        });
    }

    @Override
    public void setScrollY(int scrollY, int threshold) {
        View view = getView();
        if (view == null) {
            return;
        }
        ObservableRecyclerView recyclerView = view.findViewById(R.id.scroll);
        if (recyclerView == null) {
            return;
        }
        View firstVisibleChild = recyclerView.getChildAt(0);
        if (firstVisibleChild != null) {
            int offset = scrollY;
            int position = 0;
            if (threshold < scrollY) {
                int baseHeight = firstVisibleChild.getHeight();
                position = scrollY / baseHeight;
                offset = scrollY % baseHeight;
            }
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm != null && lm instanceof LinearLayoutManager) {
                ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, -offset);
            }
        }
    }

    @Override
    public void updateFlexibleSpace(int scrollY, View view) {
        if (first) {
            first = false;
            return;
        }
        updateScrollableMaskView(scrollY);
        // Also pass this event to parent Activity
        CloudMusicTabActivity parentActivity =
                (CloudMusicTabActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, view.findViewById(R.id.scroll));
        }
    }

    @Override
    public void updateScrollableMaskView(int scrollY) {
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        View recyclerViewBackground = getView().findViewById(R.id.list_background);
        ViewHelper.setTranslationY(recyclerViewBackground, Math.max(0, -scrollY + flexibleSpaceImageHeight));
    }
}
