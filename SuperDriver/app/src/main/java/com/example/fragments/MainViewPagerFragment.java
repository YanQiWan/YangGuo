package com.example.fragments;

import com.example.adapters.MainPagerAdapter;
import com.example.library.NoScrollViewPager;
import com.example.library.SingleViewPagerFragment;
import com.example.superdriver.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

public class MainViewPagerFragment extends SingleViewPagerFragment {
    @SuppressLint("ValidFragment")
    public MainViewPagerFragment(int layout) {
        super(layout);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public MainViewPagerFragment() {
        // TODO Auto-generated constructor stub
    }

    protected void initData() {
        super.initData();
        mTitle.add("嘿Siri");
        mTitle.add("导  航");
        mTitle.add("辅助驾驶");
        mTitle.add("我  的");

        mFragment.add(new SiriFragmentTest());
        mFragment.add(new MapFragment());
        mFragment.add(new AidedDrivingFragment());
        mFragment.add(new UserPageFragment());
    }

    @Override
    protected void initView(View v) {
        // TODO Auto-generated method stub
        mTabLayout = (TabLayout) v.findViewById(R.id.main_TabLayout);
        mViewPager = (NoScrollViewPager) v.findViewById(R.id.main_ViewPager);
        super.initView(v);
        MainPagerAdapter myFragmentPagerAdapter = new MainPagerAdapter(getActivity().getSupportFragmentManager(),
                mFragment, mTitle, this.getActivity());
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());

        // mViewPager
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                mTabLayout.getTabAt(arg0).select();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(myFragmentPagerAdapter.getTabView(i));
        }
        mTabLayout.setSelectedTabIndicatorHeight(0);
        //mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        // this.reflex(mTabLayout);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onTabUnselected(Tab tab) {
                // TODO Auto-generated method stub
                switch (tab.getPosition()) {
                    case 0:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab1_normal);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab2_normal);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab3_normal);
                        break;
                    case 3:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab4_normal);
                        break;
                }

            }

            @SuppressWarnings("deprecation")
            @Override
            public void onTabSelected(Tab tab) {
                // TODO Auto-generated method stub
                switch (tab.getPosition()) {
                    case 0:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab1_selected);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab2_selected);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab3_selected);
                        break;
                    case 3:
                        tab.getCustomView().findViewById(R.id.bottom_view).setBackgroundResource(R.drawable.main_tab4_selected);
                        break;
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab arg0) {
                // TODO Auto-generated method stub

            }
        });
        mViewPager.setCurrentItem(2);
        mTabLayout.getTabAt(2).select();
    }
}
