package com.gu.userinfo.observablescroll.sinaweibo;

import android.os.Bundle;
import android.view.View;

import com.gu.observableviewlibrary.ObservableScrollViewCallbacks;
import com.gu.observableviewlibrary.ScrollState;
import com.gu.observableviewlibrary.Scrollable;
import com.gu.userinfo.observablescroll.BaseFragment;
import com.gu.userinfo.observablescroll.R;


public abstract class ObservableFragment extends BaseFragment
        implements ObservableScrollViewCallbacks {


    public ObservableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (getView() == null) {
            return;
        }
        if (!firstScroll)
            updateFlexibleSpace(scrollY, getView());
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    public void setScrollY(int scrollY) {
        View view = getView();
        if (view == null) {
            return;
        }
        Scrollable scrollView = view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        scrollView.scrollVerticallyTo(scrollY);
    }

    abstract public void updateFlexibleSpace(int scrollY, View view);

    abstract public void syncMove(int deltaY);
}
