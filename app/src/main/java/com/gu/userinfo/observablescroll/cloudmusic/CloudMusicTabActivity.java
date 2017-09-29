/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gu.userinfo.observablescroll.cloudmusic;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gu.observableviewlibrary.CacheFragmentStatePagerAdapter;
import com.gu.observableviewlibrary.ScrollUtils;
import com.gu.observableviewlibrary.Scrollable;
import com.gu.userinfo.observablescroll.FlexibleSpaceWithImageBaseFragment;
import com.gu.userinfo.observablescroll.FlexibleSpaceWithImageRecyclerViewFragment;
import com.gu.userinfo.observablescroll.R;
import com.gu.userinfo.observablescroll.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import in.srain.cube.views.ptr.indicator.PtrIndicator;


public class CloudMusicTabActivity extends PtrActivity {


    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private TextView mTitleView;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private int mToolbarSize;
    private int mTileHeight;
    private int mScrollY;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "onCreate: CloudMusicTabActivity-----");
        setContentView(R.layout.cloud_music_main_activity);
        image = (ImageView) findViewById(R.id.image);
        mTileHeight = getActionBarSize();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("我的电台");
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);


        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(R.string.title_activity_flexiblespacewithimagewithviewpagertab);

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
        initPtrLayout();
    }


    /**
     * Called by children Fragments when their scrollY are changed.
     * They all call this method even when they are inactive
     * but this Activity should listen only the active child,
     * so each Fragments will pass themselves for Activity to check if they are active.
     *
     * @param scrollY scroll position of Scrollable
     * @param s       caller Scrollable view
     */
    public void onScrollChanged(int scrollY, Scrollable s) {

        FlexibleSpaceWithImageBaseFragment fragment =
                (FlexibleSpaceWithImageBaseFragment) mPagerAdapter.getItemAt(mPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        Scrollable scrollable = view.findViewById(R.id.scroll);
        if (scrollable == null) {
            return;
        }
        if (scrollable == s) {
            int adjustedScrollY = Math.min(scrollY, mFlexibleSpaceHeight - mTabHeight - mToolbarSize);
            mScrollY = adjustedScrollY;
            translateTab(adjustedScrollY, false);
            propagateScroll(adjustedScrollY);
        }
    }

    private void translateTab(int scrollY, boolean animated) {
        View imageView = findViewById(R.id.image);
        View toolbar = findViewById(R.id.toolbar);

        // Translate overlay and image
        int minImgTransitionY = mFlexibleSpaceHeight - mToolbarSize - mTabHeight;
        float alpha = ScrollUtils.getFloat((float) scrollY / minImgTransitionY, 0, 1);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, ContextCompat.getColor(this, R.color.colorPrimary)));
//        StatusBarCompat.compat(this, ScrollUtils.getColorWithAlpha(alpha, ContextCompat.getColor(this, R.color.colorPrimaryDark)));
        ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY / 2, -minImgTransitionY, 0));

        // Translate title text
        float titleTranslationY = ScrollUtils.getFloat(-scrollY + mFlexibleSpaceHeight - mTabHeight - mTileHeight, 0, mFlexibleSpaceHeight - mTabHeight - mTileHeight);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        //modify title text alpha
        ViewHelper.setAlpha(mTitleView, 1 - alpha);

        // If tabs are moving, cancel it to start a new animation.
        ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
        // Tabs will move between the top of the screen to the bottom of the image.
        float tabTranslationY = ScrollUtils.getFloat(-scrollY + mFlexibleSpaceHeight - mTabHeight, mToolbarSize, mFlexibleSpaceHeight - mTabHeight);
        if (animated) {
            // Animation will be invoked only when the current tab is changed.
            ViewPropertyAnimator.animate(mSlidingTabLayout)
                    .translationY(tabTranslationY)
                    .setDuration(200)
                    .start();
        } else {
            // When Fragments' scroll, translate tabs immediately (without animation).
            ViewHelper.setTranslationY(mSlidingTabLayout, tabTranslationY);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle(View view) {
        final TextView mTitleView = view.findViewById(R.id.title);
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, view.findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private void propagateScroll(int scrollY) {
        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(scrollY);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            FlexibleSpaceWithImageBaseFragment f =
                    (FlexibleSpaceWithImageBaseFragment) mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            f.setScrollY(scrollY, mFlexibleSpaceHeight);
            f.updateBackGroundView(scrollY);
        }
    }

    @Override
    public void onPull(PtrIndicator ptrIndicator) {
        float scale = (float) (ptrIndicator.getCurrentPosY() + mFlexibleSpaceHeight) / mFlexibleSpaceHeight;
        ViewHelper.setPivotX(image, image.getWidth() / 2);
        ViewHelper.setPivotY(image, 0);
        ViewHelper.setScaleX(image, scale);
        ViewHelper.setScaleY(image, scale);
        ViewHelper.setTranslationY(mSlidingTabLayout, mFlexibleSpaceHeight - mTabHeight + ptrIndicator.getCurrentPosY());
        ViewHelper.setTranslationY(mTitleView, mFlexibleSpaceHeight - mTabHeight - mTileHeight + ptrIndicator.getCurrentPosY());
    }

    public boolean checkCanDoRefresh() {
        return mScrollY == 0;
    }

    /**
     * This adapter provides three types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Applepie", "Butter Cookie", "Cupcake"};
        //{"Applepie", "Butter Cookie", "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop"};

        private int mScrollY;

        NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        protected Fragment createItem(int position) {
            FlexibleSpaceWithImageBaseFragment f;
            f = new FlexibleSpaceWithImageRecyclerViewFragment();
            f.setArguments(mScrollY, position);
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


    public int getScrollY() {
        return mScrollY;
    }
}
