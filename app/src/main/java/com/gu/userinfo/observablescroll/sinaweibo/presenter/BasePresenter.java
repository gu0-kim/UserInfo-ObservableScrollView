package com.gu.userinfo.observablescroll.sinaweibo.presenter;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/3
 */

public interface BasePresenter {

    public void onInit();

    public void onDestroy();

    public void onScrollChanged(int scrollY, int deltaY);

    public void onPull(PtrIndicator indicator);
}
