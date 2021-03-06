package com.gu.userinfo.observablescroll.sinaweibo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gu.observableviewlibrary.CacheFragmentStatePagerAdapter;
import com.gu.observableviewlibrary.ScrollUtils;
import com.gu.userinfo.observablescroll.BaseActivity;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.sinaweibo.ObservableFragment;
import com.gu.userinfo.observablescroll.sinaweibo.UserInfoRecyclerViewFragment;
import com.gu.userinfo.observablescroll.sinaweibo.presenter.BasePresenter;
import com.gu.userinfo.observablescroll.sinaweibo.presenter.WeiboPresenter;
import com.gu.userinfo.observablescroll.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class SinaWeiBoUserInfoActivity extends BaseActivity implements BaseView {

  private ImageView image;
  private int mFlexibleSpaceHeight;
  // mThreshold高度以下list item高度不变
  private int mTabHeight;
  private int mToolbarSize;
  private SlidingTabLayout mSlidingTabLayout;
  private ViewPager mPager;
  private NavigationAdapter mPagerAdapter;
  private int mFrontViewScrollY;
  public static final String TAG = "TAG";

  private static final int MAX_PULL_DISTANCE = 200;
  private BasePresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e(TAG, "onCreate: SinaWeiBoUserInfoActivity-----");
    setContentView(R.layout.activity_sina_wei_bo_user_info);
    image = (ImageView) findViewById(R.id.image);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    toolbar.setTitle("我的电台");
    toolbar.setContentInsetStartWithNavigation(0);
    setSupportActionBar(toolbar);

    Toast.makeText(this, "gu say hello world!", Toast.LENGTH_LONG).show();
    mFlexibleSpaceHeight =
        getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
    mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);

    mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
    mPager = (ViewPager) findViewById(R.id.pager);
    mPager.setOffscreenPageLimit(3);
    mPager.setAdapter(mPagerAdapter);

    mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
    mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
    mSlidingTabLayout.setDistributeEvenly(true);
    mSlidingTabLayout.setViewPager(mPager);

    // Initialize the first Fragment's state when layout is completed.
    ScrollUtils.addOnGlobalLayoutListener(
        mSlidingTabLayout,
        () -> {
          mToolbarSize = toolbar.getHeight();
          translateTab(0, false);
        });
    mPresenter = new WeiboPresenter(this);
    mPresenter.onInit();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.onDestroy();
    mPresenter = null;
  }

  @Override
  public BasePresenter getPresenter() {
    return mPresenter;
  }

  @Override
  public void onInit(BasePresenter presenter) {
    final PtrClassicFrameLayout ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.pager_wrapper);
    ptrFrame.disableWhenHorizontalMove(true);
    ptrFrame.getHeader().setVisibility(View.INVISIBLE);
    ptrFrame.setPtrHandler((WeiboPresenter) presenter);
    ptrFrame.addPtrUIHandler((WeiboPresenter) presenter);
  }

  /**
   * Front view move up
   *
   * @param deltaY scroll others by deltaY when currentItem moves up.
   */
  @Override
  public void moveUp(int deltaY) {
    int adjustedScrollY =
        Math.min(mFrontViewScrollY + deltaY, mFlexibleSpaceHeight - mTabHeight - mToolbarSize);
    translateTab(adjustedScrollY, false);
    propagateScroll(adjustedScrollY - mFrontViewScrollY);
    mFrontViewScrollY = adjustedScrollY;
  }

  @Override
  public void moveDown(int scrollY) {
    translateTab(scrollY, false);
    propagateScroll(scrollY - mFrontViewScrollY);
    mFrontViewScrollY = scrollY;
  }

  @Override
  public boolean needMoveDown(int scrollY) {
    return scrollY < mFrontViewScrollY;
  }

  @Override
  public void propagateScroll(int deltaY) {

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

  public void translateTab(int scrollY, boolean animated) {
    View toolbar = findViewById(R.id.toolbar);
    View imageView = findViewById(R.id.image);
    int minImgTransitionY = mFlexibleSpaceHeight - mToolbarSize - mTabHeight;
    float alpha = scrollY >= minImgTransitionY ? 1 : 0;
    toolbar.setBackgroundColor(
        ScrollUtils.getColorWithAlpha(alpha, ContextCompat.getColor(this, R.color.colorPrimary)));
    ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY, -minImgTransitionY, 0));
    ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
    float tabTranslationY =
        ScrollUtils.getFloat(
            -scrollY + mFlexibleSpaceHeight - mTabHeight,
            mToolbarSize,
            mFlexibleSpaceHeight - mTabHeight);
    if (animated) {
      ViewPropertyAnimator.animate(mSlidingTabLayout)
          .translationY(tabTranslationY)
          .setDuration(200)
          .start();
    } else {
      ViewHelper.setTranslationY(mSlidingTabLayout, tabTranslationY);
    }
  }

  public void pull(PtrIndicator ptrIndicator) {
    float scale =
        (float) (ptrIndicator.getCurrentPosY() + mFlexibleSpaceHeight) / mFlexibleSpaceHeight;
    ViewHelper.setPivotX(image, image.getWidth() / 2);
    ViewHelper.setPivotY(image, 0);
    ViewHelper.setScaleX(image, scale);
    ViewHelper.setScaleY(image, scale);
    ViewHelper.setTranslationY(
        mSlidingTabLayout, mFlexibleSpaceHeight - mTabHeight + ptrIndicator.getCurrentPosY());
  }

  @Override
  public boolean canPull() {
    // 需要修改
    return mFrontViewScrollY == 0;
  }

  @Override
  public boolean validPullSize(int pullSize) {
    return pullSize <= MAX_PULL_DISTANCE;
  }

  private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

    private static final String[] TITLES = new String[] {"Applepie", "Butter Cookie", "Cupcake"};

    NavigationAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    protected Fragment createItem(int position) {
      ObservableFragment f;
      f = new UserInfoRecyclerViewFragment();
      return f;
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
