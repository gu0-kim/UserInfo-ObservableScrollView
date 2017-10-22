package com.gu.userinfo.observablescroll.sinaweibo_.view.activity;

import android.support.v4.app.Fragment;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/19
 */

public interface UserInfoView {
    public void showLoading();

    public void showLoadError();

    public void loadComplete();

    public boolean canPull();

    public boolean validPullSize(int pullSize);

    public Fragment getCurrentView();
}
