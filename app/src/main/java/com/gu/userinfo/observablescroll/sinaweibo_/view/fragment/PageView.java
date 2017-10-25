package com.gu.userinfo.observablescroll.sinaweibo_.view.fragment;

import com.gu.userinfo.observablescroll.sinaweibo_.presenter.PageViewPresent;

import java.util.List;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */
public interface PageView {
  public void showLoading();

  public boolean isLoading();

  public void showError(String erro);

  public void stopLoading();

  public void notifyFinishLoad();

  public void notifyStartLoad();

  public boolean isTop();

  public void setData(List<String> list);

  public void clearData();

  public PageViewPresent getPresent();
}
