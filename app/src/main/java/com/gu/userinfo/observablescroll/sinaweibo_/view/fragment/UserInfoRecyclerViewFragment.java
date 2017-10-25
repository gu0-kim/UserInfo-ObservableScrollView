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

package com.gu.userinfo.observablescroll.sinaweibo_.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gu.observableviewlibrary.ObservableRecyclerView;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.SimpleHeaderRecyclerAdapter;
import com.gu.userinfo.observablescroll.sinaweibo_.presenter.PageViewPresent;
import com.gu.userinfo.observablescroll.sinaweibo_.rxbus.Rxbus;
import com.gu.userinfo.observablescroll.sinaweibo_.view.activity.TabAndViewPagerView;

import java.util.ArrayList;
import java.util.List;

public class UserInfoRecyclerViewFragment extends ObservableFragment implements PageView {
  private boolean first = true;
  private int mScrollY;
  private int mDivide, mFlexibleSpaceHeight;
  private ArrayList<String> data;
  SimpleHeaderRecyclerAdapter adapter;
  PageViewPresent present;
  public static final String TAG = "TAG";
  private boolean loading;

  private void setArguments(int index) {
    Bundle b = new Bundle();
    b.putInt("index", index);
    setArguments(b);
  }

  private int getIndex() {
    return getArguments().getInt("index", -1);
  }

  public static UserInfoRecyclerViewFragment getInstance(int index) {
    UserInfoRecyclerViewFragment f = new UserInfoRecyclerViewFragment();
    f.setArguments(index);
    return f;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFlexibleSpaceHeight =
        getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
    mDivide = getResources().getDimensionPixelOffset(R.dimen.divide_height);
    View view = inflater.inflate(R.layout.userinfo_list_fragment, container, false);
    final ObservableRecyclerView recyclerView = view.findViewById(R.id.scroll);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setHasFixedSize(false);
    final View headerView =
        LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, container, false);
    headerView.setLayoutParams(
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFlexibleSpaceHeight));
    data = new ArrayList<>();
    adapter = new SimpleHeaderRecyclerAdapter(getContext(), data, headerView);
    recyclerView.setAdapter(adapter);
    //        setDummyDataWithHeader(recyclerView, headerView);
    recyclerView.setTouchInterceptionViewGroup(view.findViewById(R.id.fragment_root));
    recyclerView.setScrollViewCallbacks(this);

    //        loading = true;
    //        recyclerView.setVisibility(View.GONE);
    //        view.findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
    present = new PageViewPresent(this);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    present.loadData();
    Log.e(TAG, "onActivityCreated: loadData");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Log.e(TAG, "onDestroyView!");
    present.onDestroy();
    adapter = null;
    clearData();
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
    //        if (first) {
    //            first = false;
    //            return;
    //        }
    TabAndViewPagerView parentActivity = (TabAndViewPagerView) getActivity();
    if (parentActivity != null) {
      parentActivity.onScrollChanged(scrollY, scrollY - mScrollY);
    }
    mScrollY = scrollY;
  }

  @Override
  public void syncMove(int deltaY) {
    setScrollY(mScrollY + deltaY);
  }

  @Override
  public void showLoading() {
    if (getView() != null) {
      getView().findViewById(R.id.erro_page).setVisibility(View.GONE);
      getView().findViewById(R.id.scroll).setVisibility(View.GONE);
      getView().findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
    }
    loading = true;
  }

  @Override
  public void stopLoading() {
    if (getView() != null) {
      getView().findViewById(R.id.scroll).setVisibility(View.VISIBLE);
      getView().findViewById(R.id.loading_view).setVisibility(View.GONE);
    }
    loading = false;
  }

  @Override
  public void notifyStartLoad() {
    Rxbus.getInstance().sendMsg(new Rxbus.Msg(getIndex(), Rxbus.MsgType.START, "finsh load!"));
  }

  @Override
  public boolean isTop() {
    return mScrollY == 0;
  }

  @Override
  public void notifyFinishLoad() {
    Rxbus.getInstance().sendMsg(new Rxbus.Msg(getIndex(), Rxbus.MsgType.FIN, "finsh load!"));
  }

  @Override
  public boolean isLoading() {
    return loading;
  }

  @Override
  public void showError(String erro) {
    Log.e(TAG, "showError: " + erro);
    if (getView() != null) {
      getView().findViewById(R.id.scroll).setVisibility(View.GONE);
      getView().findViewById(R.id.erro_page).setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void setData(List<String> list) {
    if (list != null && !list.isEmpty()) {
      data.clear();
      data.addAll(list);
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void clearData() {
    if (data != null) data.clear();
  }

  @Override
  public PageViewPresent getPresent() {
    return present;
  }
}
