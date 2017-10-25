package com.gu.userinfo.observablescroll.sinaweibo_.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gu.userinfo.observablescroll.sinaweibo_.rxbus.Rxbus;
import com.gu.userinfo.observablescroll.sinaweibo_.view.activity.UserInfoView;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.PageView;

import io.reactivex.disposables.Disposable;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/19
 */
public class UserInfoPresentImpl<T extends UserInfoView> extends UserInfoPresent<T> {

  private Disposable busDispose;

  public UserInfoPresentImpl(@NonNull T view) {
    setView(view);
  }

  @Override
  public void onCreate() {
    Log.e("TAG", "onCreate: register bus!");
    busDispose =
        Rxbus.getInstance()
            .registerBus(
                msg -> {
                  if (view.getCurrentIndex() != msg.getIndex()) return;
                  if (msg.getType().equals(Rxbus.MsgType.START)) {
                    view.horizontalScrollable(false);
                    view.showProgressBar();
                    view.showLoading();
                    Log.e("TAG", "receive msg start ! index:" + msg.getIndex());
                  } else if (msg.getType().equals(Rxbus.MsgType.FIN)) {
                    view.horizontalScrollable(true);
                    view.stopLoading();
                    view.loadComplete();
                    Log.e("TAG", "receive msg finish ! index:" + msg.getIndex());
                  }
                });
  }

  @Override
  public void showLoading() {
    view.showLoading();
  }

  @Override
  public void refreshView() {
    PageView itemView = view.getCurrentView();
    itemView.getPresent().loadData();
  }

  @Override
  public void onDestroy() {
    Rxbus.getInstance().dispose(busDispose);
    busDispose = null;
    this.view = null;
  }

  @Override
  public boolean canPull() {
    return view.canPull();
  }
}
