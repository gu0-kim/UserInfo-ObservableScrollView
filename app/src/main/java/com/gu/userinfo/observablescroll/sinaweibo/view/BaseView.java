package com.gu.userinfo.observablescroll.sinaweibo.view;

import com.gu.userinfo.observablescroll.sinaweibo.presenter.BasePresenter;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/3
 */

public interface BaseView {

    public void onInit(BasePresenter presenter);

    public boolean canPull();

    public boolean validPullSize(int size);

    public void propagateScroll(int deltaY);

    public void translateTab(int scrollY, boolean animated);

    public void moveUp(int deltaY);

    public void moveDown(int scrollY);

    public boolean needMoveDown(int scrollY);

    public void pull(PtrIndicator indicator);

    public BasePresenter getPresenter();
}
