package com.gu.userinfo.observablescroll.sinaweibo.presenter;

import android.view.View;

import com.gu.userinfo.observablescroll.sinaweibo.view.BaseView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/3
 */

public class WeiboPresenter implements BasePresenter, PtrUIHandler, PtrHandler {
    private BaseView view;
    private boolean mValidPullSize = true;

    private WeiboPresenter() {
        this(null);
    }

    public WeiboPresenter(BaseView view) {
        this.view = view;
    }

    @Override
    public void onInit() {
        view.onInit(this);
    }

    @Override
    public void onScrollChanged(int scrollY, int deltaY) {
        if (deltaY > 0) {
            //手向上滑动
            view.moveUp(deltaY);
        } else {
            //手向下滑动
            if (view.needMoveDown(scrollY)) {
                view.moveDown(scrollY);
            }
        }
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        frame.postDelayed(frame::refreshComplete, 800);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        mValidPullSize = view.validPullSize(ptrIndicator.getCurrentPosY());
        onPull(ptrIndicator);
    }


    public void onRefreshBegin(final PtrFrameLayout frame) {
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mValidPullSize && view.canPull();
    }

    @Override
    public void onPull(PtrIndicator indicator) {
        view.pull(indicator);
    }

    @Override
    public void onDestroy() {
        this.view = null;
    }
}
