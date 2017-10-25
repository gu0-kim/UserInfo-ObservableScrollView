package com.gu.userinfo.observablescroll.sinaweibo_.view.activity;

import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.PageView;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/19
 */
public interface UserInfoView {
  public void showLoading();

  public void stopLoading();

  public boolean checkState(int index);

  public void showLoadError();

  public void loadComplete();

  public boolean canPull();

  public boolean validPullSize(int pullSize);

  public void showProgressBar();

  public void showButton();

  public void rotateProgressBar(float delta);

  public void stopProgressBarAnim();

  public PageView getCurrentView();

  public int getCurrentIndex();

  public void horizontalScrollable(boolean can);
}
