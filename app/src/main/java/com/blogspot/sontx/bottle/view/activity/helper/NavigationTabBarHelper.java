package com.blogspot.sontx.bottle.view.activity.helper;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.view.adapter.HomeFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;
import lombok.Setter;

public class NavigationTabBarHelper extends ActivityHelperBase implements ViewPager.OnPageChangeListener {
    @Setter
    private OnViewPagerTabSelectedListener onViewPagerTabSelectedListener;
    private ViewPager viewPager;

    public NavigationTabBarHelper(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    public void initializeViewPager(String currentUserId, final ViewPager viewPager) {
        this.viewPager = viewPager;

        HomeFragmentPagerAdapter homeFragmentPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        homeFragmentPagerAdapter.setCurrentUserId(currentUserId);
        viewPager.setAdapter(homeFragmentPagerAdapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final List<NavigationTabBar.Model> models = new ArrayList<NavigationTabBar.Model>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("Heart")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .title("Cup")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 1);
        //viewPager.setOffscreenPageLimit(10);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public Fragment getFragment(int position) {
        return ((HomeFragmentPagerAdapter) viewPager.getAdapter()).getRegisteredFragment(position);
    }

    @Override
    public void onPageSelected(int position) {
        if (onViewPagerTabSelectedListener != null) {
            Fragment fragment = getFragment(position);
            onViewPagerTabSelectedListener.onViewPagerTabSelected(fragment);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnViewPagerTabSelectedListener {
        void onViewPagerTabSelected(Fragment fragment);
    }
}