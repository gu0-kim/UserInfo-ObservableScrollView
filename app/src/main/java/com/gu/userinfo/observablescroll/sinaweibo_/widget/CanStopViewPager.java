package com.gu.userinfo.observablescroll.sinaweibo_.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/25
 */
public class CanStopViewPager extends ViewPager {

  private boolean canHorizontalScroll;

  public CanStopViewPager(Context context) {
    super(context);
  }

  public CanStopViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (canHorizontalScroll) return super.onTouchEvent(ev);
    return false;
  }

  public void canHorizontalScroll(boolean can) {
    this.canHorizontalScroll = can;
  }
}
