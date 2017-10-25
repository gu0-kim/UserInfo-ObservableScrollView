package com.gu.userinfo.observablescroll.sinaweibo_.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gu.observableviewlibrary.CacheFragmentStatePagerAdapter;
import com.gu.observableviewlibrary.ScrollUtils;
import com.gu.userinfo.observablescroll.BaseActivity;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.sinaweibo_.presenter.UserInfoPresent;
import com.gu.userinfo.observablescroll.sinaweibo_.presenter.UserInfoPresentImpl;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.ObservableFragment;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.PageView;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.UserInfoRecyclerViewFragment;
import com.gu.userinfo.observablescroll.sinaweibo_.widget.CanStopViewPager;
import com.gu.userinfo.observablescroll.widget.SlidingTabLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/10/20
 */
public class TabAndViewPagerView extends BaseActivity
    implements PtrUIHandler, PtrHandler, UserInfoView {

  private ImageView image;
  private int mFlexibleSpaceHeight;
  // mThreshold高度以下list item高度不变
  private int mTabHeight;
  private int mToolbarSize;
  private SlidingTabLayout mSlidingTabLayout;
  private LinearLayout headerLayout;
  private Toolbar toolbar;
  private ViewPager mPager;
  private NavigationAdapter mPagerAdapter;
  private int mFrontViewScrollY;
  public static final String TAG = "TAG";
  private static final int MAX_PULL_DISTANCE = 300;
  private boolean mValidPullSize = true;
  private Animation mRotateAnimation;
  private int headerLayoutInitY, tabLayoutInitY;
  private int minImgTransitionY;
  PtrClassicFrameLayout ptrFrame;
  ImageView pb;
  UserInfoPresent present;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_userinfo_main);

    present = new UserInfoPresentImpl<>(this);
    present.onCreate();

    image = (ImageView) findViewById(R.id.image);
    headerLayout = (LinearLayout) findViewById(R.id.headerlayout);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    toolbar.setTitle("我的电台");
    toolbar.setContentInsetStartWithNavigation(0);
    setSupportActionBar(toolbar);

    pb = (ImageView) findViewById(R.id.pb);
    RxView.clicks(pb)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .subscribe(o -> present.refreshView());

    mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
    mPager = (ViewPager) findViewById(R.id.pager);
    mPager.setOffscreenPageLimit(3);
    mPager.setAdapter(mPagerAdapter);

    mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
    mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
    mSlidingTabLayout.setDistributeEvenly(true);
    mSlidingTabLayout.setViewPager(mPager);

    toolbar.setNavigationOnClickListener(v -> finish());
    // Initialize the first Fragment's state when layout is completed.
    ScrollUtils.addOnGlobalLayoutListener(
        mSlidingTabLayout,
        () -> {
          loadDimens();
          translateTab(0);
        });

    ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.pager_wrapper);
    ptrFrame.disableWhenHorizontalMove(true);
    ptrFrame.getHeader().setVisibility(View.INVISIBLE);
    ptrFrame.setPtrHandler(this);
    ptrFrame.addPtrUIHandler(this);
  }

  /** call from addOnGlobalLayoutListener,load height and initY parameter */
  private void loadDimens() {
    mFlexibleSpaceHeight = image.getHeight();
    mTabHeight = mSlidingTabLayout.getHeight();
    mToolbarSize = toolbar.getHeight();
    minImgTransitionY = mFlexibleSpaceHeight - mToolbarSize - mTabHeight;
    tabLayoutInitY = mFlexibleSpaceHeight - mTabHeight;
    headerLayoutInitY = (mFlexibleSpaceHeight - mTabHeight - headerLayout.getHeight()) / 2;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    present.onDestroy();
    Log.e(TAG, "onDestroy!");
  }

  /** fragment监听滚动时回调 */
  public void onScrollChanged(int scrollY, int deltaY) {
    if (deltaY > 0) {
      // 手向上滑动
      moveBy(deltaY);
    } else {
      // 手向下滑动
      if (needMoveDown(scrollY)) {
        moveTo(scrollY);
      }
    }
  }

  @Override
  public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
    return present.canPull();
  }

  @Override
  public void onRefreshBegin(PtrFrameLayout frame) {
    present.refreshView();
  }

  @Override
  public void onUIReset(PtrFrameLayout frame) {
    showButton();
  }

  @Override
  public void onUIRefreshPrepare(PtrFrameLayout frame) {
    showProgressBar();
  }

  @Override
  public void onUIRefreshBegin(PtrFrameLayout frame) {}

  @Override
  public void onUIRefreshComplete(PtrFrameLayout frame) {}

  @Override
  public void onUIPositionChange(
      PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
    updatePullFlag(ptrIndicator.getCurrentPosY());
    pull(ptrIndicator.getCurrentPosY());
    if (ptrIndicator.isOverOffsetToRefresh()) {
      float delta =
          ptrIndicator.getCurrentPercent() - ptrIndicator.getRatioOfHeaderToHeightRefresh();
      rotateProgressBar(delta);
    }
  }

  @Override
  public void showLoading() {
    if (mRotateAnimation == null) {
      mRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.pb_rotate_anim);
    }
    pb.startAnimation(mRotateAnimation);
  }

  @Override
  public void stopLoading() {
    stopProgressBarAnim();
    pb.setImageLevel(0);
  }

  @Override
  public boolean checkState(int index) {
    return ((PageView) mPagerAdapter.getItem(index)).isLoading();
  }

  @Override
  public void showLoadError() {}

  @Override
  public void loadComplete() {
    ptrFrame.refreshComplete();
  }

  @Override
  public boolean canPull() {
    // 需要修改
    return mValidPullSize
        && mFrontViewScrollY == 0
        && !getCurrentView().isLoading()
        && getCurrentView().isTop();
  }

  @Override
  public boolean validPullSize(int pullSize) {
    return pullSize <= MAX_PULL_DISTANCE;
  }

  @Override
  public void showProgressBar() {
    Log.e(TAG, "showProgressBar: in !");
    pb.setImageLevel(1);
  }

  @Override
  public void showButton() {
    pb.setImageLevel(0);
  }

  @Override
  public void stopProgressBarAnim() {
    pb.clearAnimation();
  }

  @Override
  public void rotateProgressBar(float delta) {
    pb.setPivotX(pb.getWidth() / 2);
    pb.setPivotY(pb.getHeight() / 2);
    pb.setRotation(delta * 360);
  }

  @Override
  public PageView getCurrentView() {
    return (PageView) mPagerAdapter.getItem(mPager.getCurrentItem());
  }

  @Override
  public int getCurrentIndex() {
    return mPager.getCurrentItem();
  }

  @Override
  public void horizontalScrollable(boolean can) {
    ((CanStopViewPager) mPager).canHorizontalScroll(can);
    mSlidingTabLayout.setItemClickable(can);
  }

  /**
   * Front view move up
   *
   * @param deltaY scroll others by deltaY when currentItem moves up.
   */
  private void moveBy(int deltaY) {
    int adjustedScrollY = Math.min(mFrontViewScrollY + deltaY, minImgTransitionY);
    translateTab(adjustedScrollY);
    propagateScroll(adjustedScrollY - mFrontViewScrollY);
    mFrontViewScrollY = adjustedScrollY;
  }

  private void moveTo(int scrollY) {
    translateTab(scrollY);
    propagateScroll(scrollY - mFrontViewScrollY);
    mFrontViewScrollY = scrollY;
  }

  private boolean needMoveDown(int scrollY) {
    return scrollY < mFrontViewScrollY;
  }

  private void propagateScroll(int deltaY) {

    // Set scrollY for the active fragments
    for (int i = 0; i < mPagerAdapter.getCount(); i++) {
      // Skip current item
      if (i == mPager.getCurrentItem()) {
        continue;
      }

      // Skip destroyed or not created item
      ObservableFragment f = (ObservableFragment) mPagerAdapter.getItemAt(i);
      if (f == null) {
        continue;
      }

      View view = f.getView();
      if (view == null) {
        continue;
      }
      f.syncMove(deltaY);
    }
  }

  private void translateTab(int scrollY) {
    float alpha = scrollY >= minImgTransitionY ? 1 : (float) scrollY / minImgTransitionY;
    toolbar.setBackgroundColor(
        ScrollUtils.getColorWithAlpha(alpha, ContextCompat.getColor(this, R.color.colorPrimary)));
    headerLayout.setAlpha(1 - alpha);
    image.setTranslationY(ScrollUtils.getFloat(-scrollY, -minImgTransitionY, 0));
    ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
    float tabTranslationY =
        ScrollUtils.getFloat(-scrollY + tabLayoutInitY, mToolbarSize, tabLayoutInitY);
    float headLayoutTranslationY =
        ScrollUtils.getFloat(
            -scrollY + headerLayoutInitY, -headerLayout.getHeight(), headerLayoutInitY);
    mSlidingTabLayout.setTranslationY(tabTranslationY);
    headerLayout.setTranslationY(headLayoutTranslationY);
  }

  private void updatePullFlag(int posy) {
    mValidPullSize = validPullSize(posy);
  }

  private void pull(int posy) {
    float scale = (float) (posy + mFlexibleSpaceHeight) / mFlexibleSpaceHeight;
    image.setPivotX(image.getWidth() / 2);
    image.setPivotY(0);
    image.setScaleX(scale);
    image.setScaleY(scale);
    mSlidingTabLayout.setTranslationY(tabLayoutInitY + posy);
    headerLayout.setTranslationY(headerLayoutInitY + posy);
    //    ViewHelper.setPivotX(image, image.getWidth() / 2);
    //    ViewHelper.setPivotY(image, 0);
    //    ViewHelper.setScaleX(image, scale);
    //    ViewHelper.setScaleY(image, scale);
    //    ViewHelper.setTranslationY(mSlidingTabLayout, mFlexibleSpaceHeight - mTabHeight + posy);
  }

  private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

    private static final String[] TITLES = new String[] {"Applepie", "Butter Cookie", "Cupcake"};

    NavigationAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    protected Fragment createItem(int position) {
      return UserInfoRecyclerViewFragment.getInstance(position);
    }

    @Override
    public int getCount() {
      return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TITLES[position];
    }
  }
}
