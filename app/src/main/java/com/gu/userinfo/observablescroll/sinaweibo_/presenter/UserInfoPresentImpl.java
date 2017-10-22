package com.gu.userinfo.observablescroll.sinaweibo_.presenter;

import android.support.annotation.NonNull;

import com.gu.userinfo.observablescroll.sinaweibo_.module.HttpModule;
import com.gu.userinfo.observablescroll.sinaweibo_.view.activity.UserInfoView;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.PageView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/19
 */

public class UserInfoPresentImpl<T extends UserInfoView> extends UserInfoPresent<T> {

    private CompositeDisposable dp;


    public UserInfoPresentImpl(@NonNull T view) {
        setView(view);
        dp = new CompositeDisposable();
    }

    @Override
    public void onInit() {

    }

    @Override
    public void showLoading() {
        view.showLoading();
    }

    @Override
    public void refreshView() {
        PageView itemView = (PageView) view.getCurrentView();
        dp.add(Observable.just(itemView).doOnNext(PageView::showLoading
        ).flatMap(p -> HttpModule.getInstance().connectUrl("url")).observeOn(AndroidSchedulers.mainThread()).subscribe(
                strings -> {
                    itemView.setData(strings);
                    itemView.stopLoading();
                }
                , throwable -> {
                    itemView.stopLoading();
                    itemView.showError(throwable.getMessage());
                    view.loadComplete();
                }, () -> view.loadComplete()));
    }

    @Override
    public void onDestroy() {
        dp.dispose();
        this.view = null;
    }

    @Override
    public boolean canPull() {
        return view.canPull();
    }
}
