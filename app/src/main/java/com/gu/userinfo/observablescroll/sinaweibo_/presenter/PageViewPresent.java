package com.gu.userinfo.observablescroll.sinaweibo_.presenter;

import com.gu.userinfo.observablescroll.sinaweibo_.module.HttpModule;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.PageView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */
public class PageViewPresent {
  private PageView view;
  private CompositeDisposable disposables;

  public PageViewPresent(PageView view) {
    this.view = view;
    disposables = new CompositeDisposable();
  }

  public void refresh() {
    view.showLoading();
    loadData();
  }

  public void loadData() {
    disposables.add(
        Observable.just(view)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(PageView::showLoading)
            .doOnNext(PageView::notifyStartLoad)
            .observeOn(Schedulers.io())
            .flatMap(pageView -> HttpModule.getInstance().connectUrl("item"))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                strings -> {
                  view.setData(strings);
                  view.stopLoading();
                  view.notifyFinishLoad();
                },
                throwable -> {
                  view.stopLoading();
                  view.showError(throwable.getMessage());
                  view.notifyFinishLoad();
                }));
  }

  public void onDestroy() {
    disposables.dispose();
    disposables = null;
    this.view = null;
  }
}
