package com.gu.userinfo.observablescroll.sinaweibo_.presenter;

import com.gu.userinfo.observablescroll.sinaweibo_.view.activity.UserInfoView;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */

public abstract class UserInfoPresent<T extends UserInfoView> {
    protected T view;

    public void setView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    abstract public void onInit();

    abstract public void showLoading();

    abstract public void refreshView();

    abstract public void onDestroy();

    abstract public boolean canPull();

}
