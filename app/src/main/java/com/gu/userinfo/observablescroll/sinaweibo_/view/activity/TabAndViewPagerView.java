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
import android.widget.ImageView;

import com.gu.observableviewlibrary.CacheFragmentStatePagerAdapter;
import com.gu.observableviewlibrary.ScrollUtils;
import com.gu.userinfo.observablescroll.BaseActivity;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.sinaweibo_.presenter.UserInfoPresent;
import com.gu.userinfo.observablescroll.sinaweibo_.presenter.UserInfoPresentImpl;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.ObservableFragment;
import com.gu.userinfo.observablescroll.sinaweibo_.view.fragment.UserInfoRecyclerViewFragment;
import com.gu.userinfo.observablescroll.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

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

public class TabAndViewPagerView extends BaseActivity implements PtrUIHandler, PtrHandler, UserInfoView {

    private ImageView image;
    private int mFlexibleSpaceHeight;
    //mThreshold高度以下list item高度不变
    private int mTabHeight;
    private int mToolbarSize;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private int mFrontViewScrollY;
    public static final String TAG = "TAG";
    private static final int MAX_PULL_DISTANCE = 200;
    private boolean mValidPullSize = true;
    PtrClassicFrameLayout ptrFrame;
    ImageView pb;
    UserInfoPresent present;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sina_wei_bo_user_info);
        image = (ImageView) findViewById(R.id.image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("我的电台");
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
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
        ScrollUtils.addOnGlobalLayoutListener(mSlidingTabLayout, () -> {
            mToolbarSize = toolbar.getHeight();
            translateTab(0, false);
        });

        ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.pager_wrapper);
        ptrFrame.disableWhenHorizontalMove(true);
        ptrFrame.getHeader().setVisibility(View.INVISIBLE);
        ptrFrame.setPtrHandler(this);
        ptrFrame.addPtrUIHandler(this);

        present = new UserInfoPresentImpl<>(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        present.onDestroy();
    }

    /**
     * fragment监听滚动时回调
     *
     * @param scrollY
     * @param deltaY
     */
    public void onScrollChanged(int scrollY, int deltaY) {
        if (deltaY > 0) {
            //手向上滑动
            moveBy(deltaY);
        } else {
            //手向下滑动
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
        Log.e(TAG, "onRefreshBegin: start!");
        present.showLoading();
        present.refreshView();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        updatePullFlag(ptrIndicator.getCurrentPosY());
        pull(ptrIndicator.getCurrentPosY());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showLoadError() {

    }

    @Override
    public void loadComplete() {
        ptrFrame.refreshComplete();
    }

    @Override
    public boolean canPull() {
        //需要修改
        return mValidPullSize && mFrontViewScrollY == 0;
    }

    @Override
    public boolean validPullSize(int pullSize) {
        return pullSize <= MAX_PULL_DISTANCE;
    }

    @Override
    public Fragment getCurrentView() {
        return mPagerAdapter.getItem(mPager.getCurrentItem());
    }

    /**
     * Front view move up
     *
     * @param deltaY scroll others by deltaY when currentItem moves up.
     */
    private void moveBy(int deltaY) {
        int adjustedScrollY = Math.min(mFrontViewScrollY + deltaY, mFlexibleSpaceHeight - mTabHeight - mToolbarSize);
        translateTab(adjustedScrollY, false);
        propagateScroll(adjustedScrollY - mFrontViewScrollY);
        mFrontViewScrollY = adjustedScrollY;
    }

    private void moveTo(int scrollY) {
        translateTab(scrollY, false);
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
            ObservableFragment f =
                    (ObservableFragment) mPagerAdapter.getItemAt(i);
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


    private void translateTab(int scrollY, boolean animated) {
        View toolbar = findViewById(R.id.toolbar);
        View imageView = findViewById(R.id.image);
        int minImgTransitionY = mFlexibleSpaceHeight - mToolbarSize - mTabHeight;
        float alpha = scrollY >= minImgTransitionY ? 1 : 0;
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, ContextCompat.getColor(this, R.color.colorPrimary)));
        ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY, -minImgTransitionY, 0));
        ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
        float tabTranslationY = ScrollUtils.getFloat(-scrollY + mFlexibleSpaceHeight - mTabHeight, mToolbarSize, mFlexibleSpaceHeight - mTabHeight);
        if (animated) {
            ViewPropertyAnimator.animate(mSlidingTabLayout)
                    .translationY(tabTranslationY)
                    .setDuration(200)
                    .start();
        } else {
            ViewHelper.setTranslationY(mSlidingTabLayout, tabTranslationY);
        }
    }


    private void updatePullFlag(int posy) {
        mValidPullSize = validPullSize(posy);
    }

    private void pull(int posy) {
        float scale = (float) (posy + mFlexibleSpaceHeight) / mFlexibleSpaceHeight;
        ViewHelper.setPivotX(image, image.getWidth() / 2);
        ViewHelper.setPivotY(image, 0);
        ViewHelper.setScaleX(image, scale);
        ViewHelper.setScaleY(image, scale);
        ViewHelper.setTranslationY(mSlidingTabLayout, mFlexibleSpaceHeight - mTabHeight + posy);
    }


    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Applepie", "Butter Cookie", "Cupcake"};

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
