package com.gu.userinfo.observablescroll.cloudmusic;

import android.view.View;

import com.gu.userinfo.observablescroll.BaseActivity;
import com.gu.userinfo.observablescroll.R;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/9/27
 */

public abstract class PtrActivity extends BaseActivity {

    private boolean mCanPull = true;
    private static final int MAX_PULL_DISTANCE = 200;

    protected void initPtrLayout() {
        final PtrClassicFrameLayout ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.pager_wrapper);
        ptrFrame.disableWhenHorizontalMove(true);
        ptrFrame.getHeader().setVisibility(View.INVISIBLE);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(ptrFrame::refreshComplete, 800);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canPull();
            }
        });

        ptrFrame.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
                mCanPull = validPullSize(ptrIndicator.getCurrentPosY());
                onPull(ptrIndicator);
            }
        });
    }

    private boolean validPullSize(int pullSize) {
        return pullSize <= MAX_PULL_DISTANCE;
    }

    public boolean canPull() {
        return mCanPull && checkCanDoRefresh();
    }

    public abstract void onPull(PtrIndicator ptrIndicator);

    public abstract boolean checkCanDoRefresh();
}
