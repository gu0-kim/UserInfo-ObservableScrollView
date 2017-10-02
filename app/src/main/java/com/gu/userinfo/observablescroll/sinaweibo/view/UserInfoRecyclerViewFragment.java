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

package com.gu.userinfo.observablescroll.sinaweibo.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gu.observableviewlibrary.ObservableRecyclerView;
import com.gu.userinfo.observablescroll.R;


public class UserInfoRecyclerViewFragment extends ObservableFragment {
    private boolean first = true;
    private int mScrollY;
    private int mDivide, mFlexibleSpaceHeight;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mDivide = getResources().getDimensionPixelOffset(R.dimen.divide_height);
        View view = inflater.inflate(R.layout.userinfo_list_fragment, container, false);
        final ObservableRecyclerView recyclerView = view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        final View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, container, false);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFlexibleSpaceHeight));
        setDummyDataWithHeader(recyclerView, headerView);
        recyclerView.setTouchInterceptionViewGroup(view.findViewById(R.id.fragment_root));
        recyclerView.setScrollViewCallbacks(this);
        return view;
    }


    @Override
    public void setScrollY(int scrollY) {
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
            if (scrollY > mFlexibleSpaceHeight && scrollY < mFlexibleSpaceHeight + mDivide) {
                position = 1;
                offset = scrollY - mFlexibleSpaceHeight;
            } else if (scrollY >= mFlexibleSpaceHeight + mDivide) {
                int baseHeight = firstVisibleChild.getHeight();
                position = (scrollY - mFlexibleSpaceHeight - mDivide) / baseHeight + 2;
                offset = (scrollY - mFlexibleSpaceHeight - mDivide) % baseHeight;
            }
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm != null && lm instanceof LinearLayoutManager) {
                ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, -offset);
                mScrollY = scrollY;
            }
        }
    }

    @Override
    public void updateFlexibleSpace(int scrollY, View view) {
        if (first) {
            first = false;
            return;
        }
        SinaWeiBoUserInfoActivity parentActivity =
                (SinaWeiBoUserInfoActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.getPresenter().onScrollChanged(scrollY, scrollY - mScrollY);
        }
        mScrollY = scrollY;
    }

    @Override
    public void syncMove(int deltaY) {
        setScrollY(mScrollY + deltaY);
    }

}
